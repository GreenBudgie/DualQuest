package dualquest.lobby;

import dualquest.game.logic.DualQuest;
import dualquest.game.Plugin;
import dualquest.game.player.PlayerHandler;
import dualquest.util.ItemUtils;
import dualquest.util.MathUtils;
import dualquest.util.TaskManager;
import dualquest.util.WorldUtils;
import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.TileEntityJukeBox;
import net.minecraft.server.v1_15_R1.WorldServer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.block.CraftJukebox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LobbyEntertainmentHandler implements Listener {

	private static Map<Player, Jukebox> lastUsedJukebox = new HashMap<>();
	private static Map<Player, Location> lastUsedNoteBlock = new HashMap<>();
	private static Set<LobbyChair> chairs = new HashSet<>();
	private static List<Material> pottedMaterials = new ArrayList<>();
	private static Map<Material, String> noteBlockInstruments = new HashMap<>();

	public static void init() {
		Bukkit.getPluginManager().registerEvents(new LobbyEntertainmentHandler(), Plugin.INSTANCE);
		Bukkit.getPluginManager().registerEvents(new LobbyProtectionListener(), Plugin.INSTANCE);
		pottedMaterials = getPottedMaterials();
		noteBlockInstruments = getNoteBlockInstruments();
	}

	private static boolean isInLobby(Player p) {
		return PlayerHandler.isInLobby(p);
	}

	private static Map<Material, String> getNoteBlockInstruments() {
		Map<Material, String> map = new HashMap<>();
		map.put(Material.DARK_OAK_PLANKS, "Бас-гитара");
		map.put(Material.RED_SAND, "Малый барабан");
		map.put(Material.OBSIDIAN, "Большой барабан");
		map.put(Material.BONE_BLOCK, "Ксилофон");
		map.put(Material.GOLD_BLOCK, "Звонок");
		map.put(Material.CLAY, "Флейта");
		map.put(Material.PACKED_ICE, "Колокол");
		map.put(Material.BROWN_WOOL, "Гитара");
		map.put(Material.DIRT, "Пианино");
		return map;
	}

	public static void removeChair(Player p) {
		LobbyChair chair = getChair(p);
		chair.getSittingOn().remove();
		chairs.remove(chair);
	}

	public static boolean isSitting(Player p) {
		return chairs.stream().anyMatch(chair -> chair.getSittingPlayer() == p);
	}

	public static LobbyChair getChair(Player p) {
		return chairs.stream().filter(chair -> chair.getSittingPlayer() == p).findFirst().orElse(null);
	}

	public static boolean hasChairOn(Location l) {
		return chairs.stream().anyMatch(chair -> WorldUtils.compareIntegerLocations(l, chair.getLocation()));
	}

	private static List<Material> getPottedMaterials() {
		return Stream.of(Material.values()).filter(type -> type.name().startsWith("POTTED")).collect(Collectors.toList());
	}

	private static boolean isPotted(Material type) {
		return getPottedMaterials().contains(type);
	}

	@EventHandler
	public void invClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(isInLobby(p)) {
			ItemStack item = e.getCurrentItem();
			if(e.getView().getTitle().equalsIgnoreCase(ChatColor.YELLOW + "Выбери музон")) {
				Jukebox jukebox = lastUsedJukebox.get(p);
				if(item != null && item.getType() != Material.AIR) {
					if(item.getType() == Material.BARRIER && jukebox.isPlaying()) {
						CraftJukebox box = (CraftJukebox) jukebox;
						WorldServer w = ((CraftWorld) box.getWorld()).getHandle();
						BlockPosition pos = box.getPosition();
						TileEntityJukeBox tileentity = (TileEntityJukeBox) w.getTileEntity(pos);
						w.triggerEffect(1010, pos, 0);
						tileentity.clear();
						p.closeInventory();
					} else if(item.getType().isRecord()) {
						jukebox.setRecord(item);
						TaskManager.invokeLater(jukebox::update);
						p.closeInventory();
					}
				}
				e.setCancelled(true);
			}
			if(e.getView().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Выбери инструмент")) {
				if(item != null && item.getType() != Material.AIR) {
					lastUsedNoteBlock.get(p).getBlock().setType(item.getType());
					p.closeInventory();
				}
			}
		}
	}

	@EventHandler
	public void interact(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(isInLobby(p)) {
			Block block = e.getClickedBlock();
			if(e.getAction() == Action.PHYSICAL && block.getType() == Material.STONE_PRESSURE_PLATE) {
				for(PotionEffectType ef : new PotionEffectType[] {PotionEffectType.GLOWING, PotionEffectType.SPEED, PotionEffectType.JUMP}) {
					p.addPotionEffect(new PotionEffect(ef, 600, 4, false, false));
					LobbyParkourHandler.stopPassing(p);
				}
			}
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == Material.JUKEBOX && e.getHand() == EquipmentSlot.HAND) {
				Jukebox jukebox = (Jukebox) e.getClickedBlock().getState();
				lastUsedJukebox.put(p, jukebox);
				Inventory inv = Bukkit.createInventory(p, 18, ChatColor.YELLOW + "Выбери музон");
				inv.addItem(Stream.of(Material.values()).filter(Material::isRecord).map(ItemStack::new).toArray(ItemStack[]::new));
				inv.setItem(17, new ItemUtils.Builder(Material.BARRIER).withName(ChatColor.RED + "Вырубить").build());
				p.openInventory(inv);
				e.setCancelled(true);
			}
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == Material.NOTE_BLOCK && e.getHand() == EquipmentSlot.HAND && p.isSneaking()) {
				Inventory inv = Bukkit.createInventory(p, 9, ChatColor.GOLD + "Выбери инструмент");
				inv.addItem(noteBlockInstruments.keySet().stream().map(type -> ItemUtils.setName(new ItemStack(type), ChatColor.LIGHT_PURPLE + noteBlockInstruments.get(type)))
						.toArray(ItemStack[]::new));
				p.openInventory(inv);
				lastUsedNoteBlock.put(p, e.getClickedBlock().getLocation().clone().add(0, -1, 0));
				e.setCancelled(true);
			}
			if(e.getAction() == Action.LEFT_CLICK_BLOCK && block.getType() == Material.NOTE_BLOCK && e.getHand() == EquipmentSlot.HAND && p.getGameMode() == GameMode.ADVENTURE) {
				NoteBlock note = (NoteBlock) block.getBlockData();
				p.playNote(block.getLocation(), note.getInstrument(), note.getNote());
			}
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.SPRUCE_STAIRS && e.getHand() == EquipmentSlot.HAND) {
				if(!hasChairOn(block.getLocation())) {
					LobbyChair chair = new LobbyChair(p, block.getLocation().clone().add(0.5, 0, 0.5));
					chairs.add(chair);
				}
			}
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK && (isPotted(block.getType()) || block.getType() == Material.FLOWER_POT) && e.getHand() == EquipmentSlot.HAND) {
				block.setType(MathUtils.choose(pottedMaterials));
				e.setCancelled(true);
			}
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == Material.OAK_BUTTON && e.getHand() == EquipmentSlot.HAND) {
				p.getWorld().playSound(p.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1F, 1.5F);
				p.teleport(new Location(p.getWorld(), 7.5, 165, 16.5));
				p.getWorld().playSound(p.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1F, 1.5F);
			}
		}
	}

	@EventHandler
	public void tp(PlayerTeleportEvent e) {
		Player p = e.getPlayer();
		if(isInLobby(p)) {
			if(isSitting(p)) {
				removeChair(p);
			}
		}
	}

	@EventHandler
	public void quit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(isInLobby(p)) {
			if(isSitting(p)) {
				removeChair(p);
			}
		}
	}

	@EventHandler
	public void itemFrameInteract(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		Entity ent = e.getRightClicked();
		if(ent instanceof ItemFrame && isInLobby(p)) {
			ItemFrame frame = (ItemFrame) ent;
			ItemStack item = frame.getItem();
			if(item.getType() == Material.CLOCK) {
				Rotation rot = frame.getRotation();
				frame.getWorld().setTime((rot.ordinal() + 3) * 3000);
			} else {
				if(p.getGameMode() != GameMode.CREATIVE) {
					e.setCancelled(true);
				}
			}
		}
	}

}

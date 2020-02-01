package dualquest.game.player;

import dualquest.game.Plugin;
import dualquest.game.logic.GameState;
import dualquest.game.logic.WorldManager;
import dualquest.util.*;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents an in-game player. Stays if the player left the game and can rejoin
 */
public class DQPlayer {

	private final String playerName;
	private boolean valid = true;
	private Player player;
	private PlayerTeam team = null;
	private boolean onServer = true;

	//In-game leave mechanics
	private Location quitLocation;
	private ArmorStand quitStand; //An armor stand that spawns when the players leaves
	private PlayerInventory savedInventory;
	private int secondsToKick = 120; //Players will be kicked in 2 minutes after leaving the game
	private boolean kickedWhileLeft = false, diedWhileLeft = false;
	private DQPlayer killer; //A player that killed this player, or null if there was another condition to die
	private int remainingQuits = 3;

	//Fields for attackers only
	private boolean isRespawning = false;
	private int secondsToRespawn = 10;

	//Fields for questers only
	private boolean isTemporaryDead = false;
	private int secondsToStartSpectating = 5;
	private boolean isSpectatingTeammates = false;

	/**
	 * Registers a new DQPlayer
	 * @param p The player who represents this DQPlayer
	 */
	public DQPlayer(Player p) {
		player = p;
		playerName = p.getName();
	}

	/**
	 * Finds the DQPlayer that is bound to the given player. Returns null if the player wasn't found. This method uses <b>player nickname comparison</b>, that means
	 * that it searches the DQPlayer by comparing {@link Player#getName()} with {@link DQPlayer#getPlayerName()}
	 * @param player The player
	 * @return DQPlayer that is bound to the given player, or null
	 */
	@Nullable
	public static DQPlayer fromPlayer(Player player) {
		if(player == null || PlayerHandler.getPlayerList().isEmpty()) return null;
		return PlayerHandler.getPlayerList().getDQPlayers().stream().filter(dqp -> dqp.getPlayerName().equals(player.getName())).findFirst().orElse(null);
	}

	/**
	 * Gets the team that the player in
	 * @return Player's team
	 */
	public PlayerTeam getTeam() {
		return team;
	}

	/**
	 * Sets the team that the player in
	 * @param team Player's team
	 */
	public void setTeam(PlayerTeam team) {
		this.team = team;
	}

	/**
	 * Gets the unchangeable player name bound to this DQPlayer
	 * @return The player name
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * Gets the player bound to this DQPlayer. If player is not on the server it will return null
	 * @return The player bound to this DQPlayer, or null
	 */
	@Nullable
	public Player getPlayer() {
		if(isOnServer()) {
			return player;
		} else {
			return null;
		}
	}

	public void moveToSpectators() {
		PlayerHandler.joinSpectators(player);
		valid = false;
	}

	public void update() {
		if(TaskManager.isSecUpdated()) {
			if(!isOnServer()) {
				secondsToKick--;
				if(secondsToKick <= 0) {
					kickWhileLeft();
				}
			} else {
				if(team == PlayerTeam.ATTACKERS) {
					if(isRespawning()) {
						EntityUtils.sendActionBarInfo(player, ChatColor.RED + "Респавн через " + ChatColor.DARK_AQUA + TaskManager.formatTime(secondsToRespawn));
						secondsToRespawn--;
						if(secondsToRespawn <= 0) {
							respawn();
						}
					}
				} else {
					if(isTemporaryDead() && !isSpectatingTeammates()) {
						secondsToStartSpectating--;
						if(secondsToStartSpectating <= 0) {
							isSpectatingTeammates = true;
						}
					}
					if(isSpectatingTeammates()) {
						Entity target = player.getSpectatorTarget();
						if(target instanceof Player) {
							Player targetPlayer = (Player) target;
							EntityUtils.sendActionBarInfo(player, ChatColor.YELLOW + "Игрок" + ChatColor.GRAY + ": " + ChatColor.GOLD + targetPlayer.getName());
						}
					}
				}
			}
		}
		if(isSpectatingTeammates()) {
			List<Player> aliveQuesters = PlayerHandler.getPlayerList().selector().aliveQuesters().selectPlayers();
			if(aliveQuesters.size() > 0) {
				Entity target = player.getSpectatorTarget();
				if(!(target instanceof Player)) {
					Player newTarget = MathUtils.choose(aliveQuesters);
					player.setSpectatorTarget(newTarget);
				}
			}
		}
	}

	public void respawn() {
		PlayerHandler.reset(player);
		player.setGameMode(GameMode.SURVIVAL);
		if(team == PlayerTeam.ATTACKERS) {
			player.teleport(WorldManager.getAttackersSpawn());
			isRespawning = false;
		} else {
			player.teleport(WorldManager.getQuestersSpawn());
			isSpectatingTeammates = false;
			isTemporaryDead = false;
		}
	}

	private void kickWhileLeft() {
		dropItemsFromStand();
		kickedWhileLeft = true;
		valid = false;
	}

	public boolean hasQuitStand() {
		return quitStand != null;
	}

	public void killAsArmorStand(@Nullable DQPlayer killer) {
		dropItemsFromStand();
		diedWhileLeft = true;
		this.killer = killer;
	}

	private void dropItemsFromStand() {
		for(ItemStack item : savedInventory.getContents()) {
			if(item != null) {
				quitLocation.getWorld().dropItemNaturally(quitLocation, item);
			}
		}
		quitStand.remove();
		quitStand = null;
	}

	/**
	 * Whether the QUESTER player has been killed and currently waits for respawn
	 * @return Whether the player is respawning
	 */
	public boolean isTemporaryDead() {
		return isTemporaryDead;
	}

	/**
	 * Whether the QUESTER player has been killed and currently spectating his teammates
	 * @return Whether the player is respawning
	 */
	public boolean isSpectatingTeammates() {
		return isSpectatingTeammates;
	}

	/**
	 * Whether the ATTACKER player has been killed and currently waits for respawn
	 * @return Whether the player is respawning
	 */
	public boolean isRespawning() {
		return isRespawning;
	}

	/**
	 * Handles player rejoin
	 * @param p The player who rejoined
	 */
	public void rejoin(Player p) {
		this.player = p;
		this.onServer = true;
		if(valid) {
			PlayerHandler.reset(player);
			player.teleport(quitLocation);
			player.getInventory().setContents(savedInventory.getContents());
			Broadcaster.inWorld(WorldManager.getGameWorld()).and(player).toChat(team.getColor() + playerName + ChatColor.DARK_GREEN + " вернулся в игру");
			quitStand.remove();
			quitStand = null;
		} else {
			PlayerHandler.joinSpectators(player);
			if(kickedWhileLeft) {
				player.sendMessage(ChatColor.RED + "Тебя не было на сервере более двух минут. " + ChatColor.BOLD + "Ты выбыл из игры.");
				kickedWhileLeft = false;
			} else if(diedWhileLeft) {
				if(killer != null) {
					player.sendMessage(killer.getTeam().getColor() + killer.getPlayerName() + ChatColor.RED + " убил тебя, пока ты отсутствовал");
				} else {
					player.sendMessage(ChatColor.RED + "Ты каким-то образом погиб во время отсутствия");
				}
				diedWhileLeft = false;
			} else {
				player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Ты выбыл из игры!");
			}
		}
	}

	/**
	 * Handles player in-game quit
	 */
	public void quit() {
		if(GameState.isPreGame()) {
			Broadcaster.inWorld(WorldManager.getGameWorld()).toChat(team.getColor() + playerName + ChatColor.RED + ChatColor.BOLD + " вылетел с серва!");
			ParticleUtils.createParticlesAround(player, Particle.SMOKE_LARGE, null, 10);
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1F, 1F);
			player = null;
			onServer = false;
			valid = false;
			return;
		}
		if(GameState.isState(GameState.ENDING)) {
			player = null;
			onServer = false;
			//TODO
			return;
		}
		if(valid) {
			remainingQuits--;
			if(remainingQuits <= 0) {
				Broadcaster.inWorld(WorldManager.getGameWorld()).toChat(team.getColor() + playerName + ChatColor.RED + " вышел из игры " + ChatColor.BOLD + "НАХУЙ");
				death();
			} else {
				ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
				PlayerInventory inventory = player.getInventory();
				ItemStack head = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta meta = (SkullMeta) head.getItemMeta();
				meta.setOwningPlayer(player);
				head.setItemMeta(meta);
				stand.setHelmet(head);
				stand.setChestplate(inventory.getChestplate());
				stand.setLeggings(inventory.getLeggings());
				stand.setBoots(inventory.getBoots());
				stand.setItemInHand(inventory.getItemInMainHand());
				stand.setBasePlate(false);
				stand.setCustomName(playerName);
				stand.setCustomNameVisible(true);
				stand.setGravity(false);
				stand.setMetadata("dead_player", new FixedMetadataValue(Plugin.INSTANCE, true));
				Broadcaster.inWorld(WorldManager.getGameWorld()).toChat(team.getColor() + playerName + ChatColor.RED + " вышел из игры");
				this.quitLocation = player.getLocation();
				this.quitStand = stand;
				this.savedInventory = inventory;
				this.secondsToKick = 120;
				this.kickedWhileLeft = false;
				this.diedWhileLeft = false;
				this.killer = null;
				this.player = null;
				this.onServer = false;
			}
		}
	}

	public Location getQuitLocation() {
		return quitLocation;
	}

	@Nullable
	public ArmorStand getQuitStand() {
		return quitStand;
	}

	/**
	 * Handles player's death
	 */
	public void death() {
		player.setGameMode(GameMode.SPECTATOR);
		if(team == PlayerTeam.QUESTERS) {
			player.sendTitle(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Ты погиб!", ChatColor.GOLD + "Респавн после выполнения квеста", 5, 60, 20);
			isTemporaryDead = true;
			secondsToStartSpectating = 5;
		} else {
			player.sendTitle(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Ты погиб!", "", 5, 60, 20);
			player.setGameMode(GameMode.SPECTATOR);
			isRespawning = true;
			secondsToRespawn = 10;
		}
	}

	/**
	 * Determines if the player is currently online
	 * @return if the player is on server
	 */
	public boolean isOnServer() {
		return onServer;
	}

	/**
	 * If false, the player will not be counted as "playing", but will remain as DQPlayer
	 * @return Whether the player is valid
	 */
	public boolean isValid() {
		return valid;
	}

	@Override
	public boolean equals(Object another) {
		return another instanceof DQPlayer && ((DQPlayer) another).playerName.equals(playerName);
	}

	@Override
	public int hashCode() {
		return playerName.hashCode();
	}

}
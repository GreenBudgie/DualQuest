package dualquest.util;

import net.minecraft.server.v1_15_R1.ChatMessageType;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutChat;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class EntityUtils {

	/**
	 * Clears potion effects that are currently applied for the given entity
	 * @param ent Entity to remove potion effects from
	 */
	public static void clearPotionEffects(LivingEntity ent) {
		ent.getActivePotionEffects().forEach(ef -> ent.removePotionEffect(ef.getType()));
	}

	/**
	 * Teleports an entity to the given location
	 * @param ent An entity
	 * @param to Location to teleport
	 * @param saveYaw Whether to save entity's yaw rotation
	 * @param savePitch Whether to save entity's pitch rotation
	 */
	public static void teleport(LivingEntity ent, Location to, boolean saveYaw, boolean savePitch) {
		Location tp = to.clone();
		if(saveYaw) tp.setYaw(ent.getLocation().getYaw());
		if(savePitch) tp.setPitch(ent.getLocation().getPitch());
		ent.teleport(tp);
	}

	/**
	 * Applies potion effect to the given entity if this effect has larger amplifier or the same amplifier and bigger duration
	 * @param e Entity to apply effect for
	 * @param effect An effect to apply
	 * @return Whether effect has been applied
	 */
	public static boolean applyPotionEffect(LivingEntity e, PotionEffect effect) {
		PotionEffect active = e.getPotionEffect(effect.getType());
		if(active != null) {
			boolean activeHasMorePower = active.getAmplifier() > effect.getAmplifier();
			if(activeHasMorePower || active.getDuration() > effect.getDuration()) {
				return false;
			}
			e.removePotionEffect(active.getType());
		}
		e.addPotionEffect(effect);
		return true;
	}

	/**
	 * Sets the maximum amount of health for an entity
	 * @param ent The entity
	 * @param health Maximum amount of health
	 * @param instantHeal Whether to instantly heal the given entity to their new max health
	 */
	public static void setMaxHealth(LivingEntity ent, double health, boolean instantHeal) {
		AttributeInstance att = ent.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		if(att != null) {
			att.setBaseValue(health);
			if(instantHeal || ent.getHealth() > health) {
				ent.setHealth(health);
			}
		}
	}

	/**
	 * Teleports an entity to the given location without changing its rotation
	 * @param ent An entity
	 * @param to Location to teleport
	 */
	public static void teleport(LivingEntity ent, Location to) {
		teleport(ent, to, true, true);
	}

	/**
	 * Sends an action bar message to a specified player
	 * @param p The player
	 * @param message Message to send
	 */
	public static void sendActionBarInfo(Player p, String message) {
		((CraftPlayer) p).getHandle().playerConnection
				.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}"), ChatMessageType.GAME_INFO));
	}

	/**
	 * Heals the given entity. If the entity is a player, it also restores the hunger and saturation levels
	 * @param ent An entity to heal
	 */
	public static void heal(LivingEntity ent) {
		AttributeInstance maxHealth = ent.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		if(maxHealth != null) {
			ent.setHealth(maxHealth.getValue());
		}
		if(ent instanceof Player) {
			Player p = (Player) ent;
			p.setFoodLevel(20);
			p.setSaturation(20);
		}
	}

}

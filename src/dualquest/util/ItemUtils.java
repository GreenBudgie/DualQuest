package dualquest.util;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Helps to make new items and change existing ones
 */
public class ItemUtils {

	/**
	 * Sets a name for a specific item
	 * @param item An item
	 * @param name Name
	 */
	public static ItemStack setName(ItemStack item, String name) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * Removes the lore from an item stack
	 * @param item An item
	 */
	public static ItemStack removeLore(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		meta.setLore(null);
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * Sets a lore for a specific item
	 * @param item An item
	 * @param lore Lore
	 */
	public static ItemStack setLore(ItemStack item, List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * Makes an item unbreakable
	 * @param item An item
	 */
	public static ItemStack setUnbreakable(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		meta.setUnbreakable(true);
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * Sets a lore as array for a specific item
	 * @param item An item
	 * @param lore Lore
	 */
	public static ItemStack setLore(ItemStack item, String... lore) {
		setLore(item, Lists.newArrayList(lore));
		return item;
	}

	private static List<String> splitLongString(String str, int max) {
		List<String> list = new ArrayList<>();
		int prev = 0;
		for(int i = 0; i < str.length(); i++) {
			if(i - prev >= max && str.charAt(i) == ' ') {
				list.add(str.substring(prev, i));
				prev = i;
			}
			if(i == str.length() - 1) {
				list.add(str.substring(prev, i + 1));
			}
		}
		return list;
	}

	/**
	 * Adds a lore for specific item splitting it by length into multiple rows
	 * @param item An item
	 * @param lore A lore
	 * @param maxLength Maximum length of a string in a row
	 */
	public static ItemStack addSplittedLore(ItemStack item, String lore, int maxLength) {
		List<String> splitted = splitLongString(lore, maxLength);
		for(int i = 0; i < splitted.size(); i++) {
			String str = splitted.get(i);
			String prevColor = "";
			if(i > 0) {
				prevColor = ChatColor.getLastColors(splitted.get(0));
			}
			addLore(item, false, prevColor + str);
		}
		return item;
	}

	/**
	 * Adds a lore for specific item splitting it by length into multiple rows. This method uses default length of 25
	 * @param item An item
	 * @param lore A lore
	 */
	public static ItemStack addSplittedLore(ItemStack item, String lore) {
		return addSplittedLore(item, lore, 25);
	}

	/**
	 * Makes item glow: adds an oxygen enchantment and hides it
	 * @param item An item
	 */
	public static ItemStack addGlow(ItemStack item) {
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.addEnchant(Enchantment.OXYGEN, 1, true);
		itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static Builder builder(Material item) {
		return new Builder(item);
	}

	public static PotionBuilder potionBuilder() {
		return new PotionBuilder();
	}

	/**
	 * Enchants item with specified enchantments
	 * @param item An item
	 * @param enchantments Enchantments
	 */
	public static ItemStack addEnchantments(ItemStack item, Enchant... enchantments) {
		Lists.newArrayList(enchantments).forEach(ench -> ench.enchant(item));
		return item;
	}

	/**
	 * Gets the item lore or returns an empty list to prevent NullPointerException
	 * @param item An item
	 */
	public static List<String> getLore(ItemStack item) {
		return item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<>();
	}

	/**
	 * Add an array of strings to existing lore or creates new one
	 * @param item An item
	 * @param toStart Whether to add new strings to start of pre existing lore
	 * @param strings List of strings to add
	 */
	public static ItemStack addLore(ItemStack item, boolean toStart, List<String> strings) {
		return addLore(item, toStart, strings.toArray(new String[0]));
	}

	/**
	 * Add an array of strings to existing lore or creates new one, putting it to the end of the lore as default
	 * @param item An item
	 * @param strings List of strings to add
	 */
	public static ItemStack addLore(ItemStack item, String... strings) {
		return addLore(item, false, strings);
	}

	/**
	 * Add an array of strings to existing lore or creates new one
	 * @param item An item
	 * @param toStart Whether to add new strings to start of pre existing lore
	 * @param strings List of strings to add
	 */
	public static ItemStack addLore(ItemStack item, boolean toStart, String... strings) {
		List<String> prevLore = getLore(item);
		List<String> lore = Lists.newArrayList(strings);
		if(!toStart) {
			prevLore.addAll(lore);
			setLore(item, prevLore);
		} else {
			lore.addAll(Lists.newArrayList(prevLore));
			setLore(item, lore);
		}
		return item;
	}

	private static String getRandomSequence(int length) {
		Random random = new Random();
		char[] symbols = new char[62];
		char[] chars = new char[52];
		char[] numbers = new char[10];
		if(length < 1) {
			throw new IllegalArgumentException("length < 1: " + length);
		}
		if(symbols[0] == (char) 0) {
			Integer symIdx = 0, numIdx = 0, charIdx = 0;
			for(int idx = 48; idx <= 57; ++idx) {
				symbols[symIdx++] = (char) (idx);
				numbers[numIdx++] = (char) (idx);
			}
			for(int idx = 97; idx < 26 + 97; ++idx) {
				symbols[symIdx++] = (char) (idx);
				chars[charIdx++] = (char) (idx);
			}
			for(int idx = 65; idx < 26 + 65; ++idx) {
				symbols[symIdx++] = (char) (idx);
				chars[charIdx++] = (char) (idx);
			}
		}
		String returnString = "";
		for(int idx = 0; idx < length; ++idx) {
			returnString += symbols[random.nextInt(symbols.length)];
		}
		return returnString;
	}

	/**
	 * Adds a random ID to an item which makes it unstackable
	 * @param item An item
	 */
	public static ItemStack setUnstackable(ItemStack item) {
		return addLore(item, false, ChatColor.DARK_GRAY + "ID: " + getRandomSequence(16));
	}

	/**
	 * Represents an enchantment with type and level
	 */
	public static class Enchant {

		public Enchantment enchantment;
		public int level;

		public Enchant(Enchantment enchantment, int level) {
			this.enchantment = enchantment;
			this.level = level;
		}

		public Enchant(Enchantment enchantment) {
			this.enchantment = enchantment;
			this.level = 1;
		}

		public ItemStack enchant(ItemStack item) {
			item.addUnsafeEnchantment(enchantment, level);
			return item;
		}

	}

	/**
	 * Builds a potion with effects
	 */
	public static class PotionBuilder {

		private PotionMeta meta;
		private boolean splash = false;
		private boolean lingering = false;

		public PotionBuilder() {
			ItemStack item = new ItemStack(Material.POTION);
			meta = (PotionMeta) item.getItemMeta();
		}

		public PotionBuilder withColor(Color color) {
			meta.setColor(color);
			return this;
		}

		public PotionBuilder withEffects(PotionEffect... effects) {
			Lists.newArrayList(effects).forEach(ef -> meta.addCustomEffect(ef, true));
			return this;
		}

		public PotionBuilder asDrinkable() {
			splash = false;
			lingering = false;
			return this;
		}

		public PotionBuilder asSplash() {
			splash = true;
			lingering = false;
			return this;
		}

		public PotionBuilder asLingering() {
			splash = false;
			lingering = true;
			return this;
		}

		public PotionBuilder withName(String name) {
			meta.setDisplayName(name);
			return this;
		}

		public ItemStack build() {
			ItemStack potion = new ItemStack(Material.POTION);
			if(splash) {
				potion.setType(Material.SPLASH_POTION);
			}
			if(lingering) {
				potion.setType(Material.LINGERING_POTION);
			}
			potion.setItemMeta(meta);
			return potion;
		}

	}

	/**
	 * Builds an item with name, lore, enchantments e.t.c.
	 */
	public static class Builder {

		protected ItemStack item;

		public Builder(Material type) {
			item = new ItemStack(type);
		}

		public Builder(ItemStack item) {
			this.item = item.clone();
		}

		public Builder withName(String name) {
			setName(item, name);
			return this;
		}

		public Builder withLore(List<String> lore) {
			addLore(item, false, lore);
			return this;
		}

		public Builder withLore(String... lore) {
			addLore(item, false, lore);
			return this;
		}

		public Builder unbreakable() {
			setUnbreakable(item);
			return this;
		}

		public Builder withSplittedLore(String lore, int length) {
			addSplittedLore(item, lore, length);
			return this;
		}

		public Builder withSplittedLore(String lore) {
			addSplittedLore(item, lore);
			return this;
		}

		public Builder withGlow() {
			addGlow(item);
			return this;
		}

		public Builder withEnchantments(Enchant... enchantments) {
			addEnchantments(item, enchantments);
			return this;
		}

		public Builder unstackable() {
			setUnstackable(item);
			return this;
		}

		public Builder withAmount(int amount) {
			item.setAmount(amount);
			return this;
		}

		public ItemStack build() {
			return item;
		}

	}

}

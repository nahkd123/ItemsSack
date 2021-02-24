package me.nahkd.itemssack;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Utils {
	
	public static String toFriendlyName(Material mat) {
		String[] words = mat.getKey().getKey().split("_");
		String str = "";
		for (String word : words) {
			if (str.length() != 0) str += " ";
			for (int i = 0; i < word.length(); i++) str += (i == 0)? Character.toUpperCase(word.charAt(i)) : word.charAt(i);
		}
		return str;
	}
	
	public static ItemStack createItem(Material mat, String name, String... lore) {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);
		return item;
	}
	
	public static int canAccept(Material mat, ItemStack[] contents) {
		int accepts = 0;
		for (ItemStack i : contents) {
			if (i == null || i.getType() == Material.AIR) accepts += mat.getMaxStackSize();
			else if (i.getType() == mat && !i.hasItemMeta()) accepts += mat.getMaxStackSize() - i.getAmount();
		}
		return accepts;
	}
	
	public static void addMaterials(Material mat, int amount, ItemStack[] contents) {
		for (int i = 0; i < contents.length; i++) {
			if (contents[i] == null || contents[i].getType() == Material.AIR) {
				contents[i] = new ItemStack(mat, Math.min(mat.getMaxStackSize(), amount));
				amount -= Math.min(mat.getMaxStackSize(), amount);
				if (amount == 0) return;
			} else if (contents[i].getType() == mat && !contents[i].hasItemMeta()) {
				int toAdd = Math.min(mat.getMaxStackSize() - contents[i].getAmount(), amount);
				amount -= toAdd;
				contents[i].setAmount(contents[i].getAmount() + toAdd);
				if (amount == 0) return;
			}
		}
	}
	
}

package me.nahkd.itemssack;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemsSackUI {
	
	private Player player;
	private Inventory inv;
	protected ItemsSackData data;
	
	public ItemsSackUI(Player player) {
		this.player = player;
		this.data = ItemsSack.getData(player.getInventory().getItemInMainHand());
		this.inv = Bukkit.createInventory(null, getUISlots(), "Items Sack");
		updateFromData();
	}
	
	private int getUISlots() {
		return getUILines() * 9;
	}
	
	private int getUILines() {
		int materialsCount = data.materialMap.size();
		return ((materialsCount - 1) / 7) + 3;
	}
	
	private static final ItemStack BORDER = Utils.createItem(Material.BLACK_STAINED_GLASS_PANE, "§0");
	private void fillBorders() {
		int lines = getUILines();
		for (int i = 0; i < lines; i++) {
			if (i == 0 || i == (lines - 1)) {
				for (int j = 0; j < 9; j++) inv.setItem(i * 9 + j, BORDER);
				continue;
			} else {
				inv.setItem(i * 9, BORDER);
				inv.setItem(i * 9 + 8, BORDER);
			}
		}
	}
	
	private void addMaterialButtons() {
		int sum = data.getSum();
		data.materialMap.forEach((mat, amount) -> {
			ItemStack disp = Utils.createItem(mat, null, "", " §7In sack: §e" + amount, " §7Can store §e" + (data.maxMaterials - sum) + " §7more", "", " §eLeft click §7to take x64", " §eRight click §7to fill your inventory ", "");
			inv.addItem(disp);
		});
	}
	
	private void addOtherButtons() {
		int lines = getUILines();
		int bottomLineIndex = (lines - 1) * 9;
		inv.setItem(bottomLineIndex + 4, Utils.createItem(Material.CHEST, "§eItems Sack", "", " §e" + data.getSum() + "§7/" + data.maxMaterials, "", " §eLeft click item in your inventory §7to add to ", " §7your items sack ", ""));
	}
	
	protected boolean isBorderClick(int slot) {
		int bottomLineIndex = (getUILines() - 1) * 9;

		if (slot >= 0 && slot <= 8) return true;
		if (slot >= bottomLineIndex && slot <= bottomLineIndex + 8) return true;
		if (slot % 9 == 0) return true;
		if ((slot - 8) % 9 == 0) return true;
		return false;
	}
	
	protected void updateFromData() {
		inv.clear();
		fillBorders();
		addMaterialButtons();
		addOtherButtons();
	}
	
	public void openUI() {
		player.openInventory(inv);
	}
	
}

package me.nahkd.itemssack;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerEventsHandler implements Listener {
	
	protected final HashMap<Player, ItemsSackUI> uis = new HashMap<>();
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		ItemStack item = event.getItem();
		ItemsSackData data = ItemsSack.getData(item);
		if (data == null) return;
		
		event.setCancelled(true);
		ItemsSackUI ui = new ItemsSackUI(event.getPlayer());
		uis.put(event.getPlayer(), ui);
		ui.openUI();
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = Bukkit.getPlayer(event.getWhoClicked().getUniqueId());
		if (player == null) return;
		if (!uis.containsKey(player)) return;
		
		ItemsSackUI ui = uis.get(player);
		Inventory clickedInventory = event.getClickedInventory();
		if (clickedInventory == null) {
			player.closeInventory();
			return;
		}
		
		InventoryType clickedInvType = clickedInventory.getType();
		if (clickedInvType == InventoryType.CHEST) {
			event.setCancelled(true);
			ItemStack clickedItem = event.getCurrentItem();
			if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
			if (ui.isBorderClick(event.getSlot())) {
				// TODO buttons
			} else {
				// Clicked the material
				Material mat = clickedItem.getType();
				int amount = event.isLeftClick()? 64 : ui.data.materialMap.get(mat);
				amount = Math.min(amount, Utils.canAccept(mat, player.getInventory().getStorageContents()));
				int taken = ui.data.take(mat, amount);
				if (taken == 0) return;
				
				ItemStack[] contents = player.getInventory().getStorageContents();
				Utils.addMaterials(mat, taken, contents);
				player.getInventory().setStorageContents(contents);
				player.getInventory().setItemInMainHand(ItemsSack.apply(player.getInventory().getItemInMainHand(), ui.data));
				ui.updateFromData();
			}
			
		} else if (clickedInvType == InventoryType.PLAYER) {
			if (event.getSlot() == player.getInventory().getHeldItemSlot() || event.isShiftClick()) {
				event.setCancelled(true);
				return;
			}
			
			ItemStack clickedItem = event.getCurrentItem();
			if (clickedItem == null || clickedItem.getType() == Material.AIR || clickedItem.hasItemMeta()) return;
			Material clickedType = clickedItem.getType();
			if (!ui.data.materialMap.containsKey(clickedType)) return;
			
			event.setCancelled(true);
			int leftover = ui.data.add(clickedItem.getType(), clickedItem.getAmount());
			if (leftover == 0) clickedItem = null;
			else clickedItem.setAmount(leftover);
			clickedInventory.setItem(event.getSlot(), clickedItem);
			player.getInventory().setItemInMainHand(ItemsSack.apply(player.getInventory().getItemInMainHand(), ui.data));
			ui.updateFromData();
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
		if (player == null) return;
		if (!uis.containsKey(player)) return;
		
		uis.remove(player);
	}
	
	@EventHandler
	public void onItemPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		if (uis.containsKey(player)) return; // Prevent glitch
		
		Item itemEntity = event.getItem();
		ItemStack item = itemEntity.getItemStack();
		if (item.hasItemMeta()) return;
		ItemStack[] storage = player.getInventory().getStorageContents();
		
		boolean change = false;
		for (int i = 0; i < storage.length; i++) {
			ItemStack storageItem = storage[i];
			ItemsSackData data = ItemsSack.getData(storageItem);
			if (data == null) continue;
			if (!data.materialMap.containsKey(item.getType())) continue;
			
			int actualAmount = data.add(item.getType(), item.getAmount());
			if (actualAmount == item.getAmount()) continue;
			item.setAmount(actualAmount);
			ItemsSack.apply(storageItem, data);
			storage[i] = storageItem;
			change = true;
			if (actualAmount == 0) break;
		}
		if (change) player.getInventory().setStorageContents(storage);
		
		// End
		if (item.getAmount() <= 0) {
			event.setCancelled(true);
			itemEntity.remove();
		} else itemEntity.setItemStack(item);
	}
	
}

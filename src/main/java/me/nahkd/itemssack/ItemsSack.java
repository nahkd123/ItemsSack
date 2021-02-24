package me.nahkd.itemssack;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemsSack extends JavaPlugin {
	
	public static ItemStack attach(ItemStack in) {
		ItemMeta meta = in.getItemMeta();
		PersistentDataContainer pdc = meta.getPersistentDataContainer();
		pdc.set(SACK_INFO, ItemsSackData.TYPE, new ItemsSackData());
		in.setItemMeta(meta);
		return in;
	}
	
	public static ItemsSackData getData(ItemStack in) {
		if (in == null || in.getType() == Material.AIR || in.getAmount() <= 0) return null;
		if (!in.hasItemMeta()) return null;
		
		ItemMeta meta = in.getItemMeta();
		PersistentDataContainer pdc = meta.getPersistentDataContainer();
		if (!pdc.has(SACK_INFO, ItemsSackData.TYPE)) return null;
		return pdc.get(SACK_INFO, ItemsSackData.TYPE);
	}
	
	public static ItemStack apply(ItemStack in, ItemsSackData data) {
		if (in == null || in.getType() == Material.AIR || in.getAmount() <= 0) return in;
		if (!in.hasItemMeta()) return in;
		
		ItemMeta meta = in.getItemMeta();
		PersistentDataContainer pdc = meta.getPersistentDataContainer();
		pdc.set(SACK_INFO, ItemsSackData.TYPE, data);
		in.setItemMeta(meta);
		return in;
	}
	
	private static ItemsSack instance;
	public static NamespacedKey SACK_INFO;
	public static NamespacedKey SACK_MAX_MATERIALS;
	public static NamespacedKey getMaterialKey(Material mat) {
		return new NamespacedKey(instance, mat.toString());
	}
	
	private PlayerEventsHandler playerEvents;
	
	@Override
	public void onEnable() {
		instance = this;
		SACK_INFO = new NamespacedKey(this, "sack");
		SACK_MAX_MATERIALS = new NamespacedKey(this, "max_materials");
		
		getServer().getPluginManager().registerEvents(playerEvents = new PlayerEventsHandler(), instance);
		getCommand("itemssack").setExecutor(new MainCommand());
	}
	
	@Override
	public void onDisable() {
		playerEvents.uis.forEach((p, ui) -> {
			p.closeInventory();
		});
	}
	
}

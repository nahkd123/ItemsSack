package me.nahkd.itemssack;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ItemsSackData {
	
	public static final PersistentData TYPE = new PersistentData();
	public static class PersistentData implements PersistentDataType<PersistentDataContainer, ItemsSackData> {
		
		@Override
		public ItemsSackData fromPrimitive(PersistentDataContainer primitive, PersistentDataAdapterContext context) {
			ItemsSackData data = new ItemsSackData();
			primitive.getKeys().forEach(key -> {
				if (key.equals(ItemsSack.SACK_MAX_MATERIALS)) {
					data.maxMaterials = primitive.get(key, PersistentDataType.INTEGER);
					return;
				}
				
				Material mat = Material.valueOf(key.getKey().toUpperCase());
				int count = primitive.get(key, PersistentDataType.INTEGER);
				data.materialMap.put(mat, count);
			});
			return data;
		}
		
		@Override
		public Class<ItemsSackData> getComplexType() {return ItemsSackData.class;}
		
		@Override
		public Class<PersistentDataContainer> getPrimitiveType() {return PersistentDataContainer.class;}
		
		@Override
		public PersistentDataContainer toPrimitive(ItemsSackData complex, PersistentDataAdapterContext context) {
			PersistentDataContainer container = context.newPersistentDataContainer();
			complex.materialMap.forEach((mat, count) -> {
				container.set(ItemsSack.getMaterialKey(mat), PersistentDataType.INTEGER, count);
			});
			container.set(ItemsSack.SACK_MAX_MATERIALS, PersistentDataType.INTEGER, complex.maxMaterials);
			return container;
		}
		
	}
	
	public final HashMap<Material, Integer> materialMap = new HashMap<>();
	public int maxMaterials = 0;
	
	public ItemsSackData() {}
	
	public int getSum() {
		int sum = 0;
		for (int i : materialMap.values()) if (i >= 0) sum += i;
		return sum;
	}
	
	/**
	 * Add all materials to map, and return left-over materials
	 * @param mat
	 * @param amount
	 * @return
	 */
	public int add(Material mat, int amount) {
		if (!materialMap.containsKey(mat)) return amount;
		int sum = getSum();
		if (maxMaterials - sum >= amount) {
			materialMap.put(mat, materialMap.get(mat) + amount);
			return 0;
		} else {
			amount -= maxMaterials - sum;
			materialMap.put(mat, materialMap.get(mat) + (maxMaterials - sum));
			return amount;
		}
	}
	
	/**
	 * Take materials from map, returns how much materials are actually taken
	 * @param mat
	 * @param amount
	 * @return
	 */
	public int take(Material mat, int amount) {
		if (!materialMap.containsKey(mat)) return 0;
		int toTake = Math.min(materialMap.get(mat), amount);
		materialMap.put(mat, materialMap.get(mat) - toTake);
		return toTake;
	}
	
}

package me.despical.oitc.handlers.items;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * @author Despical
 * <p>
 * Created at 02.07.2020
 */
public class SpecialItemManager {

	private static final HashMap<String, SpecialItem> specialItems = new HashMap<>();

	public static void addItem(String name, SpecialItem entityItem) {
		specialItems.put(name, entityItem);
	}

	public static SpecialItem getSpecialItem(String name) {
		if (specialItems.containsKey(name)) {
			return specialItems.get(name);
		}

		return null;
	}

	public static String getRelatedSpecialItem(ItemStack itemStack) {
		for (String key : specialItems.keySet()) {
			SpecialItem entityItem = specialItems.get(key);

			if (entityItem.getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(itemStack.getItemMeta().getDisplayName())) {
				return key;
			}
		}

		return null;
	}
}
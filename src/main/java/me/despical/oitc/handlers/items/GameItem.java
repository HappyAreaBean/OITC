/*
 * OITC - Kill your opponents and reach 25 points to win!
 * Copyright (C) 2024 Despical
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package me.despical.oitc.handlers.items;

import me.despical.commons.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Despical
 * <p>
 * Created at 02.07.2020
 */
public class GameItem {

	private final ItemStack itemStack;
	private final int slot;

	public GameItem(final String displayName, final Material material, int slot, final List<String> lore) {
		this.itemStack = new ItemBuilder(material).name(displayName).lore(lore).flag(ItemFlag.HIDE_UNBREAKABLE).build();
		this.slot = slot;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public int getSlot() {
		return slot;
	}
}
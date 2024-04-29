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

package me.despical.oitc.handlers.setup.components;

import me.despical.inventoryframework.pane.StaticPane;
import me.despical.oitc.Main;
import me.despical.oitc.handlers.ChatManager;
import me.despical.oitc.handlers.setup.SetupInventory;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Despical
 * <p>
 * Created at 02.07.2020
 */
public interface SetupComponent {

	Main plugin = JavaPlugin.getPlugin(Main.class);
	ChatManager chatManager = plugin.getChatManager();

	void registerComponent(SetupInventory setupInventory, StaticPane pane);
}
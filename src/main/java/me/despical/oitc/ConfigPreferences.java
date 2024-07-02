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

package me.despical.oitc;

import me.despical.commons.serializer.InventorySerializer;
import me.despical.commons.string.StringUtils;
import me.despical.commons.util.function.BiSupplier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Despical
 * <p>
 * Created at 02.07.2020
 */
public class ConfigPreferences {

	private final Map<Option, Boolean> options;

	public ConfigPreferences(Main plugin) {
		this.options = new HashMap<>();

		plugin.saveDefaultConfig();

		for (Option option : Option.values()) {
			options.put(option, plugin.getConfig().getBoolean(option.path, option.def));
		}
	}

	public boolean getOption(Option option) {
		return options.get(option);
	}

	public enum Option {

		BLOCK_COMMANDS, GAME_BAR_ENABLED, BUNGEE_ENABLED(false), CHAT_FORMAT_ENABLED, DATABASE_ENABLED(false),
		DISABLE_FALL_DAMAGE(false), DISABLE_LEAVE_COMMAND(false), DISABLE_SEPARATE_CHAT(false),
		ENABLE_SHORT_COMMANDS, INVENTORY_MANAGER_ENABLED("Inventory-Manager.Enabled"),
		NAME_TAGS_HIDDEN, UPDATE_NOTIFIER_ENABLED, REGEN_ENABLED(false), HIDE_PLAYERS, ENABLE_ARROW_PICKUPS(false),
		LEVEL_COUNTDOWN_ENABLED(false), DISABLE_SPECTATING_ON_BUNGEE(false), INSTANT_LEAVE(false), HEAL_ON_KILL(false),

		HEAL_PLAYER((config) -> {
			final List<String> list = config.getStringList("Inventory-Manager.Do-Not-Restore");
			list.forEach(InventorySerializer::addNonSerializableElements);

			return !list.contains("health");
		});

		final String path;
		final boolean def;

		Option() {
			this(true);
		}

		Option(boolean def) {
			this.def = def;
			this.path = StringUtils.capitalize(name().replace('_', '-').toLowerCase(Locale.ENGLISH), '-', '.');
		}

		Option(String path) {
			this.def = true;
			this.path = path;
		}

		Option(BiSupplier<FileConfiguration, Boolean> supplier) {
			this.path = "";
			this.def = supplier.accept(JavaPlugin.getPlugin(Main.class).getConfig());
		}
	}
}
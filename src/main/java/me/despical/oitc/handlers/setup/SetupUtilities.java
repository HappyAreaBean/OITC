package me.despical.oitc.handlers.setup;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.despical.commonsbox.serializer.LocationSerializer;
import me.despical.oitc.Main;
import me.despical.oitc.arena.Arena;

/**
 * @author Despical
 * <p>
 * Created at 02.07.2020
 */
public class SetupUtilities {

	private Main plugin = JavaPlugin.getPlugin(Main.class);
	private FileConfiguration config;
	private Arena arena;

	SetupUtilities(FileConfiguration config, Arena arena) {
		this.config = config;
		this.arena = arena;
	}

	public String isOptionDone(String path) {
		if (config.isSet(path)) {
			return plugin.getChatManager().colorRawMessage("&a&l✔ Completed &7(value: &8" + config.getString(path) + "&7)");
		}
		return plugin.getChatManager().colorRawMessage("&c&l✘ Not Completed");
	}

	public String isOptionDoneList(String path, int minimum) {
		if (config.isSet(path)) {
			if (config.getStringList(path).size() < minimum) {
				return plugin.getChatManager().colorRawMessage("&c&l✘ Not Completed | &cPlease add more spawns");
			}
			return plugin.getChatManager().colorRawMessage("&a&l✔ Completed &7(value: &8" + config.getStringList(path).size() + "&7)");
		}
		return plugin.getChatManager().colorRawMessage("&c&l✘ Not Completed");
	}

	public String isOptionDoneBool(String path) {
		if (config.isSet(path)) {
			if (Bukkit.getServer().getWorlds().get(0).getSpawnLocation().equals(LocationSerializer.locationFromString(config.getString(path)))) {
				return plugin.getChatManager().colorRawMessage("&c&l✘ Not Completed");
			}
			return plugin.getChatManager().colorRawMessage("&a&l✔ Completed");
		}
		return plugin.getChatManager().colorRawMessage("&c&l✘ Not Completed");
	}

	public int getMinimumValueHigherThanZero(String path) {
		int amount = config.getInt("instances." + arena.getId() + "." + path);
		if (amount == 0) {
			amount = 1;
		}
		return amount;
	}
}
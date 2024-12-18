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

package me.despical.oitc.handlers.sign;

import me.despical.commons.configuration.ConfigUtils;
import me.despical.commons.serializer.LocationSerializer;
import me.despical.oitc.Main;
import me.despical.oitc.arena.Arena;
import me.despical.oitc.arena.ArenaManager;
import me.despical.oitc.arena.ArenaState;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;
import java.util.logging.Level;

/**
 * @author Despical
 * <p>
 * Created at 02.07.2020
 */
public class SignManager implements Listener {

	private final Main plugin;
	private final Set<ArenaSign> arenaSigns;
	private final List<String> signLines;
	private final Map<ArenaState, String> gameStateToString;

	public SignManager(Main plugin) {
		this.plugin = plugin;
		this.arenaSigns = new HashSet<>();
		this.signLines = plugin.getChatManager().getStringList("Signs.Lines");
		this.gameStateToString = new EnumMap<>(ArenaState.class);

		for (ArenaState state : ArenaState.values()) {
			gameStateToString.put(state, plugin.getChatManager().message("Signs.Game-States." + state.getDefaultName()));
		}

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();

		if (!player.hasPermission("oitc.admin.sign.create") || !event.getLine(0).equalsIgnoreCase("[oitc]")) {
			return;
		}

		if (event.getLine(1).isEmpty()) {
			player.sendMessage(plugin.getChatManager().prefixedMessage("Signs.Please-Type-Arena-Name"));
			return;
		}

		Arena arena = plugin.getArenaRegistry().getArena(event.getLine(1));

		if (arena == null) {
			player.sendMessage(plugin.getChatManager().prefixedMessage("Signs.Arena-Doesnt-Exists"));
			return;
		}

		arenaSigns.add(new ArenaSign((Sign) event.getBlock().getState(), arena));

		for (int i = 0; i < signLines.size(); i++) {
			event.setLine(i, formatSign(signLines.get(i), arena));
		}

		player.sendMessage(plugin.getChatManager().prefixedMessage("Signs.Sign-Created"));

		FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
		List<String> locs = config.getStringList("instances." + arena.getId() + ".signs");
		locs.add(LocationSerializer.toString(event.getBlock().getLocation()));

		config.set("instances." + arena.getId() + ".signs", locs);
		ConfigUtils.saveConfig(plugin, config, "arenas");
	}

	private String formatSign(String msg, Arena arena) {
		String formatted = msg;
		int size = arena.getPlayers().size(), max = arena.getMaximumPlayers();

		formatted = formatted.replace("%map_name%", arena.getMapName());

		if (size >= max) {
			formatted = formatted.replace("%state%", plugin.getChatManager().message("Signs.Game-States.Full-Game"));
		} else {
			formatted = formatted.replace("%state%", gameStateToString.get(arena.getArenaState()));
		}

		formatted = formatted.replace("%players%", Integer.toString(size));
		formatted = formatted.replace("%max_players%", Integer.toString(max));
		return plugin.getChatManager().coloredRawMessage(formatted);
	}

	@EventHandler
	public void onSignDestroy(BlockBreakEvent event) {
		Block block = event.getBlock();
		ArenaSign arenaSign = getArenaSignByBlock(block);

		if (arenaSign == null) {
			return;
		}

		Player player = event.getPlayer();

		if (!player.hasPermission("oitc.admin.sign.break")) {
			event.setCancelled(true);
			player.sendMessage(plugin.getChatManager().prefixedMessage("Signs.Doesnt-Have-Permission"));
			return;
		}

		arenaSigns.remove(arenaSign);

		String location = LocationSerializer.toString(block.getLocation());
		FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");

		for (String arena : config.getConfigurationSection("instances").getKeys(false)) {
			String path = "instances." + arena + ".signs";

			for (String sign : config.getStringList(path)) {
				if (!sign.equals(location)) {
					continue;
				}

				List<String> signs = config.getStringList(path);
				signs.remove(location);

				config.set(path, signs);
				ConfigUtils.saveConfig(plugin, config, "arenas");
				player.sendMessage(plugin.getChatManager().prefixedMessage("Signs.Sign-Removed"));
				return;
			}
		}

		player.sendMessage(plugin.getChatManager().prefixedRawMessage("&cCouldn't remove sign from configuration! Please do this manually!"));
	}

	@EventHandler
	public void onJoinAttempt(PlayerInteractEvent e) {
		ArenaSign arenaSign = getArenaSignByBlock(e.getClickedBlock());

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && arenaSign != null) {
			e.setCancelled(true);

			Arena arena = arenaSign.getArena();

			if (arena == null) {
				return;
			}

			if (plugin.getArenaRegistry().isInArena(e.getPlayer())) {
				e.getPlayer().sendMessage(plugin.getChatManager().prefixedMessage("In-Game.Already-Playing"));
				return;
			}

			ArenaManager.joinAttempt(e.getPlayer(), arena);
		}
	}

	private ArenaSign getArenaSignByBlock(Block block) {
		return block == null || !(block.getState() instanceof Sign) ? null : arenaSigns.stream().filter(sign -> sign.getSign().getLocation().equals(block.getLocation())).findFirst().orElse(null);
	}

	public void loadSigns() {
		arenaSigns.clear();

		FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");

		for (String path : config.getConfigurationSection("instances").getKeys(false)) {
			for (String sign : config.getStringList("instances." + path + ".signs")) {
				Location loc = LocationSerializer.fromString(sign);
				
				if (loc.getBlock().getState() instanceof Sign) {
					arenaSigns.add(new ArenaSign((Sign) loc.getBlock().getState(), plugin.getArenaRegistry().getArena(path)));
				} else {
					plugin.getLogger().log(Level.WARNING, "Block at location ''{0}'' for arena {1} not a sign.", new Object[] { sign, path });
				}
			}
		}

		updateSigns();
	}

	public void updateSigns() {
		for (final ArenaSign arenaSign : arenaSigns) {
			final Sign sign = arenaSign.getSign();

			for (int i = 0; i < signLines.size(); i++) {
				sign.setLine(i, formatSign(signLines.get(i), arenaSign.getArena()));
			}

			sign.update();
		}
	}

	public void addArenaSign(Block block, Arena arena) {
		arenaSigns.add(new ArenaSign((Sign) block.getState(), arena));
		updateSigns();
	}

	public Set<ArenaSign> getArenaSigns() {
		return new HashSet<>(arenaSigns);
	}

	public boolean isGameSign(Block block) {
		return this.arenaSigns.stream().anyMatch(sign -> sign.getSign().getLocation().equals(block.getLocation()));
	}

	public void updateSign(final Arena arena) {
		this.arenaSigns.stream().filter(arenaSign -> arenaSign.getArena().equals(arena)).forEach(this::updateSign);
	}

	private void updateSign(final ArenaSign arenaSign) {
		final Sign sign = arenaSign.getSign();

		for (int i = 0; i < signLines.size(); i++) {
			sign.setLine(i, formatSign(signLines.get(i), arenaSign.getArena()));
		}

		sign.update();
	}
}
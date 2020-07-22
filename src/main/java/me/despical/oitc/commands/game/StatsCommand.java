package me.despical.oitc.commands.game;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.despical.oitc.api.StatsStorage;
import me.despical.oitc.commands.SubCommand;
import me.despical.oitc.commands.exception.CommandException;
import me.despical.oitc.user.User;

/**
 * @author Despical
 * <p>
 * Created at 02.07.2020
 */
public class StatsCommand extends SubCommand {

	public StatsCommand(String name) {
		super("stats");
	}

	@Override
	public String getPossibleArguments() {
		return null;
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) throws CommandException {
		Player player = args.length == 1 ? Bukkit.getPlayerExact(args[0]) : (Player) sender;
		if (player == null) {
			sender.sendMessage(getPlugin().getChatManager().getPrefix() + getPlugin().getChatManager().colorMessage("Commands.Admin-Commands.Player-Not-Found"));
			return;
		}
		User user = getPlugin().getUserManager().getUser(player);
		if (player.equals(sender)) {
			sender.sendMessage(getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Header", player));
		} else {
			sender.sendMessage(getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Header-Other", player).replace("%player%", player.getName()));
		}
		sender.sendMessage(getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Kills", player) + user.getStat(StatsStorage.StatisticType.KILLS));
		sender.sendMessage(getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Deaths", player) + user.getStat(StatsStorage.StatisticType.DEATHS));
		sender.sendMessage(getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Wins", player) + user.getStat(StatsStorage.StatisticType.WINS));
		sender.sendMessage(getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Loses", player) + user.getStat(StatsStorage.StatisticType.LOSES));
		sender.sendMessage(getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Games-Played", player) + user.getStat(StatsStorage.StatisticType.GAMES_PLAYED));
		sender.sendMessage(getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Highest-Score", player) + user.getStat(StatsStorage.StatisticType.HIGHEST_SCORE));
		sender.sendMessage(getPlugin().getChatManager().colorMessage("Commands.Stats-Command.Footer", player));
	}

	@Override
	public List<String> getTutorial() {
		return null;
	}

	@Override
	public CommandType getType() {
		return CommandType.HIDDEN;
	}

	@Override
	public SenderType getSenderType() {
		return SenderType.PLAYER;
	}
}
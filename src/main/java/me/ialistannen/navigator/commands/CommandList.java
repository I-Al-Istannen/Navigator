package me.ialistannen.navigator.commands;

import me.ialistannen.bukkitutil.commandsystem.base.CommandResultType;
import me.ialistannen.bukkitutil.commandsystem.implementation.DefaultCommand;
import me.ialistannen.navigator.Navigator;
import me.ialistannen.navigator.data.PlayerPoint;
import me.ialistannen.navigator.util.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lists all homes
 */
class CommandList extends DefaultCommand {

	CommandList() {
		super(Navigator.getInstance().getLanguage(), "command_list",
				Util.tr("command_list_permission"), sender -> sender instanceof Player);
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, List<String> wholeUserChat,
	                                int indexRelativeToYou) {
		return Collections.emptyList();
	}

	@Override
	public CommandResultType execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if (Navigator.getInstance().getPlayerPointManager().getAllPoints(player.getUniqueId()).isEmpty()) {
			player.sendMessage(Util.tr("list_points_no_set"));
			return CommandResultType.SUCCESSFUL;
		}

		String result = Navigator.getInstance().getPlayerPointManager().getAllPoints(player.getUniqueId())
				.stream()
				.map(PlayerPoint::getName)
				.collect(Collectors.joining(", "));

		player.sendMessage(Util.tr("list_points", result));

		return CommandResultType.SUCCESSFUL;
	}
}

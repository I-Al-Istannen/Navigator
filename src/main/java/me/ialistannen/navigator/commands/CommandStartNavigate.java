package me.ialistannen.navigator.commands;

import me.ialistannen.bukkitutil.commandsystem.base.CommandResultType;
import me.ialistannen.bukkitutil.commandsystem.implementation.DefaultCommand;
import me.ialistannen.navigator.Navigator;
import me.ialistannen.navigator.data.PlayerPoint;
import me.ialistannen.navigator.data.PlayerPointManager;
import me.ialistannen.navigator.util.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Starts the navigation
 */
class CommandStartNavigate extends DefaultCommand {

	CommandStartNavigate() {
		super(Navigator.getInstance().getLanguage(), "command_start_navigation",
				Util.tr("command_start_navigation_permission"), sender -> sender instanceof Player);
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, List<String> wholeUserChat,
	                                int indexRelativeToYou) {
		return Navigator.getInstance().getPlayerPointManager().getAllPoints(((Player) sender).getUniqueId()).stream()
				.map(PlayerPoint::getName)
				.collect(Collectors.toList());
	}

	@Override
	public CommandResultType execute(CommandSender sender, String[] args) {
		if (args.length < 1) {
			return CommandResultType.SEND_USAGE;
		}
		Player player = (Player) sender;

		PlayerPointManager playerPointManager = Navigator.getInstance().getPlayerPointManager();

		if (!playerPointManager.containsPoint(player.getUniqueId(), args[0])) {
			player.sendMessage(Util.tr("point_not_known", args[0]));
			return CommandResultType.SUCCESSFUL;
		}

		PlayerPoint point = playerPointManager.getPlayerPoint(player.getUniqueId(), args[0]);

		if (!player.getWorld().equals(point.getLocation().getWorld())) {
			player.sendMessage(Util.tr("wrong_world",
					player.getWorld().getName(), point.getLocation().getWorld().getName()));
			return CommandResultType.SUCCESSFUL;
		}

		Navigator.getInstance().getNavigationManager().startNavigation(player.getUniqueId(), point.getLocation());

		player.sendMessage(Util.tr("started_navigation",
				point.getName(), point.getLocation().distance(player.getLocation())));

		return CommandResultType.SUCCESSFUL;
	}
}

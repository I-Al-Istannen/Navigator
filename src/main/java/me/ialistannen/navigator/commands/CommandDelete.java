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
 * Let's you delete a point
 */
class CommandDelete extends DefaultCommand {

	CommandDelete() {
		super(Navigator.getInstance().getLanguage(), "command_delete",
				Util.tr("command_delete_permission"), sender -> sender instanceof Player);
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, List<String> wholeUserChat,
	                                int indexRelativeToYou) {
		Player player = (Player) sender;
		return Navigator.getInstance().getPlayerPointManager().getAllPoints(player.getUniqueId())
				.stream()
				.map(PlayerPoint::getName)
				.collect(Collectors.toList());
	}

	@Override
	public CommandResultType execute(CommandSender sender, String[] args) {
		if(args.length < 1) {
			return CommandResultType.SEND_USAGE;
		}
		Player player = (Player) sender;

		PlayerPointManager pointManager = Navigator.getInstance().getPlayerPointManager();

		if(!pointManager.containsPoint(player.getUniqueId(), args[0])) {
			player.sendMessage(Util.tr("point_not_known", args[0]));
			return CommandResultType.SUCCESSFUL;
		}

		pointManager.removePoint(player.getUniqueId(), args[0]);

		player.sendMessage(Util.tr("deleted_point", args[0]));

		return CommandResultType.SUCCESSFUL;
	}
}

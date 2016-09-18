package me.ialistannen.navigator.commands;

import me.ialistannen.bukkitutil.commandsystem.implementation.RelayCommandNode;
import me.ialistannen.navigator.Navigator;
import me.ialistannen.navigator.util.Util;

/**
 * The Main command
 */
public class CommandNavigator extends RelayCommandNode {

	public CommandNavigator() {
		super(Navigator.getInstance().getLanguage(), "command_navigator",
				Util.tr("command_navigator_permission"), sender -> true);

		addChild(new CommandStartNavigate());
		addChild(new CommandCreate());
		addChild(new CommandDelete());
		addChild(new CommandList());
		addChild(new CommandGui());
	}
}

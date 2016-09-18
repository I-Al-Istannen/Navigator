package me.ialistannen.navigator.commands;

import me.ialistannen.bukkitutil.commandsystem.base.CommandResultType;
import me.ialistannen.bukkitutil.commandsystem.implementation.DefaultCommand;
import me.ialistannen.bukkitutil.item.ItemStackBuilder;
import me.ialistannen.navigator.Navigator;
import me.ialistannen.navigator.data.PlayerPoint;
import me.ialistannen.navigator.util.Util;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Creates a {@link PlayerPoint}
 */
class CommandCreate extends DefaultCommand {

	private static final Pattern QUOTE_PATTERN = Pattern.compile("(\".+?\")");

	CommandCreate() {
		super(Navigator.getInstance().getLanguage(), "command_create",
				Util.tr("command_create_permission"), sender -> sender instanceof Player);
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, List<String> wholeUserChat,
	                                int indexRelativeToYou) {
		return Collections.emptyList();
	}

	@Override
	public CommandResultType execute(CommandSender sender, String[] args) {
		if(args.length < 4) {
			return CommandResultType.SEND_USAGE;
		}

		Player player = (Player) sender;
		String name = args[0];

		if(Navigator.getInstance().getPlayerPointManager().containsPoint(player.getUniqueId(), name)) {
			player.sendMessage(Util.tr("point_already_exists", name));
			return CommandResultType.SUCCESSFUL;
		}

		String materialName = args[1];

		short durability = 0;

		if(materialName.contains(":")) {
			materialName = args[1].split(":")[0];
			try {
				durability = Short.parseShort(args[1].split(":")[1]);
			} catch (NumberFormatException e) {
				player.sendMessage(Util.tr("not_a_number", args[1].split(":")[1]));
			}
		}

		Material material = Material.matchMaterial(materialName);

		if(material == null) {
			player.sendMessage(Util.tr("unknown_material", materialName));
			return CommandResultType.SUCCESSFUL;
		}

		String joined = Arrays.stream(args).skip(2).collect(Collectors.joining(" "));

		List<String> extracted = extractStrings(joined);

		if(extracted.size() < 1) {
			return CommandResultType.SEND_USAGE;
		}

		String itemName = Util.color(extracted.get(0));

		List<String> lore = new ArrayList<>();

		if(extracted.size() > 1) {
			for (int i = 1; i < extracted.size(); i++) {
				lore.add(Util.color(extracted.get(i)));
			}
		}

		ItemStack itemStack = ItemStackBuilder
				.builder(material)
				.setName(itemName)
				.setLore(lore)
				.setDurability(durability)
				.build();

		PlayerPoint point = new PlayerPoint(itemStack, name, player.getLocation());

		Navigator.getInstance().getPlayerPointManager().addPoint(player.getUniqueId(), point);

		player.sendMessage(Util.tr("added_point", name));

		return CommandResultType.SUCCESSFUL;
	}

	private List<String> extractStrings(String string) {
		Matcher matcher = QUOTE_PATTERN.matcher(string);

		List<String> list = new ArrayList<>();

		while (matcher.find()) {
			String matched = matcher.group(1);
			matched = matched.substring(1, matched.length() - 1);
			list.add(matched);
		}

		return list;
	}
}

package me.ialistannen.navigator.commands;

import me.ialistannen.bukkitutil.commandsystem.base.CommandResultType;
import me.ialistannen.bukkitutil.commandsystem.implementation.DefaultCommand;
import me.ialistannen.bukkitutil.item.ItemStackBuilder;
import me.ialistannen.navigator.Navigator;
import me.ialistannen.navigator.data.PlayerPoint;
import me.ialistannen.navigator.gui.Gui;
import me.ialistannen.navigator.gui.GuiItem;
import me.ialistannen.navigator.gui.ItemClickListener;
import me.ialistannen.navigator.gui.ItemClickResult;
import me.ialistannen.navigator.gui.ItemClickResult.ItemClickResultType;
import me.ialistannen.navigator.gui.PageableGui;
import me.ialistannen.navigator.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * A simple gui interface
 */
class CommandGui extends DefaultCommand {


	CommandGui() {
		super(Navigator.getInstance().getLanguage(), "command_gui",
				Util.tr("command_gui_permission"), sender -> sender instanceof Player);
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, List<String> wholeUserChat,
	                                int indexRelativeToYou) {
		return Collections.emptyList();
	}

	@Override
	public CommandResultType execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		PageableGui gui = createGui(player);

		Navigator.getInstance().getGuiManager().openGui(player, gui);

		return CommandResultType.SUCCESSFUL;
	}

	private PageableGui createGui(Player player) {
		PageableGui gui = new PageableGui(
				Bukkit.createInventory(
						null,
						Navigator.getInstance().getConfig().getInt("gui_size", 54),
						Util.tr("gui_title")
				),
				false, true
		);

		Navigator.getInstance().getPlayerPointManager()
				.getAllPoints(player.getUniqueId())
				.forEach(point -> gui.addItem(getGuiItem(point)));

		return gui;
	}

	private GuiItem getGuiItem(PlayerPoint point) {
		return new GuiItem(point.getItem(),
				(clickEvent, gui, player) ->
						new ItemClickResult(
								createDescriptionGui(gui, point),
								ItemClickResultType.NEW_GUI_AND_CANCEL
						)
		);
	}

	private Gui createDescriptionGui(Gui oldGui, PlayerPoint point) {
		Inventory inventory = Bukkit.createInventory(null, 45, Util.tr("description_gui_title", point.getName()));
		Gui gui = new Gui(inventory, false, true);

		ItemStack defaultDelete = ItemStackBuilder.builder(Material.WOOL)
				.setName("&c&lDelete")
				.setLore("&cDeletes this point.")
				.setColor(DyeColor.RED)
				.build();

		ItemStack defaultNavigate = ItemStackBuilder.builder(Material.COMPASS)
				.setName("&a&lStart navigation")
				.setLore("&8Starts the navigation to the selected point")
				.build();

		ItemStack defaultBack = ItemStackBuilder.builder(Material.BARRIER)
				.setName("&c&lBack")
				.setLore("&8Goes back")
				.build();

		ItemStack defaultPlaceholder = ItemStackBuilder.builder(Material.STAINED_GLASS_PANE)
				.setName("")
				.setColor(DyeColor.BLACK)
				.build();


		ItemStack delete = getFromConfig("description_gui_item_delete", defaultDelete);
		ItemStack navigate = getFromConfig("description_gui_item_navigate", defaultNavigate);
		ItemStack back = getFromConfig("description_gui_item_back", defaultBack);
		ItemStack placeholder = getFromConfig("description_gui_item_placeholder", defaultPlaceholder);


		ItemClickListener deleteListener = (clickEvent, gui1, player) -> {
			// the command is cooler and shows a message. Also respects permissions.
			String command = "navigate " + Util.tr("command_delete_keyword") + " " + point.getName();
			player.performCommand(command);
			return new ItemClickResult(createGui(player), ItemClickResultType.NEW_GUI_AND_CANCEL);
		};

		ItemClickListener navigateListener = (clickEvent, gui1, player) -> {
			// the command is cooler and shows a message. Also respects permissions.
			String command = "navigate " + Util.tr("command_start_navigation_keyword") + " " + point.getName();
			player.performCommand(command);
			return new ItemClickResult(oldGui, ItemClickResultType.CLOSE_AND_CANCEL);
		};

		ItemClickListener backListener = (clickEvent, gui1, player) ->
				new ItemClickResult(oldGui, ItemClickResultType.NEW_GUI_AND_CANCEL);

		// the point
		gui.setItem(new GuiItem(point.getItem(), (cE, g, p) -> new ItemClickResult(g, ItemClickResultType.CANCEL)),
				4);

		makePlaceholderLine(gui, placeholder, 1);

		// the actions
		gui.setItem(new GuiItem(delete, deleteListener), Gui.gridToSlot(3, 2));
		gui.setItem(new GuiItem(navigate, navigateListener), Gui.gridToSlot(5, 2));


		makePlaceholderLine(gui, placeholder, 3);

		// the back button
		gui.setItem(new GuiItem(back, backListener), Gui.gridToSlot(4, 4));

		return gui;
	}

	private void makePlaceholderLine(Gui gui, ItemStack item, int y) {
		ItemClickListener listener = (clickEvent, gui1, player) ->
				new ItemClickResult(gui, ItemClickResultType.CANCEL);

		for (int x = 0; x < 9; x++) {
			int slot = Gui.gridToSlot(x, y);
			GuiItem guiItem = new GuiItem(item, listener);
			gui.setItem(guiItem, slot);
		}
	}

	private ItemStack getFromConfig(String key, ItemStack defaultItem) {
		ConfigurationSection config = Navigator.getInstance().getConfig().getConfigurationSection(key);
		if (config == null) {
			Navigator.getInstance().getLogger().warning("Section '" + key + "' doesn't exist." +
					" It should describe an item");
			return defaultItem;
		}

		String name = config.getString("name");
		if (name == null) {
			Navigator.getInstance().getLogger().warning("Section '" + key + "' misses the key 'name'");

			if (defaultItem.hasItemMeta() && defaultItem.getItemMeta().hasDisplayName()) {
				name = defaultItem.getItemMeta().getDisplayName();
			}
		}

		String materialName = config.getString("material");
		Material material = null;
		if (materialName != null) {
			material = Material.matchMaterial(materialName);
		}
		else {
			Navigator.getInstance().getLogger().warning("Section '" + key + "' misses the key 'material'");
		}
		if (material == null) {
			material = defaultItem.getType();
			Navigator.getInstance().getLogger().warning("The material in section '" + key + "' is invalid.");
		}


		List<String> lore = config.getStringList("lore");
		if (lore.isEmpty()) {
			Navigator.getInstance().getLogger().warning("Section '" + key + "' misses the key 'lore'");

			if (defaultItem.hasItemMeta() && defaultItem.getItemMeta().hasLore()) {
				lore.addAll(defaultItem.getItemMeta().getLore());
			}
		}

		short durability = (short) config.getInt("durability", -1);
		if (durability < 0) {
			durability = defaultItem.getDurability();
		}

		ItemStackBuilder builder = ItemStackBuilder.builder(material);
		if (name != null) {
			builder.setName(name);
		}
		if (!lore.isEmpty()) {
			builder.setLore(lore);
		}
		builder.setDurability(durability);

		return builder.build();
	}
}

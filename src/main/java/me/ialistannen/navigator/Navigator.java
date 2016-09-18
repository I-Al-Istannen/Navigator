package me.ialistannen.navigator;

import me.ialistannen.bukkitutil.commandsystem.base.CommandTree;
import me.ialistannen.bukkitutil.commandsystem.implementation.DefaultCommandExecutor;
import me.ialistannen.bukkitutil.commandsystem.implementation.DefaultTabCompleter;
import me.ialistannen.bukkitutil.item.ItemStackBuilder;
import me.ialistannen.bukkitutil.math.SphericalCoords;
import me.ialistannen.languageSystem.I18N;
import me.ialistannen.navigator.commands.CommandNavigator;
import me.ialistannen.navigator.data.NavigationManager;
import me.ialistannen.navigator.data.PlayerPoint;
import me.ialistannen.navigator.data.PlayerPointManager;
import me.ialistannen.navigator.gui.GuiItem;
import me.ialistannen.navigator.gui.ItemClickResult;
import me.ialistannen.navigator.gui.OpenedGuiManager;
import me.ialistannen.navigator.gui.PageableGui;
import me.ialistannen.navigator.navigatorlogic.RunningNavigator;
import me.ialistannen.navigator.particles.ParticleManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class Navigator extends JavaPlugin implements Listener {

	private static Navigator instance;

	private I18N language;

	private ParticleManager particleManager;
	private OpenedGuiManager guiManager;
	private PlayerPointManager playerPointManager;
	private NavigationManager navigationManager;

	public void onEnable() {
		instance = this;
		registerConfigurationSerializable();

		saveDefaultConfig();

		{
			Path savePath = getDataFolder().toPath().resolve("language");

			if (Files.notExists(savePath)) {
				try {
					Files.createDirectories(savePath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			I18N.copyDefaultFiles("language", savePath, false, this.getClass());

			Locale locale = Locale.forLanguageTag(getConfig().getString("language"));

			language = new I18N("language", savePath, locale, getLogger(), getClassLoader(), "Messages");
		}
		createCommands();

		particleManager = new ParticleManager();
		guiManager = new OpenedGuiManager();
		playerPointManager = PlayerPointManager.loadOrGetNew(getDataFolder().toPath().resolve("users"));
		navigationManager = new NavigationManager();

		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(guiManager, this);
	}

	private void registerConfigurationSerializable() {
		ConfigurationSerialization.registerClass(PlayerPoint.class);
		ConfigurationSerialization.registerClass(PlayerPointManager.Points.class);
	}

	private void createCommands() {
		CommandTree commandTree = new CommandTree(language);
		CommandExecutor commandExecutor = new DefaultCommandExecutor(commandTree, language);
		TabCompleter tabCompleter = new DefaultTabCompleter(commandTree);

		commandTree.addTopLevelChild(new CommandNavigator(), true, this, commandExecutor, tabCompleter);
	}

	@Override
	public void onDisable() {
		getPlayerPointManager().save();
		// prevent the old instance from still being around.
		instance = null;
	}

	/**
	 * @return The {@link NavigationManager}
	 */
	public NavigationManager getNavigationManager() {
		return navigationManager;
	}

	/**
	 * @return The language
	 */
	public I18N getLanguage() {
		return language;
	}

	/**
	 * @return The {@link PlayerPointManager}
	 */
	public PlayerPointManager getPlayerPointManager() {
		return playerPointManager;
	}

	/**
	 * @return The {@link OpenedGuiManager}
	 */
	public OpenedGuiManager getGuiManager() {
		return guiManager;
	}

	/**
	 * @return The {@link ParticleManager}
	 */
	public ParticleManager getParticleManager() {
		return particleManager;
	}

	/**
	 * Returns the plugins instance
	 *
	 * @return The plugin instance
	 */
	public static Navigator getInstance() {
		return instance;
	}


	@EventHandler
	public void onLeftClick(PlayerInteractEvent e) {
		if (e.getAction() != Action.LEFT_CLICK_AIR) {
			return;
		}

		if (e.getPlayer().isSneaking()) {
			PageableGui gui = new PageableGui(Bukkit.createInventory(null, 18), false, true);
			for (int i = 0; i < 27; i++) {
				final int j = i;
				gui.addItem(new GuiItem(
						ItemStackBuilder.builder(j < 9 ? Material.CHEST : Material.CAKE)
								.setName("&6&l" + i)
								.build(),
						(clickEvent, gui1, player) -> {
							e.getPlayer().sendMessage(j + "");
							return new ItemClickResult(gui, ItemClickResult.ItemClickResultType.CANCEL);
						}));
			}
			guiManager.openGui(e.getPlayer(), gui);
			return;
		}

		Location endLoc = e.getPlayer().getEyeLocation();

		SphericalCoords coords = new SphericalCoords(5,
				Math.toRadians(endLoc.getYaw() + 90),
				Math.toRadians(endLoc.getPitch() + 90));

		endLoc.add(coords.toBukkitVector());

		endLoc.setY(e.getPlayer().getLocation().getY());

		RunningNavigator navigator = new RunningNavigator(e.getPlayer().getUniqueId(), endLoc, getNavigationManager());
		navigator.start();
	}
}
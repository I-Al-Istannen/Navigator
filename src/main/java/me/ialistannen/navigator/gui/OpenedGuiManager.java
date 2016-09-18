package me.ialistannen.navigator.gui;

import me.ialistannen.navigator.Navigator;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages all the opened GUIs
 */
public class OpenedGuiManager implements Listener {

	private final Map<UUID, Gui> playerGuiMap = new HashMap<>();

	/**
	 * Opens a gui for a player
	 *
	 * @param player The Player to open it for
	 * @param gui    The Gui to open
	 */
	public void openGui(Player player, Gui gui) {
		if (hasOpenGui(player)) {
			closeGui(player);
		}
		// TODO: Check if this is needed
		new BukkitRunnable() {
			@Override
			public void run() {
				playerGuiMap.put(player.getUniqueId(), gui);
				gui.open(player);
			}
		}.runTask(Navigator.getInstance());
	}

	/**
	 * Checks if the player has a gui open
	 *
	 * @param player The player to check
	 *
	 * @return True if he has a gui open
	 */
	private boolean hasOpenGui(HumanEntity player) {
		return playerGuiMap.containsKey(player.getUniqueId());
	}

	/**
	 * Closes the Gui of a player
	 *
	 * @param player The player whose Gui to close
	 */
	public void closeGui(Player player) {
		if (!hasOpenGui(player)) {
			return;
		}
		playerGuiMap.remove(player.getUniqueId());
		player.closeInventory();
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (!hasOpenGui(event.getWhoClicked())) {
			return;
		}

		Gui gui = playerGuiMap.get(event.getWhoClicked().getUniqueId());
		gui.onClick(event, this);
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (!hasOpenGui(event.getPlayer())) {
			return;
		}

		Gui gui = playerGuiMap.get(event.getPlayer().getUniqueId());
		if (!gui.isAllowClose()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					gui.open(event.getPlayer());
				}
			}.runTaskLater(Navigator.getInstance(), 1);
		}
		else {
			playerGuiMap.remove(event.getPlayer().getUniqueId());
		}
	}
}

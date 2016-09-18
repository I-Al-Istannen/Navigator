package me.ialistannen.navigator.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Called when an item is clicked in a Gui
 */
@FunctionalInterface
public interface ItemClickListener {

	/**
	 * @param clickEvent The {@link InventoryClickEvent}
	 * @param gui        The Gui in which it occurred in
	 * @param player     The Player who clicked
	 *
	 * @return The resulting action
	 */
	ItemClickResult onClick(InventoryClickEvent clickEvent, Gui gui, Player player);
}

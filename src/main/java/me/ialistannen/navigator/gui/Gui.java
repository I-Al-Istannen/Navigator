package me.ialistannen.navigator.gui;

import me.ialistannen.navigator.gui.ItemClickResult.ItemClickResultType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * A gui
 */
public class Gui {

	@SuppressWarnings("WeakerAccess")
	protected final Inventory inventory;
	private final ItemClickListener inventoryFallbackListener;
	private final ItemClickListener playerInventoryListener;
	private final Map<Integer, GuiItem> items = new HashMap<>();
	private final boolean allowClose;

	/**
	 * @param inventory                 The Inventory
	 * @param inventoryFallbackListener The fallback listener. Will be called when no other matches.
	 * @param playerInventoryListener   The Listener for the player inventory. Will be called for clicks in the players
	 *                                  inventory
	 */
	private Gui(Inventory inventory, ItemClickListener inventoryFallbackListener,
	            ItemClickListener playerInventoryListener, boolean allowClose) {
		this.inventory = inventory;
		this.inventoryFallbackListener = inventoryFallbackListener;
		this.playerInventoryListener = playerInventoryListener;
		this.allowClose = allowClose;
	}

	/**
	 * @param inventory  The Inventory
	 * @param allowMove  If true, the player can move items
	 * @param allowClose If true, the player may close his inventory normally
	 */
	public Gui(Inventory inventory, boolean allowMove, boolean allowClose) {
		this(inventory, (clickEvent, gui, player) -> {
			if (allowMove) {
				return new ItemClickResult(gui, ItemClickResultType.NOTHING);
			}
			else {
				return new ItemClickResult(gui, ItemClickResultType.CANCEL);
			}
		}, (clickEvent, gui, player) -> {
			if (allowMove) {
				return new ItemClickResult(gui, ItemClickResultType.NOTHING);
			}
			else {
				return new ItemClickResult(gui, ItemClickResultType.CANCEL);
			}
		}, allowClose);
	}

	/**
	 * Opens the gui for a player
	 *
	 * @param player The player to open it for
	 */
	void open(HumanEntity player) {
		player.openInventory(inventory);
	}

	/**
	 * Checks whether the player can close the Gui using Escape or the Inventory key
	 *
	 * @return If true, the player may close the gui
	 */
	public boolean isAllowClose() {
		return allowClose;
	}

	/**
	 * Sets an item to a slot
	 *
	 * @param itemStack The Item to add
	 * @param slot      The slot to add it to
	 */
	public void setItem(GuiItem itemStack, int slot) {
		inventory.setItem(slot, itemStack);
		items.put(slot, itemStack);
	}


	void onClick(InventoryClickEvent event, OpenedGuiManager guiManager) {
		// clicked in player inventory
		if (!event.getInventory().equals(inventory)) {
			ItemClickResult result = playerInventoryListener.onClick(event, this, (Player) event.getWhoClicked());
			handleResult(event, guiManager, result);
		}
		else {
			GuiItem listener = items
					.get(event.getSlot());

			ItemClickResult result;

			if (listener == null) {
				result = inventoryFallbackListener.onClick(event, this, (Player) event.getWhoClicked());
			}
			else {
				result = listener.runClick(event, this, (Player) event.getWhoClicked());
			}
			handleResult(event, guiManager, result);
		}
	}

	private void handleResult(InventoryClickEvent event, OpenedGuiManager guiManager, ItemClickResult result) {
		switch (result.getResultType()) {
			case NOTHING:
				break;
			case CANCEL:
				event.setCancelled(true);
				break;
			case NEW_GUI_AND_CANCEL:
				event.setCancelled(true);   // fallthrough to open new
			case NEW_GUI:
				guiManager.openGui((Player) event.getWhoClicked(), result.getNextGui());
				break;
			case CLOSE_AND_CANCEL:
				event.setCancelled(true);   // fallthrough to close
			case CLOSE_GUI:
				guiManager.closeGui((Player) event.getWhoClicked());
				break;
		}
	}

	/**
	 * Converts a coordinate to a slot number
	 *
	 * @param x The x coordinate (0 based)
	 * @param y The y coordinate (0 based)
	 *
	 * @return The resulting slot index
	 */
	public static int gridToSlot(int x, int y) {
		return y * 9 + x;
	}
}

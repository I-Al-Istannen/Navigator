package me.ialistannen.navigator.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A gui item
 */
public class GuiItem extends ItemStack {

	@SuppressWarnings("CanBeFinal") // I access it in the initializer block
	private static int idCounter;

	private ItemClickListener itemClickListener;
	private final int id;

	{
		id = ++idCounter;
	}

	/**
	 * Creates a new item stack derived from the specified stack
	 *
	 * @param stack the stack to copy
	 *
	 * @throws IllegalArgumentException if the specified stack is null or
	 *                                  returns an item meta not created by the item factory
	 */
	public GuiItem(ItemStack stack, ItemClickListener itemClickListener) throws IllegalArgumentException {
		super(stack);
		this.itemClickListener = itemClickListener;
	}

	/**
	 * Reacts to a click
	 *
	 * @param clickEvent The {@link InventoryClickEvent}
	 * @param gui        The Gui
	 * @param player     The player
	 *
	 * @return The result
	 */
	@SuppressWarnings("WeakerAccess")
	public ItemClickResult runClick(InventoryClickEvent clickEvent, Gui gui, Player player) {
		return itemClickListener.onClick(clickEvent, gui, player);
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof GuiItem)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		GuiItem guiItem = (GuiItem) o;
		return id == guiItem.id && this.equals(guiItem);
	}
}

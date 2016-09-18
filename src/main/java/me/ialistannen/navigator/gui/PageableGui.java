package me.ialistannen.navigator.gui;

import me.ialistannen.bukkitutil.item.ItemStackBuilder;
import me.ialistannen.navigator.gui.ItemClickResult.ItemClickResultType;
import me.ialistannen.navigator.util.Util;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * A Gui with pages
 */
public class PageableGui extends Gui {

	private final List<GuiItem> items = new ArrayList<>();

	/**
	 * @param inventory  The Inventory
	 * @param allowMove  If true, the player can move items
	 * @param allowClose If true, the player may close his inventory normally
	 *
	 * @throws IllegalArgumentException If inventory#getSize is <= 9
	 */
	@SuppressWarnings("SameParameterValue")
	public PageableGui(Inventory inventory, boolean allowMove, boolean allowClose) {
		super(inventory, allowMove, allowClose);

		if (inventory.getSize() <= 9) {
			throw new IllegalArgumentException("Inventory size <= 9");
		}
	}

	/**
	 * <i><b>NOT SUPPORTED</b></i>
	 * <p>
	 * Use {@link #addItem(GuiItem)}
	 *
	 * @param itemStack The Item to add
	 * @param slot      The slot to add it to
	 */
	@Override
	public void setItem(GuiItem itemStack, int slot) {
		throw new UnsupportedOperationException("Direct add not supported");
	}

	private void setItemSuper(GuiItem itemStack, int slot) {
		super.setItem(itemStack, slot);
	}

	/**
	 * Adds an item to the inventory
	 *
	 * @param item The {@link GuiItem} to add
	 */
	public void addItem(GuiItem item) {
		items.add(item);
	}

	@Override
	void open(HumanEntity player) {
		buildInventory(0);
		super.open(player);
	}

	private void buildInventory(int page) {
		inventory.clear();

		int size = inventory.getSize();
		int perPage = size - 9;
		int maxPages = (int) Math.ceil(items.size() / perPage);

		if (page >= maxPages) {
			page = maxPages - 1;
		}
		if (page < 0) {
			page = 0;
		}

		int slot = 0;
		for (int i = page * perPage, max = Math.min(i + perPage, items.size())
		     ; i < max; i++) {
			setItemSuper(items.get(i), slot);
			slot++;
		}

		addControls(page, maxPages);
	}

	private void addControls(int page, int maxPages) {
		if (page == 0) {
			setLeftControl(new GuiItem(
					ItemStackBuilder.builder(Material.STAINED_GLASS_PANE)
							.setColor(DyeColor.BLACK)
							.setName(Util.tr("previous_page_item_name"))
							.build(),
					(clickEvent, gui, player) -> new ItemClickResult(this, ItemClickResultType.CANCEL))
			);
		}
		else {
			setLeftControl(new GuiItem(
					ItemStackBuilder.builder(Material.LEVER)
							.setName(Util.tr("previous_page_item_name"))
							.build(),
					(clickEvent, gui, player) -> {
						buildInventory(page - 1);
						return new ItemClickResult(gui, ItemClickResultType.CANCEL);
					})
			);
		}

		if (page < maxPages - 1) {
			setRightControl(new GuiItem(
					ItemStackBuilder.builder(Material.REDSTONE_TORCH_ON)
							.setName(Util.tr("next_page_item_name"))
							.build(),
					(clickEvent, gui, player) -> {
						buildInventory(page + 1);
						return new ItemClickResult(gui, ItemClickResultType.CANCEL);
					})
			);
		}
		else {
			setRightControl(new GuiItem(
					ItemStackBuilder.builder(Material.STAINED_GLASS_PANE)
							.setColor(DyeColor.BLACK)
							.setName(Util.tr("next_page_item_name"))
							.build(),
					(clickEvent, gui, player) -> new ItemClickResult(this, ItemClickResultType.CANCEL))
			);
		}

	}

	private void setLeftControl(GuiItem item) {
		int size = inventory.getSize();
		size /= 9;
		int slot = gridToSlot(0, size - 1);
		setItemSuper(item, slot);
	}

	private void setRightControl(GuiItem item) {
		int size = inventory.getSize();
		size /= 9;
		int slot = gridToSlot(8, size - 1);
		setItemSuper(item, slot);
	}
}

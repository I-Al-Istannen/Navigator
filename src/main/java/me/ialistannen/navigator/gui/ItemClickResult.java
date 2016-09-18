package me.ialistannen.navigator.gui;

/**
 * The result of the item click
 */
public class ItemClickResult {

	private final Gui nextGui;
	private final ItemClickResultType resultType;

	/**
	 * Creates a new {@link ItemClickResult}
	 *
	 * @param nextGui    The next gui to open. Only needed when the resultType is {@link ItemClickResultType#NEW_GUI}
	 * @param resultType The {@link ItemClickResultType}
	 */
	public ItemClickResult(Gui nextGui, ItemClickResultType resultType) {
		this.nextGui = nextGui;
		this.resultType = resultType;

		if (resultType == ItemClickResultType.NEW_GUI && nextGui == null) {
			throw new NullPointerException("Said you want a new Gui, but didn't pass one");
		}
	}

	/**
	 * Returns the next gui that should be opened
	 *
	 * @return The next Gui
	 */
	public Gui getNextGui() {
		return nextGui;
	}

	/**
	 * Returns the type of the result
	 *
	 * @return The Result type
	 */
	public ItemClickResultType getResultType() {
		return resultType;
	}

	public enum ItemClickResultType {
		/**
		 * Do nothing
		 */
		NOTHING,
		/**
		 * Cancel the click
		 */
		CANCEL,
		/**
		 * Open a new Gui
		 */
		NEW_GUI,
		/**
		 * Cancels the click and opens a new gui
		 */
		NEW_GUI_AND_CANCEL,
		/**
		 * Close this gui
		 */
		CLOSE_GUI,
		/**
		 * Closes the gui and cancels the click
		 */
		CLOSE_AND_CANCEL
	}
}

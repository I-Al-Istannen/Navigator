package me.ialistannen.navigator.util;

import me.ialistannen.navigator.Navigator;
import org.bukkit.ChatColor;

/**
 * A small utility class
 */
public class Util {

	/**
	 * @param key               The key to translate
	 * @param formattingObjects The formattingObjects
	 *
	 * @return The translated String
	 */
	public static String tr(String key, Object... formattingObjects) {
		return Navigator.getInstance().getLanguage().tr(key, formattingObjects);
	}

	/**
	 * Colors the string
	 *
	 * @param string The string to color
	 *
	 * @return The colored String
	 */
	public static String color(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}
}

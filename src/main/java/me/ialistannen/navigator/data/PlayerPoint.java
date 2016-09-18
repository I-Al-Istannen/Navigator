package me.ialistannen.navigator.data;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A point a player has defined
 */
public class PlayerPoint implements ConfigurationSerializable {

	private final ItemStack item;
	private final String name;
	private final Location location;

	/**
	 * @param item     The logo item
	 * @param name     The name it has
	 * @param location The Location it points to
	 */
	public PlayerPoint(ItemStack item, String name, Location location) {
		this.item = item.clone();
		this.name = name;
		this.location = location.clone();
	}

	/**
	 * The constructor for {@link ConfigurationSerializable}
	 */
	@SuppressWarnings("unused")
	public PlayerPoint(Map<String, Object> map) {
		this((ItemStack) map.get("item"),
				(String) map.get("name"),
				(Location) map.get("location"));
	}

	/**
	 * The logo itemstack
	 *
	 * @return The ItemStack to use as a logo
	 */
	public ItemStack getItem() {
		return item.clone();
	}

	/**
	 * Returns the name of the point
	 *
	 * @return The name of the point
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the location it points to
	 *
	 * @return The Location it points to
	 */
	public Location getLocation() {
		return location.clone();
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("item", getItem());
		map.put("name", name);
		map.put("location", getLocation());
		return map;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof PlayerPoint)) {
			return false;
		}
		PlayerPoint that = (PlayerPoint) o;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}

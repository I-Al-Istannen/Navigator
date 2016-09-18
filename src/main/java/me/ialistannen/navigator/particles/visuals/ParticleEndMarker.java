package me.ialistannen.navigator.particles.visuals;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * The end marker for the Navigator
 */
public abstract class ParticleEndMarker {

	@SuppressWarnings("UnusedParameters")
	protected ParticleEndMarker(ConfigurationSection configurationSection) {

	}

	/**
	 * Displays the end marker
	 *
	 * @param center The center location
	 * @param player The player to display it for
	 */
	@SuppressWarnings("UnusedParameters")
	public abstract void display(Location center, Player player);

}

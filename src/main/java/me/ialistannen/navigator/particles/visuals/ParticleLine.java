package me.ialistannen.navigator.particles.visuals;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * A particle line
 */
public abstract class ParticleLine {

	@SuppressWarnings("UnusedParameters")
	protected ParticleLine(ConfigurationSection configurationSection) {
	}

	/**
	 * Displays the particle line
	 *
	 * @param start  The start of the line
	 * @param target The target of the line
	 * @param player The Player to display it for
	 */
	@SuppressWarnings("UnusedParameters")
	public abstract void display(Location start, Location target, Player player);
}

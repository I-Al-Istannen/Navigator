package me.ialistannen.navigator.particles;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Displays a particle
 */
public interface ParticleProvider {

	/**
	 * @param particleName The name of the particle
	 * @param location     The Location to display it at
	 * @param otherData    The other data for the effect
	 * @param player       The Player to display it for
	 *
	 * @return The result
	 */
	@SuppressWarnings("UnusedParameters")
	ParticleDisplayResult display(String particleName, Location location, Object otherData, Player player);
}

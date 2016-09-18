package me.ialistannen.navigator.particles.visuals.implementations;

import me.ialistannen.navigator.Navigator;
import me.ialistannen.navigator.particles.ParticleManager;
import me.ialistannen.navigator.particles.visuals.ParticleEndMarker;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * A square end marker
 */
public class SquareParticleEndMarker extends ParticleEndMarker {

	private final double sideLength;
	private final double granularity;

	public SquareParticleEndMarker(ConfigurationSection configurationSection) {
		super(configurationSection);

		sideLength = configurationSection.getDouble("side_length", 2);
		granularity = configurationSection.getDouble("granularity", 0.3);
	}

	@Override
	public void display(Location center, Player player) {
		displayLeftAndRight(center.clone(), player);
		displayTopAndBottom(center.clone(), player);

		// display one in the center :)
		Navigator.getInstance().getParticleManager().displayEndMarkerParticle(center, player);
	}

	/*
	 * In minecraft: y = x and x = z
	 */
	private void displayLeftAndRight(Location center, Player player) {
		ParticleManager particleManager = Navigator.getInstance().getParticleManager();

		// the lower left corner
		double cornerX = center.getX() - (sideLength / 2);
		double leftZ = center.getZ() - (sideLength / 2);

		// for the right side
		double rightZ = center.getZ() + (sideLength / 2);

		Location location = center.clone();
		location.setX(cornerX);
		location.setZ(leftZ);

		// display left line, going from left down upwards
		for (double x = cornerX, max = cornerX + sideLength; x < max; x += granularity) {
			location.setX(x);

			// left line
			particleManager.displayEndMarkerParticle(location, player);

			// right line
			location.setZ(rightZ);
			particleManager.displayEndMarkerParticle(location, player);
			location.setZ(leftZ);
		}
	}

	/*
	 * In minecraft: y = x and x = z
	 */
	private void displayTopAndBottom(Location center, Player player) {
		ParticleManager particleManager = Navigator.getInstance().getParticleManager();

		// the top left corner
		double cornerZ = center.getZ() - (sideLength / 2);
		double topX = center.getX() + (sideLength / 2);

		double bottomX = center.getX() - (sideLength / 2);

		Location location = center.clone();
		location.setX(topX);
		location.setZ(cornerZ);

		// draw the top and bottom lines, going from left to right
		for (double z = cornerZ, max = cornerZ + sideLength; z < max; z += granularity) {
			location.setZ(z);

			// top line
			particleManager.displayEndMarkerParticle(location, player);

			// bottom line
			location.setX(bottomX);
			particleManager.displayEndMarkerParticle(location, player);
			location.setX(topX);
		}
	}
}

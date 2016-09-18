package me.ialistannen.navigator.particles.visuals.implementations;

import me.ialistannen.bukkitutil.math.SphericalCoords;
import me.ialistannen.navigator.Navigator;
import me.ialistannen.navigator.particles.ParticleManager;
import me.ialistannen.navigator.particles.visuals.ParticleLine;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Displays a simple direct line
 */
public class DirectParticleLine extends ParticleLine {

	private final double length;
	private final double minDistance;
	private final double granularity;

	public DirectParticleLine(ConfigurationSection configurationSection) {
		super(configurationSection);

		length = configurationSection.getDouble("length", 5);
		minDistance = configurationSection.getDouble("minDistance", 1);
		granularity = configurationSection.getDouble("granularity", 0.5);
	}

	@Override
	public void display(Location start, Location target, Player player) {
		Location direction = target.clone();
		direction.setDirection(target.clone().toVector().subtract(start.toVector()));

		ParticleManager particleManager = Navigator.getInstance().getParticleManager();

		Location location = start.clone();
		double max = Math.min(start.distance(target), length);

		SphericalCoords coords = new SphericalCoords(0,
				Math.toRadians(direction.getYaw() + 90),
				Math.toRadians(direction.getPitch() + 90));

		for (double rho = minDistance; rho < max; rho += granularity) {
			coords.setRho(rho);

			location.add(coords.toBukkitVector());

			particleManager.displayDefaultLineParticle(location, player);

			location.subtract(coords.toBukkitVector());
		}
	}
}

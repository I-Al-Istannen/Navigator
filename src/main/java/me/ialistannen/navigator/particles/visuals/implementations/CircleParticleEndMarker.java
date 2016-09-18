package me.ialistannen.navigator.particles.visuals.implementations;

import me.ialistannen.navigator.Navigator;
import me.ialistannen.navigator.particles.ParticleManager;
import me.ialistannen.navigator.particles.visuals.ParticleEndMarker;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * The circle particle end marker
 */
public class CircleParticleEndMarker extends ParticleEndMarker {

	private final double radius;
	private final double granularity;

	public CircleParticleEndMarker(ConfigurationSection configurationSection) {
		super(configurationSection);
		radius = configurationSection.getDouble("radius", 2);
		granularity = configurationSection.getDouble("granularity", 0.3);
	}


	@Override
	public void display(Location center, Player player) {
		ParticleManager particleManager = Navigator.getInstance().getParticleManager();

		particleManager.displayEndMarkerParticle(center, player);

		// display the circle
		for (double theta = 0, max = Math.PI * 2; theta < max; theta += granularity) {
			double x = Math.sin(theta) * radius;
			double z = Math.cos(theta) * radius;

			center.add(x, 0, z);

			particleManager.displayEndMarkerParticle(center, player);

			center.subtract(x, 0, z);
		}
	}
}

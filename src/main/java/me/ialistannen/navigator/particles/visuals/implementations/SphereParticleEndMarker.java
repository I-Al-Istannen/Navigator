package me.ialistannen.navigator.particles.visuals.implementations;

import me.ialistannen.bukkitutil.math.SphericalCoords;
import me.ialistannen.navigator.Navigator;
import me.ialistannen.navigator.particles.ParticleManager;
import me.ialistannen.navigator.particles.visuals.ParticleEndMarker;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;


/**
 * A sphere particle end marker
 */
public class SphereParticleEndMarker extends ParticleEndMarker {

	private final double radius;
	private final double granularity;

	public SphereParticleEndMarker(ConfigurationSection configurationSection) {
		super(configurationSection);

		radius = configurationSection.getDouble("radius", 1);
		granularity = configurationSection.getDouble("granularity", 0.5);
	}

	@Override
	public void display(Location center, Player player) {
		ParticleManager particleManager = Navigator.getInstance().getParticleManager();

		SphericalCoords coords = new SphericalCoords(radius, 0, 0);
		for (double phi = 0; phi < Math.PI; phi += granularity) {
			coords.setPhi(phi);

			for (double theta = 0; theta < Math.PI * 2; theta += granularity) {
				coords.setTheta(theta);

				center.add(coords.toBukkitVector());

				particleManager.displayEndMarkerParticle(center, player);

				center.subtract(coords.toBukkitVector());
			}
		}
	}
}

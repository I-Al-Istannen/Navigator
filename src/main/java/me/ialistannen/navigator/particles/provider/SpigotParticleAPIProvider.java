package me.ialistannen.navigator.particles.provider;

import me.ialistannen.navigator.particles.ParticleDisplayResult;
import me.ialistannen.navigator.particles.ParticleProvider;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

/**
 * Spawns the particle via the Spigot API
 */
public class SpigotParticleAPIProvider implements ParticleProvider {

	@Override
	public ParticleDisplayResult display(String particleName, Location location, Object otherData, Player player) {
		Particle particle;
		try {
			particle = Particle.valueOf(particleName);
		} catch (IllegalArgumentException e) {
			return ParticleDisplayResult.PARTICLE_NOT_FOUND;
		}

		player.spawnParticle(particle, location,
				1,      // count
				0, 0, 0,  // offset
				0);     // speed
		return ParticleDisplayResult.DISPLAYED;
	}
}

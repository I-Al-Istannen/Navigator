package me.ialistannen.navigator.particles.provider;

import com.darkblade12.particleeffect.ParticleEffect;
import me.ialistannen.navigator.particles.ParticleDisplayResult;
import me.ialistannen.navigator.particles.ParticleProvider;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Spawns particles using DarkBlade12's API
 */
public class DarkbladeParticleProvider implements ParticleProvider {


	@Override
	public ParticleDisplayResult display(String particleName, Location location, Object otherData, Player player) {
		ParticleEffect particle;
		try {
			particle = ParticleEffect.valueOf(particleName);
		} catch (IllegalArgumentException e) {
			return ParticleDisplayResult.PARTICLE_NOT_FOUND;
		}

		particle.display(
				0, 0, 0,        // offset
				0,              // speed
				1,              // amount
				location,       // center
				player          // the player
		);

		return ParticleDisplayResult.DISPLAYED;
	}
}

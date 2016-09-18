package me.ialistannen.navigator.particles;

import me.ialistannen.bukkitutil.commandsystem.util.ReflectionUtil;
import me.ialistannen.navigator.Navigator;
import me.ialistannen.navigator.particles.provider.DarkbladeParticleProvider;
import me.ialistannen.navigator.particles.provider.SpigotParticleAPIProvider;
import me.ialistannen.navigator.particles.visuals.ParticleEndMarker;
import me.ialistannen.navigator.particles.visuals.ParticleLine;
import me.ialistannen.navigator.particles.visuals.implementations.CircleParticleEndMarker;
import me.ialistannen.navigator.particles.visuals.implementations.DirectParticleLine;
import me.ialistannen.navigator.particles.visuals.implementations.SphereParticleEndMarker;
import me.ialistannen.navigator.particles.visuals.implementations.SquareParticleEndMarker;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Displays a default particle
 */
public class ParticleManager {

	private ParticleProvider particleProvider;

	private final Map<String, ParticleLine> particleLines = new HashMap<>();
	private final Map<String, ParticleEndMarker> endMarkers = new HashMap<>();

	{
		registerDefaultParticleProvider();

		{
			ConfigurationSection lineSection = Navigator.getInstance().getConfig()
					.getConfigurationSection("particle_line");
			particleLines.put("DirectLine", new DirectParticleLine(lineSection));
		}

		{
			ConfigurationSection markerSection = Navigator.getInstance().getConfig()
					.getConfigurationSection("particle_end_marker");
			endMarkers.put("Square", new SquareParticleEndMarker(markerSection));
			endMarkers.put("Circle", new CircleParticleEndMarker(markerSection));
			endMarkers.put("Sphere", new SphereParticleEndMarker(markerSection));
		}
	}

	private void registerDefaultParticleProvider() {
		if (ReflectionUtil.getMajorVersion() > 1 || ReflectionUtil.getMinorVersion() >= 9) {
			setParticleProvider(new SpigotParticleAPIProvider());
		}
		else {
			setParticleProvider(new DarkbladeParticleProvider());
		}
	}

	/**
	 * Sets the current particleProvider.
	 *
	 * @param particleProvider The new {@link ParticleProvider}.
	 *
	 * @throws NullPointerException if particleProvider is null
	 */
	private void setParticleProvider(ParticleProvider particleProvider) {
		Objects.requireNonNull(particleProvider);
		this.particleProvider = particleProvider;
	}

	/**
	 * Sets the {@link ParticleEndMarker} to use
	 *
	 * @param name      The name of the end marker
	 * @param endMarker The {@link ParticleEndMarker}
	 *
	 * @throws NullPointerException if endMarker or name is null
	 */
	@SuppressWarnings("unused")
	public void registerEndMarker(String name, ParticleEndMarker endMarker) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(endMarker);
		endMarkers.put(name, endMarker);
	}

	/**
	 * Returns the specified {@link ParticleEndMarker}
	 *
	 * @param name The name of the {@link ParticleEndMarker}
	 *
	 * @return The {@link ParticleEndMarker} if any
	 */
	public Optional<ParticleEndMarker> getParticleEndMarker(String name) {
		return Optional.ofNullable(endMarkers.get(name));
	}

	/**
	 * Sets the {@link ParticleLine} to use
	 *
	 * @param name         The name of the particle line
	 * @param particleLine The {@link ParticleLine}
	 *
	 * @throws NullPointerException if particleLine or name is null
	 */
	@SuppressWarnings("unused")
	public void registerParticleLine(String name, ParticleLine particleLine) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(particleLine);
		particleLines.put(name, particleLine);
	}

	/**
	 * Returns the specified {@link ParticleLine}
	 *
	 * @param name The name of the {@link ParticleLine}
	 *
	 * @return The {@link ParticleLine} if any
	 */
	public Optional<ParticleLine> getParticleLine(String name) {
		return Optional.ofNullable(particleLines.get(name));
	}

	/**
	 * Returns a fallback {@link ParticleEndMarker}
	 *
	 * @return The fallback {@link ParticleEndMarker}.
	 */
	public ParticleEndMarker getFallbackEndMarker() {
		return endMarkers.get("CircleParticleEndMarker");
	}

	/**
	 * Returns a fallback {@link ParticleLine}
	 *
	 * @return The fallback {@link ParticleLine}
	 */
	public ParticleLine getFallBackParticleLine() {
		return particleLines.get("DirectLine");
	}

	/**
	 * Displays a particle
	 *
	 * @param name     The name of the particle
	 * @param location The Location to display it at
	 * @param data     The optional data for the particle. Null for none
	 * @param player   The Player to display it for
	 *
	 * @return The {@link ParticleDisplayResult}
	 */
	@SuppressWarnings("SameParameterValue")
	private ParticleDisplayResult displayParticle(String name, Location location, Object data, Player player) {
		return particleProvider.display(name, location, data, player);
	}

	/**
	 * Displays the default particle effect for the line
	 *
	 * @param location The Location to display it at
	 *
	 * @return The {@link ParticleDisplayResult}
	 */
	@SuppressWarnings("UnusedReturnValue")
	public ParticleDisplayResult displayDefaultLineParticle(Location location, Player player) {
		String name = Navigator.getInstance().getConfig().getString("particle_line.particle");

		// yes, this renders the data field useless... And makes certain particles not work.
		ParticleDisplayResult result = displayParticle(name, location, null, player);

		if (result == ParticleDisplayResult.PARTICLE_NOT_FOUND) {
			Navigator.getInstance().getLogger().warning("Can't find particle for the particle line: '" + name + "'");
		}

		return result;
	}

	/**
	 * Displays the default particle effect for the end marker
	 *
	 * @param location The Location to display it at
	 *
	 * @return The {@link ParticleDisplayResult}
	 */
	@SuppressWarnings("UnusedReturnValue")
	public ParticleDisplayResult displayEndMarkerParticle(Location location, Player player) {
		String name = Navigator.getInstance().getConfig().getString("particle_end_marker.particle");

		// yes, this renders the data field useless... And makes certain particles not work.
		ParticleDisplayResult result = displayParticle(name, location, null, player);

		if (result == ParticleDisplayResult.PARTICLE_NOT_FOUND) {
			Navigator.getInstance().getLogger().warning("Can't find particle for end marker: '" + name + "'");
		}

		return result;
	}
}

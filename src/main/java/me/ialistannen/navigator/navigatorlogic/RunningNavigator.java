package me.ialistannen.navigator.navigatorlogic;

import me.ialistannen.navigator.Navigator;
import me.ialistannen.navigator.data.NavigationManager;
import me.ialistannen.navigator.particles.ParticleManager;
import me.ialistannen.navigator.particles.visuals.ParticleEndMarker;
import me.ialistannen.navigator.particles.visuals.ParticleLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;
import java.util.UUID;

/**
 * A running navigator.
 */
public class RunningNavigator extends BukkitRunnable {

	private final UUID playerID;
	private final Location endLocation;

	private final ParticleEndMarker endMarker;
	private final ParticleLine particleLine;

	private final NavigationManager manager;

	public RunningNavigator(UUID playerID, Location endLocation, NavigationManager manager) {
		this.playerID = playerID;
		this.endLocation = endLocation;
		this.manager = manager;

		ParticleManager particleManager = Navigator.getInstance().getParticleManager();
		{
			String name = Navigator.getInstance().getConfig().getString("particle_end_marker.name");
			Optional<ParticleEndMarker> particleEndMarker = particleManager.getParticleEndMarker(name);
			if (!particleEndMarker.isPresent()) {
				Navigator.getInstance().getLogger().warning("Can't find end line marker with name: '" + name + "'");
				endMarker = particleManager.getFallbackEndMarker();
			}
			else {
				endMarker = particleEndMarker.get();
			}
		}

		{
			String name = Navigator.getInstance().getConfig().getString("particle_line.name");
			Optional<ParticleLine> particleLine = particleManager.getParticleLine(name);
			if (!particleLine.isPresent()) {
				Navigator.getInstance().getLogger().warning("Can't find particle line with name: '" + name + "'");
				this.particleLine = particleManager.getFallBackParticleLine();
			}
			else {
				this.particleLine = particleLine.get();
			}
		}
	}

	@Override
	public void run() {
		if (isPlayerNotValid()
				|| isPlayerNearEnd()) {
			cancel();
			return;
		}

		Player player = getPlayer();

		if (!player.getLocation().getWorld().equals(endLocation.getWorld())) {
			cancel();
			return;
		}

		if (player.getLocation().distance(endLocation) <=
				Navigator.getInstance().getConfig().getDouble("distance_to_end_to_show_marker", 40)) {
			showEndMarker(player);
		}

		showLine(player);
	}

	private void showEndMarker(Player player) {
		endMarker.display(endLocation, player);
	}

	private void showLine(Player player) {
		particleLine.display(player.getLocation(), endLocation, player);
	}

	private boolean isPlayerNearEnd() {
		return getPlayer().getLocation().distance(endLocation) <
				Navigator.getInstance().getConfig().getDouble("arrived_distance", 2);
	}

	private boolean isPlayerNotValid() {
		return getPlayer() == null || !getPlayer().isValid();
	}

	private Player getPlayer() {
		return Bukkit.getPlayer(playerID);
	}

	@Override
	public synchronized void cancel() throws IllegalStateException {
		super.cancel();
		manager.stopNavigation(playerID);
	}

	/**
	 * Starts this Navigator
	 */
	public void start() {
		runTaskTimer(Navigator.getInstance(), 0, 5);
	}
}

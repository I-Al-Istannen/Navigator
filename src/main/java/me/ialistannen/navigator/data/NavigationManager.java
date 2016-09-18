package me.ialistannen.navigator.data;

import me.ialistannen.navigator.navigatorlogic.RunningNavigator;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages the currently running navigation s
 */
public class NavigationManager {

	private final Map<UUID, RunningNavigator> runningNavigatorMap = new HashMap<>();

	/**
	 * Starts the navigation.
	 * <p>
	 * Stops the current Navigation, if any.
	 *
	 * @param uuid   The {@link UUID} of the player
	 * @param target The target {@link Location}
	 */
	public void startNavigation(UUID uuid, Location target) {
		if (containsPlayer(uuid)) {
			stopNavigation(uuid);
		}
		RunningNavigator runningNavigator = new RunningNavigator(uuid, target, this);
		runningNavigatorMap.put(uuid, runningNavigator);
		runningNavigator.start();
	}

	/**
	 * Checks whether this contains the player
	 *
	 * @param uuid The {@link UUID} of the player
	 *
	 * @return True if it contains the player
	 */
	private boolean containsPlayer(UUID uuid) {
		return runningNavigatorMap.containsKey(uuid);
	}

	/**
	 * Stops the navigation
	 *
	 * @param uuid The {@link UUID} of the player
	 */
	public void stopNavigation(UUID uuid) {
		if (containsPlayer(uuid)) {
			// order is important, otherwise an infinite loop will be created, as the cancel method of the navigator
			// calls stopNavigation
			RunningNavigator removed = runningNavigatorMap.remove(uuid);
			removed.cancel();
		}
	}
}

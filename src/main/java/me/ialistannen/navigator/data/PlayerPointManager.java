package me.ialistannen.navigator.data;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * The {@link PlayerPoint} manager
 */
public class PlayerPointManager {

	private final Map<UUID, Points> pointMap = new HashMap<>();

	private final Path saveDir;

	private PlayerPointManager(Path saveDir) {
		this.saveDir = saveDir;
	}

	/**
	 * @param uuid  The {@link UUID} of the player
	 * @param point The point to add
	 */
	public void addPoint(UUID uuid, PlayerPoint point) {
		if (!contains(uuid)) {
			pointMap.put(uuid, new Points());
		}
		Points points = pointMap.get(uuid);

		points.addNotOverwrite(point);
	}

	/**
	 * Removes a point
	 *
	 * @param uuid The {@link UUID} of the player
	 * @param name The name of the {@link PlayerPoint}
	 */
	public void removePoint(UUID uuid, String name) {
		if (!contains(uuid)) {
			return;
		}
		pointMap.get(uuid).remove(name);
	}

	private boolean contains(UUID uuid) {
		return pointMap.containsKey(uuid);
	}

	/**
	 * Checks if the player has a point with that name
	 *
	 * @param uuid The {@link UUID} of the player
	 * @param name The name of the {@link PlayerPoint}
	 *
	 * @return True if there is a point with that name
	 */
	public boolean containsPoint(UUID uuid, String name) {
		return contains(uuid) && pointMap.get(uuid).contains(name);
	}

	/**
	 * @param uuid The {@link UUID} of the player
	 * @param name The name of the {@link PlayerPoint}
	 *
	 * @return The {@link PlayerPoint} or null if not found
	 */
	public PlayerPoint getPlayerPoint(UUID uuid, String name) {
		if (!contains(uuid)) {
			return null;
		}
		return pointMap.get(uuid).get(name);
	}

	/**
	 * Returns all the points of a player.
	 *
	 * @param uuid The {@link UUID} of the player
	 *
	 * @return All the {@link PlayerPoint}s in an unmodifiable collection
	 */
	public Collection<PlayerPoint> getAllPoints(UUID uuid) {
		if (!contains(uuid)) {
			return Collections.emptyList();
		}
		return pointMap.get(uuid).getAll();
	}

	/**
	 * Saves all the points
	 */
	public void save() {
		try {
			Files.createDirectories(saveDir);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Entry<UUID, Points> entry : pointMap.entrySet()) {
			Path saveFile = saveDir.resolve(entry.getKey().toString() + ".yml");

			if (entry.getValue().getAll().isEmpty()) {
				try {
					Files.deleteIfExists(saveFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				continue;
			}

			YamlConfiguration yamlConfiguration = new YamlConfiguration();
			yamlConfiguration.set("points", entry.getValue());
			try {
				yamlConfiguration.save(saveFile.toFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static PlayerPointManager loadOrGetNew(Path saveDir) {
		PlayerPointManager manager = new PlayerPointManager(saveDir);
		if (Files.notExists(saveDir)) {
			return manager;
		}
		try {
			Files.walkFileTree(saveDir, new FileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (file.getName(file.getNameCount() - 1).toString().endsWith(".yml")) {
						YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file.toFile());
						if (configuration.contains("points")) {
							Points points = (Points) configuration.get("points");
							UUID uuid = UUID.fromString(
									file
											.getName(file.getNameCount() - 1)
											.toString()
											.replace(".yml", "")
							);

							manager.pointMap.put(uuid, points);
						}
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		return manager;
	}

	public static class Points implements ConfigurationSerializable {
		private final Map<String, PlayerPoint> pointMap = new HashMap<>();

		private Points() {
		}

		/**
		 * The Constructor for ConfigurationSerializable
		 */
		@SuppressWarnings("unused")
		public Points(Map<String, Object> map) {
			@SuppressWarnings("unchecked")
			List<PlayerPoint> points = (List<PlayerPoint>) map.get("points");

			points.forEach(this::add);
		}

		/**
		 * Adds a point
		 *
		 * @param point The point to add
		 *
		 * @return True if an existing point was overwritten
		 */
		private boolean add(PlayerPoint point) {
			return pointMap.put(point.getName(), point) == null;
		}

		/**
		 * Adds a point, if it doesn't already exist
		 *
		 * @param point The point to add
		 */
		private void addNotOverwrite(PlayerPoint point) {
			if (contains(point.getName())) {
				return;
			}
			add(point);
		}

		/**
		 * Checks whether there is already a point with that name
		 *
		 * @param name The name of the point
		 *
		 * @return True if it contains the point
		 */
		private boolean contains(String name) {
			return pointMap.containsKey(name);
		}

		/**
		 * Removes a {@link PlayerPoint}
		 *
		 * @param name The name of the {@link PlayerPoint}
		 */
		private void remove(String name) {
			pointMap.remove(name);
		}

		/**
		 * Returns a {@link PlayerPoint}
		 *
		 * @param name The name of the point
		 *
		 * @return The {@link PlayerPoint} or null if none
		 */
		private PlayerPoint get(String name) {
			return pointMap.get(name);
		}

		/**
		 * Returns all the points
		 *
		 * @return All the points in an unmodifiable map
		 */
		private Collection<PlayerPoint> getAll() {
			return Collections.unmodifiableCollection(pointMap.values());
		}

		@Override
		public Map<String, Object> serialize() {
			Map<String, Object> map = new HashMap<>();
			map.put("points", new ArrayList<>(pointMap.values()));
			return map;
		}
	}
}

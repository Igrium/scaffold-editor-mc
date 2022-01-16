package org.scaffoldeditor.editormc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.lwjgl.glfw.GLFW;
import org.scaffoldeditor.editormc.engine.EditorServer;
import org.scaffoldeditor.editormc.engine.ScaffoldEditorMod;
import org.scaffoldeditor.editormc.engine.world.EditorServerWorld;
import org.scaffoldeditor.editormc.render_entities.RenderEntityManager;
import org.scaffoldeditor.editormc.scaffold_interface.WorldInterface;
import org.scaffoldeditor.editormc.ui.ScaffoldUI;
import org.scaffoldeditor.editormc.ui.attribute_types.RenderAttributeRegistry;
import org.scaffoldeditor.editormc.util.UIUtils;
import org.scaffoldeditor.nbt.block.WorldMath.ChunkCoordinate;
import org.scaffoldeditor.nbt.block.WorldMath.SectionCoordinate;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.util.event.EventDispatcher;
import org.scaffoldeditor.scaffold.util.event.EventListener;

import javafx.application.Platform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.Vec3d;

public class ScaffoldEditor {
	public static class UpdateSelectionEvent {
		public final Set<Entity> newSelection;

		public UpdateSelectionEvent(Set<Entity> newSelection) {
			this.newSelection = newSelection;
		}
	}

	public static final String CACHE_FILE_NAME = "editorcache.json";

	public final EditorOperationManager operationManager = new EditorOperationManager(this);

	private Level level;
	protected MinecraftClient client = MinecraftClient.getInstance();
	protected EditorServer server;
	protected ScaffoldUI ui;
	private Project project;
	protected File levelFile;
	private final Set<Entity> selectedEntities = new HashSet<>();
	private final EventDispatcher<UpdateSelectionEvent> updateSelectionDispatcher = new EventDispatcher<>();
	public String worldpath_cache;
	private boolean pauseCache = true;
	private JSONObject cache = new JSONObject();
	private RenderEntityManager renderEntityManager;

	public ScaffoldEditor() {

	}

	public static ScaffoldEditor getInstance() {
		return ScaffoldEditorMod.getInstance().getEditor();
	}

	/**
	 * Launch the scaffold editor. Should be called when Minecraft is NOT ingame.
	 */
	public void start(@Nullable Level level) {
		if (client.world != null) {
			System.out.print("Warning: Scaffold Editor can only be launched when not ingame");
			return;
		}
		pauseCache = client.options.pauseOnLostFocus;
		client.options.pauseOnLostFocus = false;

		client.startIntegratedServer("");
		server = (EditorServer) client.getServer();

		if (level != null) {
			setLevel(level);
		}

		ui = ScaffoldUI.open(this);

		RenderAttributeRegistry.initDefaults();
		renderEntityManager = new RenderEntityManager(this);
		ScaffoldEditorMod.getInstance().isInEditor = true;

	}

	/**
	 * Gracefully exit from the editor.
	 */
	public void exit() {
		if (level != null && level.hasUnsavedChanges()) {
			if (!ui.showUnsavedDialog())
				return;
		}

		client.execute(() -> {
			client.world.disconnect();
			client.disconnect();
			client.openScreen(null);
		});
	}

	/**
	 * Called when the server disconnects or any other time Scaffold has to exit due
	 * to reasons outside its control.
	 */
	public void forceExit() {
		ui.exit();
		ScaffoldEditorMod.getInstance().isInEditor = false;
		onClose();
	}

	protected void onClose() {
		client.options.pauseOnLostFocus = pauseCache;
		try {
			saveCache();
		} catch (IOException e) {
			LogManager.getLogger().error(e);
		}
		if (project != null)
			project.close();
		project = null;
		client.execute(() -> {
			// Reset framebuffer
			Window window = client.getWindow();
			int[] width = new int[1];
			int[] height = new int[1];
			GLFW.glfwGetFramebufferSize(window.getHandle(), width, height);
			LogManager.getLogger().info("Framebuffer size: " + width[0] + ", " + height[0]);
			window.setFramebufferWidth(width[0]);
			window.setFramebufferHeight(height[0]);

			client.onResolutionChanged();
		});
		renderEntityManager.clear();
	}

	public void setLevel(Level level) {
		if (level != null) {
			this.level = level;
			this.setProject(level.getProject());
			renderEntityManager.clear();

			level.onWorldUpdate(e -> {
				if (e.updatedSections.isEmpty()) {
					loadLevel(false);
				} else {
					EditorServerWorld world = server.getEditorWorld();
					for (SectionCoordinate c : e.updatedSections) {
						try {
							WorldInterface.loadScaffoldSection(
									level.getBlockWorld().getChunks().get(new ChunkCoordinate(c.x(), c.z())).sections[c.y()],
									world, c);
						} catch (NullPointerException ex) {
							world.clearSection(c);
						}
					}
				}
			});

			level.onUpdateEntityStack(() -> {
				ui.updateEntityList();
			});
			renderEntityManager.init();
			ui.updateEntityList();
			loadLevel(true);
			level.updateRenderEntities();

		}
		ui.reloadRecentFiles();
		try {
			saveCache();
		} catch (IOException e) {
			LogManager.getLogger().error(e);
		}
	}

	public Level getLevel() {
		return level;
	}

	/**
	 * Create a new level and open it. Only works if the project is not null.
	 */
	public void newLevel() {
		if (project == null) {
			throw new IllegalStateException("Project cannot be null");
		}
		;
		levelFile = null;
		Level level = new Level(getProject());
		setLevel(level);
	}

	protected void loadLevel(boolean compile) {
		if (level == null) {
			return;
		}

		project.execute(() -> {
			if (compile) {
				level.compileBlockWorld(false);
			} else {
				// World automatically loads on compile
				EditorServerWorld world = server.getEditorWorld();
				WorldInterface.loadScaffoldWorld(level.getBlockWorld(), world);
			}
		});

	}

	public EditorServer getServer() {
		return server;
	}

	public ScaffoldUI getUI() {
		return ui;
	}

	/**
	 * Get the set of selected entities.
	 * 
	 * @return Selected entities. Make sure to call <code>updateSelection()</code>
	 *         after modifying.
	 */
	public Set<Entity> getSelectedEntities() {
		return selectedEntities;
	}

	public void selectEntity(Entity entity) {
		selectedEntities.add(entity);
		updateSelection();
	}

	public void deselectEntity(Entity entity) {
		selectedEntities.remove(entity);
		updateSelection();
	}

	public void updateSelection() {
		updateSelectionDispatcher.fire(new UpdateSelectionEvent(selectedEntities));
	}

	public void onUpdateSelection(EventListener<UpdateSelectionEvent> listener) {
		updateSelectionDispatcher.addListener(listener);
	}

	public void removeOnUpdateSelection(EventListener<UpdateSelectionEvent> listener) {
		updateSelectionDispatcher.removeListener(listener);
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
		
		project.getThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				if (getUI() != null) {
					UIUtils.showError("Exception in level thread", e);
				}
			}
		});
	}

	public RenderEntityManager getRenderEntityManager() {
		return renderEntityManager;
	}

	/**
	 * Open a project, or create one if it doesn't exist.
	 * 
	 * @param folder Project folder.
	 * @return Opened project.
	 * @throws IOException If an IO exception occurs while creating the project.
	 */
	public Project openProject(Path folder) throws IOException {
		LogManager.getLogger().info("Opening project: " + folder.toString());
		if (!folder.toFile().isDirectory()) {
			LogManager.getLogger().error(folder.toString() + " is not a directory!");
			return null;
		}

		if (folder.resolve("gameInfo.json").toFile().isFile()) {
			this.project = Project.loadProject(folder.toString());
		} else {
			this.project = Project.init(folder.toString(), folder.getFileName().toString());
		}
		loadCache();
		ui.reloadRecentFiles();
		return project;
	}

	/**
	 * Open a level file.
	 * 
	 * @param file File to load.
	 * @return Loaded level.
	 */
	public CompletableFuture<Level> openLevelFile(File file) {
		if (level != null && level.hasUnsavedChanges()) {
			if (!ui.showUnsavedDialog())
				return null;
		}
		File oldLevel = levelFile;
		levelFile = file;
		CompletableFuture<Level> future = new CompletableFuture<Level>();
		project.execute(() -> {
			Level level;
			try {
				level = Level.loadFile(project, file);
			} catch (IOException e) {
				Platform.runLater(() -> {
					UIUtils.showError("Error loading level", e);
				});
				levelFile = oldLevel;
				return;
			}
			setLevel(level);
			level.setName(FilenameUtils.getBaseName(file.getName()));
			future.complete(level);

			JSONArray cameraPos = getLevelCache().optJSONArray("cameraPos");
			if (cameraPos != null) {
				double x = cameraPos.getDouble(0);
				double y = cameraPos.getDouble(1);
				double z = cameraPos.getDouble(2);

				getServer().execute(() -> {
					getServer().teleportPlayers(x, y, z);
				});
			}
		});
		ui.reloadRecentFiles();
		{
			List<Object> recentLevels = cache.has("recentLevels") ? cache.getJSONArray("recentLevels").toList()
					: new ArrayList<>();
			String filename = project.assetManager().relativise(file);
			if (recentLevels.contains(filename)) {
				recentLevels.remove(filename);
			}
			recentLevels.add(0, filename);
			cache.put("recentLevels", recentLevels);
		}

		return future;
	}

	public void save() {
		if (level != null) {
			if (levelFile == null) {
				throw new IllegalStateException(
						"Attempted to save a level that doesn't have a corrisponding file. Use saveAs() instead.");
			}

			project.execute(() -> {
				try {
					level.saveFile(levelFile);
				} catch (IOException e) {
					UIUtils.showError("Error saving level", e);
				}
			});
		}
		try {
			saveCache();
		} catch (IOException e) {
			LogManager.getLogger().error("Error saving cache ", e);
		}
	}

	public void saveAs(File newFile) {
		levelFile = newFile;
		level.setName(FilenameUtils.getBaseName(newFile.getName()));
		save();
		ui.reloadRecentFiles();
		{
			List<Object> recentLevels = cache.has("recentLevels") ? cache.getJSONArray("recentLevels").toList()
					: new ArrayList<>();
			String filename = project.assetManager().relativise(newFile);
			if (recentLevels.contains(filename)) {
				recentLevels.remove(filename);
			}
			recentLevels.add(0, filename);
			cache.put("recentLevels", recentLevels);
		}
	}

	/**
	 * Get the file the level was last saved to.
	 * 
	 * @return The file, or {@code null} if it was never saved.
	 */
	public File getLevelFile() {
		return levelFile;
	}

	/**
	 * Get the primary JSON object used for caching values used by the high-level
	 * editor (recently opened, etc)
	 * 
	 * @return JSONObject representing the file at
	 *         <code>.scaffold/editorcache.json</code>
	 */
	public JSONObject getCache() {
		return cache;
	}

	/**
	 * Get the JSON object used for caching values specific to this level.
	 * 
	 * @return A child object of {@link #getCache()}.
	 */
	public JSONObject getLevelCache() {
		if (level == null)
			return null;

		if (!cache.has("levels")) {
			cache.put("levels", new JSONObject());
		}

		JSONObject levels = cache.getJSONObject("levels");

		if (!levels.has(level.getName())) {
			levels.put(level.getName(), new JSONObject());
		}
		return levels.getJSONObject(level.getName());
	}

	public void loadCache() {
		if (project != null) {
			File file = project.getCacheFolder().resolve(CACHE_FILE_NAME).toFile();
			if (file.isFile()) {
				try {
					cache = new JSONObject(new JSONTokener(new FileInputStream(file)));
				} catch (JSONException | FileNotFoundException e) {
					LogManager.getLogger().error(e);
					cache = new JSONObject();
				}
			} else {
				cache = new JSONObject();
			}
		}
	}

	public void saveCache() throws IOException {
		if (project != null) {
			if (level != null) {
				JSONObject levelCache = getLevelCache();
				Vec3d pos = client.player.getPos();
				levelCache.put("cameraPos", List.of(pos.x, pos.y, pos.z));
			}

			File file = project.getCacheFolder().resolve(CACHE_FILE_NAME).toFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(getCache().toString());
			writer.close();
		}
	}

	/**
	 * Close the editor and open a Minecraft world ingame.
	 * 
	 * @param worldName World to load.
	 */
	public void openWorld(String worldName) {
		LogManager.getLogger().info("Shutting down editor and opening world: " + worldName);
		client.execute(() -> {
			client.world.disconnect();
			client.disconnect();
			client.startIntegratedServer(worldName);
		});
	}
}

package org.scaffoldeditor.editormc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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
import org.scaffoldeditor.editormc.engine.EditorServer;
import org.scaffoldeditor.editormc.engine.ScaffoldEditorMod;
import org.scaffoldeditor.editormc.engine.world.EditorServerWorld;
import org.scaffoldeditor.editormc.render_entities.RenderEntityManager;
import org.scaffoldeditor.editormc.scaffold_interface.WorldInterface;
import org.scaffoldeditor.editormc.ui.ScaffoldUI;
import org.scaffoldeditor.editormc.ui.attribute_types.RenderAttributeRegistry;
import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.operation.DeleteEntityOperation;
import org.scaffoldeditor.scaffold.operation.PasteEntitiesOperation;
import org.scaffoldeditor.scaffold.util.ClipboardManager;
import org.scaffoldeditor.scaffold.util.event.EventDispatcher;
import org.scaffoldeditor.scaffold.util.event.EventListener;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class ScaffoldEditor {
	public static class UpdateSelectionEvent {
		public final Set<Entity> newSelection;
		public UpdateSelectionEvent(Set<Entity> newSelection) {
			this.newSelection = newSelection;
		}
	}
	
	public static final String CACHE_FILE_NAME = "editorcache.json";
	
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
	 * Launch the scaffold editor.
	 * Should be called when Minecraft is NOT ingame.
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
		
		ui = ScaffoldUI.open();
		ui.setEditor(this);
		
		RenderAttributeRegistry.initDefaults();
		renderEntityManager = new RenderEntityManager(this);
		ScaffoldEditorMod.getInstance().isInEditor = true;
		
	}
	
	/**
	 * Gracefully exit from the editor.
	 */
	public void exit() {
		client.getServer().stop(true);
	}
	
	/**
	 * Called when the server disconnects or any other time Scaffold has
	 * to exit due to reasons outside its control.
	 */
	public void forceExit() {
		ui.exit();
		ScaffoldEditorMod.getInstance().isInEditor = false;
		onClose();
	}
	
	protected void onClose() {
		client.onResolutionChanged();
		client.options.pauseOnLostFocus = pauseCache;
		try {
			saveCache();
		} catch (IOException e) {
			e.printStackTrace();
		}
		project.close();
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
									level.getBlockWorld().getChunks().get(new ChunkCoordinate(c.x, c.z)).sections[c.y], world, c);
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
			e.printStackTrace();
		}
	}
	
	public Level getLevel() {
		return level;
	}
	
	/**
	 * Create a new level and open it. Only works if the project is not null.
	 * @param levelFile File to save the level as.
	 */
	public void newLevel(File levelFile) {
		if (project == null) return;
		Level level = new Level(getProject());
		this.levelFile = levelFile;
		setLevel(level);
		project.getLevelService().execute(() -> level.saveFile(levelFile));
	}
	
	protected void loadLevel(boolean compile) {
		if (level == null) {
			return;
		}
		
		project.getLevelService().execute(() -> {
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
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	/**
	 * Copy the current selection to the clipboard.
	 */
	public void copySelection() {
		ClipboardManager.getInstance().copyEntities(selectedEntities);
	}
	
	/**
	 * Cut the current selection to the clipboard
	 */
	public void cutSelection() {
		copySelection();
		level.getOperationManager().execute(new DeleteEntityOperation(level, selectedEntities));
	}
	
	/**
	 * Paste the current clipboard into the level.
	 */
	public void paste() {
		level.getOperationManager().execute(new PasteEntitiesOperation(getLevel()));
	}
	
	/**
	 * Open a project, or create one if it doesn't exist.
	 * @param folder Project folder.
	 * @return Opened project.
	 */
	public Project openProject(Path folder) {
		LogManager.getLogger().info("Opening project: "+folder.toString());
		if (!folder.toFile().isDirectory()) {
			LogManager.getLogger().error(folder.toString()+" is not a directory!");
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
	 * @param file File to load.
	 * @return Loaded level.
	 */
	public CompletableFuture<Level> openLevelFile(File file) {
		CompletableFuture<Level> future = new CompletableFuture<Level>();
		project.getLevelService().execute(() -> {
			Level level = Level.loadFile(project, file);
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
		levelFile = file;
		ui.reloadRecentFiles();
		{
			List<Object> recentLevels = cache.has("recentLevels") ? cache.getJSONArray("recentLevels").toList() : new ArrayList<>();
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
		if (level != null && levelFile != null) {
			project.getLevelService().execute(() -> level.saveFile(levelFile));
		}
		try {
			saveCache();
		} catch (IOException e) {
			LogManager.getLogger().error("Error saving cache ", e);
		}
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
	 * @return A child object of {@link #getCache()}.
	 */
	public JSONObject getLevelCache() {
		if (level == null) return null;
		
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
					e.printStackTrace();
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
	
//	public static ScaffoldEditor startWithTestProject() {
//		@SuppressWarnings("resource")
//		Project project = Project.init(MinecraftClient.getInstance().runDirectory.toPath().resolve("testProject").toString(), "testProject");
//		Level level = new Level(project, "Test Level");
//		level.setName("testLevel");
//		
//		WorldStatic prop = (WorldStatic) level.newEntity("world_static", "propStatic", new Vector(5, 5, 5));
//		prop.setAttribute("model", new StringAttribute("schematics/smiley.nbt"));
//		
//		level.saveFile(project.getProjectFolder().resolve("maps/testlevel.mclevel").toFile());
//		ScaffoldEditor editor = new ScaffoldEditor();
//		editor.start(level);
//		return editor;
//	}
}

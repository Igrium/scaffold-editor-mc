package org.scaffoldeditor.editormc;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.Nullable;
import org.scaffoldeditor.editormc.engine.EditorServer;
import org.scaffoldeditor.editormc.engine.EditorServerWorld;
import org.scaffoldeditor.editormc.engine.ScaffoldEditorMod;
import org.scaffoldeditor.editormc.scaffold_interface.WorldInterface;
import org.scaffoldeditor.editormc.ui.ScaffoldUI;
import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;

import net.minecraft.client.MinecraftClient;

public class ScaffoldEditor {
	private Level level;
	protected MinecraftClient client = MinecraftClient.getInstance();
	protected EditorServer server;
	protected ScaffoldUI ui;
	private Project project;
	protected File levelFile;
	private final Set<Entity> selectedEntities = new HashSet<>(); 
	private static ScaffoldEditor instance;
	
	private boolean pauseCache = true;
	
	public ScaffoldEditor() {

	}
	
	public static ScaffoldEditor getInstance() {
		return instance;
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
		instance = this;
		pauseCache = client.options.pauseOnLostFocus;
		client.options.pauseOnLostFocus = false;
		
		client.startIntegratedServer("");
		server = (EditorServer) client.getServer();
		
		if (level != null) {
			setLevel(level);
		}
		
		ui = ScaffoldUI.open();
		ui.setEditor(this);
		
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
		instance = null;
	}

	public void setLevel(Level level) {
		if (level != null) {
			this.level = level;
			this.setProject(level.getProject());
			
			level.onWorldUpdate(e -> {
				if (e.updatedSections.isEmpty()) {
					loadLevel(false);
				} else {
					EditorServerWorld world = server.getEditorWorld();
					for (SectionCoordinate c : e.updatedSections) {
						WorldInterface.loadScaffoldSection(
								level.getBlockWorld().getChunks().get(new ChunkCoordinate(c)).sections[c.y], world, c);
					}
				}
			});
			
			level.onUpdateEntityStack(() -> {
				ui.updateEntityList();
			});
			ui.updateEntityList();		
			loadLevel(true);		
		}
	}
	
	public Level getLevel() {
		return level;
	}
	
	protected void loadLevel(boolean compile) {
		if (level == null) {
			return;
		}
		
		getServer().execute(() -> {
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
		System.out.println("Selection: "+selectedEntities.toString());
	}
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	/**
	 * Open a project, or create one if it doesn't exist.
	 * @param folder Project folder.
	 * @return Opened project.
	 */
	public Project openProject(Path folder) {
		System.out.println("Opening project: "+folder.toString());
		if (!folder.toFile().isDirectory()) {
			System.err.println(folder.toString()+" is not a directory!");
			return null;
		}
		
		if (folder.resolve("gameInfo.json").toFile().isFile()) {
			this.project = Project.loadProject(folder.toString());
		} else {
			this.project = Project.init(folder.toString(), folder.getFileName().toString());
		}
		return project;
	}
	
	/**
	 * Open a level file.
	 * @param file File to load.
	 * @return Loaded level.
	 */
	public Level openLevelFile(File file) {
		Level level = Level.loadFile(project, file);
		levelFile = file;
		setLevel(level);
		return level;
	}
	
	public void save() {
		if (level != null && levelFile != null) {
			level.saveFile(levelFile);
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

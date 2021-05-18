package org.scaffoldeditor.editormc;

import java.io.File;
import java.nio.file.Path;

import org.jetbrains.annotations.Nullable;
import org.scaffoldeditor.editormc.engine.EditorServer;
import org.scaffoldeditor.editormc.engine.EditorServerWorld;
import org.scaffoldeditor.editormc.engine.ScaffoldEditorMod;
import org.scaffoldeditor.editormc.scaffold_interface.WorldInterface;
import org.scaffoldeditor.editormc.ui.ScaffoldUI;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.world.PropStatic;
import org.scaffoldeditor.scaffold.math.Vector;

import net.minecraft.client.MinecraftClient;

public class ScaffoldEditor {
	private Level level;
	protected MinecraftClient client = MinecraftClient.getInstance();
	protected EditorServer server;
	protected ScaffoldUI ui;
	private Project project;
	
	private boolean pauseCache = true;
	
	public ScaffoldEditor() {

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
			this.level = level;
			this.setProject(level.getProject());
			loadLevel();
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
	}
	
	public void setLevel(Level level) {
		if (level != null) {
			this.level = level;
			this.setProject(level.getProject());
			loadLevel();
		}
	}
	
	public Level getLevel() {
		return level;
	}
	
	protected void loadLevel() {
		if (level == null) {
			return;
		}
		
		EditorServerWorld world = server.getEditorWorld();
		world.clear();
		level.compileBlockWorld(false);
		WorldInterface.loadScaffoldWorld(level.getBlockWorld(), world);
	}
	
	public static ScaffoldEditor startWithTestProject() {
		@SuppressWarnings("resource")
		Project project = Project.init(MinecraftClient.getInstance().runDirectory.toPath().resolve("testProject").toString(), "testProject");
		Level level = new Level(project, "Test Level");
		level.setName("testLevel");
		
		PropStatic prop = (PropStatic) level.newEntity(PropStatic.class, "propStatic", new Vector(5, 5, 5));
		prop.setAttribute("model", "schematics/smiley.nbt");
		
		level.saveFile(project.getProjectFolder().resolve("maps/testlevel.mclevel").toFile());
		ScaffoldEditor editor = new ScaffoldEditor();
		editor.start(level);
		return editor;
	}
	
	public EditorServer getServer() {
		return server;
	}
	
	public ScaffoldUI getUI() {
		return ui;
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
		Level level = Level.loadFile(project, file.toPath());
		setLevel(level);
		return level;
	}
}

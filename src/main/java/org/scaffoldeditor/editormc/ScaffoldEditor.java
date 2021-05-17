package org.scaffoldeditor.editormc;

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
	protected Level level;
	protected MinecraftClient client = MinecraftClient.getInstance();
	protected EditorServer server;
	protected ScaffoldUI ui;
	
	private boolean pauseCache = true;
	
	public ScaffoldEditor() {

	}
	
	/**
	 * Launch the scaffold editor.
	 * Should be called when Minecraft is NOT ingame.
	 */
	public void start(Level level) {
		if (client.world != null) {
			System.out.print("Warning: Scaffold Editor can only be launched when not ingame");
			return;
		}
		pauseCache = client.options.pauseOnLostFocus;
		client.options.pauseOnLostFocus = false;
		
		client.startIntegratedServer("");
		server = (EditorServer) client.getServer();
		
		ui = ScaffoldUI.open();
		ui.setEditor(this);
		
		ScaffoldEditorMod.getInstance().isInEditor = true;
		
		if (level != null) {
			this.level = level;
		}
		loadLevel();
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
}

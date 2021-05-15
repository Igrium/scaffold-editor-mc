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

import javafx.application.Application;
import net.minecraft.client.MinecraftClient;

public class ScaffoldEditor {
	protected Level level;
	protected MinecraftClient client = MinecraftClient.getInstance();
	protected EditorServer server;
	protected ScaffoldUI ui;
	
	public ScaffoldEditor(Project project, Level level) {
		ScaffoldEditorMod.getInstance();
		this.level = level;
	}
	
	/**
	 * Launch the scaffold editor.
	 * Should be called when Minecraft is NOT ingame.
	 */
	public void start() {
		if (client.world != null) {
			System.out.print("Warning: Scaffold Editor can only be launched when not ingame");
			return;
		}
		client.startIntegratedServer("");
		server = (EditorServer) client.getServer();
		
		new Thread() {
			@Override
			public void run() {
				Application.launch(ScaffoldUI.class);
			}
		}.start();
		ui = ScaffoldUI.waitForinit();
		
		loadLevel();
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
		ScaffoldEditor editor = new ScaffoldEditor(project, level);
		editor.start();
		return editor;
	}
	
	public EditorServer getServer() {
		return server;
	}
}

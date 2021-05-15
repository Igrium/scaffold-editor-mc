package org.scaffoldeditor.editormc;

import org.scaffoldeditor.editormc.engine.EditorServer;
import org.scaffoldeditor.editormc.engine.EditorServerWorld;
import org.scaffoldeditor.editormc.engine.ScaffoldEditorMod;
import org.scaffoldeditor.editormc.scaffold_interface.WorldInterface;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.world.PropStatic;
import org.scaffoldeditor.scaffold.math.Vector;

import net.minecraft.client.MinecraftClient;

public class ScaffoldEditor {
	protected Level level;
	private ScaffoldEditorMod mod;
	
	public ScaffoldEditor(Project project, Level level) {
		this.mod = ScaffoldEditorMod.getInstance();
		this.level = level;
	}
	
	/**
	 * Launch the scaffold editor.
	 * Should be called when Minecraft is NOT ingame.
	 */
	public void start() {
		
	}
	
	protected void loadLevel() {
		if (level == null) {
			return;
		}
		
		EditorServer server = mod.getServer();
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
		return new ScaffoldEditor(project, level);
	}
}

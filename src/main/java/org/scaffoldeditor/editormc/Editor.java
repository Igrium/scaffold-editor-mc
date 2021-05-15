package org.scaffoldeditor.editormc;

import org.scaffoldeditor.editormc.engine.EditorServer;
import org.scaffoldeditor.editormc.engine.EditorServerWorld;
import org.scaffoldeditor.editormc.engine.ScaffoldEditor;
import org.scaffoldeditor.editormc.scaffold_interface.WorldInterface;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.world.PropStatic;
import org.scaffoldeditor.scaffold.math.Vector;

import net.minecraft.client.MinecraftClient;

public class Editor {
	protected Level level;
	private ScaffoldEditor mod;
	
	public Editor(Project project, Level level) {
		this.mod = ScaffoldEditor.getInstance();
		this.level = level;
		loadLevel();
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
	
	public static Editor startWithTestProject() {
		@SuppressWarnings("resource")
		Project project = Project.init(MinecraftClient.getInstance().runDirectory.toPath().resolve("testProject").toString(), "testProject");
		Level level = new Level(project, "Test Level");
		level.setName("testLevel");
		
		PropStatic prop = (PropStatic) level.newEntity(PropStatic.class, "propStatic", new Vector(5, 5, 5));
		prop.setAttribute("model", "schematics/smiley.nbt");
		
		
		level.saveFile(project.getProjectFolder().resolve("maps/testlevel.mclevel").toFile());
		return new Editor(project, level);
	}
}

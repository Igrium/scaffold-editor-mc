package org.scaffoldeditor.editormc.render_entities;

import java.util.HashSet;
import java.util.Set;

import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.engine.EditorServer;
import org.scaffoldeditor.editormc.engine.EditorServerWorld;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.WorldUpdates.UpdateRenderEntitiesEvent;
import org.scaffoldeditor.scaffold.level.render.MCRenderEntity;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;

/**
 * Handles the Minecraft representation of Scaffold entities.
 * Maintains lots of static variables, so it needs to be disposed of
 * properly for the sake of garbage collection.
 * @author Igrium
 */
public class RenderEntityManager {
	private Level level;
	private ScaffoldEditor editor;
	private EditorServer server;
	private EditorServerWorld world;
	private MCEntityManager mcEntityManager;
	
	public RenderEntityManager(ScaffoldEditor editor) {
		this.editor = editor;
	}
	
	
	public void init() {
		this.level = editor.getLevel();
		this.server = editor.getServer();
		this.world = server.getEditorWorld();
		this.mcEntityManager = new MCEntityManager(this);
		
		level.onUpdateRenderEntities(event -> {
			server.execute(() -> {				
				if (event.renderEntities == null || event.renderEntities.isEmpty()) {
					mcEntityManager.clear(event.subject);
					return;
				}

				Set<RenderEntity> mcRenderEntities = new HashSet<>();
				for (RenderEntity ent : event.renderEntities) {
					if (ent instanceof MCRenderEntity) mcRenderEntities.add(ent);
				}

				// Make a new UpdateRenderEntitiesEvent with only the MC render entities.
				if (!mcRenderEntities.isEmpty()) {
					mcEntityManager.handleUpdateRenderEntities(new UpdateRenderEntitiesEvent(mcRenderEntities, event.subject));
				}
			});
		});
	}

	public Level getLevel() {
		return level;
	}

	public ScaffoldEditor getEditor() {
		return editor;
	}

	public EditorServerWorld getWorld() {
		return world;
	}


	public EditorServer getServer() {
		return server;
	}
}

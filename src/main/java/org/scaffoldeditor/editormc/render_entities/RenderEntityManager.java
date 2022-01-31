package org.scaffoldeditor.editormc.render_entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.engine.EditorServer;
import org.scaffoldeditor.editormc.engine.world.EditorServerWorld;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.level.render.BillboardRenderEntity;
import org.scaffoldeditor.scaffold.level.render.BlockRenderEntity;
import org.scaffoldeditor.scaffold.level.render.BrushRenderEntity;
import org.scaffoldeditor.scaffold.level.render.LineRenderEntity;
import org.scaffoldeditor.scaffold.level.render.MCRenderEntity;
import org.scaffoldeditor.scaffold.level.render.ModelRenderEntity;
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
	
	public RenderEntityManager(ScaffoldEditor editor) {
		this.editor = editor;
		
		editor.onUpdateSelection(event -> {
			editor.getLevel().updateRenderEntities();
		});
	}
	
	/**
	 * Represents a single Scaffold entity.
	 */
	public class EntityEntry {
		public Map<String, EditorRenderEntity> entities = new HashMap<>();
		
		public void handleUpdate(Set<RenderEntity> renderEntities) {
			Set<String> newEntities = new HashSet<>();
			for (RenderEntity ent : renderEntities) {
				newEntities.add(ent.identifier());
			}
			
			for (String name : entities.keySet()) {
				if (!newEntities.contains(name)) {
					entities.get(name).despawn();
					entities.remove(name); // Allow the garbege collecter to collect the Minecraft entity(s).
				}
			}
			
			for (RenderEntity entity : renderEntities) {				
				if (entities.containsKey(entity.identifier())) {
					entities.get(entity.identifier()).update(entity);
				} else {
					entities.put(entity.identifier(), spawnEditorRenderEntity(entity));
				}
			}
		}
		
		public void clear() {
			for (EditorRenderEntity ent : entities.values()) {
				ent.despawn();
			}
			entities.clear();
		}
	}
	
	// ADD RENDER ENTITIES HERE
	protected EditorRenderEntity spawnEditorRenderEntity(RenderEntity in) {
		EditorRenderEntity ent;
		if (in instanceof MCRenderEntity) ent = new MCEditorEntity(world, editor);
		else if (in instanceof ModelRenderEntity) ent = new ModelEditorEntity(world, editor);
		else if (in instanceof BillboardRenderEntity) ent = new BillboardEditorEntity(world, editor);
		else if (in instanceof BrushRenderEntity) ent = new BrushEditorEntity(world, editor);
		else if (in instanceof BlockRenderEntity) ent = new BlockEditorEntity();
		else if (in instanceof LineRenderEntity) ent = new LineEditorEntity();
		else {
			throw new IllegalArgumentException(
					"Render entity: " + in + " is not a subclass of any known render entity classes.");
		}
		ent.spawn(in);
		return ent;
	}
	
	private Map<Entity, EntityEntry> renderEntities = new HashMap<>();
	
	public void init() {
		this.level = editor.getLevel();
		this.server = editor.getServer();
		this.world = server.getEditorWorld();
		
		level.onUpdateRenderEntities(event -> {
			EntityEntry entry = renderEntities.get(event.subject);
			if (entry != null && event.renderEntities.isEmpty()) {
				entry.clear();
				renderEntities.remove(event.subject);
			} else if (!event.renderEntities.isEmpty()) {
				if (entry == null) {
					entry = new EntityEntry();
					renderEntities.put(event.subject, entry);
				}
				entry.handleUpdate(event.renderEntities);
			}
		});
	}
	
	/**
	 * Determine which Scaffold entity is responsible for rendering a Minecraft
	 * entity.
	 * 
	 * @param entity The Minecraft entity.
	 * @return Owning Scaffold entity, or <code>null</code> if this Minecraft entity
	 *         wasn't spawned by a Scaffold entity.
	 */
	public Entity findOwner(net.minecraft.entity.Entity entity) {
		for (Entity scaffold : renderEntities.keySet()) {
			for (EditorRenderEntity render : renderEntities.get(scaffold).entities.values()) {
				if (render.ownsEntity(entity)) return scaffold;
			}
		}
		return null;
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
	
	public void clear() {
		for (EntityEntry entry : renderEntities.values()) {
			entry.clear();
		}
		renderEntities.clear();
	}
}

package org.scaffoldeditor.editormc.render_entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.scaffoldeditor.editormc.scaffold_interface.NBTConverter;
import org.scaffoldeditor.nbt.math.Vector3d;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.WorldUpdates.UpdateRenderEntitiesEvent;
import org.scaffoldeditor.scaffold.level.render.MCRenderEntity;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;
import org.scaffoldeditor.scaffold.logic.MCEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;

/**
 * Manages the Minecraft entity representations of Scaffold entities.
 * @author Igrium
 */
public class MCEntityManager {
	private RenderEntityManager parent;
	
	private class EntityEntry {
		public Map<String, Entity> entities = new HashMap<>();
		
		public void handleUpdate(Set<RenderEntity> renderEntities) {
			Set<String> newEntities = new HashSet<>();
			for (RenderEntity ent : renderEntities) {
				newEntities.add(ent.identifier());
			}
			
			for (String name : entities.keySet()) {
				if (!newEntities.contains(name)) {
					despawn(name);
				}
			}
			System.out.println();
			for (RenderEntity entity : renderEntities) {				
				MCRenderEntity ent = (MCRenderEntity) entity;
				if (entities.containsKey(ent.identifier())) {
					update(ent.getMcEntity(), ent.identifier(), ent.getPosition().toDouble(), ent.getRotation());
				} else {
					spawn(ent.getMcEntity(), ent.identifier(), ent.getPosition().toDouble(), ent.getRotation());
				}
			}
		}
		
		public void remove() {
			for (String name : entities.keySet()) {
				despawn(name);
			}
		}
		
		private void despawn(String name) {
			Entity entity = entities.get(name);
			if (entity != null) {
				entity.remove();
				entities.remove(name);
			}
		}
		
		private void spawn(MCEntity ent, String name, Vector3d pos, Vector3f rot) {
			Optional<EntityType<?>> type = EntityType.get(ent.getID());
			if (type.isPresent()) {
				Entity entity = type.get().create(parent.getWorld());
				entity.updatePositionAndAngles(pos.x, pos.y, pos.z, rot.x, rot.y);
				entities.put(name, entity);
				parent.getWorld().spawnEntity(entity);
				update(ent, name, pos, rot);
			} else {
				System.err.println("Unknown entity type: "+ent.getID());
			}
			System.out.println(entities);
		}
		
		private void update(MCEntity ent, String name, Vector3d pos, Vector3f rot) {
			Entity entity = entities.get(name);
			if (entity != null) {
				if (EntityType.getId(entity.getType()).toString().equals(ent.getID())) {
					entity.fromTag(NBTConverter.scaffoldCompoundToMinecraft(ent.getNBT()));
					
					CompoundTag newNBT = new CompoundTag();
					newNBT.putBoolean("NoAI", true);
					newNBT.putBoolean("NoGravity", true);
					newNBT.putBoolean("Silent", true);
					entity.fromTag(newNBT);
					
					entity.updatePositionAndAngles(pos.x, pos.y, pos.z, rot.x, rot.y);
					
				} else {
					despawn(name);
					spawn(ent, name, pos, rot);
				}
			}
		}
	}
	
	/**
	 * Keep track of all the render entities relative to their scaffold entity.
	 */
	private Map<org.scaffoldeditor.scaffold.level.entity.Entity, EntityEntry> entities = new HashMap<>();
	
	public MCEntityManager(RenderEntityManager parent) {
		this.parent = parent;
	}

	public RenderEntityManager getParent() {
		return parent;
	}
	
	public void handleUpdateRenderEntities(UpdateRenderEntitiesEvent e) {

		// It's important that we remove entity references so they can be garbage collected.
		if (e.renderEntities == null || e.renderEntities.isEmpty()) {
			System.out.println("Removing");
			if (entities.containsKey(e.subject)) {
				entities.get(e.subject).remove();
				entities.remove(e.subject);
			}
		} else {
			EntityEntry entry = entities.get(e.subject);
			if (entry == null) {
				entry = new EntityEntry();
			}
			entry.handleUpdate(e.renderEntities);
			entities.put(e.subject, entry);
		}
	}
	
	public void clear(org.scaffoldeditor.scaffold.level.entity.Entity subject) {
		// It's important that we remove entity references so they can be garbage collected.
		System.out.println("Removing");
		if (entities.containsKey(subject)) {
			entities.get(subject).remove();
			entities.remove(subject);
		} else {
			System.err.println("Unable to remove render entities for "+subject+" because they do not exist.");
			
		}
	}
}

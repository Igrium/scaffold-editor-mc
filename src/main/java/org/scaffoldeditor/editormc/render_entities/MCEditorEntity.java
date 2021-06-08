package org.scaffoldeditor.editormc.render_entities;

import java.util.Optional;

import org.scaffoldeditor.editormc.scaffold_interface.NBTConverter;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.render.MCRenderEntity;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;
import org.scaffoldeditor.scaffold.logic.MCEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

public class MCEditorEntity implements EditorRenderEntity {
	
	private Entity mcEntity;
	private World world;
	
	public MCEditorEntity(World world) {
		this.world = world;
	}

	@Override
	public void spawn(RenderEntity entity) {
		if (!(entity instanceof MCRenderEntity)) {
			throw new IllegalArgumentException("RenderEntity not an instance of MCRenderEntity!");
		}
		MCEntity ent = ((MCRenderEntity) entity).getMcEntity();
		Optional<EntityType<?>> type = EntityType.get(ent.getID());
		if (type.isPresent()) {
			Vector3f pos = entity.getPosition();
			Vector3f rot = entity.getRotation();
			
			mcEntity = type.get().create(world);
			mcEntity.updatePositionAndAngles(pos.x, pos.y, pos.z, rot.x, rot.y);
			world.spawnEntity(mcEntity);
			update(entity);
		} else {
			throw new IllegalArgumentException("Unknown entity class: "+ent.getID());
		}
	}

	@Override
	public void update(RenderEntity entity) {
		if (!(entity instanceof MCRenderEntity)) {
			throw new IllegalArgumentException("RenderEntity not an instance of MCRenderEntity!");
		}
		MCEntity ent = ((MCRenderEntity) entity).getMcEntity();
		if (mcEntity == null) return;
		
		if (EntityType.getId(mcEntity.getType()).toString().equals(ent.getID())) {
			mcEntity.fromTag(NBTConverter.scaffoldCompoundToMinecraft(ent.getNBT()));
			
			Vector3f pos = entity.getPosition();
			Vector3f rot = entity.getRotation();
			
			CompoundTag newNBT = new CompoundTag();
			newNBT.putBoolean("NoAI", true);
			newNBT.putBoolean("NoGravity", true);
			newNBT.putBoolean("Silent", true);
			newNBT.putBoolean("Invulnerable", true);
			mcEntity.fromTag(newNBT);
			
			mcEntity.updatePositionAndAngles(pos.x, pos.y, pos.z, rot.x, rot.y);
			
		} else {
			despawn();
			spawn(entity);
		}
		
	}

	@Override
	public void despawn() {
		if (mcEntity != null) mcEntity.remove();
	}

}

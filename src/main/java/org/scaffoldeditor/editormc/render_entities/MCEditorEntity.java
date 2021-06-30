package org.scaffoldeditor.editormc.render_entities;

import java.util.Optional;

import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.scaffold_interface.NBTConverter;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.nbt.util.MCEntity;
import org.scaffoldeditor.scaffold.level.render.MCRenderEntity;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class MCEditorEntity implements EditorRenderEntity {
	
	private Entity mcEntity;
	private World world;
	private ScaffoldEditor editor;
	
	public MCEditorEntity(World world, ScaffoldEditor editor) {
		this.world = world;
		this.editor = editor;
	}

	@Override
	public void spawn(RenderEntity entity) {
		if (!(entity instanceof MCRenderEntity)) {
			throw new IllegalArgumentException("RenderEntity not an instance of MCRenderEntity!");
		}
		if (!world.getServer().isOnThread()) {
			world.getServer().execute(() -> spawn(entity));
			return;
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
		if (!world.getServer().isOnThread()) {
			world.getServer().execute(() -> update(entity));
			return;
		}
		MCEntity ent = ((MCRenderEntity) entity).getMcEntity();
		if (mcEntity == null) return;
		
		if (EntityType.getId(mcEntity.getType()).toString().equals(ent.getID())) {		
			Vector3f pos = entity.getPosition();
			Vector3f rot = entity.getRotation();
			
			NbtCompound newNBT = NBTConverter.scaffoldCompoundToMinecraft(ent.getNBT());
			newNBT.putBoolean("NoAI", true);
			newNBT.putBoolean("NoGravity", true);
			newNBT.putBoolean("Silent", true);
			newNBT.putBoolean("Invulnerable", true);
			
			mcEntity.readNbt(newNBT);
			
			mcEntity.updatePositionAndAngles(pos.x, pos.y, pos.z, rot.x, rot.y);
			mcEntity.setGlowing(editor.getSelectedEntities().contains(entity.getEntity()));
			
		} else {
			despawn();
			spawn(entity);
		}
		
	}

	@Override
	public void despawn() {
		if (!world.getServer().isOnThread()) {
			world.getServer().execute(() -> despawn());
			return;
		}
		if (mcEntity != null) mcEntity.remove(RemovalReason.DISCARDED);
	}
	
	@Override
	public boolean ownsEntity(Entity entity) {
		return mcEntity == entity;
	}

}

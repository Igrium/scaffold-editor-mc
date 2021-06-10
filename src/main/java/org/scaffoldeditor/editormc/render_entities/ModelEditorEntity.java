package org.scaffoldeditor.editormc.render_entities;

import org.scaffoldeditor.editormc.engine.entity.ModelEntity;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.render.ModelRenderEntity;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.world.World;

public class ModelEditorEntity implements EditorRenderEntity {
	
	private ModelEntity entity;
	private World world;
	private final MinecraftClient client = MinecraftClient.getInstance();
	
	public ModelEditorEntity(World world) {
		this.world = world;
	}

	@Override
	public void spawn(RenderEntity in) {
		if (!(in instanceof ModelRenderEntity)) {
			throw new IllegalArgumentException("RenderEntity not an instance of ModelRenderEntity!");
		}
		client.execute(() -> {
			entity = new ModelEntity(ModelEntity.TYPE, world);
			Vector3f pos = in.getPosition();
			entity.updatePosition(pos.x, pos.y, pos.z);
			world.spawnEntity(entity);
			update(in);
		});
	}

	@Override
	public void update(RenderEntity in) {
		if (!(in instanceof ModelRenderEntity)) {
			throw new IllegalArgumentException("RenderEntity not an instance of ModelRenderEntity!");
		}
		client.execute(() -> {
			if (entity == null) return;
			ModelRenderEntity ent = (ModelRenderEntity) in;
			entity.setModel(new ModelIdentifier(ent.getModel()));
			
			Vector3f pos = ent.getPosition();
			entity.updatePosition(pos.x, pos.y, pos.z);
		});
	}

	@Override
	public void despawn() {
		client.execute(() -> {
			entity.remove(RemovalReason.DISCARDED);
		});
	}

	public ModelEntity getEntity() {
		return entity;
	}

	public World getWorld() {
		return world;
	}

}

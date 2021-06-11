package org.scaffoldeditor.editormc.render_entities;

import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.world.World;

/**
 * A render entity pointing to an entity that only exists on the client.
 * @author Igrium
 */
public abstract class ClientSideEntity<T extends Entity, R extends RenderEntity> implements EditorRenderEntity {
	
	protected final World world;
	protected final Class<T> entClass;
	protected final Class<R> renderEntClass;
	protected T entity;
	private final MinecraftClient client = MinecraftClient.getInstance();
	
	public ClientSideEntity(World world, Class<T> entClass, Class<R> renderEntClass) {
		this.world = world;
		this.entClass = entClass;
		this.renderEntClass = renderEntClass;
	}

	@Override
	public void spawn(RenderEntity in) {
		if (!(renderEntClass.isAssignableFrom(in.getClass()))) {
			throw new IllegalArgumentException("Passed RenderEntity is not an instance of "+entClass.getSimpleName());
		}
		client.execute(() -> spawnImpl(renderEntClass.cast(in)));
	}
	
	protected void spawnImpl(R in) {
		entity = spawnEntity(world);
		Vector3f pos = in.getPosition();
		entity.updatePosition(pos.x, pos.y, pos.z);
		world.spawnEntity(entity);
		updateImpl(in);
	}

	@Override
	public void update(RenderEntity in) {
		if (!(renderEntClass.isAssignableFrom(in.getClass()))) {
			throw new IllegalArgumentException("Passed RenderEntity is not an instance of "+entClass.getSimpleName());
		}
		client.execute(() -> updateImpl(renderEntClass.cast(in)));
	}
	
	protected void updateImpl(R in) {
		Vector3f pos = in.getPosition();
		entity.updatePosition(pos.x, pos.y, pos.z);
	}

	@Override
	public void despawn() {
		client.execute(this::despawnImpl);
	}
	
	protected void despawnImpl() {
		entity.remove(RemovalReason.DISCARDED);
	}
	
	protected abstract T spawnEntity(World world);
	
	public World getWorld() {
		return world;
	}
	
	public T getEntity() {
		return entity;
	}
}

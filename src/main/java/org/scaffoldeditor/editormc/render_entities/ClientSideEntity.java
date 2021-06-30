package org.scaffoldeditor.editormc.render_entities;

import org.scaffoldeditor.editormc.ScaffoldEditor;
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
	protected final Class<R> renderEntClass;
	protected T entity;
	private final MinecraftClient client = MinecraftClient.getInstance();
	protected final ScaffoldEditor editor;
	
	public ClientSideEntity(World world, ScaffoldEditor editor, Class<R> renderEntClass) {
		this.world = world;
		this.renderEntClass = renderEntClass;
		this.editor = editor;
	}

	@Override
	public void spawn(RenderEntity in) {
		if (!(renderEntClass.isAssignableFrom(in.getClass()))) {
			throw new IllegalArgumentException("Passed RenderEntity is not an instance of "+renderEntClass.getSimpleName());
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
			throw new IllegalArgumentException("Passed RenderEntity is not an instance of "+renderEntClass.getSimpleName());
		}
		client.execute(() -> updateImpl(renderEntClass.cast(in)));
	}
	
	protected void updateImpl(R in) {
		Vector3f pos = in.getPosition();
		entity.updatePosition(pos.x, pos.y, pos.z);
		entity.setGlowing(editor.getSelectedEntities().contains(in.getEntity()));
	}

	@Override
	public void despawn() {
		client.execute(this::despawnImpl);
	}
	
	protected void despawnImpl() {
		entity.remove(RemovalReason.DISCARDED);
	}
	
	/**
	 * Get an instance of the entity. Should NOT call <code>world.spawnEntity()</code>
	 * @param world World to instanciate with.
	 * @return Entity instance.
	 */
	protected abstract T spawnEntity(World world);
	
	public World getWorld() {
		return world;
	}
	
	public T getEntity() {
		return entity;
	}
	
	public ScaffoldEditor getEditor() {
		return editor;
	}
	
	@Override
	public boolean ownsEntity(Entity entity) {
		return entity == getEntity();
	}
}

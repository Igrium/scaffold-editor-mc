package org.scaffoldeditor.editormc.render_entities;

import org.scaffoldeditor.scaffold.level.render.RenderEntity;

import net.minecraft.entity.Entity;

public interface EditorRenderEntity {
	void spawn(RenderEntity entity);
	void update(RenderEntity entity);
	void despawn();
	
	/**
	 * Determine whether this editor entity controls this Minecraft entity.
	 * @param entity Minecraft entity to test.
	 */
	default boolean ownsEntity(Entity entity) {
		return false;
	}
}

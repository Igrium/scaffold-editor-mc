package org.scaffoldeditor.editormc.render_entities;

import org.scaffoldeditor.scaffold.level.render.RenderEntity;

public interface EditorRenderEntity {
	void spawn(RenderEntity entity);
	void update(RenderEntity entity);
	void despawn();
}

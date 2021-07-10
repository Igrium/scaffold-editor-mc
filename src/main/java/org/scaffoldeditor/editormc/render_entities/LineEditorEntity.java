package org.scaffoldeditor.editormc.render_entities;

import org.scaffoldeditor.editormc.engine.ScaffoldEditorMod;
import org.scaffoldeditor.editormc.engine.world.LineRenderDispatcher;
import org.scaffoldeditor.editormc.engine.world.LineRenderDispatcher.LineRenderer;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.render.LineRenderEntity;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;

import net.minecraft.util.math.Vec3d;

public class LineEditorEntity implements EditorRenderEntity {
	
	protected LineRenderer renderer;
	private LineRenderDispatcher dispatcher = ScaffoldEditorMod.getInstance().getLineRenderDispatcher();

	@Override
	public void spawn(RenderEntity entity) {
		if (!(entity instanceof LineRenderEntity)) {
			throw new IllegalArgumentException("Passed RenderEntity is not an instance of LineRenderEntity!");
		}
		LineRenderEntity ent = (LineRenderEntity) entity;
		renderer = new LineRenderer(convertVec(ent.getPosition()), convertVec(ent.getEndPos()));
		dispatcher.lines.add(renderer);
		update(ent);
	}

	@Override
	public void update(RenderEntity entity) {
		if (!(entity instanceof LineRenderEntity)) {
			throw new IllegalArgumentException("Passed RenderEntity is not an instance of LineRenderEntity!");
		}
		LineRenderEntity ent = (LineRenderEntity) entity;
		renderer.setStart(convertVec(ent.getPosition()));
		renderer.setEnd(convertVec(ent.getEndPos()));
		renderer.setRed(ent.getRed());
		renderer.setGreen(ent.getGreen());
		renderer.setBlue(ent.getBlue());
		renderer.setAlpha(ent.getAlpha());
	}

	@Override
	public void despawn() {
		dispatcher.lines.remove(renderer);
	}
	
	private Vec3d convertVec(Vector3f in) {
		return new Vec3d(in.x, in.y, in.z);
	}
}

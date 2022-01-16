package org.scaffoldeditor.editormc.render_entities;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.engine.entity.BrushEntity;
import org.scaffoldeditor.scaffold.level.render.BrushRenderEntity;

import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class BrushEditorEntity extends ClientSideEntity<BrushEntity, BrushRenderEntity> {

	public BrushEditorEntity(World world, ScaffoldEditor editor) {
		super(world, editor, BrushRenderEntity.class);
	}

	@Override
	protected BrushEntity spawnEntity(World world) {
		return new BrushEntity(BrushEntity.TYPE, world);
	}
	
	@Override
	protected void updateImpl(BrushRenderEntity in) {
		super.updateImpl(in);
		Vector3dc size = in.getEndPos().sub(in.getPosition(), new Vector3d());
		getEntity().setSize(new Vec3f((float) size.x(), (float) size.y(), (float) size.z()));
		getEntity().setTexture(in.getTexture());
	}

}

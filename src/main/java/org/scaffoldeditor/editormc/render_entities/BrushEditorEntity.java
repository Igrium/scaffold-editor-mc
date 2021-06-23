package org.scaffoldeditor.editormc.render_entities;

import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.engine.entity.BrushEntity;
import org.scaffoldeditor.nbt.math.Vector3f;
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
		Vector3f size = in.getEndPos().subtract(in.getPosition());
		getEntity().setSize(new Vec3f(size.x, size.y, size.z));
		getEntity().setTexture(in.getTexture());
	}

}

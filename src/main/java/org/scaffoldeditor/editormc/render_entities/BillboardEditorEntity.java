package org.scaffoldeditor.editormc.render_entities;

import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.engine.entity.BillboardEntity;
import org.scaffoldeditor.scaffold.level.render.BillboardRenderEntity;
import net.minecraft.world.World;

public class BillboardEditorEntity extends ClientSideEntity<BillboardEntity, BillboardRenderEntity> {

	public BillboardEditorEntity(World world, ScaffoldEditor editor) {
		super(world, editor, BillboardRenderEntity.class);
	}

	@Override
	protected BillboardEntity spawnEntity(World world) {
		return new BillboardEntity(BillboardEntity.TYPE, world);
	}
	
	@Override
	protected void updateImpl(BillboardRenderEntity in) {
		super.updateImpl(in);
		entity.setTexture(in.getTexture());
		entity.setScale(in.getScale());
	}

}

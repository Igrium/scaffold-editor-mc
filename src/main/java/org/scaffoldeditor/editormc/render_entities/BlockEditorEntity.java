package org.scaffoldeditor.editormc.render_entities;

import org.scaffoldeditor.editormc.engine.ScaffoldEditorMod;
import org.scaffoldeditor.editormc.engine.world.BlockRenderDispatcher;
import org.scaffoldeditor.editormc.engine.world.BlockRenderDispatcher.BlockCollectionRenderer;
import org.scaffoldeditor.scaffold.level.render.BlockRenderEntity;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;

import net.minecraft.util.math.Vec3d;

public class BlockEditorEntity implements EditorRenderEntity {
	
	protected BlockCollectionRenderer renderer;
	private BlockRenderDispatcher dispatcher = ScaffoldEditorMod.getInstance().getBlockRenderDispatcher();

	@Override
	public void spawn(RenderEntity entity) {
		if (!(entity instanceof BlockRenderEntity)) {
			throw new IllegalArgumentException("Passed RenderEntity is not an instance of BlockRenderEntity!");
		}
		BlockRenderEntity ent = (BlockRenderEntity) entity;
		renderer = new BlockCollectionRenderer(ent.getBlocks());
		dispatcher.blockCollections.add(renderer);
		update(ent);
	}

	@Override
	public void update(RenderEntity entity) {
		if (renderer == null) return;
		if (!(entity instanceof BlockRenderEntity)) {
			throw new IllegalArgumentException("Passed RenderEntity is not an instance of BlockRenderEntity!");
		}
		BlockRenderEntity ent = (BlockRenderEntity) entity;
		renderer.setBlockCollection(ent.getBlocks());
		renderer.setPos(new Vec3d(ent.getPosition().x, ent.getPosition().y, ent.getPosition().z));
		renderer.setRot(ent.getRotation().y);
	}

	@Override
	public void despawn() {
		dispatcher.blockCollections.remove(renderer);
	}

}

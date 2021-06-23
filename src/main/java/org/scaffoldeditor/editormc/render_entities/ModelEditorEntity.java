package org.scaffoldeditor.editormc.render_entities;

import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.engine.entity.ModelEntity;
import org.scaffoldeditor.scaffold.level.render.ModelRenderEntity;

import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.world.World;

public class ModelEditorEntity extends ClientSideEntity<ModelEntity, ModelRenderEntity> {

	public ModelEditorEntity(World world, ScaffoldEditor editor) {
		super(world, editor, ModelRenderEntity.class);
	}

	@Override
	protected ModelEntity spawnEntity(World world) {
		return new ModelEntity(ModelEntity.TYPE, world);
	}
	
	@Override
	protected void updateImpl(ModelRenderEntity in) {
		super.updateImpl(in);
		getEntity().setModel(new ModelIdentifier(in.getModel()));
	}

}

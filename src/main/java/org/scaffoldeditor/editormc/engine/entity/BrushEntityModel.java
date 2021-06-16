package org.scaffoldeditor.editormc.engine.entity;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;

public class BrushEntityModel extends SinglePartEntityModel<BrushEntity> {
	
//	private final BrushEntity entity;
	private ModelPart root;
	
	public BrushEntityModel(BrushEntity entity) {
//		this.entity = entity;

		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		
		ModelPartBuilder builder = ModelPartBuilder.create().cuboid(0, 0, 0, 16, 16, 16);
		modelPartData.addChild("root", builder, ModelTransform.NONE);
		this.root = TexturedModelData.of(modelData, 16, 16).createModel();
	}

	@Override
	public void setAngles(BrushEntity entity, float limbAngle, float limbDistance, float animationProgress,
			float headYaw, float headPitch) {
	}

	@Override
	public ModelPart getPart() {
		return root;
	}

}

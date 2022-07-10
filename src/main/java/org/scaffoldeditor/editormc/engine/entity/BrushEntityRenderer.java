package org.scaffoldeditor.editormc.engine.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BrushEntityRenderer extends EntityRenderer<BrushEntity> {
	
	public static void register() {
		EntityRendererRegistry.register(BrushEntity.TYPE, BrushEntityRenderer::new);
	}
		
	protected BrushEntityRenderer(Context ctx) {
		super(ctx);
	}

	@Override
	public Identifier getTexture(BrushEntity entity) {
		return new Identifier(entity.getTexture());
	}
	
	@Override
	public void render(BrushEntity entity, float yaw, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
		
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("body", ModelPartBuilder.create().uv(32, 32).cuboid(0, 0, 0, entity.getSizeX() * 16,
				entity.getSizeY() * 16, entity.getSizeZ() * 16), ModelTransform.NONE);
		ModelPart part = TexturedModelData.of(modelData, 16, 16).createModel();
		VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(getTexture(entity)));
		part.render(matrices, vertices, 255, OverlayTexture.DEFAULT_UV);
	}

}

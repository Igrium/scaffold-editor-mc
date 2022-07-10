package org.scaffoldeditor.editormc.engine.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModelEntityRenderer extends EntityRenderer<ModelEntity> {
	
	public static void register() {
		EntityRendererRegistry.register(ModelEntity.TYPE, (context) -> {
			return new ModelEntityRenderer(context);
		});
	}
	
	private final MinecraftClient client = MinecraftClient.getInstance();

	protected ModelEntityRenderer(Context ctx) {
		super(ctx);
	}
	
	@Override
	public void render(ModelEntity entity, float yaw, float tickDelta, MatrixStack matrixStack,
			VertexConsumerProvider vertexConsumers, int light) {
		super.render(entity, yaw, tickDelta, matrixStack, vertexConsumers, light);
		if (entity.getModel() == null) return;
		matrixStack.push();
		BlockRenderManager blockRenderManager = this.client.getBlockRenderManager();
        BakedModelManager bakedModelManager = blockRenderManager.getModels().getModelManager();
		blockRenderManager.getModelRenderer().render(matrixStack.peek(),
				vertexConsumers.getBuffer(TexturedRenderLayers.getEntitySolid()), null,
				bakedModelManager.getModel(entity.getModel()), 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
		
		matrixStack.pop();
	}
	
	@Override
	public boolean shouldRender(ModelEntity entity, Frustum frustum, double x, double y, double z) {
		return true;
	}

	@Override
	public Identifier getTexture(ModelEntity entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}

}

package org.scaffoldeditor.editormc.engine;

import java.util.Random;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;

public final class RenderUtils {
	private RenderUtils() {}
	
	public static void renderBakedModel(BakedModel model, MatrixStack matrices, VertexConsumer consumer) {
		renderBakedModel(model, matrices, consumer, 1, 1, 1, 255);
	}
	
	public static void renderBakedModel(BakedModel model, MatrixStack matrices, VertexConsumer consumer, float red, float green, float blue, int light) {
		model.getQuads(null, null, new Random()).forEach(quad -> {
			consumer.quad(matrices.peek(), quad, red, green, blue, light, OverlayTexture.DEFAULT_UV);
		});
	}
}

package org.scaffoldeditor.editormc.engine;

import java.util.Random;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

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
	
	public static void renderLine(MatrixStack matrices, VertexConsumer consumer, Vec3d start, Vec3d end, float red, float green, float blue, float alpha) {
		Matrix4f model = matrices.peek().getModel();
		Matrix3f normal = matrices.peek().getNormal();
		Vec3d diff = end.subtract(start);
		
		consumer.vertex(model, (float) start.x, (float) start.y, (float) start.z).color(red, green, blue, alpha)
				.normal(normal, (float) diff.x, (float) diff.y, (float) diff.z).next();
		consumer.vertex(model, (float) end.x, (float) end.y, (float) end.z).color(red, green, blue, alpha)
				.normal(normal, (float) diff.x, (float) diff.y, (float) diff.z).next();
	}
}

package org.scaffoldeditor.editormc.engine.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

@Environment(EnvType.CLIENT)
public class BillboardEntityRenderer extends EntityRenderer<BillboardEntity> {
	
	public static void register() {
		EntityRendererRegistry.INSTANCE.register(BillboardEntity.TYPE, BillboardEntityRenderer::new);
	}

	protected BillboardEntityRenderer(Context ctx) {
		super(ctx);
	}

	
	public void render(BillboardEntity entity, float yaw, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
		
		MinecraftClient client = MinecraftClient.getInstance();
		Camera camera = client.gameRenderer.getCamera();
		
		Quaternion cameraRot = camera.getRotation();
		
		Vec3f rotVector = new Vec3f(-1, -1, 0);
		rotVector.rotate(cameraRot);
		Vec3f[] verts = new Vec3f[] {new Vec3f(-1, -1, 0), new Vec3f(-1, 1, 0), new Vec3f(1, 1, 0), new Vec3f(1, -1, 0)};
		
		
		MatrixStack.Entry entry = matrices.peek();
		Matrix4f model = entry.getModel();
		
		for (int i = 0; i < 4; i++) {
			Vec3f vert = verts[i];
			vert.rotate(cameraRot);
			vert.scale(entity.getScale() / 2);
			
			Vector4f vector4 = new Vector4f(vert.getX(), vert.getY(), vert.getZ(), 1.0F);
			vector4.transform(model);
			vert.set(vector4.getX(), vector4.getY(), vector4.getZ());
		}

		
		float minU = 0;
		float maxU = 1;
		float minV = 0;
		float maxV = 1;
		VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(getTexture(entity)));
		buffer.vertex(verts[0].getX(), verts[0].getY(), verts[0].getZ(), 1, 1, 1, 1, maxU, maxV,
				OverlayTexture.DEFAULT_UV, 255, rotVector.getX(), rotVector.getY(), rotVector.getZ());
		buffer.vertex(verts[1].getX(), verts[1].getY(), verts[1].getZ(), 1, 1, 1, 1, maxU, minV,
				OverlayTexture.DEFAULT_UV, 255, rotVector.getX(), rotVector.getY(), rotVector.getZ());
		buffer.vertex(verts[2].getX(), verts[2].getY(), verts[2].getZ(), 1, 1, 1, 1, minU, minV,
				OverlayTexture.DEFAULT_UV, 255, rotVector.getX(), rotVector.getY(), rotVector.getZ());
		buffer.vertex(verts[3].getX(), verts[3].getY(), verts[3].getZ(), 1, 1, 1, 1, minU, maxV,
				OverlayTexture.DEFAULT_UV, 255, rotVector.getX(), rotVector.getY(), rotVector.getZ());
		
	}

	@Override
	public Identifier getTexture(BillboardEntity entity) {
		return new Identifier(entity.getTexture());
	}

}

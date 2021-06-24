package org.scaffoldeditor.editormc.engine.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class BillboardEntityRenderer extends EntityRenderer<BillboardEntity> {
	
	public static void register() {
		EntityRendererRegistry.INSTANCE.register(BillboardEntity.TYPE, BillboardEntityRenderer::new);
	}

	protected BillboardEntityRenderer(Context ctx) {
		super(ctx);
	}
	
	public void render_old(BillboardEntity entity, float yaw, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
		
//		if (entity.getTexture() == null || entity.getTexture().length() == 0) return;
		
		MinecraftClient client = MinecraftClient.getInstance();
		Camera camera = client.gameRenderer.getCamera();
		
		Vec3d cameraPos = camera.getPos();
		Quaternion cameraRot = camera.getRotation();
		
		
		Vec3f rotVector = new Vec3f(-1, -1, 0);
		rotVector.rotate(cameraRot);
		Vec3f[] verts = new Vec3f[] {new Vec3f(-.5f, -.5f, 0), new Vec3f(-.5f, .5f, 0), new Vec3f(.5f, .5f, 0), new Vec3f(.5f, -.5f, 0)};
		
		for (int i = 0; i < 4; i++) {
			Vec3f vert = verts[i];
			vert.rotate(cameraRot);
			vert.scale(entity.getScale());
			vert.add((float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);
		}
		
		float minU = 0;
		float maxU = 16;
		float minV = 0;
		float maxV = 16;
		
		VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(getTexture(entity)));
		buffer.vertex(verts[0].getX(), verts[0].getY(), verts[0].getZ()).texture(minU, minV).next();
		buffer.vertex(verts[1].getX(), verts[1].getY(), verts[1].getZ()).texture(minU, maxV).next();
		buffer.vertex(verts[2].getX(), verts[2].getY(), verts[2].getZ()).texture(maxU, maxV).next();
		buffer.vertex(verts[3].getX(), verts[3].getY(), verts[3].getZ()).texture(maxU, minV).next();
		
	}
	
	public void render(BillboardEntity entity, float yaw, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
		
//		if (entity.getTexture() == null || entity.getTexture().equals("")) return;
		
		MinecraftClient client = MinecraftClient.getInstance();
		Camera camera = client.gameRenderer.getCamera();
		
		Vec3d cameraPos = camera.getPos();
		Quaternion cameraRot = camera.getRotation();
		
		Vec3f rotVector = new Vec3f(-1, -1, 0);
		rotVector.rotate(cameraRot);
		Vec3f[] verts = new Vec3f[] {new Vec3f(-1, -1, 0), new Vec3f(-1, 1, 0), new Vec3f(1, 1, 0), new Vec3f(1, -1, 0)};
		
//		for (int i = 0; i < 4; i++) {
//			Vec3f vert = verts[i];
//			vert.rotate(cameraRot);
//			vert.scale(entity.getScale());
//			vert.add((float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);
//		}
		
		MatrixStack.Entry entry = matrices.peek();
		Matrix4f model = entry.getModel();
		
		float minU = 0;
		float maxU = 16;
		float minV = 0;
		float maxV = 16;
		VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(getTexture(entity)));
		buffer.vertex(verts[0].getX(), verts[0].getY(), verts[0].getZ(), 1, 1, 1, 1, maxU, maxV, 1, light, rotVector.getX(), rotVector.getY(), rotVector.getZ());
		buffer.vertex(verts[1].getX(), verts[1].getY(), verts[1].getZ(), 1, 1, 1, 1, maxU, minV, 1, light, rotVector.getX(), rotVector.getY(), rotVector.getZ());
		buffer.vertex(verts[2].getX(), verts[2].getY(), verts[2].getZ(), 1, 1, 1, 1, minU, minV, 1, light, rotVector.getX(), rotVector.getY(), rotVector.getZ());
		buffer.vertex(verts[0].getX(), verts[3].getY(), verts[3].getZ(), 1, 1, 1, 1, minU, maxV, 1, light, rotVector.getX(), rotVector.getY(), rotVector.getZ());
		
//		buffer.vertex(verts[0].getX(), verts[0].getY(), verts[0].getZ()).texture(maxU, maxV).next();	
//		buffer.vertex(verts[1].getX(), verts[1].getY(), verts[1].getZ()).texture(maxU, maxV).next();
//		buffer.vertex(verts[2].getX(), verts[2].getY(), verts[2].getZ()).texture(minU, minV).next();
//		buffer.vertex(verts[3].getX(), verts[3].getY(), verts[3].getZ()).texture(minU, maxV).next();
		
	}
	
	@Override
	public boolean shouldRender(BillboardEntity entity, Frustum frustum, double x, double y, double z) {
		return true;
	}

	@Override
	public Identifier getTexture(BillboardEntity entity) {
		return new Identifier(entity.getTexture());
	}

}

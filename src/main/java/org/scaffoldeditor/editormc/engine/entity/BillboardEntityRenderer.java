package org.scaffoldeditor.editormc.engine.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class BillboardEntityRenderer extends EntityRenderer<BillboardEntity> {
	
	public static void register() {
		EntityRendererRegistry.INSTANCE.register(BillboardEntity.TYPE, (context) -> {
			return new BillboardEntityRenderer(context);
		});
	}

	protected BillboardEntityRenderer(Context ctx) {
		super(ctx);
	}
	
	@Override
	public void render(BillboardEntity entity, float yaw, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
		
		if (entity.getTexture() == null || entity.getTexture().equals("")) return;
		
		MinecraftClient client = MinecraftClient.getInstance();
		Camera camera = client.gameRenderer.getCamera();
		
		Vec3d cameraPos = camera.getPos();
		Quaternion cameraRot = camera.getRotation();
		
		Vec3f rotVector = new Vec3f(-1, -1, 0);
		rotVector.rotate(cameraRot);
		Vec3f[] verts = new Vec3f[] {new Vec3f(-1, -1, 0), new Vec3f(-1, 1, 0), new Vec3f(1, 1, 0), new Vec3f(1, -1, 0)};
		
		for (int i = 0; i < 4; i++) {
			Vec3f vert = verts[i];
			vert.rotate(cameraRot);
			vert.scale(entity.getScale());
			vert.add((float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);
		}
		
		float minU = 0;
		float maxU = 1;
		float minV = 0;
		float maxV = 1;
		VertexConsumer buffer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull()); 
		matrices.push();

		buffer.vertex(verts[0].getX(), verts[0].getY(), verts[0].getZ(), 1, 1, 1, 1, maxU, maxV, 1, light, rotVector.getX(), rotVector.getY(), rotVector.getZ());
		buffer.vertex(verts[1].getX(), verts[1].getY(), verts[1].getZ(), 1, 1, 1, 1, maxU, minV, 1, light, rotVector.getX(), rotVector.getY(), rotVector.getZ());
		buffer.vertex(verts[2].getX(), verts[2].getY(), verts[2].getZ(), 1, 1, 1, 1, minU, minV, 1, light, rotVector.getX(), rotVector.getY(), rotVector.getZ());
		buffer.vertex(verts[0].getX(), verts[3].getY(), verts[3].getZ(), 1, 1, 1, 1, minU, maxV, 1, light, rotVector.getX(), rotVector.getY(), rotVector.getZ());
		
//		buffer.vertex(verts[0].getX(), verts[0].getY(), verts[0].getZ()).texture(maxU, maxV).next();	
//		buffer.vertex(verts[1].getX(), verts[1].getY(), verts[1].getZ()).texture(maxU, maxV).next();
//		buffer.vertex(verts[2].getX(), verts[2].getY(), verts[2].getZ()).texture(minU, minV).next();
//		buffer.vertex(verts[3].getX(), verts[3].getY(), verts[3].getZ()).texture(minU, maxV).next();
		
		matrices.pop();
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

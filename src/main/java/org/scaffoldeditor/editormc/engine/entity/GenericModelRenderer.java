package org.scaffoldeditor.editormc.engine.entity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class GenericModelRenderer extends EntityRenderer<GenericModelEntity> {

	protected GenericModelRenderer(EntityRenderDispatcher dispatcher) {
		super(dispatcher);
		
	}
	
	@Override
	public void render(GenericModelEntity entity, float yaw, float tickDelta, MatrixStack matrixStack,
			VertexConsumerProvider vertexConsumers, int light) {
		super.render(entity, yaw, tickDelta, matrixStack, vertexConsumers, light);
		matrixStack.push();
		Vec3d offset = this.getPositionOffset(entity, tickDelta);
		matrixStack.translate(-offset.x, -offset.y, -offset.z);
		
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(entity.pitch));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F - entity.yaw));
	}

	@Override
	public Identifier getTexture(GenericModelEntity entity) {
		return null;
	}

}

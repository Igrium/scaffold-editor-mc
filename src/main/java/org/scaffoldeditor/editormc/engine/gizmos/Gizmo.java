package org.scaffoldeditor.editormc.engine.gizmos;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public abstract class Gizmo {
	protected Vec3d position;
	
	public void setPos(Vec3d pos) {
		position = pos;
	}
	
	public Vec3d getPos() {
		return position;
	}
	
	public abstract void render(MatrixStack matrices, VertexConsumerProvider vertices);
}

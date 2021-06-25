package org.scaffoldeditor.editormc.engine.gizmos;

import java.util.HashSet;
import java.util.Set;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class GizmoManager {
	
	public final Set<Gizmo> gizmos = new HashSet<>();
	
	
	public void register() {
		WorldRenderEvents.AFTER_ENTITIES.register(context -> {
			render(context.matrixStack(), context.consumers(), context.camera().getPos());
		});
	}
	
	public void render(MatrixStack matrices, VertexConsumerProvider vertices, Vec3d cameraPos) {
		if (!RenderSystem.isOnRenderThread()) {
			throw new IllegalStateException("Rendering can only happen on the render thread!");
		}
		
		matrices.push();
		matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		for (Gizmo gizmo : gizmos) {
			renderGizmo(gizmo, matrices, vertices);
		}
		matrices.pop();
	}
	
	protected void renderGizmo(Gizmo gizmo, MatrixStack matrices, VertexConsumerProvider vertices) {
		matrices.push();
		Vec3d pos = gizmo.getPos();
		matrices.translate(pos.x, pos.y, pos.z);
		gizmo.render(matrices, vertices);
		matrices.pop();
	}
}

package org.scaffoldeditor.editormc.engine.world;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.scaffoldeditor.editormc.engine.RenderUtils;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class LineRenderDispatcher {
	protected final MinecraftClient client;
	
	public static class LineRenderer {
		private Vec3d start;
		private Vec3d end;
		private float red = 1;
		private float green = 1;
		private float blue = 1;
		private float alpha = 1;
		
		public LineRenderer(Vec3d start, Vec3d end) {
			this.start = start;
			this.end = end;
		}
		
		public LineRenderer(Vec3d start, Vec3d end, float red, float green, float blue, float alpha) {
			this(start, end);
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.alpha = alpha;
		}
		
		public Vec3d getStart() {
			return start;
		}
		public void setStart(Vec3d start) {
			this.start = start;
		}
		
		public Vec3d getEnd() {
			return end;
		}
		public void setEnd(Vec3d end) {
			this.end = end;
		}
		
		public float getRed() {
			return red;
		}
		public void setRed(float red) {
			this.red = red;
		}
		
		public float getGreen() {
			return green;
		}
		public void setGreen(float green) {
			this.green = green;
		}
		
		public float getBlue() {
			return blue;
		}
		public void setBlue(float blue) {
			this.blue = blue;
		}
		
		public float getAlpha() {
			return alpha;
		}
		public void setAlpha(float alpha) {
			this.alpha = alpha;
		}
		
	}
	
	public final Set<LineRenderer> lines = Collections.synchronizedSet(new HashSet<>());
	
	public LineRenderDispatcher(MinecraftClient client) {
		this.client = client;
	}
	
	public void register() {
		WorldRenderEvents.AFTER_ENTITIES.register(context -> {
			render(context.matrixStack(), context.consumers(), context.camera().getPos());
		});
	}
	
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, Vec3d cameraPos) {
		matrixStack.push();
		matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getLines());
		
		synchronized (lines) {
			for (LineRenderer line : lines) {
				RenderUtils.renderLine(matrixStack, consumer, line.getStart(), line.getEnd(), line.getRed(),
						line.getGreen(), line.getBlue(), line.getAlpha());
			}
		}
		
		matrixStack.pop();
	}
}

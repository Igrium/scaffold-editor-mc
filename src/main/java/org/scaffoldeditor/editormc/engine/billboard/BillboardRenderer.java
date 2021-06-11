package org.scaffoldeditor.editormc.engine.billboard;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class BillboardRenderer implements Iterable<Billboard> {
	
	private MinecraftClient mc = MinecraftClient.getInstance();
	private Set<Billboard> billboards = new HashSet<>();
	
	public synchronized void add(Billboard billboard) {
		billboards.add(billboard);
	}
	
	public synchronized boolean remove(Billboard billboard) {
		return billboards.remove(billboard);
	}
	
	public synchronized boolean contains(Billboard billboard) {
		return billboards.contains(billboard);
	}
	
	public void register() {
		WorldRenderEvents.AFTER_ENTITIES.register((context) -> {
			draw(context);
		});
	}
	
	public void draw(WorldRenderContext context) {
		if (!RenderSystem.isOnRenderThread()) {
			throw new IllegalStateException("Billboards can only be drawn from the render thread!");
		}
		if (billboards.isEmpty()) return;
		
		context.matrixStack().push();
		for (Billboard billboard : this) {
			drawBillboard(billboard, context.camera(), context.matrixStack());
		}
		context.matrixStack().pop();
	}
	
	/**
	 * Render a billboard to the scene.
	 * @param billboard Billboard to draw.
	 * @param view Camera position.
	 */
	private void drawBillboard(Billboard billboard, Camera camera, MatrixStack matrixStack) {
		if (!RenderSystem.isOnRenderThread()) {
			throw new IllegalStateException("Billboards can only be drawn from the render thread!");
		}
		
		mc.getTextureManager().bindTexture(billboard.getTexture());
		
		float minX = -.5f * billboard.getScale();
		float minY = -.5f * billboard.getScale();
		float maxX = .5f * billboard.getScale();
		float maxY = .5f * billboard.getScale();
		
		float minU = billboard.getMinUV().x;
		float minV = billboard.getMinUV().y;
		float maxU = billboard.getMaxUV().x;
		float maxV = billboard.getMaxUV().y;
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		
		buffer.vertex(minX, minY, 0).texture(maxU, maxV).next();
		buffer.vertex(minX, maxY, 0).texture(maxU, minV).next();
		buffer.vertex(maxX, maxY, 0).texture(minU, minV).next();
		buffer.vertex(maxX, minY, 0).texture(minU, maxV).next();
		
		matrixStack.push();
		
		Vec3d view = camera.getPos();
		Vec3d pos = billboard.getPos();
		matrixStack.translate(pos.x - view.x, pos.y - view.y, pos.z - view.z);
//		matrixStack.multiply(camera.getRotation());
//		GL11.glNormal3f(0, 1, 0);
//		GL11.glRotatef(-camera.getYaw(), 0, 1, 0);
//		GL11.glRotatef(camera.getPitch(), 1, 0, 0);
		tessellator.draw();
		
		matrixStack.pop();
	}

	@Override
	public Iterator<Billboard> iterator() {
		return billboards.iterator();
	}
}

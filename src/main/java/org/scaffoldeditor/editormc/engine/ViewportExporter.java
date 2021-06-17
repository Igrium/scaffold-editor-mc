package org.scaffoldeditor.editormc.engine;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.system.MemoryUtil;
import org.scaffoldeditor.editormc.engine.mixins.MinecraftClientAccessor;
import org.scaffoldeditor.editormc.ui.ScaffoldUI;
import org.scaffoldeditor.editormc.ui.Viewport;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import javafx.application.Platform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.util.math.Vec3d;

public class ViewportExporter {
	
	
	/**
	 * Export the Minecraft game to the Scaffold viewport.
	 * Must be called from the render thread.
	 */
	public static void export() {
		if (!RenderSystem.isOnRenderThread() || !ScaffoldEditorMod.getInstance().isInEditor) {
			return;
		};
		
		Viewport viewport;
		try {
			viewport = ScaffoldEditorMod.getInstance().editor.getUI().getViewport();	
			if (viewport == null) {
				return;
			}
		} catch (NullPointerException e) {
			return;
		}
		
		Framebuffer frameBuffer = MinecraftClient.getInstance().getFramebuffer();
		int x = frameBuffer.textureWidth; int y = frameBuffer.textureHeight;
		
		ByteBuffer buffer = MemoryUtil.memAlloc(x * y * 4);
		
		frameBuffer.beginWrite(true);
		GlStateManager._readPixels(0, 0, x, y, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, buffer);
		frameBuffer.endWrite();
		
		buffer.rewind();
		
		
		Platform.runLater(() -> {
			viewport.updateViewport(buffer, x, y);
			MemoryUtil.memFree(buffer);
			
			MinecraftClient client = MinecraftClient.getInstance();
			ScaffoldUI.getInstance().setFPSIndicator(MinecraftClientAccessor.getFPS());
			
			Vec3d coords = client.player.getPos();
			DecimalFormat format = new DecimalFormat("#.0");
			ScaffoldUI.getInstance().setCoordIndicator("<" + format.format(coords.x) + ", " + format.format(coords.y)
					+ ", " + format.format(coords.z) + ">");

		});
	}
	
}

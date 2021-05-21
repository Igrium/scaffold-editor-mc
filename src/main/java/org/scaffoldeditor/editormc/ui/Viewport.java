package org.scaffoldeditor.editormc.ui;

import java.awt.Dimension;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.gismos.TransformationGismo;
import org.scaffoldeditor.editormc.gismos.TranslationGismo;
import org.scaffoldeditor.editormc.util.RaycastUtils;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.math.Vector;

import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.Pane;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Viewport {
	public final Map<String, TransformationGismo> gismos = new HashMap<>();
	
	protected ImageView imageView;
	protected Pane parent;
	private TransformationGismo activeGismo;
	private int mouseX = 0;
	private int mouseY = 0;
	
	private final WritablePixelFormat<ByteBuffer> PIXEL_FORMAT = PixelFormat.getByteBgraInstance();
	
	public Viewport(ImageView imageView, Pane parent) {
		this.imageView = imageView;
		this.parent = parent;
		
		gismos.put("translate", new TranslationGismo(this));
	}
	
	/**
	 * Update the editor viewport. Must be called from the FX thread.
	 * @param buffer ByteBuffer to draw viewport from.
	 * @param x Width of the image in pixels.
	 * @param y Height of the image in pixels.
	 */
	public void updateViewport(ByteBuffer buffer, int x, int y) {
		WritableImage image = new WritableImage(x, y);
		if (x > 0 && y > 0) {
			image.getPixelWriter().setPixels(0, 0, x, y, PIXEL_FORMAT, buffer, x * 4);
			imageView.setImage(image);
		}
	}
	
	/**
	 * Get the resolution we should render the game at.
	 * @return Desired resolution.
	 */
	public Dimension getDesiredResolution() {
		return new Dimension((int) parent.getWidth(), (int) parent.getHeight());
	}
	
	public ImageView getImage() {
		return imageView;
	}
	
	public Pane getParent() {
		return parent;
	}
	
	@SuppressWarnings("resource")
	public Vector getCameraPos() {
		Vec3d entityPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
		return new Vector((float) entityPos.x, (float) entityPos.y, (float) entityPos.z);
	}
	
	public void select(int x, int y, boolean multiple) {
		ScaffoldEditor editor = ScaffoldUI.getInstance().getEditor();
		if (!multiple) {
			editor.getSelectedEntities().clear();
		}
		
		int width = (int) parent.getWidth();
		int height = (int) parent.getHeight();
		
		HitResult hitResult = RaycastUtils.raycastPixel(x, y, width, height, 100);
		
		if (hitResult.getType() == Type.MISS) {
		} else if (hitResult.getType() == Type.BLOCK) {
			BlockHitResult blockHit = (BlockHitResult) hitResult;
			BlockPos pos = blockHit.getBlockPos();
			Object owner = editor.getLevel().getBlockWorld().getBlockOwner(pos.getX(), pos.getY(), pos.getZ());
			if (owner instanceof Entity) {
				editor.getSelectedEntities().add((Entity) owner);
			}
		}
		editor.updateSelection();
	}
	
	public void updateMouse(int x, int y) {
		mouseX = x;
		mouseY = y;
		if (activeGismo != null) {
			activeGismo.mouseMoved(x, y);
		}
	}
	
	public void beginTransformation(String gismoName, Entity target) {
		if (activeGismo == null) {
			TransformationGismo gismo = gismos.get(gismoName);
			if (gismo != null) {
				activeGismo = gismo;
				gismo.activate(target, mouseX, mouseY);
			}
		}
	}
	
	public void applyTransformation() {
		if (activeGismo != null) {
			activeGismo.apply();
			activeGismo = null;
		}
	}
	
	public void cancelTransformation() {
		if (activeGismo != null) {
			activeGismo.cancel();
			activeGismo = null;
		}
	}

	public TransformationGismo getActiveGismo() {
		return activeGismo;
	}
	
}

package org.scaffoldeditor.editormc.ui;

import java.awt.Dimension;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.editormc.gismos.TransformationGismo;
import org.scaffoldeditor.editormc.gismos.TranslationGismo;
import org.scaffoldeditor.editormc.tools.ViewportTool;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.math.Vector;

import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class Viewport {
	public final Map<String, TransformationGismo> gismos = new HashMap<>();
	
	protected ImageView imageView;
	protected Pane parent;
	private TransformationGismo activeGismo;
	private int mouseX = 0;
	private int mouseY = 0;
	private ViewportTool activeTool;
	private boolean isMouseOverViewport;
	
	private final WritablePixelFormat<ByteBuffer> PIXEL_FORMAT = PixelFormat.getByteBgraInstance();
	
	public Viewport(ImageView imageView, Pane parent) {
		this.imageView = imageView;
		this.parent = parent;
		
		gismos.put("translate", new TranslationGismo(this));
		parent.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
			isMouseOverViewport = true;
			if (activeTool != null && activeTool.overrideCursor()) {
				parent.getScene().setCursor(activeTool.getCursor());
			}
		});
		parent.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
			isMouseOverViewport = false;
			if (activeTool != null) {
				parent.getScene().setCursor(Cursor.DEFAULT);
			}
		});
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
	
	public void handleMousePressed(MouseEvent e) {
		if (activeTool != null) {
			activeTool.onMousePressed(e);
		}
	}
	
	public void handleMouseReleased(MouseEvent e) {
		if (activeTool != null) {
			activeTool.onMouseReleased(e);
		}
	}
	
	public void handleMouseClicked(MouseEvent e) {
		if (activeTool != null) {
			activeTool.onMouseClicked(e);
		}
	}
	
	public void handleMouseMoved(int x, int y) {
		mouseX = x;
		mouseY = y;
		if (activeTool != null) {
			activeTool.onMouseMoved(x, y);
		}
		if (activeGismo != null) {
			activeGismo.mouseMoved(x, y);
		}
	}
	
	public void handleMouseDragged(MouseEvent e) {
		if (activeTool != null) {
			activeTool.onMouseDragged(e);
		}
	}
	
	public void handleKeyPressed(KeyEvent e) {
		if (activeTool != null) {
			activeTool.onKeyPressed(e);
		}
	}
	
	public void handleKeyReleased(KeyEvent e) {
		if (activeTool != null) {
			activeTool.onKeyReleased(e);
		}
	}
	
	public void handleKeyTyped(KeyEvent e) {
		if (activeTool != null) {
			activeTool.onKeyTyped(e);
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

	public ViewportTool getActiveTool() {
		return activeTool;
	}

	public void setActiveTool(ViewportTool activeTool) {
		if (this.activeTool != null) {
			this.activeTool.onDeactivate();
		}
		this.activeTool = activeTool;
		if (activeTool != null) {
			LogManager.getLogger().info("Activating tool: "+activeTool.getClass().getName());
			activeTool.onActivate();
			ScaffoldUI.getInstance().setToolVisual(activeTool);
		}
		if (isMouseOverViewport) {
			if (activeTool != null && activeTool.overrideCursor()) {
				parent.getScene().setCursor(activeTool.getCursor());	
			} else {
				parent.getScene().setCursor(Cursor.DEFAULT);
			}
		}
	}
	
}

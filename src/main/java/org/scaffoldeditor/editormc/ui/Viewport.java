package org.scaffoldeditor.editormc.ui;

import java.awt.Dimension;
import java.nio.ByteBuffer;
import org.apache.logging.log4j.LogManager;
import org.joml.Vector3d;
import org.scaffoldeditor.editormc.tools.ViewportTool;
import org.scaffoldeditor.editormc.transformations.TransformManifest;
import org.scaffoldeditor.editormc.transformations.ViewportTransformation;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class Viewport {	
	protected ImageView imageView;
	protected Pane parent;
	private ViewportTool activeTool;
	private ViewportTransformation activeTransformation;
	private boolean isMouseOverViewport;
	
	private int mouseX;
	private int mouseY;
	
	private final PixelFormat<ByteBuffer> PIXEL_FORMAT = PixelFormat.getByteBgraInstance();
	
	public Viewport(ImageView imageView, Pane parent) {
		this.imageView = imageView;
		this.parent = parent;
		
		parent.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
			isMouseOverViewport = true;
			if (activeTransformation != null && activeTransformation.overrideCursor()) {
				parent.getScene().setCursor(activeTransformation.getCursor());
			} else {
				parent.getScene().setCursor(getToolCursor());
			}
		});
		parent.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
			isMouseOverViewport = false;
			parent.getScene().setCursor(Cursor.DEFAULT);
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
	public Vector3d getCameraPos() {
		Vec3d entityPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
		return new Vector3d(entityPos.x, entityPos.y, entityPos.z);
	}
	
	public void handleMousePressed(MouseEvent e) {
		parent.requestFocus();
		if (activeTransformation != null) {
			activeTransformation.onMousePressed(e);
		} else if (activeTool != null) {
			activeTool.onMousePressed(e);
		}
	}
	
	public void handleMouseReleased(MouseEvent e) {
		if (activeTransformation != null) {
			activeTransformation.onMouseReleased(e);
		} else if (activeTool != null) {
			activeTool.onMouseReleased(e);
		}
	}
	
	public void handleMouseClicked(MouseEvent e) {
		if (activeTransformation != null) {
			if (e.getButton() == MouseButton.PRIMARY) {
				applyTransformation();
			} else if (e.getButton() == MouseButton.SECONDARY) {
				cancelTransformation();
			} else {
				activeTransformation.onMouseClicked(e);
			}
		} else if (activeTool != null) {
			activeTool.onMouseClicked(e);
		}
	}
	
	public void handleMouseMoved(int x, int y) {
		mouseX = x;
		mouseY = y;
		
		if (activeTool != null) {
			activeTool.onMouseMoved(x, y);
		}
		if (activeTransformation != null) {
			activeTransformation.onMouseMoved(x, y);
		}
	}
	
	public void handleMouseDragged(MouseEvent e) {
		mouseX = (int) e.getX();
		mouseY = (int) e.getY();
		
		if (activeTool != null) {
			activeTool.onMouseDragged(e);
		}
		if (activeTransformation != null) {
			activeTransformation.onMouseDragged(e);
		}
	}
	
	public void handleKeyPressed(KeyEvent e) {
		if (activeTransformation != null) {
			if (e.getCode() == KeyCode.ESCAPE) {
				cancelTransformation();
				e.consume();
			} else if (e.getCode() == KeyCode.ENTER) {
				applyTransformation();
				e.consume();
			} else {
				activeTransformation.onKeyPressed(e);
			}	
		} else {
			ViewportTransformation transform = TransformManifest.getTransform(e);
			if (transform != null) {
				beginTransformation(transform);
			} else if (activeTool != null) {
				activeTool.onKeyPressed(e);
			}
		}
	}
	
	public void handleKeyReleased(KeyEvent e) {
		if (activeTransformation != null) {
			activeTransformation.onKeyReleased(e);
		} else if (activeTool != null) {
			activeTool.onKeyReleased(e);
		}
	}
	
	public void handleKeyTyped(KeyEvent e) {
		if (activeTransformation != null) {
			activeTransformation.onKeyTyped(e);
		} else if (activeTool != null) {
			activeTool.onKeyTyped(e);
		}
	}

	public ViewportTool getActiveTool() {
		return activeTool;
	}

	public void setActiveTool(ViewportTool activeTool) {
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> setActiveTool(activeTool));
			return;
		}

		if (this.activeTool != null) {
			this.activeTool.onDeactivate();
		}
		this.activeTool = activeTool;
		if (activeTool != null) {
			LogManager.getLogger().info("Activating tool: "+activeTool.getClass().getSimpleName());
			activeTool.onActivate();
			ScaffoldUI.getInstance().setToolVisual(activeTool);
		}
		if (isMouseOverViewport) {
			parent.getScene().setCursor(getToolCursor());
		}
	}
	
	private Cursor getToolCursor() {
		if (activeTool != null && activeTool.overrideCursor()) {
			return activeTool.getCursor();
		} else {
			return Cursor.DEFAULT;
		}
	}
	
	public ViewportTransformation getActiveTransformation() {
		return activeTransformation;
	}
	
	public void beginTransformation(ViewportTransformation transformation) {
		if (activeTransformation != null) {
			activeTransformation.cancel();
		}
		
		this.activeTransformation = transformation;
		LogManager.getLogger().info("Starting transformation: "+transformation.getClass().getSimpleName());
		
		if (isMouseOverViewport) {
			if (transformation.overrideCursor()) {
				parent.getScene().setCursor(transformation.getCursor());
			} else {
				parent.getScene().setCursor(getToolCursor());
			}
		}
		
		if (activeTool != null) {
			activeTool.onTransformationStarted(transformation);
		}
		
		transformation.activate();
	}
	
	public void cancelTransformation() {
		if (activeTransformation == null) return;
		
		activeTransformation.cancel();
		onTransformationStop();
	}
	
	public void applyTransformation() {
		if (activeTransformation == null) return;
		
		activeTransformation.apply();
		onTransformationStop();
	}
	
	private void onTransformationStop() {
		if (activeTool != null) {
			activeTool.onTransformationEnded(activeTransformation);
		}
		activeTransformation = null;
		
		if (isMouseOverViewport) {
			parent.getScene().setCursor(getToolCursor());
		}
		
	}
	
	public int getMouseX() {
		return mouseX;
	}
	
	public int getMouseY() {
		return mouseY;
	}

	public int getWidth() {
		return (int) getParent().getWidth();
	}

	public int getHeight() {
		return (int) getParent().getHeight();
	}
}

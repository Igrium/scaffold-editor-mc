package org.scaffoldeditor.editormc.tools;

import java.util.Set;

import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.engine.world.ScaffoldRenderEvents;
import org.scaffoldeditor.editormc.transformations.Translation;
import org.scaffoldeditor.editormc.transformations.ViewportTransformation;
import org.scaffoldeditor.editormc.ui.ScaffoldUI;
import org.scaffoldeditor.editormc.ui.Viewport;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.entity.BrushEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.operation.ResizeBrushOperation;

import com.mojang.blaze3d.systems.RenderSystem;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AfterEntities;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class ResizeTool implements ViewportTool {
	

	public static final Image ICON = new Image(MoveTool.class.getResourceAsStream("/assets/scaffold/tools/resize.png"));
	protected ScaffoldEditor editor;
	protected Viewport viewport;
	protected ScaffoldUI ui;
	protected Vector3f endPoint = new Vector3f(0, 0, 0);
	protected Translation translation;
	
	public ResizeTool(ScaffoldUI ui) {
		this.editor = ui.getEditor();
		this.viewport = ui.getViewport();
		this.ui = ui;
	}
	
	private AfterEntities renderListener = (context) -> {
		render(context.matrixStack(), context.consumers(), context.camera().getPos());
	};
	
	@Override
	public void onActivate() {
		ScaffoldRenderEvents.registerAfterEntities(renderListener);
	}
	
	@Override
	public void onDeactivate() {
		ScaffoldRenderEvents.removeAfterEntities(renderListener);
		cancelTranslation();
	}
	
	@Override
	public void onMouseClicked(MouseEvent event) {
		ViewportTool.super.onMouseClicked(event);
		if (event.getButton() == MouseButton.PRIMARY) {
			if (isTranslationActive()) {
				applyTranslation();
			} else {
				beginTranslation();
			}
			event.consume();
		}
	}
	
	@Override
	public void onKeyPressed(KeyEvent event) {
		ViewportTool.super.onKeyPressed(event);
		
		if (event.getCode() == KeyCode.ENTER) {
			applyTranslation();
			event.consume();
		} else if (event.getCode() == KeyCode.ESCAPE) {
			cancelTranslation();
			event.consume();
		} else if (isTranslationActive()) {
			if (event.getCode() == KeyCode.X) {
				if (event.isShiftDown()) {
					translation.setLock("YZ");
				} else {
					translation.setLock("X");
				}
				event.consume();
			} else if (event.getCode() == KeyCode.Y) {
				if (event.isShiftDown()) {
					translation.setLock("XZ");
				} else {
					translation.setLock("Y");
				}
				event.consume();
			} else if (event.getCode() == KeyCode.Z) {
				if (event.isShiftDown()) {
					translation.setLock("XY");
				} else {
					translation.setLock("Z");
				}
				event.consume();
			}
		}
	}
	
	@Override
	public void onMouseMoved(int mouseX, int mouseY) {
		ViewportTool.super.onMouseMoved(mouseX, mouseX);
		if (isTranslationActive()) {
			Vector3f endPoint = translation.getTranslation(mouseX, mouseY);
			Vector3f startPoint = getSelectedBrush().getBrushBounds()[0];
			
			// Prevent inverted boxes.
			float x = endPoint.x < startPoint.x ? startPoint.x : endPoint.x;
			float y = endPoint.y < startPoint.y ? startPoint.y : endPoint.y;
			float z = endPoint.z < startPoint.z ? startPoint.z : endPoint.z;

			
			this.endPoint = new Vector3f(x, y, z);
			if (ui.getViewportHeader().snapToGrid()) {
				this.endPoint = this.endPoint.floor().toFloat();
			}
		}
	}
	
	void onKeyPressed() {
		
	}
	@Override
	public void onTransformationStarted(ViewportTransformation transform) {
		ViewportTool.super.onTransformationStarted(transform);
		cancelTranslation();
	}
	
	public void beginTranslation() {
		if (isTranslationActive() || getSelectedBrush() == null) return;
		translation = new Translation(viewport, endPoint);
	}
	
	public void applyTranslation() {
		if (!isTranslationActive()) return;
		
		BrushEntity brush = getSelectedBrush();
		if (brush == null) return;
		Vector3f start = brush.getBrushBounds()[0];
		editor.getLevel().getOperationManager()
				.execute(new ResizeBrushOperation(brush, new Vector3f[] { start, endPoint }));
		translation = null;
	}
	
	public void cancelTranslation() {
		translation = null;
	}
	
	public boolean isTranslationActive() {
		return translation != null;
	}
	
	public BrushEntity getSelectedBrush() {
		Set<Entity> selected = editor.getSelectedEntities();
		
		BrushEntity brush = null;
		for (Entity ent : selected) {
			if (ent instanceof BrushEntity) {
				brush = (BrushEntity) ent;
				break;
			}
		}
		return brush;
	}
	
	protected void render(MatrixStack matrices, VertexConsumerProvider vertices, Vec3d cameraPos) {
		if (!RenderSystem.isOnRenderThread()) {
			throw new IllegalStateException("Rendering can only happen from the render thread!");
		}
		
		BrushEntity brush = getSelectedBrush();
		if (brush == null) return;
		
		matrices.push();
		matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		
		Vector3f[] bounds = brush.getBrushBounds();
		Vector3f min = bounds[0];
		if (translation == null) endPoint = bounds[1];
		
		VertexConsumer consumer = vertices.getBuffer(RenderLayer.getLines());
		WorldRenderer.drawBox(matrices, consumer, min.x, min.y, min.z, endPoint.x, endPoint.y, endPoint.z, 1, 1, 1, 1);
		
		matrices.pop();
	}

	@Override
	public Image getIcon() {
		return ICON;
	}

	@Override
	public String getName() {
		return "Resize Tool";
	}

}

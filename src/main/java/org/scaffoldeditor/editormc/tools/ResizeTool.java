package org.scaffoldeditor.editormc.tools;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.engine.world.ScaffoldRenderEvents;
import org.scaffoldeditor.editormc.transformations.Translation;
import org.scaffoldeditor.editormc.transformations.ViewportTransformation;
import org.scaffoldeditor.editormc.ui.ScaffoldUI;
import org.scaffoldeditor.editormc.ui.Viewport;
import org.scaffoldeditor.editormc.util.RaycastUtils;
import org.scaffoldeditor.nbt.util.SingleTypePair;
import org.scaffoldeditor.scaffold.level.entity.BrushEntity;
import org.scaffoldeditor.scaffold.math.MathUtils;
import org.scaffoldeditor.scaffold.operation.ResizeBrushesOperation;

import com.mojang.blaze3d.systems.RenderSystem;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AfterEntities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class ResizeTool implements ViewportTool {
	
	protected class MovingBrush {
		/**
		 * Subject of the brush.
		 */
		public final BrushEntity subject;
		
		public MovingBrush(BrushEntity subject) {
			this.subject = subject;
		}
		
		public MovingBrush(BrushEntity subject, Vector3dc logicalStart, Vector3dc logicalEnd) {
			this(subject);
			Vector3dc[] bounds = subject.getBrushBounds();
			
			double startX = remap(bounds[0].x(), logicalStart.x(), logicalEnd.x(), 0, 1);
			double startY = remap(bounds[0].y(), logicalStart.y(), logicalEnd.y(), 0, 1);
			double startZ = remap(bounds[0].z(), logicalStart.z(), logicalEnd.z(), 0, 1);
			startPercent = new Vector3d(startX, startY, startZ);
			
			double endX = remap(bounds[1].x(), logicalStart.x(), logicalEnd.x(), 0, 1);
			double endY = remap(bounds[1].y(), logicalStart.y(), logicalEnd.y(), 0, 1);
			double endZ = remap(bounds[1].z(), logicalStart.z(), logicalEnd.z(), 0, 1);
			endPercent = new Vector3d(endX, endY, endZ);

		}
		
		/**
		 * The percentage of the way to the normalized logical end this brush starts at,
		 * where [0,0,0] is the logical start and [1,1,1] is the logical end.
		 */
		public Vector3d startPercent = new Vector3d(0, 0, 0);
		
		/**
		 * The percentage of the way to the normalized logical end this brush ends at,
		 * where [0,0,0] is the logical start and [1,1,1] is the logical end.
		 */
		public Vector3d endPercent = new Vector3d(1, 1, 1);
		
		public Vector3dc[] apply(Vector3dc logicalStart, Vector3dc logicalEnd) {
			float startX = (float) remap(startPercent.x, 0, 1, logicalStart.x(), logicalEnd.x());
			float startY = (float) remap(startPercent.y, 0, 1, logicalStart.y(), logicalEnd.y());
			float startZ = (float) remap(startPercent.z, 0, 1, logicalStart.z(), logicalEnd.z());
			Vector3d start = new Vector3d(startX, startY, startZ);
			
			float endX = (float) remap(endPercent.x, 0, 1, logicalStart.x(), logicalEnd.x());
			float endY = (float) remap(endPercent.y, 0, 1, logicalStart.y(), logicalEnd.y());
			float endZ = (float) remap(endPercent.z, 0, 1, logicalStart.z(), logicalEnd.z());
			Vector3d end = new Vector3d(endX, endY, endZ);
			
			if (subject.isGridLocked()) {
				start.floor();
				end.floor();
			}
			
			return new Vector3dc[] { start, end };
		}
	}

	public static final Image ICON = new Image(MoveTool.class.getResourceAsStream("/assets/scaffold/tools/resize.png"));
	protected ScaffoldEditor editor;
	protected Viewport viewport;
	protected ScaffoldUI ui;
	protected Translation translation;
	protected Set<MovingBrush> movingBrushes = new HashSet<>();
	
	protected Vector3d logicalStart = new Vector3d(0, 0, 0);
	protected Vector3d logicalEnd = new Vector3d(0, 0, 0);
	
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
			this.logicalEnd = translation.getTranslation(mouseX, mouseY);
		}
	}
	
	// Viewport transformations
	@Override
	public void onTransformationStarted(ViewportTransformation transform) {
		ViewportTool.super.onTransformationStarted(transform);
		cancelTranslation();
	}
	
	public void beginTranslation() {
		Set<BrushEntity> selected = getSelectedBrushes();
		if (isTranslationActive() || selected.isEmpty()) return;
		
		Vector3dc start;
		Vector3dc end;
		if (selected.size() == 1) {
			Vector3dc[] bounds = selected.iterator().next().getBrushBounds();
			start = bounds[0];
			end = bounds[1];
		} else {
			// Calculate bounding box.
			double minX = Collections.min(selected.stream().map(ent -> ent.getBrushBounds()[0].x()).toList());
			double minY = Collections.min(selected.stream().map(ent -> ent.getBrushBounds()[0].y()).toList());
			double minZ = Collections.min(selected.stream().map(ent -> ent.getBrushBounds()[0].z()).toList());
			start = new Vector3d(minX, minY, minZ);
			
			double maxX = Collections.max(selected.stream().map(ent -> ent.getBrushBounds()[1].x()).toList());
			double maxY = Collections.max(selected.stream().map(ent -> ent.getBrushBounds()[1].y()).toList());
			double maxZ = Collections.max(selected.stream().map(ent -> ent.getBrushBounds()[1].z()).toList());
			end = new Vector3d(maxX, maxY, maxZ);
		}
		
		// Move correct corner;
		Vector3dc corner = getCorner(start, end);
		
		double minX = corner.x() == 1 ? start.x() : end.x();
		double minY = corner.y() == 1 ? start.y() : end.y();
		double minZ = corner.z() == 1 ? start.z() : end.z();
		logicalStart = new Vector3d(minX, minY, minZ);
		
		double maxX = corner.x() == 0 ? start.x() : end.x();
		double maxY = corner.y() == 0 ? start.y() : end.y();
		double maxZ = corner.z() == 0 ? start.z() : end.z();
		logicalEnd = new Vector3d(maxX, maxY, maxZ);
		
		movingBrushes = new HashSet<>();
		for (BrushEntity ent : selected) {
			movingBrushes.add(new MovingBrush(ent, start, end));
		}
		
		translation = new Translation(viewport, logicalEnd);
	}
	
	public void applyTranslation() {
		if (!isTranslationActive()) return;
		
		SingleTypePair<Vector3dc> bounds = MathUtils.normalizeBox(logicalStart, logicalEnd);
		Map<BrushEntity, Vector3dc[]> map = new HashMap<>();
		for (MovingBrush brush : movingBrushes) {
			Vector3dc[] brushBounds = brush.apply(bounds.getFirst(), bounds.getSecond());
			map.put(brush.subject, brushBounds);
		}
		editor.getLevel().getOperationManager().execute(new ResizeBrushesOperation(map, editor.getLevel()));
		
		translation = null;
	}
	
	public void cancelTranslation() {
		translation = null;
	}
	
	public boolean isTranslationActive() {
		return translation != null;
	}
	
	public Set<BrushEntity> getSelectedBrushes() {
		return editor.getSelectedEntities().stream().filter(ent -> ent instanceof BrushEntity)
				.map(ent -> (BrushEntity) ent).collect(Collectors.toSet());
	}
	
	protected void render(MatrixStack matrices, VertexConsumerProvider vertices, Vec3d cameraPos) {
		if (!RenderSystem.isOnRenderThread()) {
			throw new IllegalStateException("Rendering can only happen from the render thread!");
		}
		
		Set<BrushEntity> brushes = getSelectedBrushes();
		if (brushes.isEmpty()) return;
		
		matrices.push();
		matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		
		if (isTranslationActive()) {
			SingleTypePair<Vector3dc> normalized = MathUtils.normalizeBox(logicalStart, logicalEnd);
			for (MovingBrush brush : movingBrushes) {
				Vector3dc[] bounds = brush.apply(normalized.getFirst(), normalized.getSecond());
				drawBox(matrices, vertices, bounds[0], bounds[1]);
			}
		} else {
			for (BrushEntity brush : brushes) {
				Vector3dc[] bounds = brush.getBrushBounds();
				drawBox(matrices, vertices, bounds[0], bounds[1]);
			}
		}
		
		matrices.pop();
	}

	private void drawBox(MatrixStack matrices, VertexConsumerProvider vertices, Vector3dc min, Vector3dc max) {
		VertexConsumer consumer = vertices.getBuffer(RenderLayer.getLines());
		WorldRenderer.drawBox(matrices, consumer, min.x(), min.y(), min.z(), max.x(), max.y(), max.z(), 1, 1, 1, 1);
	}
	
	private double remap(double value, double low1, double high1, double low2, double high2) {
		return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
	}
	
	/**
	 * Get the corner that will be dragged.
	 */
	private Vector3dc getCorner(Vector3dc min, Vector3dc max) {
		MinecraftClient client = MinecraftClient.getInstance();
		
		Vec3d cameraPos = client.getCameraEntity().getCameraPosVec(0);
		Vector3dc camPos = new Vector3d(cameraPos.x, cameraPos.y, cameraPos.z);
		
		Vector3d[] corners = new Vector3d[] {
				new Vector3d(0, 0, 0),
				new Vector3d(0, 0, 1),
				new Vector3d(1, 0, 1),
				new Vector3d(1, 0, 0),
				new Vector3d(0, 1, 0),
				new Vector3d(0, 1, 1),
				new Vector3d(1, 1, 1),
				new Vector3d(1, 1, 0)
		};
		
		double minDistanceSquared = Double.MAX_VALUE;
		for (Vector3d corner : corners) {
			Vector3d cornerVal = getCorner(corner, min, max);
			double distanceSquared = camPos.sub(cornerVal, new Vector3d()).lengthSquared();
			if (distanceSquared < minDistanceSquared) {
				minDistanceSquared = distanceSquared;
			}
		}
		
		double distance = Math.sqrt(minDistanceSquared);
		int viewWidth = (int) viewport.getParent().getWidth();
		int viewHeight = (int) viewport.getParent().getHeight();
		
		Vec3d raycast = RaycastUtils.raycastPixel(viewport.getMouseX(), viewport.getMouseY(), viewWidth, viewHeight, distance, false).getPos();
		Vector3d targetPos = new Vector3d(raycast.x, raycast.y, raycast.z);
		
		minDistanceSquared = Double.MAX_VALUE;
		Vector3dc minCorner = null;
		for (Vector3dc corner : corners) {
			Vector3dc cornerVal = getCorner(corner, min, max);
			double distanceSquered = targetPos.sub(cornerVal, new Vector3d()).lengthSquared();
			if (distanceSquered < minDistanceSquared) {
				minDistanceSquared = distanceSquered;
				minCorner = corner;
			}
		}
		
		return minCorner;
	}
	
	private Vector3d getCorner(Vector3dc corner, Vector3dc min, Vector3dc max) {
		double x = corner.x() == 0 ? min.x() : max.x();
		double y = corner.y() == 0 ? min.y() : max.y();
		double z = corner.z() == 0 ? min.z() : max.z();
		return new Vector3d(x, y, z);
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

package org.scaffoldeditor.editormc.transformations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.scaffoldeditor.editormc.ui.ScaffoldUI;
import org.scaffoldeditor.editormc.util.RaycastUtils;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.operation.MoveEntitiesOperation;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class Translation implements ViewportTransformation {
	
	private ScaffoldUI ui;
	private double distance;
	private Map<Entity, Vector3f> entities;
	private Vector3f target = new Vector3f(0, 0, 0);
	private String lock = "";
	private Vector3f startPos = new Vector3f(0, 0, 0);
	public boolean castMode = false;

	public Translation(ScaffoldUI ui) {
		this.ui = ui;
	}
	
	@Override
	public void activate() {
		Set<Entity> selected = ui.getEditor().getSelectedEntities();
		Vector3f avg = new Vector3f(0, 0, 0);
		for (Entity ent : selected) {
			avg = avg.add(ent.getPosition());
		}
		avg = avg.divide(selected.size());
		
		int mouseX = ui.getViewport().getMouseX();
		int mouseY = ui.getViewport().getMouseY();
				
		distance = avg.subtract(ui.getViewport().getCameraPos()).length();
		startPos = parseVector(performRaycast(mouseX, mouseY).getPos());
		if (ui.getViewportHeader().snapToGrid()) startPos = startPos.floor().toFloat();
		target = startPos;
		entities = new HashMap<>();
		for (Entity ent : selected) {
			entities.put(ent, ent.getPosition().subtract(startPos));
		}
	}
	
	@Override
	public void onMouseMoved(int x, int y) {
		ViewportTransformation.super.onMouseMoved(x, y);
		
		target = parseVector(performRaycast(x, y).getPos());
		if (ui.getViewportHeader().snapToGrid()) target = target.floor().toFloat();
		target = applyLock(target);
		for (Entity ent : entities.keySet()) {
			ent.setPreviewPosition(target.add(entities.get(ent)));
		}
	}
	
	@Override
	public void onKeyPressed(KeyEvent event) {
		ViewportTransformation.super.onKeyPressed(event);
		
		if (event.getCode() == KeyCode.X) {
			if (event.isShiftDown()) {
				setLock("YZ");
			} else {
				setLock("X");
			}
			event.consume();
		} else if (event.getCode() == KeyCode.Y) {
			if (event.isShiftDown()) {
				setLock("XZ");
			} else {
				setLock("Y");
			}
		} else if (event.getCode() == KeyCode.Z) {
			if (event.isShiftDown()) {
				setLock("XY");
			} else {
				setLock("Z");
			}
		} else if (event.getCode() == KeyCode.CONTROL) {
			castMode = false;
		}
	}
	
	@Override
	public void onKeyReleased(KeyEvent event) {
		ViewportTransformation.super.onKeyReleased(event);
		
		if (event.getCode() == KeyCode.CONTROL) {
			castMode = true;
		}
	}

	@Override
	public void cancel() {
		for (Entity ent : entities.keySet()) {
			ent.disableTransformPreview();
		}
	}

	@Override
	public void apply() {
		Map<Entity, Vector3f> targets = new HashMap<>();
		entities.keySet().stream().forEach(ent -> {
			targets.put(ent, target.add(entities.get(ent)));
			ent.disableTransformPreview();
		});
		
		Level level = ui.getEditor().getLevel();
		level.getOperationManager().execute(new MoveEntitiesOperation(targets, level));
	}
	
	public void setLock(String lock) {
		this.lock = lock.toUpperCase();
	}
	
	public String getLock() {
		return lock;
	}
	
	private Vector3f applyLock(Vector3f in) {
		if (lock.length() == 0) return in;
		if (!lock.contains("X")) in = new Vector3f(startPos.x, in.y, in.z);
		if (!lock.contains("Y")) in = new Vector3f(in.x, startPos.y, in.z);
		if (!lock.contains("Z")) in = new Vector3f(in.x, in.y, startPos.z);
		return in;
	}
	
	private HitResult performRaycast(int x, int y) {
		Pane pane = ui.getViewport().getParent();
		if (castMode) {
			return RaycastUtils.raycastPixel(x, y, (int) pane.getWidth(), (int) pane.getHeight(), 500, true);
		} else {
			return RaycastUtils.raycastPixel(x, y, (int) pane.getWidth(), (int) pane.getHeight(), distance, false);
		}
	}
	
	private Vector3f parseVector(Vec3d in) {
		return new Vector3f((float) in.x, (float) in.y, (float) in.z);
	}
}

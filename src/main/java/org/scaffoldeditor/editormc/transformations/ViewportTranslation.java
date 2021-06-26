package org.scaffoldeditor.editormc.transformations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.scaffoldeditor.editormc.ui.ScaffoldUI;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.operation.MoveEntitiesOperation;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ViewportTranslation implements ViewportTransformation {
	
	private ScaffoldUI ui;
	private Map<Entity, Vector3f> entities;
	private Vector3f target = new Vector3f(0, 0, 0);
	private Translation translation;
	
	public ViewportTranslation(ScaffoldUI ui) {
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
				
		Vector3f startPos = avg;
		if (ui.getViewportHeader().snapToGrid()) startPos = startPos.floor().toFloat();
		
		target = startPos;
		entities = new HashMap<>();
		for (Entity ent : selected) {
			entities.put(ent, ent.getPosition().subtract(startPos));
		}
		translation = new Translation(ui.getViewport(), startPos);
	}
	
	@Override
	public void onMouseMoved(int x, int y) {
		ViewportTransformation.super.onMouseMoved(x, y);
		if (translation == null) return;
		
		target = translation.getTranslation(x, y);
		if (ui.getViewportHeader().snapToGrid()) target = target.floor().toFloat();
		for (Entity ent : entities.keySet()) {
			ent.setPreviewPosition(target.add(entities.get(ent)));
		}
	}
	
	@Override
	public void onKeyPressed(KeyEvent event) {
		ViewportTransformation.super.onKeyPressed(event);
		if (translation == null) return;
		
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
		} else if (event.getCode() == KeyCode.Z) {
			if (event.isShiftDown()) {
				translation.setLock("XY");
			} else {
				translation.setLock("Z");
			}
		} else if (event.getCode() == KeyCode.CONTROL) {
			translation.castMode = false;
		}
	}
	
	@Override
	public void onKeyReleased(KeyEvent event) {
		ViewportTransformation.super.onKeyReleased(event);
		if (translation == null) return;
		
		if (event.getCode() == KeyCode.CONTROL) {
			translation.castMode = true;
		}
	}

	@Override
	public void cancel() {
		for (Entity ent : entities.keySet()) {
			ent.disableTransformPreview();
		}
		translation = null;
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
		translation = null;
	}

}

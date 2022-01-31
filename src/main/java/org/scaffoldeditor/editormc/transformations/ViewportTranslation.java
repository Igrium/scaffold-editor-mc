package org.scaffoldeditor.editormc.transformations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.scaffoldeditor.editormc.EditorOperationManager;
import org.scaffoldeditor.editormc.ui.ScaffoldUI;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.operation.MoveEntitiesOperation;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ViewportTranslation implements ViewportTransformation {
	
	private ScaffoldUI ui;
	private Map<Entity, Vector3dc> entities;
	private Vector3dc target = new Vector3d();
	private Translation translation;
	
	public ViewportTranslation(ScaffoldUI ui) {
		this.ui = ui;
	}
	
	@Override
	public void activate() {
		Set<Entity> selected = ui.getEditor().getSelectedEntities();
		Vector3d startPos = new Vector3d();
		for (Entity ent : selected) {
			startPos.add(ent.getPosition());
		}
		startPos.div(selected.size());
				
		if (ui.getViewportHeader().snapToGrid()) startPos.floor();
		
		target = startPos;
		entities = new HashMap<>();
		for (Entity ent : selected) {
			entities.put(ent, ent.getPosition().sub(startPos, new Vector3d()));
		}
		translation = new Translation(ui.getViewport(), startPos);
	}
	
	@Override
	public void onMouseMoved(int x, int y) {
		ViewportTransformation.super.onMouseMoved(x, y);
		if (translation == null) return;
		
		Vector3d subject = translation.getTranslation(x, y);
		target = subject;
		if (ui.getViewportHeader().snapToGrid()) subject.floor();
		for (Entity ent : entities.keySet()) {
			ent.setPreviewPosition(target.add(entities.get(ent), new Vector3d()));
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
		Map<Entity, Vector3dc> targets = new HashMap<>();
		entities.keySet().stream().forEach(ent -> {
			targets.put(ent, target.add(entities.get(ent), new Vector3d()));
			ent.disableTransformPreview();
		});
		
		EditorOperationManager.getInstance().runOperation(new MoveEntitiesOperation(targets));
		translation = null;
	}

}

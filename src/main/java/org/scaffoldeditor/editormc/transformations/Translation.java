package org.scaffoldeditor.editormc.transformations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.editormc.ui.ScaffoldUI;
import org.scaffoldeditor.editormc.util.RaycastUtils;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.operation.MoveEntitiesOperation;
import org.scaffoldeditor.scaffold.operation.MoveEntityOperation;

import javafx.scene.layout.Pane;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class Translation implements ViewportTransformation {
	
	private ScaffoldUI ui;
	private double distance;
	private Map<Entity, Vector3f> entities;
	Vector3f target = new Vector3f(0, 0, 0);

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
		
		LogManager.getLogger().info("Average point: "+avg);
		
		distance = avg.subtract(ui.getViewport().getCameraPos()).length();
		LogManager.getLogger().info("Distance: "+distance);
		Vector3f initialPoint = parseVector(performRaycast(mouseX, mouseY).getPos());
		target = initialPoint;
		LogManager.getLogger().info("Raycast result: "+target);
		entities = new HashMap<>();
		for (Entity ent : selected) {
			entities.put(ent, ent.getPosition().subtract(initialPoint));
		}
	}
	
	@Override
	public void onMouseMoved(int x, int y) {
		ViewportTransformation.super.onMouseMoved(x, y);
		
		target = parseVector(performRaycast(x, y).getPos());
		for (Entity ent : entities.keySet()) {
			ent.setPreviewPosition(target.add(entities.get(ent)));
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
	
	private HitResult performRaycast(int x, int y) {
		Pane pane = ui.getViewport().getParent();
		return RaycastUtils.raycastPixel(x, y, (int) pane.getWidth(), (int) pane.getHeight(), distance, false);
	}
	
	private Vector3f parseVector(Vec3d in) {
		return new Vector3f((float) in.x, (float) in.y, (float) in.z);
	}
}

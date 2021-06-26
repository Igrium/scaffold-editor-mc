package org.scaffoldeditor.editormc.transformations;

import org.scaffoldeditor.editormc.ui.Viewport;
import org.scaffoldeditor.editormc.util.RaycastUtils;
import org.scaffoldeditor.nbt.math.Vector3f;

import javafx.scene.layout.Pane;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

/**
 * Interprets 2d mouse movements as a 3d transformation.
 * @author Igrium
 */
public class Translation {
	public final Viewport viewport;
	public boolean castMode = false;
	private String lock = "";
	private double distance;
	private Vector3f startPos;
	
	public Translation(Viewport viewport, Vector3f startPos) {
		this.viewport = viewport;
		this.startPos = startPos;
		distance = startPos.subtract(viewport.getCameraPos()).length();
	}
	
	public Vector3f getTranslation(int mouseX, int mouseY) {
		Vector3f target = parseVector(performRaycast(mouseX, mouseY).getPos());
		return applyLock(target);
	}

	public String getLock() {
		return lock;
	}

	public void setLock(String lock) {
		this.lock = lock;
	}
	
	public Vector3f getStartPos() {
		return startPos;
	}
	
	private Vector3f applyLock(Vector3f in) {
		if (lock.length() == 0) return in;
		if (!lock.contains("X")) in = new Vector3f(startPos.x, in.y, in.z);
		if (!lock.contains("Y")) in = new Vector3f(in.x, startPos.y, in.z);
		if (!lock.contains("Z")) in = new Vector3f(in.x, in.y, startPos.z);
		return in;
	}
	
	private HitResult performRaycast(int x, int y) {
		Pane pane = viewport.getParent();
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

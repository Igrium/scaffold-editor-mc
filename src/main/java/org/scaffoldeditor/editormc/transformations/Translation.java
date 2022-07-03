package org.scaffoldeditor.editormc.transformations;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.scaffoldeditor.editormc.ui.Viewport;
import org.scaffoldeditor.editormc.util.RaycastUtils;

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
	private Vector3dc startPos;
	
	public Translation(Viewport viewport, Vector3dc startPos) {
		this.viewport = viewport;
		this.startPos = startPos;
		distance = startPos.sub(viewport.getCameraPos(), new Vector3d()).length();
	}
	
	public Vector3d getTranslation(int mouseX, int mouseY) {
		Vector3dc target = parseVector(performRaycast(mouseX, mouseY).getPos());
		return applyLock(target);
	}

	public String getLock() {
		return lock;
	}

	public void setLock(String lock) {
		this.lock = lock;
	}
	
	public Vector3dc getStartPos() {
		return startPos;
	}
	
	private Vector3d applyLock(Vector3dc in) {
		Vector3d val = new Vector3d(in);
		if (lock.length() == 0) return val;
		if (!lock.contains("X")) val.x = startPos.x();
		if (!lock.contains("Y")) val.y = startPos.y();
		if (!lock.contains("z")) val.z = startPos.z();
		return val;
	}
	
	private HitResult performRaycast(int x, int y) {
		Pane pane = viewport.getParent();
		if (castMode) {
			return RaycastUtils.raycastPixel(x, y, (int) pane.getWidth(), (int) pane.getHeight(), true);
		} else {
			return RaycastUtils.raycastPixel(x, y, (int) pane.getWidth(), (int) pane.getHeight(), false);
		}
	}
	
	private Vector3d parseVector(Vec3d in) {
		return new Vector3d(in.x, in.y, in.z);
	}
}

package org.scaffoldeditor.editormc.gismos;

import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.ui.Viewport;
import org.scaffoldeditor.editormc.util.RaycastUtils;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.math.Vector;
import org.scaffoldeditor.scaffold.operation.MoveEntityOperation;

import javafx.scene.layout.Pane;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class TranslationGismo implements TransformationGismo {
	
	public final Pane pane;
	public final Viewport viewport;
	/** Target distance to the entity */
	protected float distanceSquared;
	protected Entity entity;
	protected Vector targetOffset;
	protected Vector startPosition;
	
	/**
	 * Create a translation gizmo.
	 * @param pane Viewport pane.
	 */
	public TranslationGismo(Viewport viewport) {
		this.pane = viewport.getParent();
		this.viewport = viewport;
	}

	@Override
	public void activate(Entity entity, int x, int y) {
		this.entity = entity;
		distanceSquared = Vector.subtract(entity.getPosition(), viewport.getCameraPos()).lengthSquared();
		startPosition = entity.getPosition();
		
		HitResult initial = performRaycast(x, y);
		targetOffset = Vector.subtract(entity.getPosition(), parseVector(initial.getPos()));	
	}

	@Override
	public void mouseMoved(int x, int y) {
		if (entity != null) {
			HitResult target = performRaycast(x, y);
			entity.setPosition(Vector.add(parseVector(target.getPos()), targetOffset));
		}
	}

	@Override
	public void apply() {
		ScaffoldEditor.getInstance().getLevel().getOperationManager()
				.execute(new MoveEntityOperation(entity, startPosition, entity.getPosition()));
		cleanUp();
	}

	@Override
	public void cancel() {
		entity.setPosition(startPosition);
		cleanUp();
	}
	
	private void cleanUp() {
		distanceSquared = 0;
		entity = null;
		startPosition = null;
		targetOffset = null;
	}
	
	private HitResult performRaycast(int x, int y) {
		return RaycastUtils.raycastPixel(x, y, (int) pane.getWidth(), (int) pane.getHeight(), Math.sqrt(distanceSquared), false);
	}
	
	private Vector parseVector(Vec3d in) {
		return new Vector((float) in.x, (float) in.y, (float) in.z);
	}

}

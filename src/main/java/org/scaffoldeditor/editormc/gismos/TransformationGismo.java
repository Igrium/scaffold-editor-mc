package org.scaffoldeditor.editormc.gismos;

import org.scaffoldeditor.scaffold.level.entity.Entity;

/**
 * Represents the login behind a transformation gismo.
 * @author Igrium
 */
public interface TransformationGismo {
	/**
	 * Activate the gisso.
	 * @param entity Target entity.
	 * @param x Initial mouse X.
	 * @param y Initial mouse Y.
	 */
	public void activate(Entity entity, int x, int y);
	public void mouseMoved(int x, int y);
	public void apply();
	public void cancel();
}

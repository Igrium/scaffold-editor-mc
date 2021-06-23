package org.scaffoldeditor.editormc.transformations;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Contains the code for a viewport transformation (for example, what happens
 * when you press the 'g' button).
 * 
 * @author Igrium
 */
public interface ViewportTransformation {
	/**
	 * Called when this transformation is activated.
	 */
	void activate();
	
	/**
	 * Called when this transformation is canceled.
	 */
	void cancel();
	
	/**
	 * Called when this transformation is applied.
	 */
	void apply();
	
	/**
	 * Called when the transformation is active and a mouse button (other than right click) is pressed.
	 */
	default void onMousePressed(MouseEvent event) {};
	
	/**
	 * Called when the transformation is active and the mouse is clicked.
	 */
	default void onMouseClicked(MouseEvent event) {};
	
	/**
	 * Called when the transformation is active and a mouse button (other than right click) is released.
	 */
	default void onMouseReleased(MouseEvent event) {};
	
	/**
	 * Called when the transformation is active and the mouse is moved.
	 * @param x New x coordinate of the mouse (relative to the viewport)
	 * @param y New y cooddinate of the mouse (relative to the viewport)
	 */
	default void onMouseMoved(int x, int y) {};
	
	/**
	 * Called when the transformation is active and the mouse is dragged.
	 * <br>
	 * <b>Note:</b> See JavaFX documentation for the difference between moving and dragging.
	 */
	default void onMouseDragged(MouseEvent event) {};
	
	/**
	 * Called when the transformation is active and a key is pressed.
	 */
	default void onKeyPressed(KeyEvent event) {};
	
	/**
	 * Called when the transformation is active and a key is released.
	 */
	default void onKeyReleased(KeyEvent event) {};
	
	/**
	 * Called when the transformation is active and a key is typed.
	 */
	default void onKeyTyped(KeyEvent event) {};
	
	/**
	 * Whether this transformation should override the cursor image.
	 */
	default boolean overrideCursor() {
		return false;
	}
	
	/**
	 * Get the image this transformation should use for the cursor. Only used if
	 * {@link #overrideCursor()} returns true.
	 */
	default Cursor getCursor() {
		return ImageCursor.DEFAULT;
	}
}

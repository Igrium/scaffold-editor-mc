package org.scaffoldeditor.editormc.tools;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public interface ViewportTool {
	
	/**
	 * Called when the tool is selected from the toolbar.
	 */
	void onActivate();
	
	/**
	 * Called when the tool is deselected.
	 */
	void onDeactivate();
	
	/**
	 * Called when the tool is active and a mouse button (other than right click) is pressed.
	 */
	void onMousePressed(MouseEvent e);
	
	/**
	 * Called when the tool is active and the mouse is clicked.
	 */
	void onMouseClicked(MouseEvent e);
	
	/**
	 * Called when the tool is active and a mouse button (other than right click) is released.
	 */
	void onMouseReleased(MouseEvent e);
	
	/**
	 * Called when the tool is active and the mouse is moved.
	 * @param x New x coordinate of the mouse (relative to the viewport)
	 * @param y New y cooddinate of the mouse (relative to the viewport)
	 */
	void onMouseMoved(int x, int y);
	
	/**
	 * Called when the tool is active and the mouse is dragged.
	 * <br>
	 * <b>Note:</b> See JavaFX documentation for the difference between moving and dragging.
	 */
	void onMouseDragged(MouseEvent e);
	
	/**
	 * Called when the tool is active and a key is pressed.
	 */
	void onKeyPressed(KeyEvent e);
	
	/**
	 * Called when the tool is active and a key is released.
	 */
	void onKeyReleased(KeyEvent e);
	
	/**
	 * Called when the tool is active and a key is typed.
	 */
	void onKeyTyped(KeyEvent e);
	
	/**
	 * Get the icon this tool should use.
	 * @return Icon image. Reccomended size: 16x16
	 */
	Image getIcon();
	
	/**
	 * Whether this tool should override the cursor image.
	 */
	default boolean overrideCursor() {
		return false;
	}
	
	/**
	 * Get the image this tool should use for the cursor.
	 */
	default Cursor getCursor() {
		return new ImageCursor(getIcon());
	}
}

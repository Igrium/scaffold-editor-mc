package org.scaffoldeditor.editormc.tools;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public interface ViewportTool {
	
	/**
	 * Called when the tool is selected from the toolbar.
	 * @return 
	 */
	default void onActivate() {}
	
	/**
	 * Called when the tool is deselected.
	 */
	default void onDeactivate() {};
	
	/**
	 * Called when the tool is active and a mouse button (other than right click) is pressed.
	 */
	default void onMousePressed(MouseEvent event) {};
	
	/**
	 * Called when the tool is active and the mouse is clicked.
	 */
	default void onMouseClicked(MouseEvent event) {};
	
	/**
	 * Called when the tool is active and a mouse button (other than right click) is released.
	 */
	default void onMouseReleased(MouseEvent event) {};
	
	/**
	 * Called when the tool is active and the mouse is moved.
	 * @param x New x coordinate of the mouse (relative to the viewport)
	 * @param y New y cooddinate of the mouse (relative to the viewport)
	 */
	default void onMouseMoved(int x, int y) {};
	
	/**
	 * Called when the tool is active and the mouse is dragged.
	 * <br>
	 * <b>Note:</b> See JavaFX documentation for the difference between moving and dragging.
	 */
	default void onMouseDragged(MouseEvent event) {};
	
	/**
	 * Called when the tool is active and a key is pressed.
	 */
	default void onKeyPressed(KeyEvent event) {};
	
	/**
	 * Called when the tool is active and a key is released.
	 */
	default void onKeyReleased(KeyEvent event) {};
	
	/**
	 * Called when the tool is active and a key is typed.
	 */
	default void onKeyTyped(KeyEvent event) {};
	
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
	
	/**
	 * Get the tool properties UI.
	 * @return Root node of the tool properties UI.
	 */
	default Node getPropertiesPane() {
		return new Pane();
	}
	
	/**
	 * Get the friendly name of this tool.
	 */
	String getName();
}

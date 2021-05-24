package org.scaffoldeditor.editormc.tools;

import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class SelectTool implements ViewportTool {
	
	public final Image icon;
	
	public SelectTool() {
		icon = new Image(getClass().getResourceAsStream("/assets/scaffold/tools/select.png"));
	}

	@Override
	public void onActivate() {
	}

	@Override
	public void onDeactivate() {
	}

	@Override
	public void onMousePressed(MouseEvent e) {
	}

	@Override
	public void onMouseReleased(MouseEvent e) {
	}

	@Override
	public void onMouseMoved(int x, int y) {
	}

	@Override
	public void onMouseDragged(MouseEvent e) {
	}

	@Override
	public void onKeyPressed(KeyEvent e) {
	}

	@Override
	public void onKeyReleased(KeyEvent e) {
	}

	@Override
	public void onKeyTyped(KeyEvent e) {
	}

	@Override
	public Image getIcon() {
		return icon;
	}

}

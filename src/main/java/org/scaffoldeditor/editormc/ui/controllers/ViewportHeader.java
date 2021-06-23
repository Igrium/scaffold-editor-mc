package org.scaffoldeditor.editormc.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;

public class ViewportHeader {
	@FXML
	public ToggleButton snapButton;
	
	public boolean snapToGrid() {
		return snapButton.isSelected();
	}
	
	public void setSnapToGrid(boolean value) {
		snapButton.setSelected(value);
	}
}

package org.scaffoldeditor.editormc.ui.controllers;

import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.scaffold.entity.Entity;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;

public class ViewportHeader {
	@FXML
	public ToggleButton snapButton;
	
	public void init(ScaffoldEditor editor) {
		editor.onUpdateSelection(event -> {
			for (Entity ent : event.newSelection) {
				if (ent.isGridLocked()) {
					setSnapToGrid(true);
					return;
				}
			}
			setSnapToGrid(false);
		});
	}
	
	public boolean snapToGrid() {
		return snapButton.isSelected();
	}
	
	public void setSnapToGrid(boolean value) {
		snapButton.setSelected(value);
	}
}

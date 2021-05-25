package org.scaffoldeditor.editormc.ui.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class EntityToolPropertiesController {
	@FXML
	private TextField entityClassField;
	
	@FXML
	private TextField nameField;
	
	@FXML
	private Label warningLabel;
	
	@FXML
	private CheckBox snapToBlock;
	
	@FXML
	public void browse() {
		EntityBrowser browser;
		try {
			browser = EntityBrowser.open();
			browser.onEntitySelected(e -> {
				entityClassField.setText(e.selectedEntityType);
			});
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
	}
	
	public void setWarningText(String text) {
		warningLabel.setText(text);
	}
	
	public String getEnteredClass() {
		return entityClassField.getText();
	}
	
	public String getEnteredName() {
		return nameField.getText();
	}
	
	public boolean shouldSnapToBlock() {
		return snapToBlock.isSelected();
	}
}

package org.scaffoldeditor.editormc.ui.controllers;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.joml.Vector3d;
import org.scaffoldeditor.editormc.tools.EntityTool;
import javafx.application.Platform;
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
	private TextField xField;
	@FXML
	private TextField yField;
	@FXML
	private TextField zField;
	
	private EntityTool parentTool;
	
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
		Platform.runLater(() -> {
			warningLabel.setText(text);
		});
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
	
	public void setParent(EntityTool parent) {
		this.parentTool = parent;
	}
	
	/**
	 * Spawn the entity at the entered coordinates.
	 */
	@FXML
	public void spawn() {
		if (xField.getText().length() == 0 || yField.getText().length() == 0 || zField.getText().length() == 0) {
			return;
		}
		try {
			float x = Float.valueOf(xField.getText());
			float y = Float.valueOf(yField.getText());
			float z = Float.valueOf(zField.getText());
			
			parentTool.spawn(new Vector3d(x, y, z), shouldSnapToBlock());
		} catch (NumberFormatException e) {
			LogManager.getLogger().error("Malformatted vector!");
			return;
		}
	}
}

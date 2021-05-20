package org.scaffoldeditor.editormc.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class FXMLEntityEditorController {
	
	@FXML
	public Button applyButton;
	
	@FXML
	public Button applyAndCloseButton;
	
	@FXML
	public GridPane attributePane;
	
	@FXML
	public TextField nameField;
	
	@FXML
	public Label entityTypeLabel;
}

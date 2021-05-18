package org.scaffoldeditor.editormc.ui.controllers;

import java.io.File;
import java.nio.file.Paths;

import org.scaffoldeditor.editormc.ui.ScaffoldUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class FXMLSplashScreenController {
	@FXML
	private Button openButton;
	
	@FXML
	private TextField textField;
	
	@FXML
	private void browse() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Open or Create project");
		File initial = new File(textField.getText());
		if (initial.isDirectory()) directoryChooser.setInitialDirectory(initial);		
		
		File file = directoryChooser.showDialog(textField.getScene().getWindow());
		if (file != null) {
			textField.setText(file.getPath());
			openButton.setDisable(false);
		}
	}
	
	@FXML
	private void updateText() {
		openButton.setDisable(textField.getText().length() == 0);
	}
	
	@FXML
	private void open() {
		((Stage) textField.getScene().getWindow()).close();
		ScaffoldUI.getInstance().getEditor().openProject(Paths.get(textField.getText()));
		ScaffoldUI.getInstance().openLevel();
	}
}

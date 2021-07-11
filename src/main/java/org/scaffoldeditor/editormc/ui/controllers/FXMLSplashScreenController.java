package org.scaffoldeditor.editormc.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.scaffoldeditor.editormc.Config;
import org.scaffoldeditor.editormc.ScaffoldEditor;
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
	private void initialize() {
		textField.setText(Config.getValue("technical.project.defaultProject"));
		updateText();
	}
	
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
		Config.setValue("technical.project.defaultProject", "StringSetting", textField.getText());
		
		try {
			Config.save(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		((Stage) textField.getScene().getWindow()).close();
		ScaffoldEditor.getInstance().openProject(Paths.get(textField.getText()));
		ScaffoldEditor.getInstance().newLevel();
	}
}

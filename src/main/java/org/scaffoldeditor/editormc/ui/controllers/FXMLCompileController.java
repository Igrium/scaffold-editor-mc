package org.scaffoldeditor.editormc.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.scaffold.compile.Compiler.CompileEndStatus;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class FXMLCompileController {
		
	@FXML
	private TextField compilePathField;
	@FXML
	private CheckBox enableCheats;
	@FXML
	private CheckBox fullCompile;
	@FXML
	private CheckBox autoLaunch;
	
	@FXML
	private Button launchButton;
	@FXML
	private Button compileButton;	
	private ScaffoldEditor editor;
	private Level level;
	
	protected Stage stage;
	
	boolean isCompiling = false;
	
	@FXML
	private void initialize() {
		editor = ScaffoldEditor.getInstance();
		level = editor.getLevel();
		
		if (editor.worldpath_cache != null) {
			compilePathField.setText(editor.worldpath_cache);
		} else {
			compilePathField.setText(
					editor.getProject().assetManager().getAbsolutePath("game/saves/" + level.getName()).toString());
		}
		
		compilePathField.focusedProperty().addListener(e -> {
			updateButton();
		});
		updateButton();
	}
	
	public void updateButton() {
		if (Paths.get(compilePathField.getText()).resolve("level.dat").toFile().isFile()) {
			launchButton.setDisable(false);
		} else {
			launchButton.setDisable(true);
		}
	}
	
	@FXML
	public void launch() {
		LogManager.getLogger().info("If this was implemented, we would launch the game to: "+compilePathField.getText());
	}
	
	@FXML
	public void openBrowser() {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Choose world folder");
		if (new File(compilePathField.getText()).isDirectory()) {
			chooser.setInitialDirectory(new File(compilePathField.getText()));
		}
		File choosenDirectory = chooser.showDialog(stage);
		if (choosenDirectory == null) return;
		if (choosenDirectory.getName().equals("saves")) {
			Alert a = new Alert(AlertType.WARNING, "Target directory should be the world folder itself; not the saves folder.");
			a.show();
		} else {
			compilePathField.setText(choosenDirectory.toString());
			updateButton();
		}
	}
	
	@FXML
	public void compile() {
		if (isCompiling) return;
		
		CompileProgressUI controller;
		
		try {
			controller = CompileProgressUI.open(stage);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		controller.onFinishedCompile(c -> {
			if (c.endStatus == CompileEndStatus.FINISHED) {
				if (autoLaunch.isSelected()) {
					launch();
				}
				
				editor.worldpath_cache = compilePathField.getText();
				updateButton();
			}
		});
		
		Map<String, Attribute<?>> args = new HashMap<>();
		args.put("cheats", new BooleanAttribute(enableCheats.isSelected()));
		args.put("full", new BooleanAttribute(fullCompile.isSelected()));
		
		controller.compile(level, Paths.get(compilePathField.getText()), args);
	}
	
	public static FXMLCompileController open(Window parent) throws IOException {
		FXMLLoader loader = new FXMLLoader(CompileProgressUI.class.getResource("/assets/scaffold/ui/compile_window.fxml"));
		Parent root = loader.load();
		
		Scene scene = new Scene(root, 400, 300);
		Stage stage = new Stage();
		stage.setTitle("Compile Level");
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(parent);
		FXMLCompileController controller = loader.getController();
		controller.stage = stage;
		stage.show();
			
		return controller;
	}
}


package org.scaffoldeditor.editormc.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.scaffold.compile.Compiler.CompileEndStatus;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.LevelData.GameType;
import org.scaffoldeditor.scaffold.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.entity.attribute.BooleanAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.EnumAttribute;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.minecraft.client.MinecraftClient;

public class FXMLCompileController {
		
	@FXML
	private TextField compilePathField;
	@FXML
	private ChoiceBox<String> gamemodeBox;
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
	private MinecraftClient client = MinecraftClient.getInstance();
	
	@FXML
	private void initialize() {
		editor = ScaffoldEditor.getInstance();
		level = editor.getLevel();
		
		String[] gamemodes = new String[] { "Adventure", "Creative", "Survival", "Spectator" };
		gamemodeBox.getItems().addAll(gamemodes);
		gamemodeBox.getSelectionModel().select(0);
		
		gamemodeBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				enableCheats.setSelected(newValue.equals("Creative") || newValue.equals("Spectator"));
			}
		});
		
		JSONObject levelCache = editor.getLevelCache();
		if (levelCache.has("export_path")) {
			compilePathField.setText(levelCache.getString("export_path"));
		} else {
			String path = editor.getProject().assetManager().getAbsolutePath("game/saves/" + level.getName()).toString();
			compilePathField.setText(path);
		}
		
		compilePathField.textProperty().addListener(e -> {
			updateButton();
		});
		updateButton();
	}
	
	public void updateButton() {
		Path compilePath = Paths.get(compilePathField.getText()).resolve("level.dat");
		
		Path saves = client.runDirectory.toPath().resolve("saves").normalize();
		if (compilePath.startsWith(saves) && compilePath.toFile().isFile()) {
			launchButton.setDisable(false);
		} else {
			launchButton.setDisable(true);
		}
	}
	
	@FXML
	public void launch() {
		editor.openWorld(new File(compilePathField.getText()).getName());
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
		editor.getLevelCache().put("export_path", compilePathField.getText());
		
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
				
				updateButton();
			}
		});
		
		Map<String, Attribute<?>> args = new HashMap<>();
		args.put("cheats", new BooleanAttribute(enableCheats.isSelected()));
		args.put("full", new BooleanAttribute(fullCompile.isSelected()));
		args.put("gameType", new EnumAttribute<GameType>(
				GameType.valueOf(gamemodeBox.getSelectionModel().getSelectedItem().toUpperCase())));
		
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


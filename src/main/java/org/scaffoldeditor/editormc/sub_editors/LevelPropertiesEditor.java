package org.scaffoldeditor.editormc.sub_editors;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.editormc.sub_editors.nbt.NBTEditorController;
import org.scaffoldeditor.editormc.ui.controllers.CompileProgressUI;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.operation.ChangeLevelPropertiesOperation;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;

public class LevelPropertiesEditor {
	@FXML
	private TextField prettyNameField;
	
	@FXML
	private TableView<String> gamerulesTable;
	
	private Level level;
	private CompoundTag data;
	private Stage stage;
		
	public void load(Level level) {
		this.level = level;
		prettyNameField.setText(level.getPrettyName());
		this.data = level.levelData().getData().clone();
	}
	
	@FXML
	public void editNBT() {
		NBTEditorController editor = NBTEditorController.openPopup(stage);
		editor.loadNBT(new NamedTag("Data", data));
		editor.onUpdateNBT(tag -> {
			data = (CompoundTag) tag.getTag();
		});
	}
	
	@FXML
	public void apply() {
		level.getOperationManager().execute(new ChangeLevelPropertiesOperation(level, prettyNameField.getText(), data));
		stage.close();
	}
	
	@FXML
	public void cancel() {
		stage.close();
	}
	
	public static LevelPropertiesEditor open(Window parent, Level level) {
		FXMLLoader loader = new FXMLLoader(CompileProgressUI.class.getResource("/assets/scaffold/ui/level_meta_editor.fxml"));
		Parent root;
		try {
			root = loader.load();
		} catch (IOException e) {
			throw new AssertionError("Error loading level meta editor!", e);
		}
		LogManager.getLogger().info("Opening level properties editor.");
		Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setTitle("Edit Level Properties");
		stage.setScene(scene);
		LevelPropertiesEditor controller = loader.getController();
		
		controller.stage = stage;
		controller.load(level);
		stage.show();
		
		return controller;
	}
}

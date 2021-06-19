package org.scaffoldeditor.editormc.sub_editors.nbt;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.editormc.ui.controllers.CompileProgressUI;
import org.scaffoldeditor.scaffold.util.event.EventDispatcher;
import org.scaffoldeditor.scaffold.util.event.EventListener;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.Tag;

public class SNBTEditor {
	@FXML
	private TextArea textArea;
	
	private Stage stage;
	private EventDispatcher<Tag<?>> finishedDispatcher = new EventDispatcher<>();
	
	public void loadTag(Tag<?> tag) {
		try {
			textArea.setText(SNBTUtil.toSNBT(tag));
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}
	
	@FXML
	public void apply() {
		if (textArea.getText().length() == 0) cancel();
		
		Tag<?> tag;
		try {
			tag = SNBTUtil.fromSNBT(textArea.getText());
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("SNBT Error");
			alert.setHeaderText("Unable to parse SNBT!");
			alert.setContentText(e.getMessage());
			alert.show();
			
			LogManager.getLogger().warn("Unable to parse SNBT!", e);
			return;
		}
		
		finishedDispatcher.fire(tag);
		stage.close();
	}
	
	@FXML
	public void cancel() {
		stage.close();
	}
	
	public void onFinished(EventListener<Tag<?>> listener) {
		finishedDispatcher.addListener(listener);
	}
	
	public static SNBTEditor open(Window parent, Tag<?> tag) throws IOException {
		FXMLLoader loader = new FXMLLoader(CompileProgressUI.class.getResource("/assets/scaffold/ui/nbt/snbt_editor.fxml"));
		Parent root = loader.load();
		
		Scene scene = new Scene(root, 600, 400);
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(parent);
		stage.setTitle("Edit SNBT");
		stage.setScene(scene);
		SNBTEditor controller = loader.getController();
		controller.stage = stage;
		
		controller.loadTag(tag);
		stage.show();
		
		return controller;
	} 
}

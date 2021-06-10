package org.scaffoldeditor.editormc.ui.controllers;

import java.io.IOException;

import org.jetbrains.annotations.Nullable;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.util.event.EventDispatcher;
import org.scaffoldeditor.scaffold.util.event.EventListener;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EntityBrowser {
	public static class SelectEntityEvent {
		@Nullable
		public final String selectedEntityType;
		public SelectEntityEvent(String type) {
			this.selectedEntityType = type;
		}
	}
	
	protected Stage stage;
	private EventDispatcher<SelectEntityEvent> dispatcher = new EventDispatcher<>();
	
	public void onEntitySelected(EventListener<SelectEntityEvent> listener) {
		dispatcher.addListener(listener);
	}
	
	@FXML
	private ListView<String> typeList;
	
	@FXML
	private void initialize() {
		for (String name : EntityRegistry.registry.keySet()) {
			typeList.getItems().add(name);
		}
	}
	
	@FXML
	private void select() {
		String selection = typeList.getSelectionModel().getSelectedItem();
		if (selection != null) {
			dispatcher.fire(new SelectEntityEvent(selection));
		}
		stage.close();
	}
	
	public static EntityBrowser open() throws IOException {
		FXMLLoader loader = new FXMLLoader(CompileProgressUI.class.getResource("/assets/scaffold/ui/entity_browser.fxml"));
		Parent root = loader.load();
		
		Scene scene = new Scene(root, 600, 400);
		Stage stage = new Stage();
		stage.setTitle("Select Entity Class");
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setResizable(false);
		
		EntityBrowser controller = loader.getController();
		controller.stage = stage;
		
		stage.show();
		
		return controller;
	}
}

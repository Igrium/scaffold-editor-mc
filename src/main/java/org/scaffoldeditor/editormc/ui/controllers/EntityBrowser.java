package org.scaffoldeditor.editormc.ui.controllers;

import java.io.IOException;
import java.util.Collections;
import org.jetbrains.annotations.Nullable;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.util.event.EventDispatcher;
import org.scaffoldeditor.scaffold.util.event.EventListener;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
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
	private ObservableList<String> types;
	
	@FXML
	private ListView<String> typeList;
	FilteredList<String> filteredList;
	
	@FXML
	private TextField searchField;
	
	public void onEntitySelected(EventListener<SelectEntityEvent> listener) {
		dispatcher.addListener(listener);
	}
	
	@FXML
	private void initialize() {
		types = FXCollections.observableArrayList(EntityRegistry.registry.keySet());
		Collections.sort(types);
		filteredList = new FilteredList<>(types);
		typeList.setItems(filteredList);
		
		searchField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				setFilter(newValue);
			}
		});
		
		typeList.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() > 1) {
				select();
			}
		});
	}
	
	public void setFilter(String filter) {
		filteredList.setPredicate(t -> t.contains(filter));
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

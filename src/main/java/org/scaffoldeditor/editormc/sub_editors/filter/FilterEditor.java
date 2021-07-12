package org.scaffoldeditor.editormc.sub_editors.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommandBuilder;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommand.SubCommand;
import org.scaffoldeditor.scaffold.util.event.EventDispatcher;
import org.scaffoldeditor.scaffold.util.event.EventListener;
import org.scaffoldeditor.editormc.sub_editors.filter.FilterParts.*;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class FilterEditor {
	
	@FXML
	public ListView<FilterPart<?>> listView;
	@FXML
	public TextField commandString;
	
	public final FilterPartRegistry registry = new FilterPartRegistry();
	protected EventDispatcher<List<SubCommand>> dispatcher = new EventDispatcher<>();
	protected Stage stage;
	
	@FXML
	private void initialize() {
		listView.setCellFactory(list -> new ListCell<>() {
			protected void updateItem(FilterPart<?> item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					HBox box = new HBox(10);
					
					ChoiceBox<String> types = new ChoiceBox<>();
					types.getItems().addAll(registry.registry.keySet());
					types.getSelectionModel().select(item.getRegistryName());	
					
					Node setter = item.getSetter();
					types.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

						@Override
						public void changed(ObservableValue<? extends String> observable, String oldValue,
								String newValue) {
							if (newValue.equals(oldValue)) return;
							FilterPart<?> part = registry.create(newValue);
							getListView().getItems().set(getIndex(), part);
							part.onUpdate(FilterEditor.this::updateCommandText);
							updateCommandText();
						}
					});
					
					box.getChildren().add(types);
					box.getChildren().add(setter);
					setGraphic(box);
				}
			};
		});
	}
	
	public void init(List<SubCommand> commands) {
		for (SubCommand command : commands) {
			listView.getItems().add(registry.create(command));
		}
		updateCommandText();
	}
	
	public void newPart() {
		AsFilter part = new AsFilter();
		listView.getItems().add(part);
		part.onUpdate(this::updateCommandText);
		updateCommandText();
	}
	
	public void removePart() {
		int index = listView.getSelectionModel().getSelectedIndex();
		if (index >= 0) {
			listView.getItems().remove(index);
		}
	}
	
	@FXML
	public void apply() {
		List<SubCommand> commands = new ArrayList<>();
		for (FilterPart<?> part : listView.getItems()) {
			commands.add(part.getValue());
		}
		
		dispatcher.fire(commands);
		stage.close();
	}
	
	@FXML
	public void cancel() {
		stage.close();
	}
	
	public void updateCommandText() {
		String text = listView.getItems().stream().map(item -> ((SubCommand) item.getValue()).get())
				.collect(Collectors.joining(" "));
		
		commandString.setText(text);
	}
	
	@FXML
	public void parseCommandText() {
		String text = commandString.getText();
		ExecuteCommandBuilder builder;
		try {
			builder = ExecuteCommandBuilder.parse(text);
		} catch (IllegalArgumentException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Improperly formatted command");
			alert.setContentText(e.getLocalizedMessage());
			alert.show();
			return;
		}
		listView.getItems().clear();
		for (SubCommand command : builder.getSubCommands()) {
			listView.getItems().add(registry.create(command));
		}
		updateCommandText();
	}
	
	public void onApply(EventListener<List<SubCommand>> listener) {
		dispatcher.addListener(listener);
	}
	
	public static FilterEditor open(Window parent, List<SubCommand> items) {
		FXMLLoader loader = new FXMLLoader(FilterEditor.class.getResource("/assets/scaffold/ui/filter_editor.fxml"));
		Parent root;
		try {
			root = loader.load();
		} catch (IOException e) {
			throw new AssertionError("Unable to load filter editor UI!", e);
		}
		
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(parent);
		stage.setTitle("Edit Filter");
		
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
		FilterEditor controller = loader.getController();
		controller.stage = stage;
		controller.init(items);
		
		return controller;
	}
}

package org.scaffoldeditor.editormc.sub_editors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scaffoldeditor.editormc.ui.attribute_types.ChangeAttributeEvent;
import org.scaffoldeditor.editormc.ui.attribute_types.RenderAttributeRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.ListAttribute;
import org.scaffoldeditor.scaffold.util.event.EventDispatcher;
import org.scaffoldeditor.scaffold.util.event.EventListener;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ListAttributeEditor {
	@FXML
	private ListView<AttributeEntry> attributes;
	
	private Map<AttributeEntry, Node> setterCache = new HashMap<>();
	private EventDispatcher<ListAttribute> dispatcher = new EventDispatcher<>();
	private Stage stage;
	
	@FXML
	private void initialize() {
		attributes.setCellFactory(param -> {
			return new AttributeCell();
		});
	}
	
	public void load(ListAttribute list) {
		for (Attribute<?> attribute : list.getValue()) {
			attributes.getItems().add(new AttributeEntry(attribute));
		}
	}
	
	@FXML
	public void save() {
		List<Attribute<?>> attributes = new ArrayList<>();
		for (AttributeEntry item : this.attributes.getItems()) {
			attributes.add(item.value);
		}
		
		dispatcher.fire(new ListAttribute(attributes));
	}
	
	@FXML
	public void cancel() {
		stage.close();
	}
	
	public void onSave(EventListener<ListAttribute> listener) {
		dispatcher.addListener(listener);
	}
	
	private static class AttributeEntry {
		public Attribute<?> value;
		public AttributeEntry(Attribute<?> value) {
			this.value = value;
		}
	}
	
	private class AttributeCell extends ListCell<AttributeEntry> {
		@Override
		protected void updateItem(AttributeEntry item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				setText(null);
				setGraphic(null);
				return;
			}
			
			Node setter = setterCache.get(item);
			if (setter == null) {
				setter = createSetter(item);
				setterCache.put(item, setter);
			}
			
			setGraphic(setter);
		}
	}
	
	private Node createSetter(AttributeEntry item) {
		Attribute<?> attribute = item.value;
		Node setter = RenderAttributeRegistry.createSetter(attribute.registryName, attribute);
		setter.addEventHandler(ChangeAttributeEvent.ATTRIBUTE_CHANGED, event -> {
			item.value = event.newValue;
		});
		return setter;
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public static ListAttributeEditor open(Window parent) {
		FXMLLoader loader = new FXMLLoader(ListAttributeEditor.class.getResource("/assets/scaffold/ui/list_attribute_editor.fxml"));
		Parent root;
		try {
			root = loader.load();
		} catch (IOException e) {
			throw new AssertionError("Unable to load list editor UI!", e);
		}
		
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(parent);
		stage.setTitle("Edit List Attribute");
		
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
			
		ListAttributeEditor controller = loader.getController();
		controller.stage = stage;
		
		return controller;
	}
}

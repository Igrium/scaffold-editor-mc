package org.scaffoldeditor.editormc.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.scaffoldeditor.editormc.EditorOperationManager;
import org.scaffoldeditor.editormc.ui.attribute_types.ChangeAttributeEvent;
import org.scaffoldeditor.editormc.ui.attribute_types.DefaultAttributeType;
import org.scaffoldeditor.editormc.ui.attribute_types.IRenderAttributeType;
import org.scaffoldeditor.editormc.ui.attribute_types.RenderAttributeRegistry;
import org.scaffoldeditor.editormc.ui.controllers.FXMLEntityEditorController;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.Macro;
import org.scaffoldeditor.scaffold.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.io.Output;
import org.scaffoldeditor.scaffold.operation.ChangeAttributesOperation;
import org.scaffoldeditor.scaffold.sdoc.ComponentDoc;
import org.scaffoldeditor.scaffold.sdoc.SDoc;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EntityEditor {
	public final Stage parent;
	public final Entity entity;
	protected SDoc doc;
	protected Scene scene;
	protected Stage stage;
	protected Map<String, Attribute<?>> cachedAttributes = new HashMap<>();
	protected FXMLEntityEditorController controller;
	
	public static final IRenderAttributeType DEFAULT_ATTRIBUTE_TYPE = new DefaultAttributeType();
	
	protected GridPane attributePane;
	
	public EntityEditor(Stage parent, Entity entity) {		
		this.parent = parent;
		this.entity = entity;
		this.stage = new Stage();
		
		Parent root;
		FXMLLoader loader;
		try {
			loader = new FXMLLoader(getClass().getResource("/assets/scaffold/ui/entity_editor.fxml"));
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}		
		
		scene = new Scene(root);
		stage.setTitle("Edit "+entity.getName());
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(parent);
		stage.setResizable(false);
		
		controller = loader.getController();	
		controller.nameField.setText(entity.getName());	
		controller.entityTypeLabel.setText(entity.registryName);
		attributePane = controller.attributePane;
		doc = entity.getDocumentation();
		loadAttributes();
		loadMacros();
		controller.updateDoc(doc.getDescription());
		
		scene.addEventHandler(ChangeAttributeEvent.ATTRIBUTE_CHANGED, e -> {
			cachedAttributes.put(e.name, e.newValue);
		});
		
		controller.applyButton.setOnAction(e -> {
			apply();
		});
		
		controller.applyAndCloseButton.setOnAction(e -> {
			apply();
			close();
		});
		
		controller.loadOutputs(entity);
	}
	
	protected void loadAttributes() {
		int i = 2;
		List<String> ordered = new ArrayList<>();
		
		for (ComponentDoc att : doc.attributes) {
			ordered.add(att.getName());
		}
		
		for (String name : entity.getAttributes()) {
			if (!ordered.contains(name)) ordered.add(name);
		}
		
		for (String name : ordered) {
			Attribute<?> attribute = entity.getAttribute(name);
			
			Node setter = RenderAttributeRegistry.createSetter(name, attribute, entity);
			ComponentDoc comp = doc.attributes.stream().filter(val -> val.getName().equals(name)).findFirst().orElse(null);
			Label label = new Label(comp != null ? comp.getPrettyName() : name);
			
			attributePane.add(label, 1, i);
			attributePane.add(setter, 2, i);
			
			if (comp != null) {
				label.setTooltip(new Tooltip(comp.getDescription()));
			}
			
			i++;
		}
	}
	
	protected void loadMacros() {
		for (Macro macro : entity.getMacros()) {
			Button button = new Button(macro.name);
			controller.macroBox.getChildren().add(button);
			button.setOnAction(event -> {
				if (macro.confirmation != null) {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Execute macro");
					alert.setHeaderText(macro.confirmation.header);
					alert.setContentText(macro.confirmation.body);
					Optional<ButtonType> result = alert.showAndWait();
					if (result.isEmpty() || result.get() != ButtonType.OK) {
						return;
					}
				}
				
				macro.run();
			});
		}
	}
	
	public void show() {
		stage.show();
	}
	
	public void close() {
		stage.close();
	}
	
	public void apply() {
		Map<String, Attribute<?>> newAttributes = null;
		List<Output> newOutputs = null;
		if (cachedAttributes.size() > 0) newAttributes = cachedAttributes;
		if (controller.hasBeenUpdated) newOutputs = controller.outputTable.getItems();
		String newName = controller.nameField.getText();

		if (newAttributes != null || newOutputs != null || !newName.equals(entity.getName())) {
			EditorOperationManager.getInstance().runOperation(
				new ChangeAttributesOperation(entity, newAttributes, newOutputs, newName.equals(entity.getName()) ? null : newName, true)
			);
			
			controller.hasBeenUpdated = false;
		}
	}
}

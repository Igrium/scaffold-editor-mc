package org.scaffoldeditor.editormc.ui.attribute_types;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.scaffoldeditor.editormc.ui.EntityEditor;
import org.scaffoldeditor.scaffold.io.AssetLoader;
import org.scaffoldeditor.scaffold.io.AssetLoaderRegistry;
import org.scaffoldeditor.scaffold.io.AssetManager;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.attribute.AssetAttribute;
import org.scaffoldeditor.scaffold.entity.attribute.Attribute;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class AssetAttributeType implements IRenderAttributeType {

	@Override
	public Node createSetter(String name, Attribute<?> defaultValue, Entity entity) {
		if (!(defaultValue instanceof AssetAttribute)) {
			return EntityEditor.DEFAULT_ATTRIBUTE_TYPE.createSetter(name, defaultValue, entity);
		}
		
		AssetAttribute attribute = (AssetAttribute) defaultValue;
		
		HBox box = new HBox();
		box.setSpacing(5);
		
		TextField field = new TextField();
		field.setText(attribute.getValue());
		box.getChildren().add(field);
		HBox.setHgrow(field, Priority.ALWAYS);
		
		List<String> extensions = new ArrayList<>();
		for (String ext : attribute.getAssignableExtensions()) {
			extensions.add("*."+ext);
		}
		ExtensionFilter filter = new ExtensionFilter(attribute.getAssetType().name, extensions);
		
		Button browseButton = new Button("...");
		box.getChildren().add(browseButton);
		browseButton.setOnAction(event -> {
			FileChooser chooser = new FileChooser();
			File def = AssetManager.getInstance().getAbsoluteFile(field.getText());
			if (def.isFile()) {
				chooser.setInitialDirectory(def.getParentFile());
				chooser.setInitialFileName(def.getName());
			} else {
				chooser.setInitialDirectory(AssetManager.getInstance().getProject().getProjectFolder().toFile());
			}
			
			chooser.getExtensionFilters().add(filter);
			File file = chooser.showOpenDialog(browseButton.getScene().getWindow());
			if (file != null) {
				field.setText(AssetManager.getInstance().relativise(file));
				field.fireEvent(new ChangeAttributeEvent(ChangeAttributeEvent.ATTRIBUTE_CHANGED, name,
						new AssetAttribute(attribute.getAssetTypeName(), field.getText())));
			}
		});
		
		field.focusedProperty().addListener(event -> {
			if (!field.isFocused()) {
				AssetLoader<?> loader = AssetLoaderRegistry.getAssetLoader(field.getText());
				// We can only use this path if it's empty or it's the right file extension.
				if (field.getText().length() == 0 || (loader != null && loader.isAssignableTo(attribute.getAssetClass()))) {
					field.fireEvent(new ChangeAttributeEvent(ChangeAttributeEvent.ATTRIBUTE_CHANGED, name,
							new AssetAttribute(attribute.getAssetTypeName(), field.getText())));
				} else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error setting attribute");
					alert.setHeaderText("Unable to set attribute!");
					alert.setContentText("File type: ." + FilenameUtils.getExtension(field.getText())
							+ " is not applicable to asset type: " + attribute.getAssetTypeName());
					alert.show();
				}
			}
		});
		
		return box;
	}

}

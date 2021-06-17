package org.scaffoldeditor.editormc.ui.attribute_types;

import org.scaffoldeditor.editormc.ui.EntityEditor;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.VectorAttribute;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class VectorAttributeType implements IRenderAttributeType {

	@Override
	public Node createSetter(String name, Attribute<?> defaultValue) {
		if (!(defaultValue instanceof VectorAttribute)) {
			return EntityEditor.DEFAULT_ATTRIBUTE_TYPE.createSetter(name, defaultValue);
		}
		VectorAttribute attribute = (VectorAttribute) defaultValue;
		
		
		HBox box = new HBox();
		
		TextField xField = new TextField(String.valueOf(attribute.getValue().x));
		xField.setPromptText("x");
		xField.setTextFormatter(createFormatter());
		box.getChildren().add(xField);
		HBox.setHgrow(xField, Priority.ALWAYS);
		
		TextField yField = new TextField(String.valueOf(attribute.getValue().y));
		yField.setPromptText("y");
		yField.setTextFormatter(createFormatter());
		box.getChildren().add(yField);
		HBox.setHgrow(yField, Priority.ALWAYS);
		
		TextField zField = new TextField(String.valueOf(attribute.getValue().z));
		zField.setPromptText("z");
		zField.setTextFormatter(createFormatter());
		box.getChildren().add(zField);
		HBox.setHgrow(zField, Priority.ALWAYS);
		
		for (TextField field : new TextField[] { xField, yField, zField }) {
			field.focusedProperty().addListener(e -> {
				float x = xField.getText().length() > 0 ? Float.valueOf(xField.getText()) : 0f;
				float y = yField.getText().length() > 0 ? Float.valueOf(yField.getText()) : 0f;
				float z = zField.getText().length() > 0 ? Float.valueOf(zField.getText()) : 0f;
				box.fireEvent(new ChangeAttributeEvent(ChangeAttributeEvent.ATTRIBUTE_CHANGED, name, new VectorAttribute(new Vector3f(x, y, z))));
			});
		}
		
		return box;
	}
	
	private TextFormatter<Float> createFormatter() {
		return new TextFormatter<>(change -> {
			if (change.getControlNewText().isEmpty()) {
				return change;
			}
			try {
				Float.valueOf(change.getControlNewText());
				return change;
			} catch (NumberFormatException e) {
				return null;
			}			
		});
	}

}

package org.scaffoldeditor.editormc.ui.attribute_types;

import org.scaffoldeditor.editormc.ui.EntityEditor;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.entity.attribute.FloatAttribute;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class FloatAttributeType implements IRenderAttributeType {

	@Override
	public Node createSetter(String name, Attribute<?> defaultValue, Entity entity) {
		if (!(defaultValue instanceof FloatAttribute)) {
			return EntityEditor.DEFAULT_ATTRIBUTE_TYPE.createSetter(name, defaultValue, entity);
		}
		FloatAttribute attribute = (FloatAttribute) defaultValue;
		
		TextField field = new TextField(attribute.getValue().toString());
		field.setPromptText("Float");
		TextFormatter<Float> formatter = new TextFormatter<>(change -> {
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
		field.setTextFormatter(formatter);
		
		field.focusedProperty().addListener(e -> {
			if (field.getText().length() > 0) {
				field.fireEvent(new ChangeAttributeEvent(ChangeAttributeEvent.ATTRIBUTE_CHANGED, name,
						new FloatAttribute(Float.valueOf(field.getText()))));			
			}
		});
		
		return field;
		
	}
}

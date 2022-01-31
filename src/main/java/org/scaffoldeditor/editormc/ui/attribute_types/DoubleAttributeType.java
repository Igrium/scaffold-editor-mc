package org.scaffoldeditor.editormc.ui.attribute_types;

import org.scaffoldeditor.editormc.ui.EntityEditor;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.entity.attribute.DoubleAttribute;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class DoubleAttributeType implements IRenderAttributeType {

	@Override
	public Node createSetter(String name, Attribute<?> defaultValue, Entity entity) {
		if (!(defaultValue instanceof DoubleAttribute)) {
			return EntityEditor.DEFAULT_ATTRIBUTE_TYPE.createSetter(name, defaultValue, entity);
		}
		DoubleAttribute attribute = (DoubleAttribute) defaultValue;
		
		TextField field = new TextField(attribute.getValue().toString());
		field.setPromptText("Double");
		TextFormatter<Double> formatter = new TextFormatter<>(change -> {
			if (change.getControlNewText().isEmpty()) {
				return change;
			}
			try {
				Double.valueOf(change.getControlNewText());
				return change;
			} catch (NumberFormatException e) {
				return null;
			}			
		});
		field.setTextFormatter(formatter);
		
		field.focusedProperty().addListener(e -> {
			if (field.getText().length() > 0) {
				field.fireEvent(new ChangeAttributeEvent(ChangeAttributeEvent.ATTRIBUTE_CHANGED, name,
						new DoubleAttribute(Double.valueOf(field.getText()))));			
			}
		});
		
		return field;
		
	}
}

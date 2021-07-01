package org.scaffoldeditor.editormc.ui.attribute_types;

import org.scaffoldeditor.editormc.ui.EntityEditor;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.LongAttribute;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class LongAttributeType implements IRenderAttributeType {

	@Override
	public Node createSetter(String name, Attribute<?> defaultValue, Entity entity) {
		if (!(defaultValue instanceof LongAttribute)) {
			return EntityEditor.DEFAULT_ATTRIBUTE_TYPE.createSetter(name, defaultValue, entity);
		}
		LongAttribute attribute = (LongAttribute) defaultValue;
		
		TextField field = new TextField(attribute.getValue().toString());
		field.setPromptText("Long");
		TextFormatter<Long> formatter = new TextFormatter<>(change -> {
			if (change.getControlNewText().isEmpty()) {
				return change;
			}
			try {
				Long.parseLong(change.getControlNewText());
				return change;
			} catch (NumberFormatException e) {
				return null;
			}			
		});
		field.setTextFormatter(formatter);
		
		field.focusedProperty().addListener(e -> {
			if (field.getText().length() > 0) {
				field.fireEvent(new ChangeAttributeEvent(ChangeAttributeEvent.ATTRIBUTE_CHANGED, name,
						new LongAttribute(Long.parseLong(field.getText()))));			
			}
		});
		
		return field;
		
	}
}

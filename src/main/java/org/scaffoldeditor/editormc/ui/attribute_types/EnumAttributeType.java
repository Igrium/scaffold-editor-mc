package org.scaffoldeditor.editormc.ui.attribute_types;

import org.scaffoldeditor.editormc.ui.EntityEditor;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.entity.attribute.EnumAttribute;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;

public class EnumAttributeType implements IRenderAttributeType {
	
	// We're not doing anything with the enum before sending it to code that's blind
	// to what class it is, so typing or checking it won't do anything.
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Node createSetter(String name, Attribute<?> defaultValue, Entity entity) {
		if (!(defaultValue instanceof EnumAttribute)) {
			return EntityEditor.DEFAULT_ATTRIBUTE_TYPE.createSetter(name, defaultValue, entity);
		}
		
		EnumAttribute<?> attribute = (EnumAttribute<?>) defaultValue;
		ChoiceBox<String> choiceBox = new ChoiceBox<>();
		
		for (Enum<?> value : attribute.getEnumClass().getEnumConstants()) {
			choiceBox.getItems().add(value.toString());
		}
		choiceBox.setValue(attribute.getValue().toString());
		
		choiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				Enum<?> newEnum = Enum.valueOf(attribute.getEnumClass(), newValue);
				choiceBox.fireEvent(new ChangeAttributeEvent(ChangeAttributeEvent.ATTRIBUTE_CHANGED, name, new EnumAttribute(newEnum)));
			}
		});
		
		return choiceBox;
	}

}

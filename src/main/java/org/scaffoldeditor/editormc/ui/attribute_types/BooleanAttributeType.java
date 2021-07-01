package org.scaffoldeditor.editormc.ui.attribute_types;

import org.scaffoldeditor.editormc.ui.EntityEditor;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BooleanAttribute;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;

public class BooleanAttributeType implements IRenderAttributeType {

	@Override
	public Node createSetter(String name, Attribute<?> defaultValue, Entity entity) {
		if (!(defaultValue instanceof BooleanAttribute)) {
			return EntityEditor.DEFAULT_ATTRIBUTE_TYPE.createSetter(name, defaultValue, entity);
		}
		BooleanAttribute attribute = (BooleanAttribute) defaultValue;
		CheckBox checkBox = new CheckBox();
		checkBox.setSelected(attribute.getValue());
		
		checkBox.selectedProperty().addListener(e -> {
			checkBox.fireEvent(new ChangeAttributeEvent(ChangeAttributeEvent.ATTRIBUTE_CHANGED, name,
					new BooleanAttribute(checkBox.isSelected())));
		});
		return checkBox;
	}

}

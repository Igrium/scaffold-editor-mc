package org.scaffoldeditor.editormc.ui.attribute_types;

import java.util.stream.Collectors;

import org.scaffoldeditor.editormc.ui.EntityEditor;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.EntityAttribute;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

public class EntityAttributeType implements IRenderAttributeType {

	@Override
	public Node createSetter(String name, Attribute<?> defaultValue, Entity entity) {
		if (!(defaultValue instanceof EntityAttribute)) {
			return EntityEditor.DEFAULT_ATTRIBUTE_TYPE.createSetter(name, defaultValue, entity);
		}
		
		EntityAttribute attribute = (EntityAttribute) defaultValue;
		ComboBox<String> box = new ComboBox<>();
		box.getItems().addAll(
				entity.getLevel().getLevelStack().stream().map(ent -> ent.getName()).collect(Collectors.toList()));
		box.setEditable(true);
		box.setValue(attribute.getValue());
		
		box.valueProperty().addListener(new ChangeListener<>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				box.fireEvent(new ChangeAttributeEvent(ChangeAttributeEvent.ATTRIBUTE_CHANGED, name,
						new EntityAttribute(newValue)));
			}
		});
		
		return box;
	}

}

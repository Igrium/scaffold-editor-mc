package org.scaffoldeditor.editormc.ui.attribute_types;

import java.io.IOException;

import org.scaffoldeditor.editormc.sub_editors.ContainerEditor;
import org.scaffoldeditor.editormc.ui.EntityEditor;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.ContainerAttribute;
import javafx.scene.Node;
import javafx.scene.control.Button;

public class ContainerAttributeType implements IRenderAttributeType {

	@Override
	public Node createSetter(String name, Attribute<?> defaultValue, Entity entity) {
		if (!(defaultValue instanceof ContainerAttribute)) {
			return EntityEditor.DEFAULT_ATTRIBUTE_TYPE.createSetter(name, defaultValue, entity);
		}
		ContainerAttribute attribute = (ContainerAttribute) defaultValue;
		return new SetterInstance(name, attribute.clone()).getSetter();
		
	}

	private class SetterInstance {
		private ContainerAttribute value;
		private String name;
		
		public SetterInstance(String name, ContainerAttribute defaultValue) {
			this.name = name;
			this.value = defaultValue;
		}
		
		public Node getSetter() {
			Button editButton = new Button("Edit Container");
			editButton.setOnAction(e -> {
				ContainerEditor editor;
				try {
					editor = ContainerEditor.open(editButton.getScene().getWindow(), 0, 0);
				} catch (IOException e1) {
					throw new AssertionError("Unable to load container ui!", e1);
				}
				
				editor.onFinished(content -> {
					this.value = new ContainerAttribute(content);
					editButton.fireEvent(new ChangeAttributeEvent(ChangeAttributeEvent.ATTRIBUTE_CHANGED, name, value));
				});
				
				editor.init(9, 3);
				editor.setContent(value.getValue());
			});
			
			return editButton;
		}
	}
	
}

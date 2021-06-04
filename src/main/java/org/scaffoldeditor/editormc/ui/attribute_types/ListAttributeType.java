package org.scaffoldeditor.editormc.ui.attribute_types;

import org.scaffoldeditor.editormc.sub_editors.ListAttributeEditor;
import org.scaffoldeditor.editormc.ui.EntityEditor;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.ListAttribute;

import javafx.scene.Node;
import javafx.scene.control.Button;

public class ListAttributeType implements IRenderAttributeType {

	@Override
	public Node createSetter(String name, Attribute<?> defaultValue) {
		if (!(defaultValue instanceof ListAttribute)) {
			return EntityEditor.DEFAULT_ATTRIBUTE_TYPE.createSetter(name, defaultValue);
		}
		
		return new SetterInstance(name, (ListAttribute) defaultValue).getSetter();
	}
	
	private static class SetterInstance {
		private ListAttribute value;
		private String name;
		
		public SetterInstance(String name, ListAttribute defaultValue) {
			this.name = name;
			this.value = defaultValue;
		}
		
		public Node getSetter() {
			Button button = new Button("Edit List");
			button.setOnAction(e -> {
				ListAttributeEditor editor = ListAttributeEditor.open(button.getScene().getWindow());
				editor.load(value);
				editor.onSave(newValue -> {
					value = newValue;
					button.fireEvent(new ChangeAttributeEvent(ChangeAttributeEvent.ATTRIBUTE_CHANGED, name, value));
					editor.getStage().close();
				});
			});
			
			return button;
		}
	}
}

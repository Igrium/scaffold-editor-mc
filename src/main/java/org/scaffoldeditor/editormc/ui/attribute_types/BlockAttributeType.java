package org.scaffoldeditor.editormc.ui.attribute_types;

import org.scaffoldeditor.editormc.sub_editors.BlockEditor;
import org.scaffoldeditor.editormc.ui.EntityEditor;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BlockAttribute;
import javafx.scene.Node;
import javafx.scene.control.Button;

public class BlockAttributeType implements IRenderAttributeType {

	@Override
	public Node createSetter(String name, Attribute<?> defaultValue) {
		if (!(defaultValue instanceof BlockAttribute)) {
			return EntityEditor.DEFAULT_ATTRIBUTE_TYPE.createSetter(name, defaultValue);
		}
		
		return new SetterInstance(name, (BlockAttribute) defaultValue).getSetter();
	}
	
	private class SetterInstance {
		private BlockAttribute value;
		private String name;
		
		public SetterInstance(String name, BlockAttribute defaultValue) {
			this.name = name;
			this.value = defaultValue;
		}
		
		public Node getSetter() {
			Button editButton = new Button("Block: " + value.getValue().getName());			
			editButton.setOnAction(e -> {
				BlockEditor editor = BlockEditor.open(editButton.getScene().getWindow(), value.getValue());
				editor.onApply(block -> {
					this.value = new BlockAttribute(block);
					editButton.fireEvent(new ChangeAttributeEvent(ChangeAttributeEvent.ATTRIBUTE_CHANGED, name, value));
					editButton.setText("Block: " + value.getValue().getName());
				});
			});
			
			return editButton;
		}
	}

}

package org.scaffoldeditor.editormc.ui.attribute_types;

import java.io.IOException;

import org.scaffoldeditor.editormc.sub_editors.ContainerEditor;
import org.scaffoldeditor.editormc.ui.EntityEditor;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.ContainerAttribute;

import javafx.scene.Node;
import javafx.scene.control.Button;

public class ContainerAttributeType implements IRenderAttributeType {

	@Override
	public Node createSetter(String name, Attribute<?> defaultValue) {
		if (!(defaultValue instanceof ContainerAttribute)) {
			return EntityEditor.DEFAULT_ATTRIBUTE_TYPE.createSetter(name, defaultValue);
		}
		
		Button editButton = new Button("Edit Container");
		editButton.setOnAction(e -> {
			ContainerEditor editor;
			try {
				editor = ContainerEditor.open(editButton.getScene().getWindow(), 0, 0);
			} catch (IOException e1) {
				throw new AssertionError("Unable to load container ui!", e1);
			}
			
			editor.init(9, 3);
		});
		
		return editButton;
	}

}

package org.scaffoldeditor.editormc.ui.attribute_types;

import org.scaffoldeditor.editormc.sub_editors.nbt.NBTEditorController;
import org.scaffoldeditor.editormc.ui.EntityEditor;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.NBTAttribute;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;

public class NBTAttributeType implements IRenderAttributeType {

	@Override
	public Node createSetter(String name, Attribute<?> defaultValue) {
		if (!(defaultValue instanceof NBTAttribute)) {
			return EntityEditor.DEFAULT_ATTRIBUTE_TYPE.createSetter(name, defaultValue);
		}
		
		CompoundTag nbt = ((NBTAttribute) defaultValue).getValue().clone();
		
		Button editButton = new Button("Browse NBT");
		editButton.setOnAction(e -> {
			NBTEditorController controller = NBTEditorController.openPopup((Stage) editButton.getScene().getWindow());
			controller.loadNBT(new NamedTag("root", nbt));
			
			controller.onUpdateNBT(tag -> {
				editButton.fireEvent(new ChangeAttributeEvent(ChangeAttributeEvent.ATTRIBUTE_CHANGED, name,
						new NBTAttribute((CompoundTag) tag.getTag())));
			});
		});
		
		GridPane.setVgrow(editButton, Priority.NEVER);
		return editButton;
	}

}

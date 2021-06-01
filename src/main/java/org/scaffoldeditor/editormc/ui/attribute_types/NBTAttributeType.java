package org.scaffoldeditor.editormc.ui.attribute_types;

import org.scaffoldeditor.editormc.nbt_browser.NBTBrowserController;
import org.scaffoldeditor.editormc.ui.EntityEditor;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.NBTAttribute;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;

public class NBTAttributeType implements IRenderAttributeType {

	@Override
	public Node createSetter(String name, Attribute<?> defaultValue) {
		if (!(defaultValue instanceof NBTAttribute)) {
			return EntityEditor.DEFAULT_ATTRIBUTE_TYPE.createSetter(name, defaultValue);
		}
		
		CompoundTag nbt = ((NBTAttribute) defaultValue).getValue();
		Button editButton = new Button("Browse NBT");
		editButton.setOnAction(e -> {
			NBTBrowserController.openPopup((Stage) editButton.getScene().getWindow())
					.loadNBT(new NamedTag("root", nbt));
		});
		
		return editButton;
	}

}

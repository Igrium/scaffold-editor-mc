package org.scaffoldeditor.editormc.ui.attribute_types;

import org.scaffoldeditor.editormc.sub_editors.blocktexture.BlockTextureEditor;
import org.scaffoldeditor.editormc.ui.EntityEditor;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BlockTextureAttribute;

import javafx.scene.Node;
import javafx.scene.control.Button;

public class BlockTextureAttributeType implements IRenderAttributeType {
	
	private BlockTextureAttribute old;
	
	@Override
	public Node createSetter(String name, Attribute<?> defaultValue) {
		if (!(defaultValue instanceof BlockTextureAttribute)) {
			return EntityEditor.DEFAULT_ATTRIBUTE_TYPE.createSetter(name, defaultValue);
		}
		old = (BlockTextureAttribute) defaultValue;
		
		Button button = new Button("Edit BlockTexture");
		button.setOnAction(e -> {
			BlockTextureEditor editor = BlockTextureEditor.open(button.getScene().getWindow());
			if (old.isExternal()) {
				editor.loadBlockTexture(old.getExternalPath());
			} else {
				// We don't need to copy because the editor does that automatically
				editor.setBlockTexture(old.getValue());
			}
			
			editor.onSaved(event -> {
				BlockTextureAttribute attribute;
				if (event.isExternal) {
					attribute = new BlockTextureAttribute(event.assetPath);
				} else {
					attribute = new BlockTextureAttribute(event.newTexture);
				}
				attribute.registryName = old.registryName;
				old = attribute;
								
				editor.getStage().close();
				button.fireEvent(new ChangeAttributeEvent(ChangeAttributeEvent.ATTRIBUTE_CHANGED, name, attribute));
			});
		});
		
		return button;
	}

}

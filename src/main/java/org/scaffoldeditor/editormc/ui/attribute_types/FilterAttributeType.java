package org.scaffoldeditor.editormc.ui.attribute_types;

import java.util.function.Consumer;

import org.scaffoldeditor.editormc.sub_editors.filter.FilterEditor;
import org.scaffoldeditor.editormc.ui.EntityEditor;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.entity.attribute.FilterAttribute;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommandBuilder;

import javafx.scene.Node;
import javafx.stage.Window;

public class FilterAttributeType implements IRenderAttributeType {

	@Override
	public Node createSetter(String name, Attribute<?> defaultValue, Entity entity) {
		if (!(defaultValue instanceof FilterAttribute)) {
			return EntityEditor.DEFAULT_ATTRIBUTE_TYPE.createSetter(name, defaultValue, entity);
		}
		
		return new ExternalAttributeEditor<FilterAttribute>(name, (FilterAttribute) defaultValue) {

			@Override
			public void launchUI(FilterAttribute value, Window parent, Consumer<FilterAttribute> consumer) {
				FilterEditor editor = FilterEditor.open(parent, value.getValue().getSubCommands());
				editor.onApply(commands -> {
					ExecuteCommandBuilder builder = new ExecuteCommandBuilder();
					builder.getSubCommands().addAll(commands);
					consumer.accept(new FilterAttribute(builder));
				});
			}

			@Override
			public String getText() {
				return "Edit Filter";
			}
			
		}.getSetter();
		
	}

}

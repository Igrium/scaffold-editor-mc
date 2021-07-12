package org.scaffoldeditor.editormc.sub_editors.filter;

import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommand.SubCommand;

import javafx.scene.Node;

public abstract class FilterPart<T extends SubCommand> {
	public abstract Node getSetter();
	public abstract T getValue();
	public abstract String getRegistryName();
	public abstract void setValue(T value);
	
	@SuppressWarnings("unchecked")
	public void setSubCommand(SubCommand value) {
		setValue((T) value);
	};
}
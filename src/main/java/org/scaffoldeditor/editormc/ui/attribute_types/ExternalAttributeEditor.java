package org.scaffoldeditor.editormc.ui.attribute_types;

import java.util.function.Consumer;

import org.scaffoldeditor.scaffold.entity.attribute.Attribute;

import javafx.scene.control.Button;
import javafx.stage.Window;

public abstract class ExternalAttributeEditor<T extends Attribute<?>> {
	
	protected String name;
	protected T value;
	
	public ExternalAttributeEditor(String name, T defaultValue) {
		this.name = name;
		this.value = defaultValue;
	}
	
	public Button getSetter() {
		Button editButton = new Button(getText());
		editButton.setOnAction(event -> {
			launchUI(value, editButton.getScene().getWindow(), value -> {
				this.value = value;
				editButton.fireEvent(new ChangeAttributeEvent(ChangeAttributeEvent.ATTRIBUTE_CHANGED, name, value));
			});
		});
		
		return editButton;
	}
	
	public abstract void launchUI(T value, Window parent, Consumer<T> consumer);
	
	public abstract String getText();
	
}

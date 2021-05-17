package org.scaffoldeditor.editormc.ui.setting_types;

import org.w3c.dom.Element;

import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;

public class StringSetting implements ISettingType {

	@Override
	public Node createSetter(Element element, String path, String defaultValue, Scene scene) {
		TextField field = new TextField();
		field.setText(defaultValue);
		field.focusedProperty().addListener(e -> {
			if (!field.getText().matches(defaultValue)) {
				ChangeSettingEvent event = new ChangeSettingEvent(ChangeSettingEvent.SETTING_CHANGED, field.getText(), path, getName());
				field.fireEvent(event);	
			}

		});
		
		return field;
	}

	@Override
	public EventType<? extends ChangeSettingEvent> getEventType() {
		return ChangeSettingEvent.SETTING_CHANGED;
	}

	@Override
	public String getName() {
		return "StringSetting";
	}

	

}

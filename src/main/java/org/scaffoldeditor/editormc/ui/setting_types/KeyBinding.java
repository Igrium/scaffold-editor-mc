package org.scaffoldeditor.editormc.ui.setting_types;

import org.w3c.dom.Element;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;

public class KeyBinding implements ISettingType {

	@Override
	public Node createSetter(Element element, String path, String defaultValue, Scene scene) {
		TextField field = new TextField();
		field.setText(defaultValue);
		field.onActionProperty().addListener(e -> {
			ChangeSettingEvent event = new ChangeSettingEvent(ChangeSettingEvent.SETTING_CHANGED, path, field.getText());
			Event.fireEvent(scene, event);
		});
		
		return field;
	}

	@Override
	public EventType<? extends ChangeSettingEvent> getEventType() {
		return ChangeSettingEvent.SETTING_CHANGED;
	}



}

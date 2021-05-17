package org.scaffoldeditor.editormc.ui.setting_types;

import org.w3c.dom.Element;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;

public class KeyBinding implements ISettingType {

	@Override
	public Node createSetter(Element element, String path, String defaultValue, Scene scene) {
		TextField field = new TextField();
		field.setText(defaultValue);
		field.setEditable(false);
		
		field.focusedProperty().addListener(new ChangeListener<Boolean>() {
			
			private String text = field.getText();

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (field.isFocused()) {
					text = field.getText();
					field.setText("Press a key");
				} else {
					if (field.getText().equals("Press a key")) {
						field.setText(text);
					}
				}
			}
			
		});
		
		field.setOnKeyPressed(e -> {
			field.setText(e.getCode().getName());
			ChangeSettingEvent event = new ChangeSettingEvent(getEventType(), field.getText(), path, getName());
			field.fireEvent(event);
		});
		
		return field;
	}

	@Override
	public EventType<? extends ChangeSettingEvent> getEventType() {
		return ChangeSettingEvent.SETTING_CHANGED;
	}

	@Override
	public String getName() {
		return "KeyBinding";
	}

	

}

package org.scaffoldeditor.editormc.ui.setting_types;

import org.w3c.dom.Element;

import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;

public interface ISettingType {
	public Node createSetter(Element element, String path, String defaultValue, Scene scene);
	
	public EventType<? extends ChangeSettingEvent> getEventType();
	
	public String getName();
}

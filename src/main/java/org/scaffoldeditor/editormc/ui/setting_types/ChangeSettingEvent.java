package org.scaffoldeditor.editormc.ui.setting_types;

import javafx.event.Event;
import javafx.event.EventType;

public class ChangeSettingEvent extends Event {
	
	private static final long serialVersionUID = 1L;
	public static final EventType<ChangeSettingEvent> ANY = new EventType<ChangeSettingEvent>(Event.ANY, "CHANGE_SETTING");
	
	public static final EventType<ChangeSettingEvent> SETTING_CHANGED = new EventType<ChangeSettingEvent>(ANY, "SETTING_CHANGED");
	
	private final String newValue;
	private final String path;

	public ChangeSettingEvent(EventType<? extends ChangeSettingEvent> eventType, String newValue, String path) {
		super(eventType);
		this.newValue = newValue;
		this.path = path;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EventType<? extends ChangeSettingEvent> getEventType() {
		// TODO Auto-generated method stub
		return (EventType<? extends ChangeSettingEvent>) super.getEventType();
	}
	
	public String getNewValue() {
		return newValue;
	}
	
	public String getPath() {
		return path;
	}
	
	@Override
	public String toString() {
		return ("ChangeSettingEvent[newValue = '"+newValue+"']");
	}

}

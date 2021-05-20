package org.scaffoldeditor.editormc.ui.attribute_types;

import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;

import javafx.event.Event;
import javafx.event.EventType;

public class ChangeAttributeEvent extends Event {
	
	private static final long serialVersionUID = 1L;
	public static final EventType<ChangeAttributeEvent> ANY = new EventType<ChangeAttributeEvent>(Event.ANY, "CHANGE_ATTRIBUTE");
	
	public static final EventType<ChangeAttributeEvent> ATTRIBUTE_CHANGED = new EventType<ChangeAttributeEvent>(ANY, "ATTRIBUTE_CHANGED");
	
	public final String name;
	public final Attribute<?> newValue;
	
	/**
	 * Create a change attribute event.
	 * @param eventType The event type.
	 * @param name Name of the attribute changed.
	 * @param newValue Value it was changed to.
	 */
	public ChangeAttributeEvent(EventType<? extends ChangeAttributeEvent> eventType, String name, Attribute<?> newValue) {
		super(eventType);
		this.name = name;
		this.newValue = newValue;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EventType<? extends ChangeAttributeEvent> getEventType() {
		// TODO Auto-generated method stub
		return (EventType<? extends ChangeAttributeEvent>) super.getEventType();
	}
	
	
	@Override
	public String toString() {
		return ("ChangeAttributeEvent[newValue = '"+newValue+"']");
	}

}

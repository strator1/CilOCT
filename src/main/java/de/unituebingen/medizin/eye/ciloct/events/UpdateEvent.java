package de.unituebingen.medizin.eye.ciloct.events;

import javafx.beans.Observable;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author strasser
 */
public class UpdateEvent extends Event {
	public static final EventType<UpdateEvent> EVENT_TYPE = new EventType<>("update");

	public UpdateEvent(final Observable source) {
		super(EVENT_TYPE);
	}
}

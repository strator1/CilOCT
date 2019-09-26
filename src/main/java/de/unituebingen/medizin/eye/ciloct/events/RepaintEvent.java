package de.unituebingen.medizin.eye.ciloct.events;

import javafx.beans.Observable;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author strasser
 */
public class RepaintEvent extends Event {
	public static final EventType<RepaintEvent> EVENT_TYPE = new EventType<>("repaint");

	public RepaintEvent(final Observable source) {
		super(source, null, EVENT_TYPE);
	}
}
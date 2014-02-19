package org.vaadin.jouni.animator.client;

import java.util.HashMap;

import com.vaadin.shared.AbstractComponentState;

public class DomState extends AbstractComponentState {

	private static final long serialVersionUID = 7707257396804839934L;

	public Css css = new Css();

	public HashMap<String, String> attributes = new HashMap<String, String>();

	/** Internal use only. Used to force a state change */
	public int increment = 0;

}
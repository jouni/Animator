package org.vaadin.jouni.animator.client;

import java.util.HashMap;

public class DomState extends com.vaadin.shared.AbstractComponentState {

    public Css css = new Css();

    public HashMap<String, String> attributes = new HashMap<String, String>();

    /** Internal use only. Used to force a state change */
    public int increment = 0;

}
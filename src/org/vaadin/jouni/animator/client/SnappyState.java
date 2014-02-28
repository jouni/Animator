package org.vaadin.jouni.animator.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.vaadin.shared.AbstractComponentState;

public class SnappyState extends AbstractComponentState {
    private static final long serialVersionUID = -6978954986593595109L;
    public HashMap<ClientEvent, ArrayList<CssAnimation>> eventToAnimateActions = new HashMap<ClientEvent, ArrayList<CssAnimation>>();
    public HashMap<ClientEvent, ArrayList<Action>> eventToActions = new HashMap<ClientEvent, ArrayList<Action>>();
}

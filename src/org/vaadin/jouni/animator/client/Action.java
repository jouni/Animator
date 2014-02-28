package org.vaadin.jouni.animator.client;

import com.vaadin.shared.Connector;

public class Action {
	public enum ActionType {
		BLUR //
		, FOCUS //
		, SHOW //
		, HIDE//
		, ADD_STYLENAME //
		, REMOVE_STYLENAME //
		, TOGGLE_STYLENAME //
		, CLICK //
		, SET_TEXT //
		, ENABLE //
		, DISABLE //
	}

	public ActionType type;
	public Connector target;
	public String[] stringParams;
	public int[] intParams;
	public Object[] objParams;

	public Action() {

	}

	public Action(ActionType type, Connector target) {
		this.type = type;
		this.target = target;
	}
}

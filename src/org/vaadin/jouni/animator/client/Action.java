package org.vaadin.jouni.animator.client;

import com.vaadin.shared.Connector;

public class Action {
	public enum ActionType {
		BLUR //
		, FOCUS //
	}

	public ActionType type;
	public Connector target;

	public Action() {

	}

	public Action(ActionType type, Connector target) {
		this.type = type;
		this.target = target;
	}
}

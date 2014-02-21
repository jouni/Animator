package org.vaadin.jouni.animator;

import java.util.ArrayList;

import org.vaadin.jouni.animator.client.Action;
import org.vaadin.jouni.animator.client.Action.ActionType;
import org.vaadin.jouni.animator.client.ClientEvent;
import org.vaadin.jouni.animator.client.Css;
import org.vaadin.jouni.animator.client.CssAnimation;
import org.vaadin.jouni.animator.client.SnappyState;

import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.AbstractComponent;

public class Snappy extends AbstractExtension {

	private static final long serialVersionUID = -6291739338315576979L;

	public Snappy(AbstractComponent target) {
		super.extend(target);
	}

	protected ClientEvent currentEvent;

	public Snappy on(ClientEvent event) {
		currentEvent = event;
		return this;
	}

	public Snappy animate(AbstractComponent target, Css style) {
		if (currentEvent == null) {
			throw new IllegalStateException(
					"No event speficied for this action. You need to call the 'on' method first.");
		}
		ArrayList<CssAnimation> actions = getState().eventToAnimateActions
				.get(currentEvent);
		if (actions == null) {
			actions = new ArrayList<CssAnimation>();
			getState().eventToAnimateActions.put(currentEvent, actions);
		}
		actions.add(new CssAnimation(target, style));
		return this;
	}

	public Snappy blur(AbstractComponent target) {
		if (currentEvent == null) {
			throw new IllegalStateException(
					"No event speficied for this action. You need to call the 'on' method first.");
		}
		ArrayList<Action> actions = getState().eventToActions.get(currentEvent);
		if (actions == null) {
			actions = new ArrayList<Action>();
			getState().eventToActions.put(currentEvent, actions);
		}
		actions.add(new Action(ActionType.BLUR, target));
		return this;
	}

	@Override
	public SnappyState getState() {
		return (SnappyState) super.getState();
	}

}

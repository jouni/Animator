package org.vaadin.jouni.animator.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;

import org.vaadin.jouni.animator.Snappy;
import org.vaadin.jouni.animator.client.Action.ActionType;
import org.vaadin.jouni.animator.client.ClientEvent.EventType;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

@Connect(Snappy.class)
public class SnappyConnector extends AbstractExtensionConnector {

	Widget targetWidget;

	HandlerRegistration keydownHandler;
	HandlerRegistration focusHandler;
	HandlerRegistration blurHandler;
	HandlerRegistration clickHandler;

	@Override
	protected void extend(ServerConnector target) {
		targetWidget = ((AbstractComponentConnector) target).getWidget();
	}

	@Override
	public SnappyState getState() {
		return (SnappyState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);

		// Collect needed event types

		Set<ClientEvent> keySet = getState().eventToAnimateActions.keySet();
		Set<ClientEvent> keySet2 = getState().eventToActions.keySet();

		boolean needsFocusHandler = false;
		boolean needsBlurHandler = false;
		boolean needsKeydownHandler = false;
		boolean needsClickHandler = false;

		for (ClientEvent e : keySet) {
			switch (e.type) {
			case BLUR:
				needsBlurHandler = true;
				break;
			case FOCUS:
				needsFocusHandler = true;
				break;
			case KEYDOWN:
				needsKeydownHandler = true;
				break;
			case CLICK:
				needsClickHandler = true;
				break;
			}
		}
		for (ClientEvent e : keySet2) {
			switch (e.type) {
			case BLUR:
				needsBlurHandler = true;
				break;
			case FOCUS:
				needsFocusHandler = true;
				break;
			case KEYDOWN:
				needsKeydownHandler = true;
				break;
			case CLICK:
				needsClickHandler = true;
				break;
			}
		}

		// Blur handler
		if (needsBlurHandler && blurHandler == null) {
			blurHandler = targetWidget.addDomHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					runActions(EventType.BLUR, null);
				}
			}, BlurEvent.getType());
		} else if (blurHandler != null) {
			blurHandler.removeHandler();
		}

		// Focus handler
		if (needsFocusHandler && focusHandler == null) {
			AnimatorConnector.log("Needs focus handler");
			focusHandler = targetWidget.addDomHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					runActions(EventType.FOCUS, null);
				}
			}, FocusEvent.getType());
		} else if (focusHandler != null) {
			focusHandler.removeHandler();
		}

		// Keydown handler
		if (needsKeydownHandler && keydownHandler == null) {
			keydownHandler = targetWidget.addDomHandler(new KeyDownHandler() {
				@Override
				public void onKeyDown(KeyDownEvent event) {
					runActions(EventType.KEYDOWN,
							new int[] { event.getNativeKeyCode() });
				}
			}, KeyDownEvent.getType());
		} else if (keydownHandler != null) {
			keydownHandler.removeHandler();
		}

		// Click handler
		if (needsClickHandler && clickHandler == null) {
			clickHandler = targetWidget.addDomHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					runActions(EventType.CLICK,
							new int[] { event.getNativeButton() });
				}
			}, ClickEvent.getType());
		} else if (clickHandler != null) {
			clickHandler.removeHandler();
		}

	}

	protected void runActions(EventType type, int[] intParams) {
		for (Entry<ClientEvent, ArrayList<CssAnimation>> e : getState().eventToAnimateActions
				.entrySet()) {
			if (checkEventParams(type, e.getKey(), intParams, null, null)) {
				for (CssAnimation animation : e.getValue()) {
					AnimatorConnector.runAnimation(animation);
				}
			}
		}
		for (Entry<ClientEvent, ArrayList<Action>> e : getState().eventToActions
				.entrySet()) {
			if (checkEventParams(type, e.getKey(), intParams, null, null)) {
				for (Action action : e.getValue()) {
					doAction(action);
				}
			}
		}
	}

	private boolean checkEventParams(EventType type, ClientEvent e,
			int[] intParams, String[] stringParams, Object[] objParams) {
		if (e.type == type && Arrays.equals(e.intParams, intParams))
			return true;
		return false;
	}

	protected void doAction(Action action) {
		Widget targetWidget = ((AbstractComponentConnector) action.target)
				.getWidget();

		if (action.type == ActionType.FOCUS) {
			if (targetWidget instanceof FocusWidget) {
				((FocusWidget) targetWidget).setFocus(true);
			}
		} else if (action.type == ActionType.BLUR) {
			if (targetWidget instanceof FocusWidget) {
				((FocusWidget) targetWidget).setFocus(false);
			}
		}
	}

}

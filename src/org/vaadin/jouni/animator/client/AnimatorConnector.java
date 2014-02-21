package org.vaadin.jouni.animator.client;

import java.util.HashMap;

import org.vaadin.jouni.animator.Animator;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

@Connect(Animator.class)
public class AnimatorConnector extends AbstractExtensionConnector {

	private static final long serialVersionUID = 5590072744432016088L;

	private static final String transitionProperty = DomConnector
			.whichTransitionProperty();
	private static final String transitionEndEvent = DomConnector
			.whichTransitionEndEvent();

	private static final String animationProperty = DomConnector
			.whichAnimationProperty();
	private static final String animationEndEvent = DomConnector
			.whichAnimationEndEvent();

	private static final String keyframesRule = DomConnector
			.whichKeyframesRule();

	AnimatorServerRpc rpc = RpcProxy.create(AnimatorServerRpc.class, this);

	static HashMap<String, CssAnimation> queue = new HashMap<String, CssAnimation>();

	public AnimatorConnector() {
		registerRpc(AnimatorClientRpc.class, new AnimatorClientRpc() {

			private static final long serialVersionUID = -5974716642919513367L;

			@Override
			public void animate(final CssAnimation animation) {
				// if (animation.event == null) {
				// No event trigger, animate instantly
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						runAnimation(animation);
					}
				});
				// } else {
				// // Trigger on a particular event
				// // I really wish there was a more generic way of doing
				// // this event nonsense...
				//
				// Widget targetWidget = ((AbstractComponentConnector)
				// animation.animationTarget)
				// .getWidget();
				// if (animation.eventTarget != null) {
				// targetWidget = ((AbstractComponentConnector)
				// animation.eventTarget)
				// .getWidget();
				// }
				//
				// switch (animation.event) {
				// case BLUR:
				// targetWidget.addDomHandler(new BlurHandler() {
				// @Override
				// public void onBlur(BlurEvent event) {
				// runAnimation(animation);
				// }
				// }, BlurEvent.getType());
				// break;
				// case FOCUS:
				// targetWidget.addDomHandler(new FocusHandler() {
				// @Override
				// public void onFocus(FocusEvent event) {
				// runAnimation(animation);
				// }
				// }, FocusEvent.getType());
				// break;
				// case CLICK_PRIMARY:
				// case CLICK_SECONDARY:
				// targetWidget.addDomHandler(new ClickHandler() {
				// @Override
				// public void onClick(ClickEvent event) {
				// if (event.getNativeButton() == Integer
				// .parseInt(animation.event.getParams()[0]))
				// runAnimation(animation);
				// }
				// }, ClickEvent.getType());
				// break;
				// case MOUSEDOWN:
				// targetWidget.addDomHandler(new MouseDownHandler() {
				// @Override
				// public void onMouseDown(MouseDownEvent event) {
				// runAnimation(animation);
				// }
				// }, MouseDownEvent.getType());
				// break;
				// case MOUSEUP:
				// targetWidget.addDomHandler(new MouseUpHandler() {
				// @Override
				// public void onMouseUp(MouseUpEvent event) {
				// runAnimation(animation);
				// }
				// }, MouseUpEvent.getType());
				// case MOUSEOUT:
				// targetWidget.addDomHandler(new MouseOutHandler() {
				// @Override
				// public void onMouseOut(MouseOutEvent event) {
				// runAnimation(animation);
				// }
				// }, MouseOutEvent.getType());
				// break;
				// case MOUSEOVER:
				// targetWidget.addDomHandler(new MouseOverHandler() {
				// @Override
				// public void onMouseOver(MouseOverEvent event) {
				// runAnimation(animation);
				// }
				// }, MouseOverEvent.getType());
				// break;
				// case WINDOW_CLOSE:
				// if (targetWidget instanceof VOverlay) {
				// VOverlay overlay = (VOverlay) targetWidget;
				// overlay.addCloseHandler(new CloseHandler<PopupPanel>() {
				// @Override
				// public void onClose(CloseEvent<PopupPanel> event) {
				// runAnimation(animation);
				// }
				// });
				// } else {
				// // TODO log a warning/error message
				// }
				// default:
				// if (animation.event.toString().startsWith("KEYDOWN")) {
				// targetWidget.addDomHandler(new KeyDownHandler() {
				// @Override
				// public void onKeyDown(KeyDownEvent event) {
				// int keycode = Integer
				// .parseInt(animation.event
				// .getParams()[0]);
				// if (event.getNativeKeyCode() == keycode)
				// runAnimation(animation);
				// }
				// }, KeyDownEvent.getType());
				// }
				// }
				// }
			}

		});
	}

	static void runAnimation(CssAnimation animation) {
		Element target = ((AbstractComponentConnector) animation.animationTarget)
				.getWidget().getElement();
		Style targetStyle = target.getStyle();

		hookEvents(target);

		if (animation.useKeyframeAnimation) {
			addKeyframes(animation);
			runKeyframesAnimation(animation);
		} else {
			String newTransition = "";
			for (String propName : animation.css.properties.keySet()) {
				String transitionValue = DomConnector
						.prefixPropertyName(propName)
						+ " "
						+ animation.duration
						+ "ms "
						+ animation.easing.cssValue()
						+ " "
						+ animation.delay
						+ "ms";

				if (newTransition.length() > 0)
					newTransition += ", ";
				newTransition += transitionValue;

				// Set the target value for the transition
				String value = animation.css.properties.get(propName);
				targetStyle.setProperty(DomConnector.domPropertyName(propName),
						value);
			}

			// Re-apply old, still valid, transitions
			String oldTransitions = targetStyle.getProperty(transitionProperty);
			for (String t : oldTransitions.split(",")) {
				String transitionProp = t.split(" ")[0].trim();
				if (!newTransition.contains(transitionProp)) {
					if (newTransition.length() > 0)
						newTransition += ", ";
					newTransition += t;
				}
			}

			// Set the new transition value, which finally triggers the
			// animation
			targetStyle.setProperty(transitionProperty, newTransition);
		}
	}

	static void runKeyframesAnimation(CssAnimation animation) {
		Style targetStyle = ((AbstractComponentConnector) animation.animationTarget)
				.getWidget().getElement().getStyle();
		String value = targetStyle.getProperty(animationProperty);
		if (value.length() > 0)
			value += ", ";
		value += "animator-" + animation.id + " " + animation.duration + "ms "
				+ animation.easing.cssValue() + " " + animation.delay
				+ "ms forwards";
		targetStyle.setProperty(animationProperty, value);
		queue.put("animator-" + animation.id, animation);
	}

	// TODO TODO TODO do we need to clean up this listener, or is it collected
	// at the same time as the element itself, since this connector should be
	// collected when the target is collected?
	private static native void hookEvents(Element el)
	/*-{
	    var transitionEndEvent = @org.vaadin.jouni.animator.client.AnimatorConnector::transitionEndEvent;
	    var animationEndEvent = @org.vaadin.jouni.animator.client.AnimatorConnector::animationEndEvent;
	    var self = this;
	    
	    el.addEventListener(transitionEndEvent, function(e) {
	        @org.vaadin.jouni.animator.client.AnimatorConnector::onTransitionEnd(Lcom/google/gwt/user/client/Element;Ljava/lang/String;)(this, e.propertyName);
	    });
	    
	    el.addEventListener(animationEndEvent, function(e) {
	        self.@org.vaadin.jouni.animator.client.AnimatorConnector::onAnimationEnd(Lcom/google/gwt/user/client/Element;Ljava/lang/String;)(this, e.animationName);
	    });
	}-*/;

	static void onTransitionEnd(Element target, String propertyName) {
		// Remove transition for this property
		String transitions = target.getStyle().getProperty(transitionProperty);
		String newTransition = "";
		for (String t : transitions.split(",")) {
			if (!t.trim().contains(propertyName)) {
				if (newTransition.length() > 0)
					newTransition += ", ";
				newTransition += t;
			}
		}
		target.getStyle().setProperty(transitionProperty, newTransition);
	}

	void onAnimationEnd(Element target, String animationName) {
		CssAnimation animation = queue.get(animationName);

		// Apply animated properties as inline style
		for (String propName : animation.css.properties.keySet()) {
			target.getStyle().setProperty(
					DomConnector.domPropertyName(propName),
					animation.css.properties.get(propName));
		}

		// if (animation.event == null) {
		// Remove style element & keyframes that define the animation
		com.google.gwt.dom.client.Element keyframeStyle = Document.get()
				.getElementById("animator-" + animation.id);
		keyframeStyle.removeFromParent();
		// }

		// Remove inline style animation property
		String animProp = target.getStyle().getProperty(animationProperty);
		String newAnimProp = "";
		for (String part : animProp.split(",")) {
			if (!part.trim().startsWith("animator-" + animation.id)) {
				if (newAnimProp.length() > 0)
					newAnimProp += ", ";
				newAnimProp += part;
			}
		}
		target.getStyle().setProperty(animationProperty, newAnimProp);

		rpc.animationEnd(animation);
	}

	static String buildKeyframesRule(CssAnimation animation) {
		String keyframes = keyframesRule + " animator-" + animation.id
				+ " { 100% { ";
		for (String propName : animation.css.properties.keySet()) {
			keyframes += DomConnector.prefixPropertyName(propName) + ":"
					+ animation.css.properties.get(propName) + "; ";
		}
		keyframes += "}}";
		return keyframes;
	}

	static void addKeyframes(CssAnimation animation) {
		Element style = DOM.createElement("style");
		style.setId("animator-" + animation.id);
		style.setInnerHTML(buildKeyframesRule(animation));
		Document.get().getElementsByTagName("head").getItem(0)
				.appendChild(style);
	}

	static native void log(Object msg)
	/*-{
		console.log("ANIMATOR", msg);
	}-*/;

	@Override
	protected void extend(ServerConnector target) {
		// TODO Auto-generated method stub

	}

}

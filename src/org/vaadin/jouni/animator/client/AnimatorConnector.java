package org.vaadin.jouni.animator.client;

import java.util.HashMap;

import org.vaadin.jouni.animator.Animator;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
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

	private AbstractComponentConnector target;
	private Element targetElement;
	private Style targetStyle;

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

	HashMap<String, CssAnimation> queue = new HashMap<String, CssAnimation>();

	HashMap<String, String> propertyToTransition = new HashMap<String, String>();

	public AnimatorConnector() {
		registerRpc(AnimatorClientRpc.class, new AnimatorClientRpc() {

			private static final long serialVersionUID = -5974716642919513367L;

			@Override
			public void animate(final CssAnimation animation) {

				if (animation.useKeyframeAnimation) {
					addKeyframes(animation);
				}

				if (animation.event == null) {
					// No event trigger, animate instantly
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							runAnimation(animation);
						}
					});
				} else {
					// Trigger on a particular event
					// I really wish there was a more generic way of doing
					// this event nonsense...
					switch (animation.event) {
					case BLUR:
						target.getWidget().addDomHandler(new BlurHandler() {
							@Override
							public void onBlur(BlurEvent event) {
								runAnimation(animation);
							}
						}, BlurEvent.getType());
						break;
					case FOCUS:
						target.getWidget().addDomHandler(new FocusHandler() {
							@Override
							public void onFocus(FocusEvent event) {
								runAnimation(animation);
							}
						}, FocusEvent.getType());
						break;
					case CLICK:
						target.getWidget().addDomHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								runAnimation(animation);
							}
						}, ClickEvent.getType());
						break;
					case MOUSE_DOWN:
						target.getWidget().addDomHandler(
								new MouseDownHandler() {
									@Override
									public void onMouseDown(MouseDownEvent event) {
										runAnimation(animation);
									}
								}, MouseDownEvent.getType());
						break;
					case MOUSE_UP:
						target.getWidget().addDomHandler(new MouseUpHandler() {
							@Override
							public void onMouseUp(MouseUpEvent event) {
								runAnimation(animation);
							}
						}, MouseUpEvent.getType());
					case MOUSE_OUT:
						target.getWidget().addDomHandler(new MouseOutHandler() {
							@Override
							public void onMouseOut(MouseOutEvent event) {
								runAnimation(animation);
							}
						}, MouseOutEvent.getType());
						break;
					case MOUSE_OVER:
						target.getWidget().addDomHandler(
								new MouseOverHandler() {
									@Override
									public void onMouseOver(MouseOverEvent event) {
										runAnimation(animation);
									}
								}, MouseOverEvent.getType());
						break;
					}
				}
			}

		});
	}

	void runAnimation(CssAnimation animation) {
		if (animation.useKeyframeAnimation) {
			runKeyframesAnimation(animation);
		} else {
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
				propertyToTransition.put(
						DomConnector.prefixPropertyName(propName),
						transitionValue);

				String value = animation.css.properties.get(propName);
				targetStyle.setProperty(DomConnector.domPropertyName(propName),
						value);
			}
			applyTransitions();
		}
	}

	void applyTransitions() {
		String transitions = "";
		for (String t : propertyToTransition.values()) {
			if (transitions.length() > 0)
				transitions += ", ";
			transitions += t;
		}
		targetStyle.setProperty(transitionProperty, transitions);
	}

	void runKeyframesAnimation(CssAnimation animation) {
		String value = targetStyle.getProperty(animationProperty);
		if (value.length() > 0)
			value += ", ";
		value += "animator-" + animation.id + " " + animation.duration + "ms "
				+ animation.easing.cssValue() + " " + animation.delay
				+ "ms forwards";
		targetStyle.setProperty(animationProperty, value);
		queue.put("animator-" + animation.id, animation);
	}

	@Override
	protected void extend(ServerConnector target) {
		this.target = (AbstractComponentConnector) target;
		targetElement = this.target.getWidget().getElement();
		targetStyle = targetElement.getStyle();
		hookEvents();
	}

	// TODO TODO TODO do we need to clean up this listener, or is it collected
	// at the same time as the element itself, since this connector should be
	// collected when the target is collected?
	private native void hookEvents()
	/*-{
	    var el = this.@org.vaadin.jouni.animator.client.AnimatorConnector::targetElement;
	    var transitionEndEvent = @org.vaadin.jouni.animator.client.AnimatorConnector::transitionEndEvent;
	    var animationEndEvent = @org.vaadin.jouni.animator.client.AnimatorConnector::animationEndEvent;
	    var self = this;
	    
	    el.addEventListener(transitionEndEvent, function(e) {
	        self.@org.vaadin.jouni.animator.client.AnimatorConnector::onTransitionEnd(Ljava/lang/String;)(e.propertyName);
	    });
	    
	    el.addEventListener(animationEndEvent, function(e) {
	        self.@org.vaadin.jouni.animator.client.AnimatorConnector::onAnimationEnd(Ljava/lang/String;)(e.animationName);
	    });
	}-*/;

	void onTransitionEnd(String propertyName) {
		// Remove transition for this property
		propertyToTransition.remove(propertyName);
		applyTransitions();
	}

	void onAnimationEnd(String animationName) {
		CssAnimation animation = queue.get(animationName);

		// Apply animated properties as inline style
		for (String propName : animation.css.properties.keySet()) {
			targetStyle.setProperty(DomConnector.domPropertyName(propName),
					animation.css.properties.get(propName));
		}

		if (animation.event == null) {
			// Remove style element & keyframes that define the animation
			com.google.gwt.dom.client.Element keyframeStyle = Document.get()
					.getElementById("animator-" + animation.id);
			keyframeStyle.removeFromParent();
		}

		// Remove inline style animation property
		String animProp = targetStyle.getProperty(animationProperty);
		String newAnimProp = "";
		for (String part : animProp.split(",")) {
			if (!part.trim().startsWith("animator-" + animation.id)) {
				if (newAnimProp.length() > 0)
					newAnimProp += ", ";
				newAnimProp += part;
			}
		}
		targetStyle.setProperty(animationProperty, newAnimProp);

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

	native void log(String msg)
	/*-{
		console.log("ANIMATOR", msg);
	}-*/;

}

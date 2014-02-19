package org.vaadin.jouni.animator.client;

import java.util.HashMap;

import org.vaadin.jouni.animator.Dom;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

@Connect(Dom.class)
public class DomConnector extends AbstractExtensionConnector {

	private static final long serialVersionUID = -7807146623985961818L;

	private AbstractComponentConnector target;
	private Element targetElement;
	private Style targetStyle;

	static final String transformProperty = whichTransformProperty();

	public static native String whichTransitionProperty()
	/*-{
	    var el = document.createElement('fakeelement');
	    var transitions = [
	      'transition',
	      'OTransition',
	      'MozTransition',
	      'WebkitTransition'
	    ]
	
	    for(var i=0; i < transitions.length; i++) {
	        if( el.style[transitions[i]] !== undefined ){
	            return transitions[i];
	        }
	    }
	}-*/;

	public static native String whichAnimationProperty()
	/*-{
	    var el = document.createElement('fakeelement');
	    var anims = [
	      'animation',
	      'OAnimation',
	      'MozAnimation',
	      'WebkitAnimation'
	    ]
	
	    for(var i=0; i < anims.length; i++) {
	        if( el.style[anims[i]] !== undefined ){
	            return anims[i];
	        }
	    }
	}-*/;

	public static native String whichAnimationEndEvent()
	/*-{
	    var el = document.createElement('fakeelement');
	    var anims = {
	      'animationName': 'animationend',
	      'OAnimationName': 'oAnimationEnd',
	      'MozAnimation': 'animationend',
	      'WebkitAnimation': 'webkitAnimationEnd'
	    }
	
	    for(var a in anims){
	        if( el.style[a] !== undefined ){
	            return anims[a];
	        }
	    }
	}-*/;

	public static native String whichTransitionEndEvent()
	/*-{
	    var el = document.createElement('fakeelement');
	    var transitions = {
	      'transition': 'transitionend',
	      'OTransition': 'oAnimationEnd',
	      'MozTransition': 'transitionend',
	      'WebkitTransition': 'webkitTransitionEnd'
	    }
	
	    for(var t in transitions){
	        if( el.style[t] !== undefined ){
	            return transitions[t];
	        }
	    }
	}-*/;

	public static native String whichTransformProperty()
	/*-{
	    var t;
	    var el = document.createElement('fakeelement');
	    var transforms = {
	      'transform': 'transform',
	      'MozTransform': '-moz-transform',
	      'WebkitTransform': '-webkit-transform'
	    }
	
	    for(t in transforms){
	        if( el.style[t] !== undefined ){
	            return transforms[t];
	        }
	    }
	}-*/;

	public static native String whichKeyframesRule()
	/*-{
	    var el = document.createElement('fakeelement');
	    var keyframes = {
	      'animationName': '@keyframes',
	      'OAnimationName': '@-o-keyframes',
	      'MozAnimation': '@-moz-keyframes',
	      'WebkitAnimation': '@-webkit-keyframes'
	    }
	
	    for(var k in keyframes){
	        if( el.style[k] !== undefined ){
	            return keyframes[k];
	        }
	    }
	}-*/;

	@Override
	protected void extend(ServerConnector target) {
		this.target = (AbstractComponentConnector) target;
		targetElement = this.target.getWidget().getElement();
		targetStyle = targetElement.getStyle();
	}

	@Override
	public DomState getState() {
		return (DomState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);
		applyStyles();
		applyAttributes();
	}

	public void applyStyles() {
		applyStyles(getState().css.properties, targetElement);
	}

	public static void applyStyles(HashMap<String, String> styles,
			Element target) {
		for (String propName : styles.keySet()) {
			String value = styles.get(propName);
			propName = domPropertyName(propName);
			if (value != null) {
				target.getStyle().setProperty(propName, value);
			} else {
				target.getStyle().clearProperty(propName);
			}
		}
	}

	public void applyAttributes() {
		for (String attrName : getState().attributes.keySet()) {
			String value = getState().attributes.get(attrName);
			if (value != null) {
				targetElement.setAttribute(attrName, value);
			} else {
				targetElement.setAttribute(attrName, "");
			}
		}
	}

	public static native String toCamelCase(String text)
	/*-{
	    return text.replace(/-([a-z])/g, function (g) { return g[1].toUpperCase(); });
	}-*/;

	public static String prefixPropertyName(String propName) {
		if (propName.startsWith("transform"))
			propName = propName.replace("transform", transformProperty);
		return propName;
	}

	public static String domPropertyName(String cssPropertyName) {
		return toCamelCase(prefixPropertyName(cssPropertyName));
	}
}

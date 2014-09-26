package org.vaadin.jouni.animator.client;

import java.util.HashMap;
import java.util.logging.Logger;

import org.vaadin.jouni.animator.Animator;
import org.vaadin.jouni.dom.client.DomConnector;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
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
                // TODO set the 'from' properties on the targets
                Timer t = new Timer() {
                    @Override
                    public void run() {
                        runAnimation(animation);

                        if (animation.preserveStyles) {
                            rpc.preserveStyles(animation);
                        }

                    }
                };
                t.schedule(animation.delay);
            }

        });
    }

    public static void runAnimation(final CssAnimation animation) {
        if (animation.animationTargets == null
                || animation.animationTargets.length == 0) {
            getLogger().warning(
                    "No targets specified for this CssAnimation: " + animation);
            return;
        }
        if (animation.useKeyframeAnimation) {
            addKeyframes(animation);
            runKeyframesAnimation(animation);
        } else {
            Scheduler.get().scheduleFinally(new ScheduledCommand() {
                @Override
                public void execute() {
                    for (int i = 0; i < animation.animationTargets.length; i++) {
                        Element target = ((AbstractComponentConnector) animation.animationTargets[i])
                                .getWidget().getElement();
                        final Style targetStyle = target.getStyle();

                        hookEvents(target, true, false);

                        String newTransition = "";
                        for (String propName : animation.to.properties.keySet()) {
                            String transitionValue = DomConnector
                                    .prefixPropertyName(propName)
                                    + " "
                                    + animation.duration
                                    + "ms "
                                    + animation.easing.cssValue();

                            if (newTransition.length() > 0) {
                                newTransition += ", ";
                            }
                            newTransition += transitionValue;

                            // Set the target value for the transition
                            String value = animation.to.properties
                                    .get(propName);
                            targetStyle.setProperty(
                                    DomConnector.domPropertyName(propName),
                                    value);
                        }

                        // Re-apply old, still valid, transitions
                        String oldTransitions = targetStyle
                                .getProperty(transitionProperty);
                        for (String t : oldTransitions.split(",")) {
                            String transitionProp = t.split(" ")[0].trim();
                            if (!newTransition.contains(transitionProp)) {
                                if (newTransition.length() > 0) {
                                    newTransition += ", ";
                                }
                                newTransition += t;
                            }
                        }
                        final String finalTransition = newTransition;

                        // Set the new transition value, which finally triggers
                        // the animation

                        targetStyle.setProperty(transitionProperty,
                                finalTransition);
                    }

                }
            });
        }
    }

    static void runKeyframesAnimation(CssAnimation animation) {
        if (animation.animationTargets == null
                || animation.animationTargets.length == 0) {
            getLogger().warning(
                    "No targets specified for this CssAnimation: " + animation);
            return;
        }

        // Only one target needs the end listener
        Element target = ((AbstractComponentConnector) animation.animationTargets[0])
                .getWidget().getElement();
        hookEvents(target, false, true);

        for (int i = 0; i < animation.animationTargets.length; i++) {
            target = ((AbstractComponentConnector) animation.animationTargets[i])
                    .getWidget().getElement();
            Style targetStyle = target.getStyle();
            String value = targetStyle.getProperty(animationProperty);
            if (value.length() > 0) {
                value += ", ";
            }
            value += "animator-" + animation.id + " " + animation.duration
                    + "ms " + animation.easing.cssValue() + " forwards";
            targetStyle.setProperty(animationProperty, value);
        }

        if (!queue.containsKey("animator-" + animation.id)) {
            queue.put("animator-" + animation.id, animation);
        }
    }

    // TODO TODO TODO do we need to clean up this listener, or is it collected
    // at the same time as the element itself, since this connector should be
    // collected when the target is collected?
    private static native void hookEvents(Element el, boolean transitionEnd,
            boolean animationEnd)
    /*-{
        var transitionEndEvent = @org.vaadin.jouni.animator.client.AnimatorConnector::transitionEndEvent;
        var animationEndEvent = @org.vaadin.jouni.animator.client.AnimatorConnector::animationEndEvent;
        var self = this;
        
        if(transitionEnd) {
            el.addEventListener(transitionEndEvent, function(e) {
                @org.vaadin.jouni.animator.client.AnimatorConnector::onTransitionEnd(Lcom/google/gwt/dom/client/Element;Ljava/lang/String;)(this, e.propertyName);
            });
        }
        
        if(animationEnd) {
            el.addEventListener(animationEndEvent, function(e) {
                self.@org.vaadin.jouni.animator.client.AnimatorConnector::onAnimationEnd(Lcom/google/gwt/dom/client/Element;Ljava/lang/String;)(this, e.animationName);
            });
        }
    }-*/;

    static void onTransitionEnd(Element target, String propertyName) {
        // Remove transition for this property
        String transitions = target.getStyle().getProperty(transitionProperty);
        String newTransition = "";
        for (String t : transitions.split(",")) {
            if (!t.trim().contains(propertyName)) {
                if (newTransition.length() > 0) {
                    newTransition += ", ";
                }
                newTransition += t;
            }
        }
        target.getStyle().setProperty(transitionProperty, newTransition);
        // TODO set the end value as an inline style property (to allow any
        // queued keyframe animations to start properly from this state)
    }

    void onAnimationEnd(Element target, String animationName) {
        CssAnimation animation = queue.get(animationName);

        // Apply animated properties as inline style
        for (String propName : animation.to.properties.keySet()) {
            target.getStyle().setProperty(
                    DomConnector.domPropertyName(propName),
                    animation.to.properties.get(propName));
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
                if (newAnimProp.length() > 0) {
                    newAnimProp += ", ";
                }
                newAnimProp += part;
            }
        }
        target.getStyle().setProperty(animationProperty, newAnimProp);

        rpc.animationEnd(animation);
    }

    static String buildKeyframesRule(CssAnimation animation) {
        String keyframes = keyframesRule + " animator-" + animation.id
                + " { 100% { ";
        for (String propName : animation.to.properties.keySet()) {
            keyframes += DomConnector.prefixPropertyName(propName) + ":"
                    + animation.to.properties.get(propName) + "; ";
        }
        keyframes += "}}";
        return keyframes;
    }

    static void addKeyframes(CssAnimation animation) {
        String styleId = "animator-" + animation.id;
        if (Document.get().getElementById(styleId) == null) {
            Element style = DOM.createElement("style");
            style.setId(styleId);
            style.setInnerHTML(buildKeyframesRule(animation));
            Document.get().getElementsByTagName("head").getItem(0)
                    .appendChild(style);
        }
    }

    static Logger getLogger() {
        return Logger.getLogger(AnimatorConnector.class.getName());
    }

    @Override
    protected void extend(ServerConnector target) {
        // TODO Auto-generated method stub
    }

}

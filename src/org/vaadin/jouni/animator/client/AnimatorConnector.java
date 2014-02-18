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

    private AbstractComponentConnector target;
    private Element targetElement;
    private Style targetStyle;

    static final String animationProperty = DomConnector.whichAnimation();
    private final String animationEndEvent = DomConnector.whichAnimationEvent();
    private final String keyframesRule = DomConnector.whichKeyframes();

    AnimatorServerRpc rpc = RpcProxy.create(AnimatorServerRpc.class, this);

    HashMap<String, CssAnimation> queue = new HashMap<String, CssAnimation>();

    public AnimatorConnector() {
        registerRpc(AnimatorClientRpc.class, new AnimatorClientRpc() {

            @Override
            public void animate(final CssAnimation animation) {
                // Build keyframes declaration
                String keyframes = keyframesRule + " animator-" + animation.id
                        + " { 100% { ";
                for (String propName : animation.css.properties.keySet()) {
                    keyframes += DomConnector.prefixPropertyName(propName)
                            + ":" + animation.css.properties.get(propName)
                            + "; ";
                }
                keyframes += "}}";
                addKeyframes(animation.id, keyframes);

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

                }
            }

        });
    }

    void runAnimation(CssAnimation animation) {
        String value = targetStyle.getProperty(animationProperty);
        if (value.length() > 0)
            value += ", ";
        value += "animator-" + animation.id + " " + animation.duration + "ms "
                + animation.easing + " " + animation.delay + "ms forwards";
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
        var eventName = this.@org.vaadin.jouni.animator.client.AnimatorConnector::animationEndEvent;
        var self = this;
        
        el.addEventListener(eventName, function(e) {
            self.@org.vaadin.jouni.animator.client.AnimatorConnector::onAnimationEnd(Ljava/lang/String;)(e.animationName);
        });
    }-*/;

    public void onAnimationEnd(String animationName) {
        CssAnimation animation = queue.get(animationName);
        for (String propName : animation.css.properties.keySet()) {
            targetStyle.setProperty(DomConnector.domPropertyName(propName),
                    animation.css.properties.get(propName));
            com.google.gwt.dom.client.Element keyframeStyle = Document.get()
                    .getElementById("animator-" + animation.id);
            keyframeStyle.removeFromParent();
            // TODO remove the animation from the elements inline style
        }
    }

    static void addKeyframes(int id, String keyframes) {
        Element style = DOM.createElement("style");
        style.setId("animator-" + id);
        style.setInnerHTML(keyframes);
        Document.get().getElementsByTagName("head").getItem(0)
                .appendChild(style);
    }

}

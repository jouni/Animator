package org.vaadin.jouni.animator;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.vaadin.jouni.animator.client.Animation;
import org.vaadin.jouni.animator.client.AnimatorClientRpc;
import org.vaadin.jouni.animator.client.AnimatorServerRpc;
import org.vaadin.jouni.dom.Dom;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Extension;
import com.vaadin.shared.Connector;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.util.ReflectTools;

public class Animator extends AbstractExtension {

    private static final long serialVersionUID = 2876055108100881743L;

    private HashMap<AbstractComponent, Dom> targetToDom = new HashMap<AbstractComponent, Dom>();

    private AnimatorServerRpc rpc = new AnimatorServerRpc() {
        private static final long serialVersionUID = 6125808362723118718L;

        @Override
        public void animationEnd(Animation animation) {
            // TODO only fire for listeners of _this_ animation
            fireAnimationEndEvent(animation);
        }

        @Override
        public void preserveStyles(Animation animation) {
            if (animation.preserveStyles && animation.animationTargets != null) {
                for (Connector target : animation.animationTargets) {
                    if (target == null || target.getParent() == null) {
                        continue;
                    }

                    // TODO these need to be cleaned when the component is not
                    // used anymore. Perhaps use a WeakHashMap?
                    Dom dom = targetToDom.get(target);
                    if (dom == null) {
                        dom = new Dom((AbstractClientConnector) target);
                        targetToDom.put((AbstractComponent) target, dom);
                    }

                    for (String prop : animation.to.properties.keySet()) {
                        dom.getStyle().setProperty(prop,
                                animation.to.properties.get(prop));
                    }

                    // These updates can be lazy, so no need to send to client
                    dom.getUI().getConnectorTracker().markClean(dom);
                }
            }
        }
    };

    protected Animator(UI target) {
        super.extend(target);
        registerRpc(rpc);
    }

    public static Animation add(Animation animation) {
        return add(animation, null);
    }

    public static Animation add(Animation animation, AnimationListener listener) {
        Animator animator = null;
        UI ui = ((AbstractClientConnector) animation.animationTargets[0])
                .getUI();
        if (ui == null) {
            ui = UI.getCurrent();
        }
        for (Extension ex : ui.getExtensions()) {
            if (ex instanceof Animator) {
                animator = (Animator) ex;
            }
        }
        if (animator == null) {
            animator = new Animator(ui);
        }

        if (listener != null) {
            animator.addListener(listener);
        }

        animator.getRpcProxy(AnimatorClientRpc.class).animate(animation);
        return animation;
    }

    protected void fireAnimationEndEvent(Animation animation) {
        fireEvent(new AnimationEndEvent((Component) getParent(), animation));
    }

    public interface AnimationListener {
        public static final Method animMethod = ReflectTools.findMethod(
                AnimationListener.class, "onAnimationEnd",
                AnimationEndEvent.class);

        public void onAnimationEnd(AnimationEndEvent event);
    }

    protected void addListener(AnimationListener listener) {
        addListener(AnimationEndEvent.EVENT_ID, AnimationEndEvent.class,
                listener, AnimationListener.animMethod);
    }

    public void removeListener(AnimationListener listener) {
        removeListener(AnimationEndEvent.EVENT_ID, AnimationEndEvent.class,
                listener);
    }

    public class AnimationEndEvent extends Component.Event {

        private static final long serialVersionUID = 2181324846162279412L;

        public static final String EVENT_ID = "animation";
        private Animation animation;

        public AnimationEndEvent(Component source, Animation animation) {
            super(source);
            this.animation = animation;
        }

        public Animation getAnimation() {
            return animation;
        }

        @Override
        public String toString() {
            return animation.toString();
        }
    }

}

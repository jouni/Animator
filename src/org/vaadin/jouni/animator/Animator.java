package org.vaadin.jouni.animator;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.vaadin.jouni.animator.client.AnimatorClientRpc;
import org.vaadin.jouni.animator.client.AnimatorServerRpc;
import org.vaadin.jouni.animator.client.CssAnimation;
import org.vaadin.jouni.dom.Dom;
import org.vaadin.jouni.dom.client.Css;

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

    private static Animator currentAnimator;

    private CssAnimation currentAnimation;

    private HashMap<AbstractComponent, Dom> targetToDom = new HashMap<AbstractComponent, Dom>();

    private AnimatorServerRpc rpc = new AnimatorServerRpc() {
        private static final long serialVersionUID = 6125808362723118718L;

        @Override
        public void animationEnd(CssAnimation animation) {
            fireAnimationEndEvent(animation);
        }

        @Override
        public void preserveStyles(CssAnimation animation) {
            if (animation.preserveStyles && animation.animationTargets != null) {
                for (Connector target : animation.animationTargets) {
                    if (target == null) {
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

    public static Animator animate(CssAnimation animation) {
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
        currentAnimator = animator;
        animator.currentAnimation = animation;
        return animator;
    }

    public static Animator animate(AbstractComponent... targets) {
        if (targets.length == 0) {
            throw new IllegalArgumentException(
                    "You need to specify at least one animation target.");
        }
        return animate(new CssAnimation(targets));
    }

    public Animator from(Css css) {
        if (currentAnimation == null) {
            throw new IllegalStateException(
                    "No animation targets specified. Call the 'animate' method first.");
        }
        currentAnimation.from = css;
        return currentAnimator;
    }

    public Animator to(Css css) {
        return to(css, CssAnimation.DEFAULT_DURATION);
    }

    public Animator to(Css css, int duration) {
        if (currentAnimation == null) {
            throw new IllegalStateException(
                    "No animation targets specified. Call the 'animate' method first.");
        }
        currentAnimation.to = css;
        currentAnimation.duration = duration;
        sendAnimation(currentAnimation);
        return this;
    }

    protected void sendAnimation(CssAnimation animation) {
        getRpcProxy(AnimatorClientRpc.class).animate(animation);
    }

    public Animator queue(CssAnimation next) {
        next.delay += currentAnimation.delay + currentAnimation.duration;
        sendAnimation(next);
        currentAnimation = next;
        return this;
    }

    protected void fireAnimationEndEvent(CssAnimation animation) {
        fireEvent(new AnimationEndEvent((Component) getParent(), animation));
    }

    public interface AnimationListener {
        public static final Method animMethod = ReflectTools.findMethod(
                AnimationListener.class, "animationEnd",
                AnimationEndEvent.class);

        public void animationEnd(AnimationEndEvent event);
    }

    public void addListener(AnimationListener listener) {
        addListener(AnimationEndEvent.EVENT_ID, AnimationEndEvent.class,
                listener, AnimationListener.animMethod);
    }

    public void removeListener(AnimationListener listener) {
        removeListener(AnimationEndEvent.EVENT_ID, AnimationEndEvent.class,
                listener);
    }

    public static class AnimationEndEvent extends Component.Event {

        private static final long serialVersionUID = 2181324846162279412L;

        public static final String EVENT_ID = "animation";
        private CssAnimation animation;

        public AnimationEndEvent(Component source, CssAnimation animation) {
            super(source);
            this.animation = animation;
        }

        public CssAnimation getAnimation() {
            return animation;
        }

        @Override
        public String toString() {
            return animation.toString();
        }
    }

}

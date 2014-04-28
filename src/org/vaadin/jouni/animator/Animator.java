package org.vaadin.jouni.animator;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.vaadin.jouni.animator.client.AnimatorClientRpc;
import org.vaadin.jouni.animator.client.AnimatorServerRpc;
import org.vaadin.jouni.animator.client.CssAnimation;
import org.vaadin.jouni.dom.Dom;
import org.vaadin.jouni.dom.client.Css;

import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Extension;
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
        public void animationEnd(CssAnimation animation) {
            fireAnimationEndEvent(animation);
        }

        @Override
        public void preserveStyles(CssAnimation animation) {
            if (animation.preserveStyles && animation.animationTarget != null) {
                AbstractComponent target = (AbstractComponent) animation.animationTarget;

                // TODO these need to be cleaned when the component is not used
                // anymore. Perhaps use a WeakHashMap?
                Dom dom = targetToDom.get(target);
                if (dom == null) {
                    dom = new Dom(target);
                    targetToDom.put(target, dom);
                }

                for (String prop : animation.css.properties.keySet()) {
                    dom.getStyle().setProperty(prop,
                            animation.css.properties.get(prop));
                }

                // These updates can be lazy, so no need to send to client
                // dom.getUI().getConnectorTracker().markClean(dom);
            }
        }
    };

    private Animator(UI target) {
        super.extend(target);
        registerRpc(rpc);
    }

    public static CssAnimation animate(CssAnimation animation) {
        if (animation.animationTarget == null) {
            throw new IllegalStateException("Animation target can not be null");
        }
        Animator animator = null;
        UI ui = ((AbstractComponent) animation.animationTarget).getUI();
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
        animator.sendAnimation(animation);
        return animation;
    }

    public static CssAnimation animate(AbstractComponent target, Css properties) {
        return animate(new CssAnimation(target, properties));
    }

    protected void sendAnimation(CssAnimation animation) {
        getRpcProxy(AnimatorClientRpc.class).animate(animation);
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

package org.vaadin.jouni.animator;

import org.vaadin.jouni.animator.client.AnimatorClientRpc;
import org.vaadin.jouni.animator.client.AnimatorServerRpc;
import org.vaadin.jouni.animator.client.ClientEvent;
import org.vaadin.jouni.animator.client.Css;
import org.vaadin.jouni.animator.client.CssAnimation;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;

public class Animator extends AbstractExtension {

    // HashMap<Integer, CssAnimation> queue = new HashMap<Integer,
    // CssAnimation>();

    private AnimatorServerRpc rpc = new AnimatorServerRpc() {
        @Override
        public void animationEnd(int id) {
            // CssAnimation styles = queue.get(id);
            // queue.remove(id);

            // TODO trigger listeners for given id
        }
    };

    public Animator(AbstractClientConnector target) {
        super.extend(target);
        registerRpc(rpc);
    }

    public CssAnimation animateOn(AbstractClientConnector eventTarget,
            ClientEvent event, Css properties) {

        CssAnimation anim = new CssAnimation();
        if (eventTarget != null) {
            anim.eventTarget = eventTarget;
        } else {
            anim.eventTarget = (AbstractClientConnector) getParent();
        }
        anim.event = event;
        anim.css = properties;

        // queue.put(anim.id, anim);

        getRpcProxy(AnimatorClientRpc.class).animate(anim);

        return anim;
    }

    // public Animator animate(Css styles, int duration, int delay, Ease easing)
    // {
    // styles.persist = true;
    //
    // int id = (int) (Math.random() * 10000);
    //
    // getRpcProxy(AnimatorClientRpc.class).animate(
    // styles,
    // duration,
    // delay,
    // easing,
    // id,
    // triggerOnEvent != null ? ClientEvent.valueOf(triggerOnEvent
    // .name()) : null);
    //
    // triggerOnEvent = null;
    //
    // queue.put(id, styles);
    //
    // return this;
    // }
    //
    // public Animator animate(Css styles, int duration, int delay) {
    // animate(styles, duration, delay, Ease.DEFAULT);
    // return this;
    // }
    //
    // public Animator animate(Css styles, int duration) {
    // animate(styles, duration, 0, Ease.DEFAULT);
    // return this;
    // }
    //
    // private ClientEvent triggerOnEvent = null;
    //
    // public Animator on(AbstractClientConnector target, ClientEvent event) {
    // triggerOnEvent = event;
    // return this;
    // }

}

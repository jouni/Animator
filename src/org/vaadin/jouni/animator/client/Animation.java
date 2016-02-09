package org.vaadin.jouni.animator.client;

import org.vaadin.jouni.dom.client.Css;

import com.vaadin.shared.Connector;

public class Animation {

    public static final int DEFAULT_DURATION = 200;

    static int idCounter = 0;
    public int id = idCounter++;

    public Connector[] animationTargets = null;
    public Css from = new Css();
    public Css to = new Css();

    public int delay = 0;
    public float[] spring = new float[] { 120, 10, 3 };

    public boolean preserveStyles = true;

    public Animation() {

    }

    public Animation(Connector... targets) {
        if (targets.length == 0) {
            throw new IllegalArgumentException(
                    "You need to specify at least one animation target.");
        }
        animationTargets = targets;
    }

    public Animation delay(int millis) {
        delay = millis;
        return this;
    }

    public Animation preserveStyles(boolean preserve) {
        preserveStyles = preserve;
        return this;
    }

    public Animation from(Css css) {
        from = css;
        return this;
    }

    public Animation to(Css css) {
        to = css;
        return this;
    }

    public Animation spring(float stiffness, float mass, float friction) {
        spring = new float[] { stiffness, mass, friction };
        return this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id: " + id + ", targets: "
                + animationTargets + ", from: " + from + ", to: " + to
                + ", delay: " + delay + "]";
    }
}

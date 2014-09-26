package org.vaadin.jouni.animator.client;

import org.vaadin.jouni.dom.client.Css;

import com.vaadin.shared.Connector;

public class CssAnimation {

    public static final int DEFAULT_DURATION = 200;

    static int idCounter = 0;

    public int id = idCounter++;

    public Connector[] animationTargets = null;

    public Css from = new Css();

    public Css to = new Css();

    public int duration = DEFAULT_DURATION;

    public int delay = 0;

    public int iterationCount = 1;

    public Ease easing = Ease.DEFAULT;

    public boolean useKeyframeAnimation = false;

    public boolean preserveStyles = true;

    public CssAnimation() {

    }

    public CssAnimation(Connector... targets) {
        if (targets.length == 0) {
            throw new IllegalArgumentException(
                    "You need to specify at least one animation target.");
        }
        animationTargets = targets;
    }

    public CssAnimation duration(int millis) {
        duration = millis;
        return this;
    }

    public CssAnimation delay(int millis) {
        delay = millis;
        return this;
    }

    public CssAnimation ease(Ease easing) {
        this.easing = easing;
        return this;
    }

    public CssAnimation sendEndEvent() {
        useKeyframeAnimation = true;
        return this;
    }

    public CssAnimation preserveStyles(boolean preserve) {
        preserveStyles = preserve;
        return this;
    }

    public CssAnimation from(Css css) {
        from = css;
        return this;
    }

    public CssAnimation to(Css css) {
        to = css;
        return this;
    }

    @Override
    public String toString() {
        return "CssAnimation[id: " + id + ", targets: " + animationTargets
                + ", from: " + from + ", to: " + to + ", duration: " + duration
                + ", delay: " + delay + ", iterations: " + iterationCount
                + ", " + easing + ", keyframe-animation: "
                + useKeyframeAnimation + "]";
    }
}

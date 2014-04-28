package org.vaadin.jouni.animator.client;

import org.vaadin.jouni.dom.client.Css;

import com.vaadin.shared.Connector;

public class CssAnimation {

    public static final int DEFAULT_DURATION = 200;

    static int idCounter = 0;

    public int id = idCounter++;

    public Connector animationTarget = null;

    public Css css = new Css();

    public int duration = DEFAULT_DURATION;

    public int delay = 0;

    public int iterationCount = 1;

    public Ease easing = Ease.DEFAULT;

    public boolean useKeyframeAnimation = false;

    public boolean preserveStyles = true;

    public CssAnimation() {

    }

    public CssAnimation(Connector target, Css css) {
        animationTarget = target;
        this.css = css;
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

    @Override
    public String toString() {
        return "CssAnimation[" + id + ", " + animationTarget + ", " + css
                + ", " + duration + ", " + delay + ", " + iterationCount + ", "
                + easing + ", " + useKeyframeAnimation + "]";
    }
}

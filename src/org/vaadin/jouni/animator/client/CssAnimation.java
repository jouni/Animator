package org.vaadin.jouni.animator.client;

import com.vaadin.shared.Connector;

public class CssAnimation {

    static int idCounter = 0;

    public int id = idCounter++;

    public Connector animationTarget = null;

    public Css css = new Css();

    public int duration = 200;

    public int delay = 0;

    public int iterationCount = 1;

    public Ease easing = Ease.DEFAULT;

    public boolean useKeyframeAnimation = false;

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

}

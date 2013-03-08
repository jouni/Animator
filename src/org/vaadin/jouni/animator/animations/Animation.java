package org.vaadin.jouni.animator.animations;

import org.vaadin.jouni.animator.shared.AnimationState;
import org.vaadin.jouni.animator.shared.AnimationType;

public class Animation {

    private static int idCount = 0;

    private AnimationState state = new AnimationState();

    public Animation() {
        state.id = idCount++;
    }

    public Animation(AnimationType type) {
        super();
        state.type = type;
    }

    public AnimationState getState() {
        return state;
    }

    public Animation setDuration(int millis) {
        state.duration = millis;
        return this;
    }

    public Animation setDelay(int millis) {
        state.delay = millis;
        return this;
    }

    public Animation setData(String data) {
        state.data = data;
        return this;
    }

    public int getDuration() {
        return state.duration;
    }

    public int getDelay() {
        return state.delay;
    }

    public AnimationType getType() {
        return state.type;
    }

    public String getData() {
        return state.data;
    }

    public Animation cancel() {
        state.cancelled = true;
        return this;
    }

    public boolean isCancelled() {
        return state.cancelled;
    }

    @Override
    public String toString() {
        return "Animation[" + getType() + ", duration=" + getDuration()
                + ", delay=" + getDelay()
                + (getData() != null ? ", data=" + getData() : "") + ", "
                + (isCancelled() ? ": cancelled" : "") + "]";
    }
}

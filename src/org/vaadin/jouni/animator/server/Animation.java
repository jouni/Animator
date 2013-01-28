package org.vaadin.jouni.animator.server;

import org.vaadin.jouni.animator.shared.AnimationState;
import org.vaadin.jouni.animator.shared.AnimationType;

public class Animation {

    private static int idCount = 0;

    private AnimationState state = new AnimationState();

    public Animation() {
        state.setId(idCount++);
    }

    public Animation(AnimationType type) {
        super();
        state.setType(type);
    }

    public AnimationState getState() {
        return state;
    }

    public Animation setDuration(int millis) {
        state.setDuration(millis);
        return this;
    }

    public Animation setDelay(int millis) {
        state.setDelay(millis);
        return this;
    }

    public Animation setData(String data) {
        state.setData(data);
        return this;
    }

    public int getDuration() {
        return state.getDuration();
    }

    public int getDelay() {
        return state.getDelay();
    }

    public AnimationType getType() {
        return state.getType();
    }

    public String getData() {
        return state.getData();
    }

    public Animation cancel() {
        state.setCancelled(true);
        return this;
    }

    public boolean isCancelled() {
        return state.isCancelled();
    }

    @Override
    public String toString() {
        return "Animation[" + getType() + ", duration=" + getDuration()
                + ", delay=" + getDelay()
                + (getData() != null ? ", data=" + getData() : "") + ", "
                + (isCancelled() ? ": cancelled" : "") + "]";
    }
}

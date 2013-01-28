package org.vaadin.jouni.animator.shared;

/**
 * This is an internal class used by the Animator add-on, do not use in your
 * code directly.
 */
public class AnimationState {

    private int id;
    private int duration = 200;
    private int delay = 0;
    private String data;
    private AnimationType type;
    private boolean cancelled = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public AnimationType getType() {
        return type;
    }

    public void setType(AnimationType type) {
        this.type = type;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}

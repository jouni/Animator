package org.vaadin.jouni.animator.shared;

/**
 * This is an internal class used by the Animator add-on, do not use in your
 * code directly.
 */
public class AnimationState {

    public int id;
    public int duration = 200;
    public int delay = 0;
    public String data;
    public AnimationType type;
    public boolean cancelled = false;

}

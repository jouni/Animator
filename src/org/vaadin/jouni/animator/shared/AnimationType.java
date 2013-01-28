package org.vaadin.jouni.animator.shared;

public enum AnimationType {

    /**
     * Fades in the target.
     */
    FADE_IN,
    /**
     * Fades out the component.
     */
    FADE_OUT;

    @Override
    public String toString() {
        return super.toString().toLowerCase().replace("_", "-");
    }

}
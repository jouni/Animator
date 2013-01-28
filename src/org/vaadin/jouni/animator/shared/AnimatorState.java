package org.vaadin.jouni.animator.shared;

import java.util.LinkedList;
import java.util.List;

import com.vaadin.shared.communication.SharedState;

/**
 * This is an internal class used by the Animator add-on, do not use in your
 * code directly.
 */
public class AnimatorState extends SharedState {

    public List<AnimationState> queue = new LinkedList<AnimationState>();

}

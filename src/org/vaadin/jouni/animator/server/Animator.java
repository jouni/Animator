package org.vaadin.jouni.animator.server;

import org.vaadin.jouni.animator.shared.AnimationType;
import org.vaadin.jouni.animator.shared.AnimatorState;

import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.AbstractComponent;

public class Animator extends AbstractExtension {

    public Animator() {

    }

    public Animator(AbstractComponent target) {
        extend(target);
    }

    @Override
    public AnimatorState getState() {
        return (AnimatorState) super.getState();
    }

    /**
     * Add a new animation to this Animator's queue.
     * 
     * @param animation
     * @param params
     *            specify the duration and delay for the animation (in
     *            milliseconds).
     *            <p>
     *            Both are optional. The default values are 200 (duration) and 0
     *            (delay).
     *            <p>
     *            If you want to specify the delay, you need to specify the
     *            duration also:
     *            <p>
     * 
     *            <pre>
     * // The animation will be run immediately
     * // and it will last for 200 ms (default)
     * animator.fadeIn();
     * 
     * // The animation will be run immediately
     * // and it will last for 500 ms
     * animator.fadeIn(500);
     * 
     * // The animation will be run after 1 second and it will last for 200 ms
     * animator.fadeIn(200, 1000);
     * </pre>
     * @return this Animator instance
     */
    public Animator addAnimation(Animation animation, int... params) {
        if (params.length == 1) {
            animation.setDuration(params[0]);
        } else if (params.length == 2) {
            animation.setDuration(params[0]);
            animation.setDelay(params[1]);
        }
        getState().queue.add(animation.getState());
        return this;
    }

    /**
     * Fades in the target component.
     * 
     * See the {@link #addAnimation(Animation, int...)} documentation for
     * parameter values.
     * 
     * @return the new {@link Animation} instance which was created and added to
     *         this Animator's queue.
     */
    public Animation fadeIn(int... params) {
        Animation animation = new Animation(AnimationType.FADE_IN);
        addAnimation(animation, params);
        return animation;
    }

}

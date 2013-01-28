package org.vaadin.jouni.animator.client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.vaadin.jouni.animator.client.animations.FadeIn;
import org.vaadin.jouni.animator.server.Animator;
import org.vaadin.jouni.animator.shared.AnimationState;
import org.vaadin.jouni.animator.shared.AnimationType;
import org.vaadin.jouni.animator.shared.AnimatorState;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;

@Connect(Animator.class)
public class AnimatorConnector extends AbstractExtensionConnector {

    Widget widget;

    @Override
    protected void extend(ServerConnector target) {
        widget = ((ComponentConnector) target).getWidget();
    }

    @Override
    public AnimatorState getState() {
        return (AnimatorState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        processQueue(new Date().getTime());
    }

    void processQueue(long previousIterationTime) {
        long currentTime = new Date().getTime();
        int elapsedTime = (int) (currentTime - previousIterationTime);

        int shortestDelay = -1;

        List<AnimationState> remove = new LinkedList<AnimationState>();

        for (AnimationState anim : getState().queue) {
            // Mark any cancelled animations to be removed
            if (anim.isCancelled()) {
                remove.add(anim);
                continue;
            }

            // Reduce the previous delay from this animation
            anim.setDelay(anim.getDelay() - elapsedTime);

            // Check if the animation still has a delay
            if (anim.getDelay() > 0 && anim.getDelay() < shortestDelay) {
                shortestDelay = anim.getDelay();
            } else {
                // Run the animation instantly
                runAnimation(anim);

                // Mark the animition to be removed
                remove.add(anim);
            }
        }

        // Clear any removed animations
        // TODO use rpc to remove these from the queue, since the state can't be
        // modified from the client side
        for (AnimationState anim : remove) {
            getState().queue.remove(anim);
        }

        // Schedule the next iteration
        if (shortestDelay > 0) {
            final long iterationTime = new Date().getTime();
            new Timer() {
                @Override
                public void run() {
                    processQueue(iterationTime);
                }
            }.schedule(shortestDelay);
        }
    }

    void runAnimation(AnimationState anim) {
        if (anim.getType() == AnimationType.FADE_IN) {
            new FadeIn(widget, anim.getDuration(), anim.getId());
        } else {
            System.out.println(anim.getType());
        }
    }
}

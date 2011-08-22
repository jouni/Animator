package org.vaadin.jouni.animator;

import java.util.Map;

import org.vaadin.jouni.animator.client.ui.VAnimator;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;

/**
 * Server side component for the VAnimator widget.
 */
@SuppressWarnings("serial")
@com.vaadin.ui.ClientWidget(VAnimator.class)
public class Animator extends CustomComponent {

    private boolean fadeInRequested = false;
    private boolean fadeOutRequested = false;
    private boolean rollDownRequested = false;
    private boolean rollUpRequested = false;

    private int fadeDuration = 200;
    private int rollDuration = 200;

    private int fadeDelay = 0;
    private int rollDelay = 0;

    private boolean fadedOut = false;
    private boolean rolledUp = false;

    public Animator(Component toAnimate) {
        setSizeUndefined();
        setCompositionRoot(toAnimate);
    }

    public Animator setContent(Component toAnimate) {
        setCompositionRoot(toAnimate);
        requestRepaint();
        return this;
    }

    public Animator fadeIn() {
        fadeIn(200, 0);
        return this;
    }

    public Animator fadeIn(int duration, int delay) {
        fadeInRequested = true;
        fadeOutRequested = false;
        fadeDuration = duration;
        fadeDelay = delay;
        requestRepaint();
        return this;
    }

    public Animator fadeOut() {
        fadeOut(200, 0);
        return this;
    }

    public Animator fadeOut(int duration, int delay) {
        fadeInRequested = false;
        fadeOutRequested = true;
        fadeDuration = duration;
        fadeDelay = delay;
        requestRepaint();
        return this;
    }

    public Animator rollDown() {
        rollDown(200, 0);
        return this;
    }

    public Animator rollDown(int duration, int delay) {
        rollDownRequested = true;
        rollUpRequested = false;
        rollDuration = duration;
        rollDelay = delay;
        requestRepaint();
        return this;
    }

    public Animator rollUp() {
        rollUp(200, 0);
        return this;
    }

    public Animator rollUp(int duration, int delay) {
        rollDownRequested = false;
        rollUpRequested = true;
        rollDuration = duration;
        rollDelay = delay;
        requestRepaint();
        return this;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {

        super.paintContent(target);

        if (fadeInRequested || fadeOutRequested) {
            target.addAttribute(VAnimator.ATTR_FADE, fadeInRequested ? 1 : -1);
            target.addAttribute(VAnimator.ATTR_FADE_DURATION, fadeDuration);
            target.addAttribute(VAnimator.ATT_FADE_DELAY, fadeDelay);
        }

        if (rollDownRequested || rollUpRequested) {
            target
                    .addAttribute(VAnimator.ATTR_ROLL, rollDownRequested ? 1
                            : -1);
            target.addAttribute(VAnimator.ATTR_ROLL_DURATION, rollDuration);
            target.addAttribute(VAnimator.ATT_ROLL_DELAY, rollDelay);
        }

        if (isFadedOut()) {
            target.addAttribute(VAnimator.ATTR_FADED_OUT, true);
        }
        if (isRolledUp()) {
            target.addAttribute(VAnimator.ATTR_ROLLED_UP, true);
        }

        clearRequests();
    }

    private void clearRequests() {
        fadeInRequested = false;
        fadeOutRequested = false;
        rollDownRequested = false;
        rollUpRequested = false;
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);
        if (variables.containsKey(VAnimator.VAR_FADED_OUT)) {
            fadedOut = ((Boolean) variables.get(VAnimator.VAR_FADED_OUT))
                    .booleanValue();
        }
        if (variables.containsKey(VAnimator.VAR_ROLLED_UP)) {
            rolledUp = ((Boolean) variables.get(VAnimator.VAR_ROLLED_UP))
                    .booleanValue();
        }
    }

    public boolean isRolledUp() {
        return rolledUp;
    }

    public boolean isFadedOut() {
        return fadedOut;
    }

    public Animator setRolledUp(boolean rolledUp) {
        this.rolledUp = rolledUp;
        requestRepaint();
        return this;
    }

    public Animator setFadedOut(boolean fadedOut) {
        this.fadedOut = fadedOut;
        requestRepaint();
        return this;
    }

    /**
     * Cancels all requested animations that have yet to be run.
     * 
     * @return the instance of the Animator
     */
    public Animator cancelAll() {
        clearRequests();
        return this;
    }

}

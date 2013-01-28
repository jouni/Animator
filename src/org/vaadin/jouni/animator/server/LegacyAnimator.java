package org.vaadin.jouni.animator.server;

import java.util.Map;

import org.vaadin.jouni.animator.client.VLegacyAnimator;

import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.LegacyComponent;

/**
 * Server side component for the VLegacyAnimator widget.
 * 
 * @deprecated Use the Animator extension instead, it provides additional
 *             functionality to this component, without the extra wrapper
 *             component.
 */
public class LegacyAnimator extends CustomComponent implements LegacyComponent {

    private static final long serialVersionUID = 9147461261436068140L;

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

    public LegacyAnimator(Component toAnimate) {
        setSizeUndefined();
        setCompositionRoot(toAnimate);
    }

    public LegacyAnimator setContent(Component toAnimate) {
        setCompositionRoot(toAnimate);
        requestRepaint();
        return this;
    }

    public LegacyAnimator fadeIn() {
        fadeIn(200, 0);
        return this;
    }

    public LegacyAnimator fadeIn(int duration, int delay) {
        fadeInRequested = true;
        fadeOutRequested = false;
        fadeDuration = duration;
        fadeDelay = delay;
        requestRepaint();
        return this;
    }

    public LegacyAnimator fadeOut() {
        fadeOut(200, 0);
        return this;
    }

    public LegacyAnimator fadeOut(int duration, int delay) {
        fadeInRequested = false;
        fadeOutRequested = true;
        fadeDuration = duration;
        fadeDelay = delay;
        requestRepaint();
        return this;
    }

    public LegacyAnimator rollDown() {
        rollDown(200, 0);
        return this;
    }

    public LegacyAnimator rollDown(int duration, int delay) {
        rollDownRequested = true;
        rollUpRequested = false;
        rollDuration = duration;
        rollDelay = delay;
        requestRepaint();
        return this;
    }

    public LegacyAnimator rollUp() {
        rollUp(200, 0);
        return this;
    }

    public LegacyAnimator rollUp(int duration, int delay) {
        rollDownRequested = false;
        rollUpRequested = true;
        rollDuration = duration;
        rollDelay = delay;
        requestRepaint();
        return this;
    }

    public void paintContent(PaintTarget target) throws PaintException {

        // super.paintContent(target);

        if (fadeInRequested || fadeOutRequested) {
            target.addAttribute(VLegacyAnimator.ATTR_FADE, fadeInRequested ? 1
                    : -1);
            target.addAttribute(VLegacyAnimator.ATTR_FADE_DURATION,
                    fadeDuration);
            target.addAttribute(VLegacyAnimator.ATT_FADE_DELAY, fadeDelay);
        }

        if (rollDownRequested || rollUpRequested) {
            target.addAttribute(VLegacyAnimator.ATTR_ROLL,
                    rollDownRequested ? 1 : -1);
            target.addAttribute(VLegacyAnimator.ATTR_ROLL_DURATION,
                    rollDuration);
            target.addAttribute(VLegacyAnimator.ATT_ROLL_DELAY, rollDelay);
        }

        if (isFadedOut()) {
            target.addAttribute(VLegacyAnimator.ATTR_FADED_OUT, true);
        }
        if (isRolledUp()) {
            target.addAttribute(VLegacyAnimator.ATTR_ROLLED_UP, true);
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
        if (variables.containsKey(VLegacyAnimator.VAR_FADED_OUT)) {
            fadedOut = ((Boolean) variables.get(VLegacyAnimator.VAR_FADED_OUT))
                    .booleanValue();
        }
        if (variables.containsKey(VLegacyAnimator.VAR_ROLLED_UP)) {
            rolledUp = ((Boolean) variables.get(VLegacyAnimator.VAR_ROLLED_UP))
                    .booleanValue();
        }
    }

    public boolean isRolledUp() {
        return rolledUp;
    }

    public boolean isFadedOut() {
        return fadedOut;
    }

    public LegacyAnimator setRolledUp(boolean rolledUp) {
        this.rolledUp = rolledUp;
        requestRepaint();
        return this;
    }

    public LegacyAnimator setFadedOut(boolean fadedOut) {
        this.fadedOut = fadedOut;
        requestRepaint();
        return this;
    }

    /**
     * Cancels all requested animations that have yet to be run.
     * 
     * @return the instance of the Animator
     */
    public LegacyAnimator cancelAll() {
        clearRequests();
        return this;
    }

}
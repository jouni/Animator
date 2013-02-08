package org.vaadin.jouni.animator.client;

import java.util.Date;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.DOM;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.VCustomComponent;

/**
 * Client side widget which communicates with the server. Messages from the
 * server are shown as HTML and mouse clicks are sent to the server.
 */
public class VAnimator extends VCustomComponent implements Paintable {

    private static final String ATTR_SUFFIX_DELAY = "delay";
    private static final String ATTR_SUFFIX_DURATION = "duration";

    public static final String ATTR_FADE = "fade";
    public static final String ATTR_FADE_DURATION = ATTR_FADE
            + ATTR_SUFFIX_DURATION;
    public static final String ATT_FADE_DELAY = ATTR_FADE + ATTR_SUFFIX_DELAY;

    public static final String ATTR_ROLL = "roll";
    public static final String ATTR_ROLL_DURATION = ATTR_ROLL
            + ATTR_SUFFIX_DURATION;
    public static final String ATT_ROLL_DELAY = ATTR_ROLL + ATTR_SUFFIX_DELAY;

    public static final String ATTR_ROLLED_UP = "rolled";
    public static final String VAR_ROLLED_UP = "isRolled";

    public static final String ATTR_FADED_OUT = "faded";
    public static final String VAR_FADED_OUT = "isFaded";

    /** The client side widget identifier */
    protected String paintableId;

    /** Reference to the server connection object. */
    protected ApplicationConnection client;
    private boolean immediate;

    public VAnimator() {
        super();
        // Disallow styling of the Animator element
        setStyleName("");
        DOM.setStyleAttribute(getElement(), "overflow", "hidden");
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        paintableId = uidl.getId();
        this.client = client;
        immediate = uidl.hasAttribute("immediate");

        if (uidl.hasAttribute(ATTR_FADE)) {
            DOM.setStyleAttribute(getElement(), "visibility", "");
            int dir = uidl.getIntAttribute(ATTR_FADE);
            int duration = uidl.getIntAttribute(ATTR_FADE_DURATION);
            int delay = uidl.getIntAttribute(ATT_FADE_DELAY);
            fade.setDir(dir);
            fade.run(duration, new Date().getTime() + delay);
        } else if (uidl.hasAttribute(ATTR_FADED_OUT)) {
            DOM.setStyleAttribute(getElement(), "visibility", "hidden");
        }

        if (uidl.hasAttribute(ATTR_ROLL)) {
            DOM.setStyleAttribute(getElement(), "height", "");
            int dir = uidl.getIntAttribute(ATTR_ROLL);
            int duration = uidl.getIntAttribute(ATTR_ROLL_DURATION);
            int delay = uidl.getIntAttribute(ATT_ROLL_DELAY);
            roll.setDir(dir);
            roll.run(duration, new Date().getTime() + delay);
        } else if (uidl.hasAttribute(ATTR_ROLLED_UP)) {
            DOM.setStyleAttribute(getElement(), "height", "0");
        }

        client.runDescendentsLayout(this);
    }

    protected FadeAnimation fade = new FadeAnimation();

    protected class FadeAnimation extends Animation {

        private int dir;

        public void setDir(int dir) {
            this.dir = dir;
            if (dir > 0) {
                getElement().getStyle().setOpacity(0);
            }
        }

        @Override
        protected void onUpdate(double progress) {
            if (dir > 0) {
                if (BrowserInfo.get().isIE8()) {
                    getElement().getStyle().setProperty(
                            "filter",
                            "progid:DXImageTransform.Microsoft.Alpha(Opacity="
                                    + (int) (progress * 100) + ")");
                } else {
                    getElement().getStyle().setOpacity(progress);
                }
            } else {
                if (BrowserInfo.get().isIE8()) {
                    getElement().getStyle().setProperty(
                            "filter",
                            "progid:DXImageTransform.Microsoft.Alpha(Opacity="
                                    + (int) ((1 - progress) * 100) + ")");
                } else {
                    getElement().getStyle().setOpacity(1 - progress);
                }
            }
        }

        @Override
        public void onComplete() {
            super.onComplete();
            if (dir < 0) {
                getElement().getStyle().setVisibility(Visibility.HIDDEN);
            }
            getElement().getStyle().clearOpacity();
            if (BrowserInfo.get().isIE8()) {
                // TODO
                // DOM.setStyleAttribute(getElement(), "-ms-filter", "");
            }
            client.updateVariable(paintableId, VAR_FADED_OUT, dir < 0,
                    immediate);
        }

        @Override
        protected void onCancel() {
            // NOP
        }
    }

    protected RollAnimation roll = new RollAnimation();

    protected class RollAnimation extends Animation {

        private int dir;
        private int height;

        public void setDir(int dir) {
            height = getWidget().getOffsetHeight();
            this.dir = dir;
            if (dir > 0) {
                DOM.setStyleAttribute(getElement(), "height", "0");
            } else {
                DOM.setStyleAttribute(getElement(), "height", "");
            }
        }

        @Override
        protected void onUpdate(double progress) {
            // -1 == roll up, 1 == roll down
            if (dir > 0) {
                DOM.setStyleAttribute(getElement(), "height", progress * height
                        + "px");
            } else {
                DOM.setStyleAttribute(getElement(), "height", (1 - progress)
                        * height + "px");
            }
            // TODO
            // com.vaadin.terminal.gwt.client.Util.notifyParentOfSizeChange(
            // VAnimator.this, false);
        }

        @Override
        protected void onComplete() {
            super.onComplete();
            if (dir > 0) {
                DOM.setStyleAttribute(getElement(), "height", "");
            }
            client.updateVariable(paintableId, VAR_ROLLED_UP, dir < 0,
                    immediate);
        }

        @Override
        protected void onCancel() {
            // NOP
        }
    }

}
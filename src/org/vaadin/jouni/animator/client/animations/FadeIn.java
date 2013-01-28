package org.vaadin.jouni.animator.client.animations;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ui.VWindow;

public class FadeIn extends AbstractAnimation {

    public FadeIn(final Widget target, final int duration, final int id) {
        super(target, duration, id, "fade-in");
        if (!useCss) {
            new Animation() {
                @Override
                protected void onUpdate(double progress) {
                    if (BrowserInfo.get().isIE8()) {
                        target.getElement()
                                .getStyle()
                                .setProperty(
                                        "filter",
                                        "progid:DXImageTransform.Microsoft.Alpha(Opacity="
                                                + (int) (progress * 100) + ")");
                        if (target instanceof VWindow) {
                            // Animate the shadow of the window as well
                            ((Element) target.getElement().getPreviousSibling())
                                    .getStyle().setProperty(
                                            "filter",
                                            "progid:DXImageTransform.Microsoft.Alpha(Opacity="
                                                    + (int) (progress * 100)
                                                    + ")");
                        }
                    } else {
                        target.getElement().getStyle().setOpacity(progress);
                        if (target instanceof VWindow) {
                            ((Element) target.getElement().getPreviousSibling())
                                    .getStyle().setOpacity(progress);
                        }
                    }
                }
            }.run(duration);

        }
    }

}

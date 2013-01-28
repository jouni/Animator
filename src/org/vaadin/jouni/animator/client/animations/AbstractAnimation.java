package org.vaadin.jouni.animator.client.animations;

import org.vaadin.jouni.animator.client.CSSRule;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;

public class AbstractAnimation {

    protected final static boolean useCss;

    protected final static String vendor;

    protected final static String animationEnd;

    static {
        BrowserInfo b = BrowserInfo.get();
        if (b.isIE() && b.getBrowserMajorVersion() >= 10) {
            useCss = true;
            vendor = "ms";
            animationEnd = "MSAnimationEnd";
        } else if (b.isGecko() && b.getBrowserMajorVersion() >= 5) {
            useCss = true;
            vendor = "Moz";
            animationEnd = "animationend";
        } else if (b.isWebkit()) {
            // Assume all recent WebKit versions have CSS animation support
            useCss = true;
            vendor = "webkit";
            animationEnd = "webkitAnimationEnd";
        } else if (b.isOpera() && b.getBrowserMajorVersion() >= 12) {
            useCss = true;
            vendor = "O";
            animationEnd = "oanimationend";
        } else {
            useCss = false;
            vendor = "";
            animationEnd = "animationEnd";
        }
    }

    protected final static String stylePrefix = "v-animate-";

    private Widget target;

    private int duration;

    private int id;

    private String animationName;

    public AbstractAnimation(Widget target, final int duration, final int id,
            String animationName) {
        this.target = target;
        this.duration = duration;
        this.id = id;
        this.animationName = animationName;

        if (useCss) {
            animateUsingCss();
        }
    }

    protected static native void registerCssCallback(Element elem,
            AsyncCallback<String> callback)
    /*-{
          elem.addEventListener(@org.vaadin.jouni.animator.client.animations.AbstractAnimation::animationEnd, function(e) {
              $entry(@org.vaadin.jouni.animator.client.animations.AbstractAnimation::cssCallback(Lcom/google/gwt/user/client/rpc/AsyncCallback;Ljava/lang/String;)(callback, e.animationName));
          }, false);
    }-*/;

    protected static void cssCallback(AsyncCallback<String> callback,
            String animationName) {
        callback.onSuccess(animationName);
    }

    protected void animateUsingCss() {
        CSSRule r = CSSRule.create("." + stylePrefix + id);
        // TODO use a separate value for each animation so one element can have
        // animations with different durations
        r.setProperty(vendor + "AnimationDuration", duration + "ms");

        target.addStyleName(stylePrefix + id);
        target.addStyleName(stylePrefix + animationName);

        // TODO only register one listener per element
        registerCssCallback(target.getElement(), new AsyncCallback<String>() {
            @Override
            public void onSuccess(String animationName) {
                target.removeStyleName(stylePrefix + id);
                target.removeStyleName(stylePrefix + animationName);
            }

            @Override
            public void onFailure(Throwable caught) {
                target.removeStyleName(stylePrefix + id);
                target.removeStyleName(stylePrefix + animationName);
            }
        });
    }

}

package org.vaadin.jouni.animator.server;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Vector;
import java.util.WeakHashMap;

import org.vaadin.jouni.animator.client.VAnimatorProxy;
import org.vaadin.jouni.animator.client.VAnimatorProxy.AnimType;

import com.vaadin.event.EventRouter;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.LegacyComponent;
import com.vaadin.ui.Window;
import com.vaadin.util.ReflectTools;

/**
 * Server side component for the VAnimator widget.
 * 
 * @deprecated Use the Animator extension instead, which provides the same
 *             functionality but without the extra invisible UI component.
 */
public class AnimatorProxy extends AbstractComponent implements LegacyComponent {

    private static final long serialVersionUID = -1456900127464089642L;

    public class Animation {
        private int duration = 200;
        private int delay = 0;
        private Component target;
        private AnimType type;
        private String data;
        private boolean cancel = false;

        public Animation(Component target, AnimType type) {
            this.type = type;
            this.target = target;
            // We need to handle situations where the component is rendered
            // before the animation is triggered. These styles take care of some
            // initializations. They are cleared after the animation is
            // finished.
            // TODO handle concurrent animations of the same type on a same
            // component (this style may be prematurely removed)
            target.addStyleName(VAnimatorProxy.ANIM_STYLE_PREFIX
                    + type.toString());
        }

        public Animation setDuration(int millis) {
            duration = millis;
            return this;
        }

        public Animation setDelay(int millis) {
            delay = millis;
            return this;
        }

        public Animation setData(String data) {
            this.data = data;
            return this;
        }

        public Component getTarget() {
            return target;
        }

        public int getDuration() {
            return duration;
        }

        public int getDelay() {
            return delay;
        }

        public AnimType getType() {
            return type;
        }

        public String getData() {
            return data;
        }

        public Animation cancel() {
            cancel = true;
            return this;
        }

        public boolean isCancelled() {
            return cancel;
        }

        @Override
        public String toString() {
            return "AnimRequest[" + type + " (duration=" + duration
                    + ", delay=" + delay
                    + (data != null ? ", data=" + data : "") + "): " + target
                    + "]";
        }
    }

    private Vector<Animation> queue;

    private boolean cancelAllRequested = false;

    public AnimatorProxy() {
    }

    public Animation animate(Component target, AnimType type) {
        if (queue == null) {
            queue = new Vector<Animation>();
        }
        Animation ar = new Animation(target, type);
        queue.add(ar);
        requestRepaint();
        return ar;
    }

    /* Internal mapping to handle events from client side */
    // TODO are the objects garbage collected ever?
    private Map<Integer, Animation> animIdToRequest = new WeakHashMap<Integer, Animation>();
    /* Incremental id for components; used in client-server communication */
    private int animId = 0;

    public void paintContent(PaintTarget target) throws PaintException {
        if (queue != null) {
            for (Animation a : queue) {
                if (a.isCancelled()) {
                    continue;
                }
                if (!animIdToRequest.containsValue(a)) {
                    animIdToRequest.put(++animId, a);
                }
                target.startTag("a");
                target.addAttribute("aid", animId);
                target.addAttribute("target", a.getTarget().getConnectorId());
                target.addAttribute("type", a.getType().toString());
                target.addAttribute("dur", a.getDuration());
                target.addAttribute("delay", a.getDelay());
                if (a.getData() != null) {
                    target.addAttribute("data", a.getData());
                }
                target.endTag("a");
            }
        }
        if (cancelAllRequested) {
            target.addAttribute("cancelAll", true);
        }

        clearRequests();
    }

    @SuppressWarnings("unchecked")
    public void changeVariables(Object source, Map<String, Object> variables) {
        if (variables.containsKey("anim")) {
            Map<String, Object> map = (Map<String, Object>) variables
                    .get("anim");
            for (String key : map.keySet()) {
                int aid = Integer.parseInt(key);
                String sType = (String) map.get(key);
                AnimType type = AnimType.valueOf(sType.toUpperCase()
                        .replace("-", "_").split(",")[0]);
                Animation ar = animIdToRequest.get(aid);
                // Clear extra style name
                ar.getTarget().removeStyleName(
                        VAnimatorProxy.ANIM_STYLE_PREFIX + type.toString());
                fireAnimationEvent(ar, type);
                // Cleanup
                animIdToRequest.remove(aid);

                // Was the animation cancelled?
                boolean cancelled = false;
                String[] values = sType.split(",");
                if (values.length > 1) {
                    for (String s : values) {
                        if (s.startsWith("cancelled")) {
                            cancelled = true;
                        }
                    }
                }

                if (cancelled)
                    continue;

                if (type.equals(AnimType.FADE_OUT_REMOVE)
                        || type.equals(AnimType.ROLL_UP_CLOSE_REMOVE)
                        || type.equals(AnimType.ROLL_LEFT_CLOSE_REMOVE)) {
                    if (ar.getTarget() instanceof Window) {
                        ar.getTarget().getUI()
                                .removeWindow((Window) ar.getTarget());
                    } else if (ar.getTarget().getParent() instanceof ComponentContainer) {
                        ((ComponentContainer) ar.getTarget().getParent())
                                .removeComponent(ar.getTarget());
                    }
                } else if (type.equals(AnimType.SIZE)) {
                    int width = -1;
                    int height = -1;
                    for (String keyValue : values) {
                        String[] keyValuePair = keyValue.split("=");
                        if (keyValuePair[0].trim().equals("width")) {
                            width = Integer.parseInt(keyValuePair[1].trim());
                        } else if (keyValuePair[0].trim().equals("height")) {
                            height = Integer.parseInt(keyValuePair[1].trim());
                        }
                    }
                    if (width > -1) {
                        ar.getTarget().setWidth(width + "px");
                    }
                    if (height > -1) {
                        ar.getTarget().setHeight(height + "px");
                    }
                }
            }
        }
    }

    protected void fireAnimationEvent(Animation request, AnimType type) {
        fireEvent(new AnimationEvent(request, type));
    }

    /**
     * Cancel all requested animations.
     * 
     * @return the instance of the Animator
     */
    public AnimatorProxy cancelAll() {
        clearRequests();
        cancelAllRequested = true;
        requestRepaint();
        return this;
    }

    private void clearRequests() {
        if (queue != null) {
            queue.clear();
        }
    }

    /**
     * Request to cancel a particular animation. This method should usually be
     * called after the initial animation request has already been sent to the
     * client, and you wish to stop it after the fact.
     * 
     * @param animation
     * @return
     */
    // public AnimatorProxy cancelAnimation(Animation animation) {
    // animation.cancel();
    // queue.add(animation);
    // requestRepaint();
    // return this;
    // }

    public interface AnimationListener {
        public static final Method animMethod = ReflectTools.findMethod(
                AnimationListener.class, "onAnimation", AnimationEvent.class);

        public void onAnimation(AnimationEvent event);
    }

    public void addListener(AnimationListener listener) {
        addListener(AnimationEvent.EVENT_ID, AnimationEvent.class, listener,
                AnimationListener.animMethod);
    }

    public void removeListener(AnimationListener listener) {
        removeListener(AnimationEvent.EVENT_ID, AnimationEvent.class, listener);
    }

    public class AnimationEvent extends Component.Event {

        private static final long serialVersionUID = 7075848445136103472L;

        /**
         * Identifier for event that can be used in {@link EventRouter}
         */
        public static final String EVENT_ID = "anim";
        private Animation anim;

        public AnimationEvent(Animation anim, AnimType type) {
            super(anim.getTarget());
            this.anim = anim;
        }

        public Animation getAnimation() {
            return anim;
        }

        @Override
        public String toString() {
            return anim.toString();
        }
    }

}

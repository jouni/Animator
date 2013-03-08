package org.vaadin.jouni.animator.shared;

public enum AnimType {
    /**
     * Fades in the target. The required space for the target is reserved from
     * the layout before the animation is triggered, even if a delay is used.
     */
    FADE_IN,
    /**
     * Fades in the target. The required space for the target is not reserved
     * until the animation is triggered.
     * <p>
     * <b>NOTE</b> This type can not be used together with any ROLL or SIZE
     * animations.
     */
    FADE_IN_POP,
    /**
     * Fades out the component. The required space of the component is preserved
     * in the layout, and the component is not removed from the layout.
     */
    FADE_OUT,
    /**
     * Fades out the component. The component is removed from the parent layout
     * after the animation is finished, and the required space of the target is
     * collapsed.
     * <p>
     * <b>NOTE</b> This event is mostly just for convenience, and you can get
     * the same result by adding an AnimationListener to the AnimatorProxy, and
     * removing the target component after the event is triggered (you can
     * filter the event by target and type).
     */
    FADE_OUT_REMOVE,
    /**
     * Hides the target by clipping it with a mask moving upwards.
     * <p>
     * <b>NOTE</b> After the animation, the target will become visible again if
     * it has a defined height. Undefined high targets will remain hidden. If
     * you wish to keep the target hidden in any case, you need to add an
     * AnimationListener to the AnimatorProxy, and explicitly set the height of
     * the target to zero after the animation has finished.
     * */
    ROLL_UP_CLOSE,
    /**
     * Hides the target by clipping it with a mask moving upwards. Removes the
     * component from its parent layout after the animation is finished.
     */
    ROLL_UP_CLOSE_REMOVE,
    /**
     * Reveals the target by clipping it with a mask moving downwards. The
     * required space for the target is reserved from the layout before the
     * animation is triggered, even if a delay is used.
     * */
    ROLL_DOWN_OPEN,
    /**
     * Reveals the target by clipping it with a mask moving downwards. The
     * required space for the target is not reserved until the animation is
     * triggered.
     */
    ROLL_DOWN_OPEN_POP,
    /**
     * Reveals the target by clipping it with a mask moving to the right. The
     * required space for the target is reserved from the layout before the
     * animation is triggered, even if a delay is used.
     * */
    ROLL_RIGHT_OPEN,
    /**
     * Reveals the target by clipping it with a mask moving to the right. The
     * required space for the target is not reserved until the animation is
     * triggered.
     */
    ROLL_RIGHT_OPEN_POP,
    /**
     * Hides the target by clipping it with a mask moving to the left.
     * <p>
     * <b>NOTE</b> After the animation, the target will become visible again if
     * it has a defined width. Undefined wide targets will remain hidden. If you
     * wish to keep the target hidden in any case, you need to add an
     * AnimationListener to the AnimatorProxy, and explicitly set the width of
     * the target to zero after the animation has finished.
     * */
    ROLL_LEFT_CLOSE,
    /**
     * Hides the target by clipping it with a mask moving to the left. Removes
     * the component from its parent layout after the animation is finished.
     */
    ROLL_LEFT_CLOSE_REMOVE,
    /**
     * Animate the target's size to a specific pixel value. The final size is
     * given using the
     * {@link org.vaadin.jouni.animator.AnimatorProxy.Animation#setData(String)}
     * method, in the form of "width=100,height=100". Either width or height
     * must be specified, but both are not required. The values are pixels. You
     * can prefix the values with either '+' or '-' to size the component
     * relatively, instead of absolutely.
     * <p>
     * If you're animating a sub-window, you can also animate its position using
     * this animation type. Just provide x and y coordinates with
     * {@link org.vaadin.jouni.animator.AnimatorProxy.Animation#setData(String)}
     * , e.g. <code>anim.setData("x=50,y=100");</code>
     */
    SIZE;
    @Override
    public String toString() {
        return super.toString().toLowerCase().replace("_", "-");
    }
}
======================================
Animator add-on for Vaadin
--------------------------------------
http://vaadin.com/addon/animator
--------------------------------------
by Jouni Koivuviita (jouni@vaadin.com)
======================================

Animate any component, even sub-windows with a small set of usable animations.

The AnimatorProxy is an invisible component that animates other components directly, without additional component hierarchy or DOM element overhead. In principal, you only need one AnimatorProxy in your application, and you can then animate any other components in that same application: size, position, fading and rolling/curtaining.

The AnimatorProxy also provides a convenient listener mechanism for all animations that are run through it, allowing you to make actions after an animation has finished.

See the online demo for available animation types and examples.

All animations allow you to specify the duration and delay of the animation. The animations are atomic, so they won't repeat automatically each time the user reloads the application.


Using the AnimatorProxy
=====

AnimatorProxy proxy = new AnimatorProxy();
mainWindow.addComponent(proxy);

Label label = new Label("Animate Me!");
proxy.animate(label, AnimType.FADE_IN).setDuration(500).setDelay(100);

// This listener will catch all animations that are passed through this proxy
proxy.addListener(new AnimationListener() {
  public void onAnimation(AnimationEvent event) {
    System.out.println(event.getAnimation());
  }
});


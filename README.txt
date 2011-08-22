======================================
Animator component
--------------------------------------
by Jouni Koivuviita (jouni@vaadin.com)
======================================

Animate any component with a small set of usable animations.

This component is used as a wrapper to any other component (layout or single), 
and it provides a small set of animations that can then be applied to the 
contained component.

Currently only two animations are available (the ones I thought were the most 
used and non-intrusive)
 * fade (in/out)
 * roll (down/up)

All animations allow you to specify the duration and delay of the animation.
The animations are atomic, so they won't repeat automatically each time the user 
reloads the application.

Tested to work in all browsers that Vaadin supports. IE6 has some small issue 
when multiple animations are used simultaneously (namely rolling up a component 
and then fading it will cause the roll to disappear).


Notes
=====
 * You need to set dimensions on the Animator like all other components. It
   inherits the CustomComponent, but its default size is undefined.


Usage
=====

Animator anim = new Animator(new Label("Animate Me!"));
anim.rollDown(300, 200);
anim.fadeIn(300, 400);

addComponent(anim);
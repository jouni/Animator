package org.vaadin.jouni.animator;

import java.lang.reflect.Method;

import org.vaadin.jouni.animator.client.AnimatorClientRpc;
import org.vaadin.jouni.animator.client.AnimatorServerRpc;
import org.vaadin.jouni.animator.client.ClientEvent;
import org.vaadin.jouni.animator.client.Css;
import org.vaadin.jouni.animator.client.CssAnimation;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

public class Animator extends AbstractExtension {

	private static final long serialVersionUID = 2876055108100881743L;

	private AnimatorServerRpc rpc = new AnimatorServerRpc() {

		private static final long serialVersionUID = 6125808362723118718L;

		@Override
		public void animationEnd(CssAnimation animation) {
			fireAnimationEndEvent(animation);
		}
	};

	public Animator(AbstractClientConnector target) {
		super.extend(target);
		registerRpc(rpc);
	}

	public CssAnimation animateOn(AbstractClientConnector eventTarget,
			ClientEvent event, Css properties) {

		CssAnimation anim = new CssAnimation();
		if (eventTarget != null) {
			anim.eventTarget = eventTarget;
		} else {
			anim.eventTarget = (AbstractClientConnector) getParent();
		}
		anim.event = event;
		anim.css = properties;

		// queue.put(anim.id, anim);

		getRpcProxy(AnimatorClientRpc.class).animate(anim);

		return anim;
	}

	protected void fireAnimationEndEvent(CssAnimation animation) {
		fireEvent(new AnimationEndEvent((Component) getParent(), animation));
	}

	public interface AnimationListener {
		public static final Method animMethod = ReflectTools.findMethod(
				AnimationListener.class, "animationEnd",
				AnimationEndEvent.class);

		public void animationEnd(AnimationEndEvent event);
	}

	public void addListener(AnimationListener listener) {
		addListener(AnimationEndEvent.EVENT_ID, AnimationEndEvent.class,
				listener, AnimationListener.animMethod);
	}

	public void removeListener(AnimationListener listener) {
		removeListener(AnimationEndEvent.EVENT_ID, AnimationEndEvent.class,
				listener);
	}

	public static class AnimationEndEvent extends Component.Event {

		private static final long serialVersionUID = 2181324846162279412L;

		public static final String EVENT_ID = "animation";
		private CssAnimation animation;

		public AnimationEndEvent(Component source, CssAnimation animation) {
			super(source);
			this.animation = animation;
		}

		public CssAnimation getAnimation() {
			return animation;
		}

		public String toString() {
			return animation.toString();
		}
	}

}

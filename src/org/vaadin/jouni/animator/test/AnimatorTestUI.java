package org.vaadin.jouni.animator.test;

import org.vaadin.jouni.animator.Animator;
import org.vaadin.jouni.animator.Animator.AnimationEndEvent;
import org.vaadin.jouni.animator.Animator.AnimationListener;
import org.vaadin.jouni.animator.Dom;
import org.vaadin.jouni.animator.client.ClientEvent;
import org.vaadin.jouni.animator.client.Css;
import org.vaadin.jouni.animator.client.Ease;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class AnimatorTestUI extends UI {
	@Override
	public void init(VaadinRequest request) {
		setContent(new VerticalLayout() {
			Label label = new Label("Animate Me");
			Animator animator = new Animator(label);
			{

				// new Dom(label).tabIndex(0).style().translate("200%",
				// "1600%");
				label.setSizeUndefined();

				// animator.animateOn(null, null, new Css().translateY("100%"))
				// .duration(1500).ease(Ease.OUT);
				//
				// animator.animateOn(null, null, new Css().opacity(0.3))
				// .duration(300).delay(1000);

				animator.animateOn(null, ClientEvent.CLICK,
						new Css().scale(1.5)).duration(1500);

				animator.animateOn(null, ClientEvent.MOUSE_OUT,
						new Css().scale(1)).duration(300);

				addComponent(label);

				animator.addListener(new AnimationListener() {
					@Override
					public void animationEnd(AnimationEndEvent event) {
						System.out.println(event);
					}
				});

			}
		});

	}
}

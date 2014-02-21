package org.vaadin.jouni.animator.test;

import java.awt.Desktop.Action;

import org.vaadin.jouni.animator.Animator;
import org.vaadin.jouni.animator.Dom;
import org.vaadin.jouni.animator.Snappy;
import org.vaadin.jouni.animator.client.ClientEvent;
import org.vaadin.jouni.animator.client.ClientEvent.Key;
import org.vaadin.jouni.animator.client.Css;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("chameleon")
public class AnimatorTestUI extends UI {
	@Override
	public void init(VaadinRequest request) {
		setContent(new VerticalLayout() {
			Label label = new Label("Animate Me") {
				{
					setSizeUndefined();
				}
			};
			Button button = new Button("Animate Me");
			// Window window = new Window("Animate Me") {
			// {
			// setContent(new Label(
			// "Plura mihi bona sunt, inclinet, amari petere vellent. Ab illo tempore, ab est sed immemorabili. Morbi fringilla convallis sapien, id pulvinar odio volutpat."));
			// setWidth("400px");
			// center();
			// }
			// };
			// Animator animator = new Animator(button);
			{
				Animator.animate(label, new Css().translateX("100px"))
						.delay(1000).duration(2000);
				addComponent(button);
				addComponent(label);
				Animator.animate(button, new Css().opacity(0));

				TextField tf = new TextField();
				new Snappy(tf)
						.on(ClientEvent.keydown(Key.ESC))
						.animate(button,
								new Css().translateX("100px").opacity(1))
						.blur(tf);
				addComponent(tf);

				// addWindow(window);

				// animator.animateOn(window, ClientEvent.WINDOW_CLOSE,
				// new Css().scale(0.5).opacity(0)).duration(500)
				// .sendEndEvent();
				//
				// animator.addListener(new AnimationListener() {
				// @Override
				// public void animationEnd(AnimationEndEvent event) {
				// System.out.println(event.getComponent());
				// window.close();
				// }
				// });

				addComponent(new SearchBar());

			}
		});
	}

	static class SearchBar extends CssLayout {

		Label title = new Label("Search Here");
		Dom titleDom = new Dom(title);

		TextField searchField = new TextField();
		NativeButton cancel = new NativeButton("Cancel");
		CssLayout searchWrapper = new CssLayout();
		Dom searchWrapperDom = new Dom(searchWrapper);

		HorizontalLayout filters = new HorizontalLayout();
		NativeButton sender = new NativeButton("Sender");
		NativeButton subject = new NativeButton("Subject");
		NativeButton body = new NativeButton("Body");
		NativeButton all = new NativeButton("All");

		VerticalLayout wrapper = new VerticalLayout();


		public SearchBar() {
			setWidth("320px");
			setHeight("60px");

			title.setHeight("30px");
			titleDom.style().setProperty("text-align", "center");
			titleDom.style().setProperty("font-size", "18px");
			titleDom.style().setProperty("line-height", "30px");
			titleDom.style().setProperty("font-weight", "bold");

			searchField.setWidth("320px");
			searchField.setHeight("30px");
			cancel.setWidth("80px");
			cancel.setHeight("30px");
			searchWrapper.addComponents(searchField, cancel);
			searchWrapperDom.style().setProperty("white-space", "nowrap")
					.setProperty("position", "relative");

			filters.setWidth("100%");
			filters.setHeight("30px");
			sender.setSizeFull();
			subject.setSizeFull();
			body.setSizeFull();
			all.setSizeFull();
			filters.addComponents(sender, subject, body, all);

			wrapper.addComponents(title, searchWrapper, filters);
			addComponent(wrapper);

			new Snappy(searchField).on(ClientEvent.focus())
					.animate(wrapper, new Css().translateY("-30px"))
					.animate(cancel, new Css().translateX("-100%"))
					.animate(title, new Css().translateY("20px"))
					.on(ClientEvent.keydown(Key.ESC))
					.animate(wrapper, new Css().translateY("0"))
					.animate(cancel, new Css().translateX("0"))
					.animate(title, new Css().translateY("0"))
					.blur(searchField);

			new Snappy(cancel).on(ClientEvent.clickPrimary())
					.animate(wrapper, new Css().translateY("0"))
					.animate(cancel, new Css().translateX("0"))
					.animate(title, new Css().translateY("0"))
					.blur(searchField);

		}
	}
}

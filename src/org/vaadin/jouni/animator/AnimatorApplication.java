package org.vaadin.jouni.animator;

import org.vaadin.jouni.animator.AnimatorProxy.Animation;
import org.vaadin.jouni.animator.AnimatorProxy.AnimationEvent;
import org.vaadin.jouni.animator.AnimatorProxy.AnimationListener;
import org.vaadin.jouni.animator.client.ui.VAnimatorProxy.AnimType;

import com.vaadin.Application.LegacyApplication;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Root.LegacyWindow;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

@SuppressWarnings("serial")
public class AnimatorApplication extends LegacyApplication {
	boolean fade = false;
	boolean roll = false;
	private AnimatorProxy ap;

	@Override
	public void init() {
		final LegacyWindow mainWindow = new LegacyWindow(
				"Animator Add-on for Vaadin 7");
		setMainWindow(mainWindow);
		mainWindow.getContent().setSizeFull();

		setTheme("animator-demo");

		ap = new AnimatorProxy();
		mainWindow.addComponent(ap);

		HorizontalLayout header = new HorizontalLayout();
		header.setWidth("100%");
		header.setMargin(true);
		mainWindow.addComponent(header);

		Label title = new Label("Animator Add-on for Vaadin 7 Demo");
		title.addStyleName(Reindeer.LABEL_H1);
		header.addComponent(title);
		header.setExpandRatio(title, 1);

		Button directory = new Button("Download from the Add-on Directory",
				new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						mainWindow.open(new ExternalResource(
								"http://vaadin.com/addon/animator"));
					}
				});
		header.addComponent(directory);
		directory.addStyleName(Reindeer.BUTTON_DEFAULT);
		header.setComponentAlignment(directory, Alignment.MIDDLE_RIGHT);

		ap.animate(header, AnimType.FADE_IN).setDuration(500).setDelay(1500);

		// TabSheet tabs = new TabSheet();
		// tabs.setSizeFull();
		// tabs.addStyleName(Reindeer.TABSHEET_BORDERLESS);
		// mainWindow.addComponent(tabs);
		// ((VerticalLayout) mainWindow.getContent()).setExpandRatio(tabs, 1);
		// ((VerticalLayout) mainWindow.getContent()).setMargin(false);

		// ap.animate(tabs, AnimType.FADE_IN).setDuration(300).setDelay(2000);

		// tabs.addTab(proxy(), "AnimatorProxy", null);
		Component proxy;
		mainWindow.addComponent(proxy = proxy());
		((VerticalLayout) mainWindow.getContent()).setExpandRatio(proxy, 1);
		((VerticalLayout) mainWindow.getContent()).setMargin(false);

		// Welcome window
		Window w = new Window("Animator Add-on");
		w.addStyleName(Reindeer.WINDOW_BLACK);
		w.setClosable(false);
		w.setResizable(false);
		w.setDraggable(false);
		w.setWidth("400px");
		w.setHeight("200px");
		w.getContent().setSizeFull();
		Label welcome = new Label("Welcome");
		welcome.setSizeUndefined();
		welcome.addStyleName(Reindeer.LABEL_H1);
		w.addComponent(welcome);
		((VerticalLayout) w.getContent()).setComponentAlignment(welcome,
				Alignment.MIDDLE_CENTER);
		mainWindow.addWindow(w);
		w.center();
		ap.animate(w, AnimType.FADE_IN).setDelay(300).setDuration(500);
		ap.animate(w, AnimType.SIZE)
				.setData("y=-100,x=-100,width=+200,height=+200").setDelay(500)
				.setDuration(500);
		ap.animate(w, AnimType.FADE_OUT_REMOVE).setDelay(1400).setDuration(300);
	}

	class AnimEdit extends HorizontalLayout {
		TextField duration = new TextField("Duration");
		TextField delay = new TextField("Delay");

		public AnimEdit() {
			super();
			addComponent(duration);
			addComponent(delay);
			duration.setWidth("4em");
			delay.setWidth("4em");
			duration.setInputPrompt("Duration (millis)");
			delay.setInputPrompt("Delay (millis)");
			duration.setValue("300");
			delay.setValue("0");
			setSpacing(true);
			setMargin(true, false, false, false);
		}
	}

	private Component proxy() {
		CssLayout margin = new CssLayout();
		margin.setMargin(true);
		margin.setWidth("100%");
		// TabSheet tabs = new TabSheet();
		// tabs.setWidth("100%");
		// tabs.addStyleName(Reindeer.TABSHEET_MINIMAL);
		// margin.addComponent(tabs);
		// tabs.addTab(proxyIntro(), "Introduction", null);
		// tabs.addTab(proxyDemo(), "Usage Examples", null);
		// tabs.addListener(new TabSheet.SelectedTabChangeListener() {
		// public void selectedTabChange(SelectedTabChangeEvent event) {
		// ap.animate(event.getTabSheet().getSelectedTab(),
		// AnimType.FADE_IN);
		// }
		// });
		margin.addComponent(proxyIntro());
		return margin;
	}

	private Layout proxyIntro() {
		HorizontalLayout main = new HorizontalLayout();
		main.setWidth("100%");
		main.setMargin(true);

		CssLayout layout = new CssLayout();
		layout.setWidth("100%");
		layout.setMargin(false, true, true, true);
		Label info = new Label(
				"<h2>AnimatorProxy</h2><p>The <code>AnimatorProxy</code> is an invisible component, that animates any component in the same application it is attached to, <b>even sub-windows</b>.</p><p>It provides more animation types than the Animator wrapper, and it also provides a listener mechanism for the animations: you get an event when an animation is finished running.</p><h3>Example code</h3><pre>AnimatorProxy proxy = new AnimatorProxy();\nmainWindow.addComponent(proxy);\nproxy.animate(component, AnimType.FADE_IN).setDuration(500).setDelay(100);\n\n// This listener will catch all animations that are\n// passed through this proxy\nproxy.addListener(new AnimationListener() {\n  public void onAnimation(AnimationEvent event) {\n    System.out.println(event.getAnimation());\n  }\n});</pre></p>",
				Label.CONTENT_XHTML);
		layout.addComponent(info);
		main.addComponent(layout);
		main.setExpandRatio(layout, 3);

		layout = new CssLayout();
		layout.setWidth("100%");
		layout.setMargin(true);
		main.addComponent(layout);
		main.setExpandRatio(layout, 2);

		layout.addComponent(new Label("<h3>Animation type examples</h3>",
				Label.CONTENT_XHTML));

		final CssLayout demo = new CssLayout();
		demo.setWidth("100%");
		demo.setHeight("200px");
		demo.addStyleName(Reindeer.LAYOUT_BLACK);
		demo.setMargin(true);
		demo.addComponent(new Label("Demo component"));

		NativeButton b = new NativeButton("Fade out",
				new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						ap.animate(demo, AnimType.FADE_OUT);
					}
				});
		layout.addComponent(b);

		b = new NativeButton("Fade in", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				ap.animate(demo, AnimType.FADE_IN);
			}
		});
		layout.addComponent(b);

		b = new NativeButton("Roll up", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				ap.animate(demo, AnimType.ROLL_UP_CLOSE);
			}
		});
		layout.addComponent(b);

		b = new NativeButton("Roll down", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				ap.animate(demo, AnimType.ROLL_DOWN_OPEN);
			}
		});
		layout.addComponent(b);

		b = new NativeButton("Roll left", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				ap.animate(demo, AnimType.ROLL_LEFT_CLOSE);
			}
		});
		layout.addComponent(b);

		b = new NativeButton("Roll right", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				ap.animate(demo, AnimType.ROLL_RIGHT_OPEN);
			}
		});
		layout.addComponent(b);

		b = new NativeButton("Size", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				Animation a = ap.animate(demo, AnimType.SIZE);
				a.setData("width=-30,height=-10,redraw=true");
			}
		});
		layout.addComponent(b);
		b = new NativeButton("Position", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				Animation a = ap.animate(demo, AnimType.SIZE);
				a.setData("x=-20,y=+5");
			}
		});
		layout.addComponent(b);

		layout.addComponent(demo);

		return main;
	}

	private Layout proxyDemo() {
		CssLayout root = new CssLayout();
		root.setWidth("100%");
		root.setMargin(true);

		/* Disclosure */
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth("100%");
		layout.setSpacing(true);
		Label text = new Label(
				"<h3>Disclosure panel</h3><p>Use the animator to hide secondary information, allowing the user to access it when needed.</p><p>This component is packaged with the Animator Add-on as a reusable component called <code>Disclosure</code>.</p>",
				Label.CONTENT_XHTML);
		layout.addComponent(text);
		layout.setExpandRatio(text, 1.5f);

		CssLayout demo = new CssLayout();
		demo.setWidth("100%");
		demo.setMargin(true, false, true, false);
		HorizontalLayout checkboxes = new HorizontalLayout();
		checkboxes.setSpacing(true);
		checkboxes.addComponent(new CheckBox("Option one"));
		checkboxes.addComponent(new CheckBox("Option two"));
		checkboxes.addComponent(new CheckBox("Option three"));
		Disclosure d = new Disclosure("Advanced Settings", checkboxes);
		d.setWidth("100%");
		d.open();
		demo.addComponent(d);
		layout.addComponent(demo);
		layout.setExpandRatio(demo, 2);

		d = new Disclosure(
				"Source Code",
				new Label(
						"<pre>Disclosure d = new Disclosure(\"Advanced Settings\",\n     new Label(\"...\");</pre>",
						Label.CONTENT_XHTML));
		d.setWidth("100%");
		d.setMargin(true, false, true, false);
		layout.addComponent(d);
		layout.setExpandRatio(d, 3);
		root.addComponent(layout);

		root.addComponent(new Label("<hr />", Label.CONTENT_XHTML));

		/* Flash new content */
		layout = new HorizontalLayout();
		layout.setWidth("100%");
		layout.setSpacing(true);
		text = new Label(
				"<h3>Emphasizing modified content</h3><p>Give the user feedback that something is changing in the user interface.</p>",
				Label.CONTENT_XHTML);
		layout.addComponent(text);
		layout.setExpandRatio(text, 1.5f);

		final CssLayout demo2 = new CssLayout();
		demo2.setWidth("100%");
		demo2.setMargin(true, false, true, false);

		Button add = new Button("Add new content", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				final Label flash = new Label("Hi! I'm some new content.");
				flash.addStyleName("flash");
				flash.addStyleName("flash-static");
				flash.setSizeUndefined();
				demo2.addComponent(flash, 1);
				ap.animate(flash, AnimType.FADE_IN).setDuration(500);
				final Animation a = ap.animate(flash, AnimType.FADE_IN)
						.setDuration(500).setDelay(500);
				ap.addListener(new AnimationListener() {
					public void onAnimation(AnimationEvent event) {
						if (event.getAnimation() == a) {
							flash.removeStyleName("flash");
						}
					}
				});
			}
		});
		demo2.addComponent(add);
		layout.addComponent(demo2);
		layout.setExpandRatio(demo2, 2);

		d = new Disclosure(
				"Source Code",
				new Label(
						"<pre>final Label flash = new Label(\"Hi! I'm some new content.\");\nflash.addStyleName(\"flash\");\nlayout.addComponent(flash);\nproxy.animate(flash, AnimType.FADE_IN).setDuration(500);\nfinal Animation a = proxy.animate(flash, AnimType.FADE_IN)\n                      .setDuration(500).setDelay(500);\n\nproxy.addListener(new AnimationListener() {\n  public void onAnimation(AnimationEvent event) {\n    if (event.getAnimation() == a) {\n      flash.removeStyleName(\"flash\");\n    }\n  }\n});</pre>",
						Label.CONTENT_XHTML));
		d.setWidth("100%");
		d.setMargin(true, false, true, false);
		layout.addComponent(d);
		layout.setExpandRatio(d, 3);
		root.addComponent(layout);

		return root;
	}
}

package org.vaadin.jouni.animator.demo;

import org.vaadin.jouni.animator.Animator;
import org.vaadin.jouni.animator.AnimatorProxy;
import org.vaadin.jouni.animator.Disclosure;
import org.vaadin.jouni.animator.AnimatorProxy.Animation;
import org.vaadin.jouni.animator.AnimatorProxy.AnimationEvent;
import org.vaadin.jouni.animator.AnimatorProxy.AnimationListener;
import org.vaadin.jouni.animator.shared.AnimType;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
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
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

@SuppressWarnings("serial")
@Title("Animator Add-on for Vaadin 7")
@Theme("animator-demo")
public class AnimatorTestUI extends UI {
    boolean fade = false;
    boolean roll = false;
    private AnimatorProxy ap;

    @Override
    protected void init(VaadinRequest request) {

        ap = new AnimatorProxy();

        setContent(new CssLayout() {
            {
                setSizeFull();

                addComponent(ap);

                HorizontalLayout header = new HorizontalLayout();
                header.setWidth("100%");
                header.setMargin(true);
                addComponent(header);

                Label title = new Label("Animator Add-on for Vaadin 7 Demo");
                title.addStyleName(Reindeer.LABEL_H1);
                header.addComponent(title);
                header.setExpandRatio(title, 1);

                Button directory = new Button(
                        "Download from the Add-on Directory",
                        new Button.ClickListener() {
                            public void buttonClick(ClickEvent event) {
                                getPage().open(
                                        "http://vaadin.com/addon/animator",
                                        "_blank");
                            }
                        });
                header.addComponent(directory);
                directory.addStyleName(Reindeer.BUTTON_DEFAULT);
                header.setComponentAlignment(directory, Alignment.MIDDLE_RIGHT);

                ap.animate(header, AnimType.FADE_IN).setDuration(500)
                        .setDelay(1500);

                TabSheet tabs = new TabSheet();
                tabs.setSizeFull();
                tabs.addStyleName(Reindeer.TABSHEET_BORDERLESS);
                addComponent(tabs);

                ap.animate(tabs, AnimType.FADE_IN).setDuration(300)
                        .setDelay(2000);

                tabs.addTab(proxy(), "AnimatorProxy", null);
                // Component proxy;
                // addComponent(proxy = proxy());

                // Welcome window
                Window w = new Window("Animator Add-on");
                w.addStyleName(Reindeer.WINDOW_BLACK);
                w.setClosable(false);
                w.setResizable(false);
                w.setDraggable(false);
                w.setWidth("400px");
                w.setHeight("200px");
                Label welcome = new Label("Welcome");
                welcome.setSizeUndefined();
                welcome.addStyleName(Reindeer.LABEL_H1);
                w.setContent(welcome);
                AnimatorTestUI.this.addWindow(w);
                w.center();
                ap.animate(w, AnimType.FADE_IN).setDelay(300).setDuration(500);
                ap.animate(w, AnimType.SIZE)
                        .setData("y=-100,x=-100,width=+200,height=+200")
                        .setDelay(500).setDuration(500);
                ap.animate(w, AnimType.FADE_OUT_REMOVE).setDelay(1400)
                        .setDuration(300);
            }
        });

    }

    private Component proxy() {
        CssLayout margin = new CssLayout();
        // margin.setMargin(true);
        margin.setWidth("100%");
        TabSheet tabs = new TabSheet();
        tabs.setWidth("100%");
        tabs.addStyleName(Reindeer.TABSHEET_MINIMAL);
        margin.addComponent(tabs);
        tabs.addTab(proxyIntro(), "Introduction", null);
        tabs.addTab(proxyDemo(), "Usage Examples", null);
        tabs.addListener(new TabSheet.SelectedTabChangeListener() {
            public void selectedTabChange(SelectedTabChangeEvent event) {
                ap.animate(event.getTabSheet().getSelectedTab(),
                        AnimType.FADE_IN);
            }
        });
        // margin.addComponent(proxyIntro());
        return margin;
    }

    private Layout proxyIntro() {
        HorizontalLayout main = new HorizontalLayout();
        main.setWidth("100%");
        main.setMargin(true);

        CssLayout layout = new CssLayout();
        layout.setWidth("100%");
        // layout.setMargin(false, true, true, true);
        Label info = new Label(
                "<h2>AnimatorProxy</h2><p>The <code>AnimatorProxy</code> is an invisible component, that animates any component in the same application it is attached to, <b>even sub-windows</b>.</p><p>It provides more animation types than the Animator wrapper, and it also provides a listener mechanism for the animations: you get an event when an animation is finished running.</p><h3>Example code</h3><pre>AnimatorProxy proxy = new AnimatorProxy();\nmainWindow.addComponent(proxy);\nproxy.animate(component, AnimType.FADE_IN).setDuration(500).setDelay(100);\n\n// This listener will catch all animations that are\n// passed through this proxy\nproxy.addListener(new AnimationListener() {\n  public void onAnimation(AnimationEvent event) {\n    System.out.println(event.getAnimation());\n  }\n});</pre></p>",
                Label.CONTENT_XHTML);
        layout.addComponent(info);
        main.addComponent(layout);
        main.setExpandRatio(layout, 3);

        layout = new CssLayout();
        layout.setWidth("100%");
        // layout.setMargin(true);
        main.addComponent(layout);
        main.setExpandRatio(layout, 2);

        layout.addComponent(new Label("<h3>Animation type examples</h3>",
                Label.CONTENT_XHTML));

        final CssLayout demo = new CssLayout();
        demo.setWidth("100%");
        demo.setHeight("200px");
        demo.addStyleName(Reindeer.LAYOUT_BLACK);
        // demo.setMargin(true);
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
        VerticalLayout root = new VerticalLayout();
        root.setMargin(true);
        root.setSpacing(true);

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
        // demo.setMargin(true, false, true, false);
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
        // d.setMargin(true, false, true, false);
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

        final VerticalLayout demo2 = new VerticalLayout();
        demo2.setMargin(true);

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
        // d.setMargin(true, false, true, false);
        layout.addComponent(d);
        layout.setExpandRatio(d, 3);
        root.addComponent(layout);

        return root;
    }

    private Layout animatorWrapperDemo() {
        HorizontalLayout main = new HorizontalLayout();
        main.setWidth("100%");
        main.setMargin(true);
        main.addStyleName(Reindeer.LAYOUT_WHITE);

        Label info = new Label(
                "<h2>Animator Wrapper</h2><p>The <code>Animator</code> wrapper component is a convinient way to add simple animations to your Vaadin applications. It behaves like a normal component: it has its own size and other features, and it provides two types of animations, fades and rolls. See example code below.</p><p><pre>Label label = new Label(\"Animate me.\");\nAnimator a = new Animator(label);\na.setWidth(\"100%\");\na.rollDown(300, 200);\na.fadeIn(300, 400);</pre></p><h3>Considerations</h3><p>Since the Animator wrapper introduces one extra component into the hierarchy, it naturally adds a bit of overhead to the rendering and processing.</p><p>The wrapper does pass the animation status back to the server when it finishes, but it does not provide any listener mechanism for those events (unlike AnimatorProxy).</p>",
                Label.CONTENT_XHTML);
        main.addComponent(info);
        main.setExpandRatio(info, 2);

        CssLayout layout = new CssLayout();
        layout.setWidth("100%");
        main.addComponent(layout);
        main.setExpandRatio(layout, 3);

        layout.addComponent(new Label("<h3>Animator wrapper demo</h3>",
                Label.CONTENT_XHTML));

        Label label = new Label(
                "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

        final Animator a = new Animator(label);
        a.setWidth("100%");
        a.setImmediate(true);
        layout.addComponent(a);

        final AnimEdit edit1 = new AnimEdit();
        Button toggle = new Button("Toggle Fade", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                if (fade) {
                    a.fadeIn(Integer.parseInt((String) edit1.duration
                            .getValue()), Integer.parseInt((String) edit1.delay
                            .getValue()));
                } else {
                    a.fadeOut(Integer.parseInt((String) edit1.duration
                            .getValue()), Integer.parseInt((String) edit1.delay
                            .getValue()));
                }
                fade = !fade;
            }
        });
        edit1.addComponent(toggle);
        edit1.setComponentAlignment(toggle, Alignment.BOTTOM_LEFT);
        layout.addComponent(edit1);

        final AnimEdit edit2 = new AnimEdit();
        toggle = new Button("Toggle Roll", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                if (roll) {
                    a.rollDown(Integer.parseInt((String) edit2.duration
                            .getValue()), Integer.parseInt((String) edit2.delay
                            .getValue()));
                } else {
                    a.rollUp(Integer.parseInt((String) edit2.duration
                            .getValue()), Integer.parseInt((String) edit2.delay
                            .getValue()));
                }
                roll = !roll;
            }
        });
        edit2.addComponent(toggle);
        edit2.setComponentAlignment(toggle, Alignment.BOTTOM_LEFT);
        layout.addComponent(edit2);

        return main;
    }

    private class AnimEdit extends HorizontalLayout {
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
            // setMargin(true, false, false, false);
        }
    }

}

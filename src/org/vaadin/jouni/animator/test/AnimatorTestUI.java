package org.vaadin.jouni.animator.test;

import org.vaadin.jouni.animator.Animator;
import org.vaadin.jouni.animator.client.CssAnimation;
import org.vaadin.jouni.dom.Dom;
import org.vaadin.jouni.dom.client.Css;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Theme("chameleon")
public class AnimatorTestUI extends UI {

    // @WebServlet(value = "/*", asyncSupported = true)
    // @VaadinServletConfiguration(productionMode = false, ui =
    // AnimatorTestUI.class, widgetset =
    // "org.vaadin.jouni.animator.AnimatorWidgetset")
    // public static class Servlet extends VaadinServlet {
    // private static final long serialVersionUID = 1L;
    // }

    @Override
    public void init(VaadinRequest request) {
        setContent(new VerticalLayout() {
            Label label = new Label("Animate Me") {
                {
                    setSizeUndefined();
                }
            };
            Button button = new Button("Animate Me");
            Window win = new Window("Window");
            // Window window = new Window("Animate Me") {
            // {
            // setContent(new Label(
            // "Plura mihi bona sunt, inclinet, amari petere vellent. Ab illo tempore, ab est sed immemorabili. Morbi fringilla convallis sapien, id pulvinar odio volutpat."));
            // setWidth("400px");
            // center();
            // }
            // };
            {
                setMargin(true);
                setSpacing(true);

                // Animator.animate(label, new Css().translateX("100px"))
                // .delay(1000).duration(2000);
                addComponent(button);
                addComponent(label);

                button.addClickListener(new ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        // Animator.animate(
                        // new CssAnimation(button, label).sendEndEvent())
                        // .to(new Css().scale(0.5));

                        new Dom(button).getStyle()
                                .setProperty("opacity", "0.5");

                        Animator.animate(button, label)
                                .to(new Css().translateY("200%"))

                                .queue(new CssAnimation(label)
                                        .to(new Css().translateY("0%"))
                                        .duration(1000).delay(60))

                                .queue(new CssAnimation(button).to(new Css()
                                        .translateX("100%")))

                                .queue(new CssAnimation(button)
                                        .to(new Css().translateX("0%"))
                                        .duration(1000).delay(60));
                    }
                });
                // Animator.animate(button, new Css().opacity(0));

                // TextField tf = new TextField();
                // new Snappy(tf)
                // .on(ClientEvent.keydown(Key.ESC))
                // .animate(button,
                // new Css().translateX("100px").opacity(1))
                // .blur(tf);
                // addComponent(tf);

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

                // addComponent(new SearchBar());

                // addWindow(win);
                // new Dom(win).getStyle().opacity(0);
                // Animator.animate(win, new Css().opacity(0)).duration(1);
                // Animator.animate(win, new Css().opacity(1)).delay(0)
                // .duration(1000);

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
            titleDom.getStyle().setProperty("text-align", "center");
            titleDom.getStyle().setProperty("font-size", "18px");
            titleDom.getStyle().setProperty("line-height", "30px");
            titleDom.getStyle().setProperty("font-weight", "bold");

            searchField.setWidth("320px");
            searchField.setHeight("30px");
            cancel.setWidth("80px");
            cancel.setHeight("30px");
            searchWrapper.addComponents(searchField, cancel);
            searchWrapperDom.getStyle().setProperty("white-space", "nowrap")
                    .setProperty("position", "relative");

            filters.setWidth("100%");
            filters.setHeight("30px");
            sender.setSizeFull();
            subject.setSizeFull();
            body.setSizeFull();
            all.setSizeFull();
            filters.addComponents(sender, subject, body, all);
            sender.setVisible(false);
            subject.setVisible(false);
            body.setVisible(false);
            all.setVisible(false);
            // filters.setVisible(false);
            // new Dom(filters).style().setProperty("display", "none");

            wrapper.addComponents(title, searchWrapper, filters);
            addComponent(wrapper);

            // new Snappy(searchField)
            // .on(ClientEvent.focus())
            // .animate(wrapper, new Css().translateY("-30px"))
            // .animate(cancel, new Css().translateX("-100%"))
            // .animate(title, new Css().translateY("20px"))
            // .show(filters)
            // .addActionListener(new ActionListener() {
            // public void onActionDone(Action action) {
            // if (action.type == ActionType.SHOW
            // && action.target == filters) {
            // // TODO make children visible
            // }
            // }
            // })
            //
            // .on(ClientEvent.keydown(Key.ESC)).setText(title, "Ah-ha!")
            // .click(cancel);

            // new Snappy(cancel).on(ClientEvent.clickPrimary())
            // .animate(wrapper, new Css().translateY("0"))
            // .animate(cancel, new Css().translateX("0"))
            // .animate(title, new Css().translateY("0"))
            // .blur(searchField);

            cancel.addClickListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    title.setValue("Back to normal");
                }
            });

        }
    }
}

package org.vaadin.jouni.animator.test;

import javax.servlet.annotation.WebServlet;

import org.vaadin.jouni.animator.Animator;
import org.vaadin.jouni.animator.Animator.AnimationEndEvent;
import org.vaadin.jouni.animator.Animator.AnimationListener;
import org.vaadin.jouni.animator.client.Animation;
import org.vaadin.jouni.dom.client.Css;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("valo")
public class AnimatorTestUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = AnimatorTestUI.class, widgetset = "org.vaadin.jouni.animator.AnimatorWidgetset")
    public static class Servlet extends VaadinServlet {
        private static final long serialVersionUID = 1L;
    }

    VerticalLayout layout;

    @Override
    public void init(VaadinRequest request) {
        // mSpring spring = new mSpring() {
        // @Override
        // protected void onChange(double massPos, float distance,
        // float acceleration, float speed) {
        // System.out.println(massPos);
        // }
        // };
        // spring.start(0, 1, 0, 0);

        setContent(layout = new VerticalLayout() {
            Label label = new Label("Animate Me") {
                {
                    setSizeUndefined();
                }
            };
            Button button = new Button("Animate Me");

            {
                addComponents(button, label);
                button.addClickListener(new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        Animator.add(
                                new Animation(label).to(
                                        new Css().setProperty("margin-left",
                                                "100px")).spring(120, 20, 5),
                                new AnimationListener() {
                                    @Override
                                    public void onAnimationEnd(
                                            AnimationEndEvent event) {
                                        // System.out.println("Animation end");
                                        // layout.removeComponent(label);
                                        // Animator.add(new Animation(button)
                                        // .to(new Css().opacity(0)));
                                    }
                                });
                        Animator.add(
                                new Animation(button)
                                        .from(new Css().opacity(1)).to(
                                                new Css().opacity(0)),
                                new AnimationListener() {
                                    @Override
                                    public void onAnimationEnd(
                                            AnimationEndEvent event) {
                                        System.out.println("Animation end");
                                    }
                                });
                    }
                });
            }
        });
    }
}

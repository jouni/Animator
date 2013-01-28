package org.vaadin.jouni.animator.demo;

import org.vaadin.jouni.animator.server.Animator;
import org.vaadin.jouni.animator.server.Disclosure;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("runo")
public class AnimatorDemo extends UI {
    @Override
    public void init(VaadinRequest request) {
        setContent(new VerticalLayout() {
            {

                Button button = new Button("Animate Me");
                addComponent(button);

                final Animator animator = new Animator(button);
                animator.fadeIn(3000);

                button.addClickListener(new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        animator.fadeIn();
                    }
                });

                Disclosure d = new Disclosure("Open");
                d.addComponent(new Label("It's just me in here!"));
                addComponent(d);

            }
        });

    }

}

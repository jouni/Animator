package org.vaadin.jouni.animator.client;

import org.vaadin.jouni.animator.server.Animator;

import com.google.gwt.core.client.GWT;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.LegacyConnector;
import com.vaadin.shared.ui.Connect;

@Connect(Animator.class)
public class LegacyAnimatorConnector extends LegacyConnector {

    @Override
    protected VAnimator createWidget() {
        return GWT.create(VAnimator.class);
    }

    @Override
    public VAnimator getWidget() {
        return (VAnimator) super.getWidget();
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidget().updateFromUIDL(uidl, client);
    }
}

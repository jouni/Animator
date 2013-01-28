package org.vaadin.jouni.animator.client;

import org.vaadin.jouni.animator.server.LegacyAnimator;

import com.google.gwt.core.client.GWT;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.LegacyConnector;
import com.vaadin.shared.ui.Connect;

@Connect(LegacyAnimator.class)
public class LegacyAnimatorConnector extends LegacyConnector {

    @Override
    protected VLegacyAnimator createWidget() {
        return GWT.create(VLegacyAnimator.class);
    }

    @Override
    public VLegacyAnimator getWidget() {
        return (VLegacyAnimator) super.getWidget();
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidget().updateFromUIDL(uidl, client);
    }
}

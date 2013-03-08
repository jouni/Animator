package org.vaadin.jouni.animator.client;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.LegacyConnector;
import com.vaadin.shared.ui.Connect;

@Connect(org.vaadin.jouni.animator.AnimatorProxy.class)
public class AnimatorProxyConnector extends LegacyConnector {

    @Override
    public VAnimatorProxy getWidget() {
        return (VAnimatorProxy) super.getWidget();
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidget().updateFromUIDL(uidl, client);
    }
}

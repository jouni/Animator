package org.vaadin.jouni.animator.client;

import org.vaadin.jouni.animator.server.AnimatorProxy;

import com.google.gwt.core.client.GWT;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.LegacyConnector;
import com.vaadin.shared.ui.Connect;

@Connect(AnimatorProxy.class)
public class AnimatorProxyConnector extends LegacyConnector {

	@Override
	protected VAnimatorProxy createWidget() {
		return GWT.create(VAnimatorProxy.class);
	}

	@Override
	public VAnimatorProxy getWidget() {
		return (VAnimatorProxy) super.getWidget();
	}

	@Override
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		getWidget().updateFromUIDL(uidl, client);
	}
}

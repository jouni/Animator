package org.vaadin.jouni.animator.client.ui;

import org.vaadin.jouni.animator.AnimatorProxy;

import com.google.gwt.core.client.GWT;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.Vaadin6Connector;

@Connect(AnimatorProxy.class)
public class AnimatorProxyConnector extends Vaadin6Connector {

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

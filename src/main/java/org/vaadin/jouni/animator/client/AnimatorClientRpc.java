package org.vaadin.jouni.animator.client;

import com.vaadin.shared.communication.ClientRpc;

public interface AnimatorClientRpc extends ClientRpc {

    public void animate(CssAnimation animation);

}
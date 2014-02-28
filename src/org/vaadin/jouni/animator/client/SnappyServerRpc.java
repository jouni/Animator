package org.vaadin.jouni.animator.client;

import com.vaadin.shared.communication.ServerRpc;

public interface SnappyServerRpc extends ServerRpc {

    public void actionDone(Action action);

}

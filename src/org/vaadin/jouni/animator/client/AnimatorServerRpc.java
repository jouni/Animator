package org.vaadin.jouni.animator.client;

import com.vaadin.shared.annotations.Delayed;
import com.vaadin.shared.communication.ServerRpc;

public interface AnimatorServerRpc extends ServerRpc {

    public void animationEnd(Animation animation);

    @Delayed
    public void preserveStyles(Animation animation);

}

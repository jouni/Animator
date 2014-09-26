package org.vaadin.jouni.animator.client;

import com.vaadin.shared.annotations.Delayed;
import com.vaadin.shared.communication.ServerRpc;

public interface AnimatorServerRpc extends ServerRpc {

    public void animationEnd(CssAnimation animation);

    @Delayed
    public void preserveStyles(CssAnimation animation);

}

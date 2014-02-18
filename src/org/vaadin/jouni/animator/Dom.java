package org.vaadin.jouni.animator;

import org.vaadin.jouni.animator.client.Css;
import org.vaadin.jouni.animator.client.DomState;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;

public class Dom extends AbstractExtension {

    public Dom(AbstractClientConnector target) {
        extend(target);
    }

    @Override
    public void extend(AbstractClientConnector target) {
        super.extend(target);
    }

    @Override
    public DomState getState() {
        return (DomState) super.getState();
    }

    protected Dom attribute(String attribute, String value) {
        getState().attributes.put(attribute.toLowerCase(), value);
        getState().increment++;
        return this;
    }

    public Dom tabIndex(int tabIndex) {
        return attribute("tabIndex", "" + tabIndex);
    }

    public Css style() {
        getState().increment++;
        return getState().css;
    }
}

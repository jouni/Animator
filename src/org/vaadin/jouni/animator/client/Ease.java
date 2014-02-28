package org.vaadin.jouni.animator.client;

public enum Ease {
    DEFAULT, IN, OUT, IN_OUT, LINEAR;

    public String cssValue() {
        if (this == DEFAULT) {
            return "";
        }
        if (this == IN || this == OUT || this == IN_OUT) {
            return "ease-" + name().toLowerCase().replace("_", "-");
        }
        return name().toLowerCase();
    }
}

package org.vaadin.jouni.animator.client;

import java.util.HashMap;

public class Css {

    public HashMap<String, String> properties = new HashMap<String, String>();

    protected String transforms = "";

    // public boolean persist = false;

    public Css setProperty(String property, String value) {
        properties.put(property.toLowerCase(), value);
        return this;
    }

    public Css opacity(float value) {
        setProperty("opacity", "" + value);
        return this;
    }

    public Css translate(String x, String y) {
        transforms += " translate(" + x + "," + y + ")";
        setProperty("transform", transforms);
        return this;
    }

    public Css translateX(String x) {
        transforms += " translateX(" + x + ")";
        setProperty("transform", transforms);
        return this;
    }

    public Css translateY(String y) {
        transforms += " translateY(" + y + ")";
        setProperty("transform", transforms);
        return this;
    }

    public Css rotate(int deg) {
        transforms += " rotate(" + deg + "deg)";
        setProperty("transform", transforms);
        return this;
    }

    public Css scale(double scale) {
        transforms += " scale(" + scale + ")";
        setProperty("transform", transforms);
        return this;
    }

    public Css scaleX(double scaleX) {
        transforms += " scaleX(" + scaleX + ")";
        setProperty("transform", transforms);
        return this;
    }

    public Css scaleY(double scaleY) {
        transforms += " scaleY(" + scaleY + ")";
        setProperty("transform", transforms);
        return this;
    }

}

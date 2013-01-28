package org.vaadin.jouni.animator.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Utility class for fetching CSS properties from DOM StyleSheets JS object and
 * for creating new CSS rules dynamically.
 */
public class CSSRule {

    private final String selector;
    private JavaScriptObject rules = null;

    private CSSRule(final String selector) {
        this.selector = selector;
    }

    /**
     * 
     * @param selector
     *            the CSS selector to search for in the stylesheets
     * @param deep
     *            should the search follow any @import statements?
     */
    public CSSRule(final String selector, final boolean deep) {
        this.selector = selector;
        fetchRule(selector, deep);
    }

    // TODO how to find the right LINK-element? We should probably give the
    // stylesheet a name.
    private native void fetchRule(final String selector, final boolean deep)
    /*-{
        var sheets = $doc.styleSheets;
        for(var i = 0; i < sheets.length; i++) {
            var sheet = sheets[i];
            if(sheet.href && sheet.href.indexOf("VAADIN/themes")>-1) {
                this.@org.vaadin.jouni.animator.client.CSSRule::rules = @org.vaadin.jouni.animator.client.CSSRule::searchForRule(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Z)(sheet, selector, deep);
                return;
            }
        }
        this.@org.vaadin.jouni.animator.client.CSSRule::rules = [];
    }-*/;

    /*
     * Loops through all current style rules and collects all matching to
     * 'rules' array. The array is reverse ordered (last one found is first).
     */
    private static native JavaScriptObject searchForRule(
            final JavaScriptObject sheet, final String selector,
            final boolean deep)
    /*-{
        if(!$doc.styleSheets)
            return null;

        selector = selector.toLowerCase();

        var allMatches = [];

        // IE handles imported sheet differently
        if(deep && sheet.imports.length > 0) {
            for(var i=0; i < sheet.imports.length; i++) {
                var imports = @org.vaadin.jouni.animator.client.CSSRule::searchForRule(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Z)(sheet.imports[i], selector, deep);
                allMatches.concat(imports);
            }
        }

        var theRules = new Array();
        if (sheet.cssRules)
            theRules = sheet.cssRules
        else if (sheet.rules)
            theRules = sheet.rules

        var j = theRules.length;
        for(var i=0; i<j; i++) {
            var r = theRules[i];
            if(r.type == 1 || sheet.imports) {
                var selectors = r.selectorText.toLowerCase().split(",");
                var n = selectors.length;
                for(var m=0; m<n; m++) {
                    if(selectors[m].replace(/^\s+|\s+$/g, "") == selector) {
                        allMatches.unshift(r);
                        break; // No need to loop other selectors for this rule
                    }
                }
            } else if(deep && r.type == 3) {
                // Search @import stylesheet
                var imports = @org.vaadin.jouni.animator.client.CSSRule::searchForRule(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Z)(r.styleSheet, selector, deep);
                allMatches.concat(imports);
            }
        }

        return allMatches;
    }-*/;

    /**
     * Returns a specific property value from this CSS rule.
     * 
     * @param propertyName
     *            camelCase CSS property name
     * @return the value of the property as a String
     */
    public native String getProperty(final String propertyName)
    /*-{
        var j = this.@org.vaadin.jouni.animator.client.CSSRule::rules.length;
        for(var i=0; i<j; i++){
            var value = this.@org.vaadin.jouni.animator.client.CSSRule::rules[i].style[propertyName];
        if(value)
            return value;
        }
        return null;
    }-*/;

    /**
     * Sets a specific property value for this CSS rule.
     * 
     * @param propertyName
     *            camelCase CSS property name
     * @param propertyValue
     *            the value of the property as a String
     */
    public native void setProperty(final String propertyName,
            final String propertyValue)
    /*-{
        this.@org.vaadin.jouni.animator.client.CSSRule::rules[0].style[propertyName] = propertyValue;
    }-*/;

    public String getSelector() {
        return selector;
    }

    public static CSSRule create(String selector) {
        CSSRule newRule = new CSSRule(selector);
        createRule(selector, newRule);
        return newRule;
    }

    /**
     * Creates a new CSS rule and attaches that to the CSSRule object passed as
     * an argument
     */
    private static native void createRule(final String selector, CSSRule rule)
    /*-{
    var sheets = $doc.styleSheets;
    for(var i = 0; i < sheets.length; i++) {
        var sheet = sheets[i];
        if(sheet.href && sheet.href.indexOf("VAADIN/themes")>-1) {
            if(sheet.insertRule) {
                sheet.insertRule(selector + "{}", sheet.cssRules.length);
                var r = sheet.cssRules[sheet.cssRules.length-1];
            } else { // IE
                sheet.addRule(selector, "foo:bar");
                var r = sheet.rules[sheet.rules.length-1];
            }
            rule.@org.vaadin.jouni.animator.client.CSSRule::rules = [r];
        }
    }
    }-*/;

}
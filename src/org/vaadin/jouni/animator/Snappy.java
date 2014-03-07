package org.vaadin.jouni.animator;

import java.util.ArrayList;
import java.util.Iterator;

import org.vaadin.jouni.animator.client.Action;
import org.vaadin.jouni.animator.client.Action.ActionType;
import org.vaadin.jouni.animator.client.ClientEvent;
import org.vaadin.jouni.animator.client.Css;
import org.vaadin.jouni.animator.client.CssAnimation;
import org.vaadin.jouni.animator.client.SnappyServerRpc;
import org.vaadin.jouni.animator.client.SnappyState;

import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;

public class Snappy extends AbstractExtension {
    private static final long serialVersionUID = -6291739338315576979L;

    private SnappyServerRpc rpc = new SnappyServerRpc() {
        private static final long serialVersionUID = 6788763332611568545L;

        @Override
        public void actionDone(Action action) {
            AbstractComponent target = (AbstractComponent) action.target;
            switch (action.type) {
            case CLICK:
                break;
            case STYLENAME_ADD:
                target.addStyleName(action.stringParams[0]);
                break;
            case STYLENAME_REMOVE:
                target.removeStyleName(action.stringParams[0]);
                break;
            case STYLENAME_TOGGLE:
                if (target.getStyleName().contains(action.stringParams[0])) {
                    target.removeStyleName(action.stringParams[0]);
                } else {
                    target.addStyleName(action.stringParams[0]);
                }
                break;
            case FOCUS:
                break;
            case BLUR:
                break;
            case ENABLE:
                target.setEnabled(true);
                break;
            case DISABLE:
                target.setEnabled(false);
                break;
            case SHOW:
                if (target instanceof ComponentContainer) {
                    for (Iterator<Component> iterator = ((ComponentContainer) target)
                            .iterator(); iterator.hasNext();) {
                        Component child = iterator.next();
                        child.setVisible(true);
                    }
                }
                break;
            case HIDE:
                if (target instanceof ComponentContainer) {
                    for (Iterator<Component> iterator = ((ComponentContainer) target)
                            .iterator(); iterator.hasNext();) {
                        Component child = iterator.next();
                        child.setVisible(false);
                    }
                }
                break;
            case SET_TEXT:
                if (target instanceof Label) {
                    Label label = (Label) target;
                    label.setValue(action.stringParams[0]);
                }
                break;
            }

            // If some other listener/rpc call changes the state back to
            // the previous one (which was sent to the client before), how do we
            // force that to be sent again to the client (since the JSON
            // encoding does so sort of diff to see if things have actually
            // changed)?

            // This is most likely not the best thing to do (does it have
            // unwanted side-effects, other than just sending the whole state
            // again?), but seems like the only way.
            target.getUI().getConnectorTracker().setDiffState(target, null);
        }
    };

    public Snappy(AbstractComponent target) {
        super.extend(target);
        registerRpc(rpc);
    }

    protected ClientEvent currentEvent;

    public Snappy on(ClientEvent event) {
        currentEvent = event;
        return this;
    }

    public Snappy animate(AbstractComponent target, Css style) {
        if (currentEvent == null) {
            throw new IllegalStateException(
                    "No event speficied for this action. You need to call the 'on' method first.");
        }
        ArrayList<CssAnimation> actions = getState().eventToAnimateActions
                .get(currentEvent);
        if (actions == null) {
            actions = new ArrayList<CssAnimation>();
            getState().eventToAnimateActions.put(currentEvent, actions);
        }
        actions.add(new CssAnimation(target, style));
        return this;
    }

    protected Snappy addAction(Action action) {
        if (currentEvent == null) {
            throw new IllegalStateException(
                    "No event speficied for this action. You need to call the 'on' method first.");
        }
        ArrayList<Action> actions = getState().eventToActions.get(currentEvent);
        if (actions == null) {
            actions = new ArrayList<Action>();
            getState().eventToActions.put(currentEvent, actions);
        }
        actions.add(action);
        return this;
    }

    public Snappy blur(AbstractComponent target) {
        return addAction(new Action(ActionType.BLUR, target));
    }

    public Snappy focus(AbstractComponent target) {
        return addAction(new Action(ActionType.FOCUS, target));
    }

    public Snappy click(AbstractComponent target) {
        return addAction(new Action(ActionType.CLICK, target));
    }

    public Snappy show(AbstractComponent target) {
        return addAction(new Action(ActionType.SHOW, target));
    }

    public Snappy hide(AbstractComponent target) {
        return addAction(new Action(ActionType.HIDE, target));
    }

    public Snappy enable(AbstractComponent target) {
        return addAction(new Action(ActionType.ENABLE, target));
    }

    public Snappy disable(AbstractComponent target) {
        return addAction(new Action(ActionType.DISABLE, target));
    }

    public Snappy setText(AbstractComponent target, String text) {
        Action action = new Action(ActionType.SET_TEXT, target);
        action.stringParams = new String[] { text };
        return addAction(action);
    }

    public Snappy addStyleName(AbstractComponent target, String stylename) {
        Action action = new Action(ActionType.STYLENAME_ADD, target);
        action.stringParams = new String[] { stylename };
        return addAction(action);
    }

    public Snappy removeStyleName(AbstractComponent target, String stylename) {
        Action action = new Action(ActionType.STYLENAME_REMOVE, target);
        action.stringParams = new String[] { stylename };
        return addAction(action);
    }

    public Snappy toggleStyleName(AbstractComponent target, String stylename) {
        Action action = new Action(ActionType.STYLENAME_TOGGLE, target);
        action.stringParams = new String[] { stylename };
        return addAction(action);
    }

    @Override
    public SnappyState getState() {
        return (SnappyState) super.getState();
    }

}

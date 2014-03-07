package org.vaadin.jouni.animator.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;

import org.vaadin.jouni.animator.Snappy;
import org.vaadin.jouni.animator.client.ClientEvent.EventType;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.VButton;
import com.vaadin.shared.ui.Connect;

@Connect(Snappy.class)
public class SnappyConnector extends AbstractExtensionConnector {

    private static final long serialVersionUID = -1303304505428230280L;

    SnappyServerRpc rpc = RpcProxy.create(SnappyServerRpc.class, this);

    Widget targetWidget;

    boolean needsFocusHandler = false;
    boolean needsBlurHandler = false;
    boolean needsKeydownHandler = false;
    boolean needsClickHandler = false;
    boolean needsChangeHandler = false;
    boolean needsMouseOutHandler = false;
    boolean needsMouseOverHandler = false;

    HandlerRegistration keydownHandler;
    HandlerRegistration focusHandler;
    HandlerRegistration blurHandler;
    HandlerRegistration clickHandler;
    HandlerRegistration changeHandler;
    HandlerRegistration mouseOutHandler;
    HandlerRegistration mouseOverHandler;

    @Override
    protected void extend(ServerConnector target) {
        targetWidget = ((AbstractComponentConnector) target).getWidget();
    }

    @Override
    public SnappyState getState() {
        return (SnappyState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        // Collect needed event types

        Set<ClientEvent> keySet = getState().eventToAnimateActions.keySet();
        Set<ClientEvent> keySet2 = getState().eventToActions.keySet();

        for (ClientEvent e : keySet) {
            updateNeededEventHandlers(e);
        }
        for (ClientEvent e : keySet2) {
            updateNeededEventHandlers(e);
        }

        // Blur handler
        if (needsBlurHandler && blurHandler == null) {
            blurHandler = targetWidget.addDomHandler(new BlurHandler() {
                @Override
                public void onBlur(BlurEvent event) {
                    runActions(EventType.BLUR, null);
                }
            }, BlurEvent.getType());
        } else if (blurHandler != null) {
            blurHandler.removeHandler();
        }

        // Focus handler
        if (needsFocusHandler && focusHandler == null) {
            AnimatorConnector.log("Needs focus handler");
            focusHandler = targetWidget.addDomHandler(new FocusHandler() {
                @Override
                public void onFocus(FocusEvent event) {
                    runActions(EventType.FOCUS, null);
                }
            }, FocusEvent.getType());
        } else if (focusHandler != null) {
            focusHandler.removeHandler();
        }

        // Keydown handler
        if (needsKeydownHandler && keydownHandler == null) {
            keydownHandler = targetWidget.addDomHandler(new KeyDownHandler() {
                @Override
                public void onKeyDown(KeyDownEvent event) {
                    runActions(EventType.KEYDOWN,
                            new int[] { event.getNativeKeyCode() });
                }
            }, KeyDownEvent.getType());
        } else if (keydownHandler != null) {
            keydownHandler.removeHandler();
        }

        // Click handler
        if (needsClickHandler && clickHandler == null) {
            clickHandler = targetWidget.addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    runActions(EventType.CLICK,
                            new int[] { event.getNativeButton() });
                }
            }, ClickEvent.getType());
        } else if (clickHandler != null) {
            clickHandler.removeHandler();
        }

        // Change handler
        if (needsChangeHandler && changeHandler == null) {
            changeHandler = targetWidget.addDomHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    runActions(EventType.CHANGE, null);
                }
            }, ChangeEvent.getType());
        } else if (changeHandler != null) {
            changeHandler.removeHandler();
        }

        // Mouse out handler
        if (needsMouseOutHandler && mouseOutHandler == null) {
            mouseOutHandler = targetWidget.addDomHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    runActions(EventType.MOUSEOUT, null);
                }
            }, MouseOutEvent.getType());
        } else if (mouseOutHandler != null) {
            mouseOutHandler.removeHandler();
        }

        // Mouse over handler
        if (needsMouseOverHandler && mouseOverHandler == null) {
            mouseOverHandler = targetWidget.addDomHandler(
                    new MouseOverHandler() {
                        @Override
                        public void onMouseOver(MouseOverEvent event) {
                            runActions(EventType.MOUSEOVER, null);
                        }
                    }, MouseOverEvent.getType());
        } else if (mouseOverHandler != null) {
            mouseOverHandler.removeHandler();
        }

    }

    private void updateNeededEventHandlers(ClientEvent e) {
        switch (e.type) {
        case BLUR:
            needsBlurHandler = true;
            break;
        case FOCUS:
            needsFocusHandler = true;
            break;
        case KEYDOWN:
            needsKeydownHandler = true;
            break;
        case CLICK:
            needsClickHandler = true;
            break;
        case CHANGE:
            needsChangeHandler = true;
            break;
        case MOUSEOUT:
            needsMouseOutHandler = true;
            break;
        case MOUSEOVER:
            needsMouseOverHandler = true;
            break;
        }
    }

    protected void runActions(EventType type, int[] intParams) {
        for (Entry<ClientEvent, ArrayList<CssAnimation>> e : getState().eventToAnimateActions
                .entrySet()) {
            if (checkEventParams(type, e.getKey(), intParams, null, null)) {
                for (CssAnimation animation : e.getValue()) {
                    AnimatorConnector.runAnimation(animation);
                }
            }
        }
        for (Entry<ClientEvent, ArrayList<Action>> e : getState().eventToActions
                .entrySet()) {
            if (checkEventParams(type, e.getKey(), intParams, null, null)) {
                for (Action action : e.getValue()) {
                    doAction(action);
                }
            }
        }
    }

    private boolean checkEventParams(EventType type, ClientEvent e,
            int[] intParams, String[] stringParams, Object[] objParams) {
        if (e.type == type && Arrays.equals(e.intParams, intParams)) {
            return true;
        }
        return false;
    }

    protected void doAction(Action action) {
        Widget targetWidget = null;
        if (action.target != null) {
            targetWidget = ((AbstractComponentConnector) action.target)
                    .getWidget();
        }

        boolean notifyServer = false;
        switch (action.type) {
        case CLICK:
            // TODO specify coordinates?
            DomEvent.fireNativeEvent(
                    Document.get().createClickEvent(1, -1, -1, -1, -1, false,
                            false, false, false), targetWidget);
            break;
        case STYLENAME_ADD:
            if (action.stringParams != null) {
                targetWidget.addStyleName(action.stringParams[0]);
            }
            notifyServer = true;
            break;
        case STYLENAME_REMOVE:
            if (action.stringParams != null) {
                targetWidget.removeStyleName(action.stringParams[0]);
            }
            notifyServer = true;
            break;
        case STYLENAME_TOGGLE:
            if (action.stringParams != null) {
                if (targetWidget.getStyleName()
                        .contains(action.stringParams[0])) {
                    targetWidget.removeStyleName(action.stringParams[0]);
                } else {
                    targetWidget.addStyleName(action.stringParams[0]);
                }
            }
            notifyServer = true;
            break;
        case FOCUS:
            boolean focus = true;
        case BLUR:
            focus = false;
            if (targetWidget instanceof FocusWidget) {
                ((FocusWidget) targetWidget).setFocus(focus);
            }
            break;
        case ENABLE:
            boolean enabled = true;
        case DISABLE:
            enabled = false;
            if (targetWidget instanceof FocusWidget) {
                ((FocusWidget) targetWidget).setEnabled(enabled);
            }
            notifyServer = true;
            break;
        case SHOW:
            boolean visible = true;
        case HIDE:
            visible = false;
            if (targetWidget != null) {
                targetWidget.setVisible(visible);
            }
            notifyServer = true;
            break;
        case SET_TEXT:
            if (targetWidget instanceof HTML && action.stringParams != null) {
                ((HTML) targetWidget).setText(action.stringParams[0]);
            }
            if (targetWidget instanceof VButton && action.stringParams != null) {
                ((VButton) targetWidget).setText(action.stringParams[0]);
            }
            notifyServer = true;
            break;
        }

        // Notify server of needed changes to target component state
        if (notifyServer) {
            rpc.actionDone(action);
        }
    }
}

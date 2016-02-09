package org.vaadin.jouni.animator.client;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.jouni.animator.Animator;
import org.vaadin.jouni.dom.client.DomConnector;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.ComputedStyle;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

@Connect(Animator.class)
public class AnimatorConnector extends AbstractExtensionConnector {

    private static final long serialVersionUID = 5590072744432016088L;

    final AnimatorServerRpc rpc = RpcProxy
            .create(AnimatorServerRpc.class, this);

    static HashMap<String, Animation> queue = new HashMap<String, Animation>();

    public AnimatorConnector() {
        registerRpc(AnimatorClientRpc.class, new AnimatorClientRpc() {

            private static final long serialVersionUID = -5974716642919513367L;

            @Override
            public void animate(final Animation animation) {
                // TODO set the 'from' properties on the targets
                Timer t = new Timer() {
                    @Override
                    public void run() {
                        runAnimation(animation);

                        if (animation.preserveStyles) {
                            rpc.preserveStyles(animation);
                        }

                    }
                };
                t.schedule(animation.delay);
            }

        });
    }

    public void runAnimation(final Animation animation) {
        if (animation.animationTargets == null
                || animation.animationTargets.length == 0) {
            getLogger().warning(
                    "No targets specified for this animation: " + animation);
            return;
        }
        Scheduler.get().scheduleFinally(new ScheduledCommand() {
            @Override
            public void execute() {
                getLogger().log(Level.INFO, animation.toString());

                for (int i = 0; i < animation.animationTargets.length; i++) {
                    Element target = ((AbstractComponentConnector) animation.animationTargets[i])
                            .getWidget().getElement();
                    final Style targetStyle = target.getStyle();
                    ComputedStyle cs = new ComputedStyle(target);

                    float[] params = animation.spring;

                    // TODO support transforms

                    for (final String propName : animation.to.properties
                            .keySet()) {
                        // Get the unit
                        float endValue = Float
                                .parseFloat(animation.to.properties.get(
                                        propName).replaceAll("[a-zA-Z%]*", ""));
                        final String unit = animation.to.properties.get(
                                propName).replaceAll("[0-9\\.]*", "");
                        getLogger().log(Level.INFO, "UNIT: " + unit);

                        final mSpring spring = new mSpring(params[0],
                                params[1], params[2]) {
                            @Override
                            protected void onChange(double massPos,
                                    float distance, float acceleration,
                                    float speed) {
                                targetStyle.setProperty(
                                        DomConnector.domPropertyName(propName),
                                        massPos + unit);
                            };

                            @Override
                            protected void onRest() {
                                rpc.animationEnd(animation);
                            };
                        };
                        int startVal = cs.getIntProperty(DomConnector
                                .domPropertyName(propName));

                        spring.start(0, startVal, 0, endValue);
                    }

                }

            }
        });

    }

    static Logger getLogger() {
        return Logger.getLogger(AnimatorConnector.class.getName());
    }

    @Override
    protected void extend(ServerConnector target) {
        // TODO Auto-generated method stub
    }

}

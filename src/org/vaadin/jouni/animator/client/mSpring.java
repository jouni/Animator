package org.vaadin.jouni.animator.client;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;

/**
 * Spring physics calculations for use in animations.
 * 
 * Adapted from Matthaeus Krenn's springTo.js script (MIT License)
 * http://jsdo.it/matthaeus/mSpring
 * 
 */
public class mSpring {

    public float acceleration = 0;
    public float distance = 0;
    public float speed = 0;
    public float springForce = 0;
    public float dampingForce = 0;
    public float anchorPos = 0;
    public float massPos = 0;

    public float stiffness = 120;
    public float mass = 10;
    public float friction = 3;

    public mSpring() {
    }

    public mSpring(float stiffness, float mass, float friction) {
        this.stiffness = stiffness;
        this.mass = mass;
        this.friction = friction;
    }

    public void start(float acceleration, float massPos, float speed,
            float anchorPos) {
        onStart();

        if (massPos > 0) {
            this.massPos = massPos;
        }
        if (speed > 0) {
            this.speed = speed;
        }
        if (acceleration > 0) {
            this.speed = acceleration * 10;
        }
        if (anchorPos > 0) {
            this.anchorPos = anchorPos;
        }

        step();
    }

    protected void step() {
        distance = massPos - anchorPos;
        dampingForce = -friction * speed;
        springForce = -stiffness * distance;

        float totalForce = springForce + dampingForce;

        acceleration = totalForce / mass;
        speed += acceleration;
        massPos += speed / 100;

        if (Math.round(100 * massPos) / 100.0f == Math.round(100 * anchorPos) / 100.0f
                && Math.abs(speed) < 0.2) {
            onRest();
        } else {
            onChange(Math.round(100 * massPos) / 100.0f, distance,
                    acceleration, speed);
            AnimationScheduler.get().requestAnimationFrame(
                    new AnimationCallback() {
                        @Override
                        public void execute(double timestamp) {
                            step();
                        }
                    });
            // try {
            // Thread.sleep(1000 / 60);
            // } catch (InterruptedException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
            // step();
        }
    }

    protected void stop(float acceleration, float massPos, float speed,
            float anchorPos) {
        massPos = anchorPos;
        speed = 0;
    }

    protected void onStart() {

    }

    protected void onChange(double massPos, float distance, float acceleration,
            float speed) {

    }

    protected void onRest() {

    }

}

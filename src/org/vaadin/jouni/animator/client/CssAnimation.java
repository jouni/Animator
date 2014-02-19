package org.vaadin.jouni.animator.client;

import com.vaadin.shared.Connector;

public class CssAnimation {

	static int idCounter = 0;

	public int id = idCounter++;

	public Css css = new Css();

	public int duration = 200;

	public int delay = 0;

	public int iterationCount = 1;

	public Ease easing = Ease.DEFAULT;

	public Connector eventTarget = null;

	public ClientEvent event = null;

	public boolean useKeyframeAnimation = false;

	public CssAnimation duration(int millis) {
		duration = millis;
		return this;
	}

	public CssAnimation delay(int millis) {
		delay = millis;
		return this;
	}

	public CssAnimation ease(Ease easing) {
		this.easing = easing;
		return this;
	}

	public CssAnimation sendEndEvent() {
		this.useKeyframeAnimation = true;
		return this;
	}

}

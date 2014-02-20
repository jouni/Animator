package org.vaadin.jouni.animator.client;

public enum ClientEvent {
	CLICK_PRIMARY("1") //
	, CLICK_SECONDARY("2") //
	, MOUSEOVER //
	, MOUSEOUT //
	, MOUSEDOWN //
	, MOUSEUP //
	, FOCUS //
	, BLUR //
	, WINDOW_CLOSE //
	, KEYDOWN_ESC("27") //
	, KEYDOWN_ENTER("13") //
	, KEYDOWN_SPACEBAR("32") //
	, KEYDOWN_ARROW_LEFT("37")//
	, KEYDOWN_ARROW_UP("38")//
	, KEYDOWN_ARROW_RIGHT("39")//
	, KEYDOWN_ARROW_DOWN("40")//
	;

	private String[] params;

	private ClientEvent(String... params) {
		this.params = params;
	}

	public String[] getParams() {
		return params;
	}

}

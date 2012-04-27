package org.vaadin.jouni.animator.client.ui;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.ui.VWindow;

/**
 * Client side widget which communicates with the server. Messages from the
 * server are shown as HTML and mouse clicks are sent to the server.
 */
public class VAnimatorProxy extends SimplePanel implements Paintable {

	public static final String ANIM_STYLE_PREFIX = "v-anim-";

	public enum AnimType {
		/**
		 * Fades in the target. The required space for the target is reserved
		 * from the layout before the animation is triggered, even if a delay is
		 * used.
		 */
		FADE_IN,
		/**
		 * Fades in the target. The required space for the target is not
		 * reserved until the animation is triggered.
		 * <p>
		 * <b>NOTE</b> This type can not be used together with any ROLL or SIZE
		 * animations.
		 */
		FADE_IN_POP,
		/**
		 * Fades out the component. The required space of the component is
		 * preserved in the layout, and the component is not removed from the
		 * layout.
		 */
		FADE_OUT,
		/**
		 * Fades out the component. The component is removed from the parent
		 * layout after the animation is finished, and the required space of the
		 * target is collapsed.
		 * <p>
		 * <b>NOTE</b> This event is mostly just for convenience, and you can
		 * get the same result by adding an AnimationListener to the
		 * AnimatorProxy, and removing the target component after the event is
		 * triggered (you can filter the event by target and type).
		 */
		FADE_OUT_REMOVE,
		/**
		 * Hides the target by clipping it with a mask moving upwards.
		 * <p>
		 * <b>NOTE</b> After the animation, the target will become visible again
		 * if it has a defined height. Undefined high targets will remain
		 * hidden. If you wish to keep the target hidden in any case, you need
		 * to add an AnimationListener to the AnimatorProxy, and explicitly set
		 * the height of the target to zero after the animation has finished.
		 * */
		ROLL_UP_CLOSE,
		/**
		 * Hides the target by clipping it with a mask moving upwards. Removes
		 * the component from its parent layout after the animation is finished.
		 */
		ROLL_UP_CLOSE_REMOVE,
		/**
		 * Reveals the target by clipping it with a mask moving downwards. The
		 * required space for the target is reserved from the layout before the
		 * animation is triggered, even if a delay is used.
		 * */
		ROLL_DOWN_OPEN,
		/**
		 * Reveals the target by clipping it with a mask moving downwards. The
		 * required space for the target is not reserved until the animation is
		 * triggered.
		 */
		ROLL_DOWN_OPEN_POP,
		/**
		 * Reveals the target by clipping it with a mask moving to the right.
		 * The required space for the target is reserved from the layout before
		 * the animation is triggered, even if a delay is used.
		 * */
		ROLL_RIGHT_OPEN,
		/**
		 * Reveals the target by clipping it with a mask moving to the right.
		 * The required space for the target is not reserved until the animation
		 * is triggered.
		 */
		ROLL_RIGHT_OPEN_POP,
		/**
		 * Hides the target by clipping it with a mask moving to the left.
		 * <p>
		 * <b>NOTE</b> After the animation, the target will become visible again
		 * if it has a defined width. Undefined wide targets will remain hidden.
		 * If you wish to keep the target hidden in any case, you need to add an
		 * AnimationListener to the AnimatorProxy, and explicitly set the width
		 * of the target to zero after the animation has finished.
		 * */
		ROLL_LEFT_CLOSE,
		/**
		 * Hides the target by clipping it with a mask moving to the left.
		 * Removes the component from its parent layout after the animation is
		 * finished.
		 */
		ROLL_LEFT_CLOSE_REMOVE,
		/**
		 * Animate the target's size to a specific pixel value. The final size
		 * is given using the
		 * {@link org.vaadin.jouni.animator.AnimatorProxy.Animation#setData(String)}
		 * method, in the form of "width=100,height=100". Either width or height
		 * must be specified, but both are not required. The values are pixels.
		 * You can prefix the values with either '+' or '-' to size the
		 * component relatively, instead of absolutely.
		 * <p>
		 * If you're animating a sub-window, you can also animate its position
		 * using this animation type. Just provide x and y coordinates with
		 * {@link org.vaadin.jouni.animator.AnimatorProxy.Animation#setData(String)}
		 * , e.g. <code>anim.setData("x=50,y=100");</code>
		 */
		SIZE;
		@Override
		public String toString() {
			return super.toString().toLowerCase().replace("_", "-");
		}
	}

	/** The client side widget identifier */
	protected String paintableId;

	/** Reference to the server connection object. */
	protected ApplicationConnection client;
	private boolean immediate;

	public VAnimatorProxy() {
		super();
		// Disallow styling of the Animator element
		setStyleName("");
		DOM.setStyleAttribute(getElement(), "overflow", "hidden");
	}

	private boolean cancellingAll = false;

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		paintableId = uidl.getId();
		this.client = client;
		immediate = uidl.hasAttribute("immediate");

		for (int i = 0; i < uidl.getChildCount(); i++) {
			UIDL ar = uidl.getChildUIDL(i);
			runAnimation(ar);
		}

		if (uidl.hasAttribute("cancelAll")) {
			cancellingAll = true;
			for (Animation a : animations.values()) {
				a.cancel();
			}
			cancellingAll = false;
		}
	}

	private HashMap<Integer, Animation> animations = new HashMap<Integer, Animation>();

	private void runAnimation(final UIDL a) {
		Widget target = (Widget) client.getPaintable(a
				.getStringAttribute("target"));

		if (target == null) {
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

				public void execute() {
					// FIXME stop endless loops, if component is already removed
					// from the client
					// VConsole.error("Trying to find animation target again...");
					runAnimation(a);
				}
			});
			return;
		}
		String type = a.getStringAttribute("type");
		int delay = a.getIntAttribute("delay");
		int duration = a.getIntAttribute("dur");
		int aid = a.getIntAttribute("aid");
		String data = a.hasAttribute("data") ? a.getStringAttribute("data")
				: null;
		if (type.indexOf("fade") == 0) {
			FadeAnimation f = new FadeAnimation(target, type, aid);
			f.run(duration, new Date().getTime() + delay);
			animations.put(aid, f);
		} else if (type.indexOf("roll") == 0) {
			RollAnimation r = new RollAnimation(target, type, aid);
			r.run(duration, new Date().getTime() + delay);
			animations.put(aid, r);
		} else if (type.indexOf("size") == 0) {
			SizeAnimation s = new SizeAnimation(target, aid, data);
			s.run(duration, new Date().getTime() + delay);
			animations.put(aid, s);
		}
	}

	protected class FadeAnimation extends Animation {

		/* 1==fade in, -1==fade out */
		private int dir;
		private Widget target;
		private int aid;
		private String type;
		private boolean removeAfter = false;
		private boolean popBefore = false;

		public FadeAnimation(Widget w, String type, int aid) {
			target = w;
			this.type = type;
			this.aid = aid;
			setDir(type.indexOf(AnimType.FADE_IN.toString()) == 0 ? 1 : -1);
			removeAfter = type.equals(AnimType.FADE_OUT_REMOVE.toString());
			popBefore = type.equals(AnimType.FADE_IN_POP.toString());

		}

		private void setDir(int dir) {
			this.dir = dir;
			if (dir > 0) {
				target.getElement().getStyle().setOpacity(0);
				if (target instanceof VWindow) {
					((Element) target.getElement().getPreviousSibling())
							.getStyle().setOpacity(0);
				}
			}
		}

		@Override
		protected void onStart() {
			target.getElement().getStyle().clearVisibility();
			if (dir == 1) {
				target.removeStyleName(ANIM_STYLE_PREFIX + type);
			}
			if (popBefore) {
				Util.notifyParentOfSizeChange((Paintable) target, false);
			}
			super.onStart();
		}

		@Override
		protected void onUpdate(double progress) {
			if (dir > 0) {
				target.removeStyleName(ANIM_STYLE_PREFIX + type);
				if (BrowserInfo.get().isIE8()) {
					target.getElement()
							.getStyle()
							.setProperty(
									"filter",
									"progid:DXImageTransform.Microsoft.Alpha(Opacity="
											+ (int) (progress * 100) + ")");
					if (target instanceof VWindow) {
						((Element) target.getElement().getPreviousSibling())
								.getStyle().setProperty(
										"filter",
										"progid:DXImageTransform.Microsoft.Alpha(Opacity="
												+ (int) (progress * 100) + ")");
					}
				} else {
					target.getElement().getStyle().setOpacity(progress);
					if (target instanceof VWindow) {
						((Element) target.getElement().getPreviousSibling())
								.getStyle().setOpacity(progress);
					}
				}
			} else {
				if (BrowserInfo.get().isIE8()) {
					target.getElement()
							.getStyle()
							.setProperty(
									"filter",
									"progid:DXImageTransform.Microsoft.Alpha(Opacity="
											+ (int) ((1 - progress) * 100)
											+ ")");
					if (target instanceof VWindow) {
						((Element) target.getElement().getPreviousSibling())
								.getStyle().setProperty(
										"filter",
										"progid:DXImageTransform.Microsoft.Alpha(Opacity="
												+ (int) ((1 - progress) * 100)
												+ ")");
					}
				} else {
					target.getElement().getStyle().setOpacity(1 - progress);
					if (target instanceof VWindow) {
						((Element) target.getElement().getPreviousSibling())
								.getStyle().setOpacity(1 - progress);
					}
				}
			}
		}

		@Override
		public void onComplete() {
			super.onComplete();
			target.removeStyleName(ANIM_STYLE_PREFIX + type);
			if (dir < 0) {
				target.getElement().getStyle().setVisibility(Visibility.HIDDEN);
				if (target instanceof VWindow) {
					((Element) target.getElement().getPreviousSibling())
							.getStyle().setVisibility(Visibility.HIDDEN);
				}
			}
			target.getElement().getStyle().clearOpacity();
			if (target instanceof VWindow) {
				((Element) target.getElement().getPreviousSibling()).getStyle()
						.clearOpacity();
			}
			if (BrowserInfo.get().isIE8()) {
				target.getElement().getStyle().setProperty("filter", "");
			}

			if (removeAfter) {
				target.getElement().getStyle().setDisplay(Display.NONE);
				if (target instanceof VWindow) {
					((Element) target.getElement().getPreviousSibling())
							.getStyle().setDisplay(Display.NONE);
				}
				Util.notifyParentOfSizeChange((Paintable) target, true);
			}
			animEvents.put(aid + "", type);
			animations.remove(aid);
			if (immediate) {
				queue.run();
			} else {
				queue.schedule(50);
			}
		}

		@Override
		protected void onCancel() {
			animEvents.put(aid + "", type + ",cancelled=true");
			if (!cancellingAll) {
				animations.remove(aid);
			}
			if (immediate) {
				queue.run();
			} else {
				queue.schedule(50);
			}
		}
	}

	private Map<String, Object> animEvents = new HashMap<String, Object>();

	private Timer queue = new Timer() {
		@Override
		public void run() {
			Map<String, Object> temp = new HashMap<String, Object>(animEvents);
			client.updateVariable(paintableId, "anim", temp, true);
			animEvents.clear();
		}
	};

	protected class RollAnimation extends Animation {

		/* -1==roll up, 1==roll down, 2==roll left, 3==roll right */
		private int dir;
		private int size;
		private Widget target;
		private int aid;
		private String type;
		private boolean removeAfter = false;
		private boolean popBefore = false;
		private String origOverflow;
		private boolean isWindow = false;
		private Element shadow;

		public RollAnimation(Widget w, String type, int aid) {
			target = w;
			this.type = type;
			this.aid = aid;
			if (type.indexOf("up") > 0 || type.indexOf("down") > 0) {
				dir = type.indexOf("open") > 0 ? 1 : -1;
			} else {
				dir = type.indexOf("open") > 0 ? 3 : 2;
			}
			removeAfter = type.indexOf("remove") > 0;
			popBefore = type.indexOf("pop") > 0;

			if (target instanceof VWindow) {
				shadow = (Element) target.getElement().getPreviousSibling();
				isWindow = true;
			}
			if (isWindow && (dir == 1 || dir == 3)) {
				shadow.getStyle().setDisplay(Display.NONE);
			}
		}

		@Override
		protected void onStart() {
			if (dir == 1 || dir == 3) {
				target.removeStyleName(ANIM_STYLE_PREFIX + type);
				if (isWindow) {
					((Element) target.getElement().getPreviousSibling())
							.getStyle().clearDisplay();
				}
				if (popBefore) {
					Util.notifyParentOfSizeChange((Paintable) target, false);
					if (target instanceof HasWidgets) {
						client.runDescendentsLayout((HasWidgets) target);
					}
				}
			}
			size = dir <= 1 ? target.getOffsetHeight() : target
					.getOffsetWidth();

			if (dir == 1) {
				if (isWindow) {
					shadow.getStyle().setHeight(0, Unit.PX);
				}
				target.getElement().getStyle().setHeight(0, Unit.PX);
			} else if (dir == 3) {
				if (isWindow) {
					shadow.getStyle().setWidth(0, Unit.PX);
				}
				target.getElement().getStyle().setWidth(0, Unit.PX);
			}

			origOverflow = target.getElement().getStyle().getOverflow();
			target.getElement().getStyle().setOverflow(Overflow.HIDDEN);
			super.onStart();
		}

		@Override
		protected void onUpdate(double progress) {
			if (dir == 1 || dir == 3) {
				target.removeStyleName(ANIM_STYLE_PREFIX + type);
			}
			if (dir == 1 || dir == -1) {
				int h = dir == 1 ? (int) (progress * size)
						: (int) ((1 - progress) * size);
				if (isWindow) {
					shadow.getStyle().setHeight(h, Unit.PX);
				}
				target.getElement().getStyle().setHeight(h, Unit.PX);
			} else if (dir == 3 || dir == 2) {
				int w = dir == 3 ? (int) (progress * size)
						: (int) ((1 - progress) * size);
				if (isWindow) {
					shadow.getStyle().setWidth(w, Unit.PX);
				}
				target.getElement().getStyle().setWidth(w, Unit.PX);
			}
			// TODO parameterize this (new anim type?)
			// com.vaadin.terminal.gwt.client.Util.notifyParentOfSizeChange(
			// VAnimatorProxy.this, false);
		}

		@Override
		protected void onComplete() {
			super.onComplete();
			if (origOverflow != null && !origOverflow.equals("")) {
				target.getElement().getStyle()
						.setProperty("overflow", origOverflow);
			} else if (dir == 1 || dir == 3) {
				target.getElement().getStyle().clearOverflow();
			}
			if (removeAfter) {
				target.getElement().getStyle().setDisplay(Display.NONE);
				if (isWindow) {
					shadow.getStyle().setDisplay(Display.NONE);
				}
			}
			Util.notifyParentOfSizeChange((Paintable) target, true);
			if (target instanceof HasWidgets) {
				client.runDescendentsLayout((HasWidgets) target);
			}
			animEvents.put(aid + "", type);
			animations.remove(aid);
			if (immediate) {
				queue.run();
			} else {
				queue.schedule(50);
			}
		}

		@Override
		protected void onCancel() {
			Util.notifyParentOfSizeChange((Paintable) target, true);
			if (target instanceof HasWidgets) {
				client.runDescendentsLayout((HasWidgets) target);
			}
			animEvents.put(aid + "", type + ",cancelled=true");
			if (!cancellingAll) {
				animations.remove(aid);
			}
			if (immediate) {
				queue.run();
			} else {
				queue.schedule(50);
			}
		}
	}

	protected class SizeAnimation extends Animation {

		private Widget target;
		private int aid;
		private String data;
		private int endWidth = -1;
		private int endHeight = -1;
		/*
		 * direction specifies if this animation is relative (1 for increasing
		 * size, -1 for decreasing size) or absolute (0)
		 */
		private int widthDirection = 0;
		private int heightDirection = 0;
		private String origOverflow;
		private int startWidth;
		private int startHeight;

		private boolean isWindow = false;

		private boolean animatePosition = false;
		private int endX = -1;
		private int endY = -1;
		private int startX = 0;
		private int startY = 0;
		private int xDirection = 0;
		private int yDirection = 0;

		/*
		 * should layouts be re-rendered after each animation step (slower if
		 * true)
		 */
		private boolean forceRedraw = false;

		public SizeAnimation(Widget w, int aid, String data) {
			target = w;
			this.aid = aid;
			this.data = data != null ? data : "";
			isWindow = target instanceof VWindow;
		}

		@Override
		protected void onStart() {
			String[] values = data.split(",");
			for (String keyValue : values) {
				String[] keyValuePair = keyValue.trim().split("=");
				if (keyValuePair[0].trim().equals("width")) {
					if (keyValuePair[1].contains("-")) {
						widthDirection = -1;
					} else if (keyValuePair[1].contains("+")) {
						widthDirection = 1;
					}
					endWidth = Math.abs(Integer.parseInt(keyValuePair[1].trim()
							.replaceAll("[+-]", "")));
				} else if (keyValuePair[0].trim().equals("height")) {
					if (keyValuePair[1].contains("-")) {
						heightDirection = -1;
					} else if (keyValuePair[1].contains("+")) {
						heightDirection = 1;
					}
					endHeight = Math.abs(Integer.parseInt(keyValuePair[1]
							.trim().replaceAll("[+-]", "")));
				} else if (keyValuePair[0].trim().equals("x")) {
					animatePosition = true;
					if (keyValuePair[1].contains("-")) {
						xDirection = -1;
					} else if (keyValuePair[1].contains("+")) {
						xDirection = 1;
					}
					endX = Integer.parseInt(keyValuePair[1].trim().replaceAll(
							"[+-]", ""));
				} else if (keyValuePair[0].trim().equals("y")) {
					animatePosition = true;
					if (keyValuePair[1].contains("-")) {
						yDirection = -1;
					} else if (keyValuePair[1].contains("+")) {
						yDirection = 1;
					}
					endY = Integer.parseInt(keyValuePair[1].trim().replaceAll(
							"[+-]", ""));
				} else if (keyValuePair[0].trim().equals("redraw")) {
					forceRedraw = keyValuePair[1].trim().equals("true");
				}
			}

			startWidth = target.getOffsetWidth();
			startHeight = target.getOffsetHeight();
			if (widthDirection != 0) {
				endWidth = startWidth + (widthDirection * endWidth);
			}
			if (heightDirection != 0) {
				endHeight = startHeight + (heightDirection * endHeight);
			}

			if (animatePosition) {
				if (!isWindow) {
					target.getElement().getStyle()
							.setPosition(Position.ABSOLUTE);
				}
				String left = target.getElement().getStyle().getLeft();
				if (left != null && !left.equals("")) {
					startX = Integer.parseInt(left.substring(0,
							left.length() - 2));
				} else {
					startX = 0;
				}
				startX = target.getElement().getOffsetLeft();
				String top = target.getElement().getStyle().getTop();
				if (top != null && !top.equals("")) {
					startY = Integer
							.parseInt(top.substring(0, top.length() - 2));
				} else {
					startY = 0;
				}
				startY = target.getElement().getOffsetTop();
			}
			if (xDirection != 0) {
				endX = startX + (xDirection * endX);
			}
			if (yDirection != 0) {
				endY = startY + (yDirection * endY);
			}

			if (data.indexOf("width") == -1) {
				endWidth = startWidth;
			}
			if (data.indexOf("height") == -1) {
				endHeight = startHeight;
			}
			if (data.indexOf("x") == -1) {
				endX = startX;
			}
			if (data.indexOf("y") == -1) {
				endY = startY;
			}

			origOverflow = target.getElement().getStyle().getOverflow();
			target.getElement().getStyle().setOverflow(Overflow.HIDDEN);
			super.onStart();
		}

		@Override
		protected void onUpdate(double progress) {
			target.setWidth((int) (startWidth + progress
					* (endWidth - startWidth))
					+ "px");
			target.setHeight((int) (startHeight + progress
					* (endHeight - startHeight))
					+ "px");
			if (isWindow && animatePosition) {
				((VWindow) target).setPopupPosition((int) (startX + progress
						* (endX - startX)), (int) (startY + progress
						* (endY - startY)));
			} else if (animatePosition) {
				target.getElement()
						.getStyle()
						.setLeft((int) (startX + progress * (endX - startX)),
								Unit.PX);
				target.getElement()
						.getStyle()
						.setTop((int) (startY + progress * (endY - startY)),
								Unit.PX);
			}

			// TODO parameterize this
			if (target instanceof HasWidgets) {
				client.runDescendentsLayout((HasWidgets) target);
			}
			if (forceRedraw) {
				Util.notifyParentOfSizeChange((Paintable) target, false);
			}
		}

		@Override
		protected void onComplete() {
			super.onComplete();
			if (origOverflow != null && !origOverflow.equals("")) {
				target.getElement().getStyle()
						.setProperty("overflow", origOverflow);
			} else {
				target.getElement().getStyle().clearOverflow();
			}
			if (target instanceof HasWidgets) {
				client.runDescendentsLayout((HasWidgets) target);
			}
			Util.notifyParentOfSizeChange((Paintable) target, true);
			// TODO save position information somewhere, so that the widget
			// positions won't reset after an update
			// TODO send window position back to server
			animEvents.put(aid + "", AnimType.SIZE.toString() + ",width="
					+ endWidth + ",height=" + endHeight);
			animations.remove(aid);
			if (immediate) {
				queue.run();
			} else {
				queue.schedule(50);
			}
		}

		@Override
		protected void onCancel() {
			if (target instanceof HasWidgets) {
				client.runDescendentsLayout((HasWidgets) target);
			}
			Util.notifyParentOfSizeChange((Paintable) target, true);
			animEvents.put(aid + "", AnimType.SIZE.toString() + ",width="
					+ endWidth + ",height=" + endHeight + ",cancelled=true");
			if (!cancellingAll) {
				animations.remove(aid);
			}
			if (immediate) {
				queue.run();
			} else {
				queue.schedule(50);
			}
		}
	}

}

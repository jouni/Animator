package org.vaadin.jouni.animator.client;

public class ClientEvent {

    public enum EventType {
        KEYDOWN //
        , FOCUS //
        , BLUR //
        , CLICK //
        , MOUSEOVER //
        , MOUSEOUT //
        , CHANGE //
    }

    public EventType type;
    public String[] stringParams;
    public int[] intParams;
    public Object[] objParams;

    public ClientEvent() {
    }

    private ClientEvent(EventType type, String[] stringParams, int[] intParams,
            Object[] objParams) {
        this.type = type;
        this.stringParams = stringParams;
        this.intParams = intParams;
        this.objParams = objParams;
    }

    public static ClientEvent keydown(Key key) {
        return new ClientEvent(EventType.KEYDOWN, null, new int[] { key.code },
                null);
    }

    public static ClientEvent focus() {
        return new ClientEvent(EventType.FOCUS, null, null, null);
    }

    public static ClientEvent blur() {
        return new ClientEvent(EventType.BLUR, null, null, null);
    }

    protected static ClientEvent click(int button) {
        return new ClientEvent(EventType.CLICK, null, new int[] { button },
                null);
    }

    public static ClientEvent clickPrimary() {
        return click(1);
    }

    public static ClientEvent clickSecondary() {
        return click(2);
    }

    public static ClientEvent mouseOver() {
        return new ClientEvent(EventType.MOUSEOVER, null, null, null);
    }

    public static ClientEvent mouseOut() {
        return new ClientEvent(EventType.MOUSEOUT, null, null, null);
    }

    public static ClientEvent change() {
        return new ClientEvent(EventType.CHANGE, null, null, null);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ClientEvent other = (ClientEvent) obj;
        if (type != other.type) {
            return false;
        }
        if (type == EventType.KEYDOWN && intParams[0] != other.intParams[0]) {
            return false;
        }
        return true;
    }

    /**
     * <p>
     * Key codes to be used with Snappy key events.
     * </p>
     * 
     * Example:
     * 
     * <pre>
     * textfield.on(Events.TextField.KEYDOWN, Key.ESCAPE.code()).addAction(...);
     * </pre>
     */
    public static enum Key {
        ENTER(13)//
        , ESC(27) //
        , PAGE_UP(33) //
        , PAGE_DOWN(34) //
        , TAB(9) //
        , ARROW_LEFT(37) //
        , ARROW_UP(38) //
        , ARROW_RIGHT(39) //
        , ARROW_DOWN(40) //
        , BACKSPACE(8) //
        , DELETE(46) //
        , INSERT(45) //
        , END(35) //
        , HOME(36) //
        , F1(112) //
        , F2(113) //
        , F3(114) //
        , F4(115) //
        , F5(116) //
        , F6(117) //
        , F7(118) //
        , F8(119) //
        , F9(120) //
        , F10(121) //
        , F11(122) //
        , F12(123) //
        , A(65) //
        , B(66) //
        , C(67) //
        , D(68) //
        , E(69) //
        , F(70) //
        , G(71) //
        , H(72) //
        , I(73) //
        , J(74) //
        , K(75) //
        , L(76) //
        , M(77) //
        , N(78) //
        , O(79) //
        , P(80) //
        , Q(81) //
        , R(82) //
        , S(83) //
        , T(84) //
        , U(85) //
        , V(86) //
        , W(87) //
        , X(88) //
        , Y(89) //
        , Z(90) //
        , NUM0(48) //
        , NUM1(49) //
        , NUM2(50) //
        , NUM3(51) //
        , NUM4(52) //
        , NUM5(53) //
        , NUM6(54) //
        , NUM7(55) //
        , NUM8(56) //
        , NUM9(57) //
        , SPACEBAR(32);

        private int code;

        Key(int keyCode) {
            code = keyCode;
        }

        public String code() {
            return toString();
        }

        @Override
        public String toString() {
            return "" + code;
        }
    }

}

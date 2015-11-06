package com.jakespringer.reagne.input;

import com.jakespringer.reagne.Reagne;
import com.jakespringer.reagne.Signal;
import com.jakespringer.reagne.gfx.Window;
import com.jakespringer.reagne.math.Vec2;
import com.jakespringer.reagne.util.Event;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class Input {

    public static final boolean KEY_PRESSED = true;
    public static final boolean KEY_RELEASED = false;

    public static final Signal<Integer> onKeyPress = new Signal<>(0);
    public static final Signal<Integer> onKeyRelease = new Signal<>(0);
    public static final Signal<Integer> onMousePress = new Signal<>(0);
    public static final Signal<Integer> onMouseRelease = new Signal<>(0);

    private static Vec2 mouse;
    private static Vec2 mouseDelta;
    private static Vec2 mouseScreen;

    public static Vec2 getMouse() {
        return mouse;
    }

    public static Vec2 getMouseDelta() {
        return mouseDelta;
    }

    public static Vec2 getMouseScreen() {
        return mouseScreen;
    }

    static {
        Reagne.continuous.forEach(dt -> {
            while (Keyboard.next()) {
                if (Keyboard.getEventKeyState() == KEY_PRESSED) {
                    onKeyPress.send(Keyboard.getEventKey());
                } else /* ..getEventKeyState() == KEY_RELEASED */ {
                    onKeyRelease.send(Keyboard.getEventKey());
                }
            }

            while (Mouse.next()) {
                if (Mouse.getEventButtonState() == KEY_PRESSED) {
                    onMousePress.send(Mouse.getEventButton());
                } else /* ..getEventButtonState() == KEY_RELEASED */ {
                    onMouseRelease.send(Mouse.getEventButton());
                }
            }

            double w = Display.getWidth();
            double h = Display.getHeight();
            double ar = Window.aspectRatio();
            double vw, vh;

            if (w / h > ar) {
                vw = ar * h;
                vh = h;
            } else {
                vw = w;
                vh = w / ar;
            }
            double left = (w - vw) / 2;
            double bottom = (h - vh) / 2;

            mouseScreen = new Vec2((Mouse.getX() - left) / vw, (Mouse.getY() - bottom) / vh).multiply(Window.viewSize);
            mouse = mouseScreen.subtract(Window.viewSize.multiply(.5)).add(Window.viewPos);
            mouseDelta = new Vec2(Mouse.getDX() / vw, Mouse.getDY() / vh).multiply(Window.viewSize);
        });
    }

    public static Signal<Boolean> isKeyPressed(final int key) {
        return new Signal<>(false)
                .sendOn(onKeyPress.filter(x -> x == key), (k, x) -> true)
                .sendOn(onKeyRelease.filter(x -> x == key), (k, x) -> false);
    }

    public static Event whenKeyPressed(final int key) {
        return new Event()
                .sendOn(onKeyPress.filter(x -> x == key));
    }

    public static Event whenKeyReleased(final int key) {
        return new Event()
                .sendOn(onKeyPress.filter(x -> x == key));
    }

    public static Event whenMousePressed(final int key) {
        return new Event()
                .sendOn(onMousePress.filter(x -> x == key));
    }

    public static Event whenMouseReleased(final int key) {
        return new Event()
                .sendOn(onMousePress.filter(x -> x == key));
    }

    public static Signal<Double> whileKeyDown(final int key) {
        return Reagne.continuous.filter(isKeyPressed(key));
    }

    public static Signal<Double> whileKeyUp(final int key) {
        return Reagne.continuous.filter(x -> !isKeyPressed(key).get());
    }

    private Input() {
    }
}

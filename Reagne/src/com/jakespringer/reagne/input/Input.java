package com.jakespringer.reagne.input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import com.jakespringer.reagne.Reagne;
import com.jakespringer.reagne.Signal;

public class Input {
    public static final boolean KEY_PRESSED = true;
    public static final boolean KEY_RELEASED = false;
    
    public static final Signal<Integer> onKeyPress = new Signal<>(0);
    public static final Signal<Integer> onKeyRelease = new Signal<>(0);
    public static final Signal<Integer> onMousePress = new Signal<>(0);
    public static final Signal<Integer> onMouseRelease = new Signal<>(0);
    
    private static final Signal<Object> eventLoop = new Signal<>(Signal.DEFAULT_STREAM_OBJECT)
            .sendOn(Reagne.continuous, (dt, x) -> {
                while (Keyboard.next()) {
                    if (Keyboard.getEventKeyState() == KEY_PRESSED) {
                        onKeyPress.send(Integer.valueOf(Keyboard.getEventKey()));
                    } else /* ..getEventKeyState() == KEY_RELEASED */ {
                        onKeyRelease.send(Integer.valueOf(Keyboard.getEventKey()));
                    }
                }
                
                while (Mouse.next()) {
                    if (Mouse.getEventButtonState() == KEY_PRESSED) {
                        onMousePress.send(Mouse.getEventButton());
                    } else /* ..getEventButtonState() == KEY_RELEASED */ {
                        onMouseRelease.send(Mouse.getEventButton());
                    }
                }
                
                return Signal.DEFAULT_STREAM_OBJECT;
            }).asRoot();
    
    public static Signal<Boolean> isKeyPressed(final int key) {
        return new Signal<Boolean>(false)
                .sendOn(onKeyPress.filter(x -> x.intValue() == key), (k, x) -> true)
                .sendOn(onKeyRelease.filter(x -> x.intValue() == key), (k, x) -> false);
    }
    
    public static Signal<Object> whenKeyPressed(final int key) {
        return new Signal<Object>(Signal.DEFAULT_STREAM_OBJECT)
                .sendOn(onKeyPress.filter(x -> x.intValue() == key), Signal.DEFAULT_STREAM_OBJECT);
    }
    
    public static Signal<Object> whenKeyReleased(final int key) {
        return new Signal<Object>(Signal.DEFAULT_STREAM_OBJECT)
                .sendOn(onKeyPress.filter(x -> x.intValue() == key), Signal.DEFAULT_STREAM_OBJECT);
    }
    
    public static Signal<Object> whenMousePressed(final int key) {
        return new Signal<Object>(Signal.DEFAULT_STREAM_OBJECT)
                .sendOn(onMousePress.filter(x -> x.intValue() == key), Signal.DEFAULT_STREAM_OBJECT);
    }
    
    public static Signal<Object> whenMouseReleased(final int key) {
        return new Signal<Object>(Signal.DEFAULT_STREAM_OBJECT)
                .sendOn(onMousePress.filter(x -> x.intValue() == key), Signal.DEFAULT_STREAM_OBJECT);
    }
    
    public static Signal<Double> whileKeyPressed(final int key) {
        return Reagne.continuous.filter(isKeyPressed(key));
    }
    
    public static Signal<Double> whileKeyReleased(final int key) {
        return Reagne.continuous.filter(x -> !isKeyPressed(key).get());
    }
    
    private Input() {}
}

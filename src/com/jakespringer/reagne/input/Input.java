package com.jakespringer.reagne.input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import com.jakespringer.reagne.Reagne;
import com.jakespringer.reagne.Stream;

public class Input {
    public static final boolean KEY_PRESSED = true;
    public static final boolean KEY_RELEASED = false;
    
    public static final Stream<Integer> onKeyPress = new Stream<>(0);
    public static final Stream<Integer> onKeyRelease = new Stream<>(0);
    public static final Stream<Integer> onMousePress = new Stream<>(0);
    public static final Stream<Integer> onMouseRelease = new Stream<>(0);
    
    private static final Stream<Object> eventLoop = new Stream<>(Stream.DEFAULT_STREAM_OBJECT)
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
                
                return Stream.DEFAULT_STREAM_OBJECT;
            }).asRoot();
    
    public static Stream<Boolean> isKeyPressed(final int key) {
        return new Stream<Boolean>(false)
                .sendOn(onKeyPress.filter(x -> x.intValue() == key), (k, x) -> true)
                .sendOn(onKeyRelease.filter(x -> x.intValue() == key), (k, x) -> false);
    }
    
    public static Stream<Object> whenKeyPressed(final int key) {
        return new Stream<Object>(Stream.DEFAULT_STREAM_OBJECT)
                .sendOn(onKeyPress.filter(x -> x.intValue() == key), Stream.DEFAULT_STREAM_OBJECT);
    }
    
    public static Stream<Object> whenKeyReleased(final int key) {
        return new Stream<Object>(Stream.DEFAULT_STREAM_OBJECT)
                .sendOn(onKeyPress.filter(x -> x.intValue() == key), Stream.DEFAULT_STREAM_OBJECT);
    }
    
    public static Stream<Object> whenMousePressed(final int key) {
        return new Stream<Object>(Stream.DEFAULT_STREAM_OBJECT)
                .sendOn(onMousePress.filter(x -> x.intValue() == key), Stream.DEFAULT_STREAM_OBJECT);
    }
    
    public static Stream<Object> whenMouseReleased(final int key) {
        return new Stream<Object>(Stream.DEFAULT_STREAM_OBJECT)
                .sendOn(onMousePress.filter(x -> x.intValue() == key), Stream.DEFAULT_STREAM_OBJECT);
    }
    
    public static Stream<Double> whileKeyPressed(final int key) {
        return Reagne.continuous.filter(isKeyPressed(key));
    }
    
    public static Stream<Double> whileKeyReleased(final int key) {
        return Reagne.continuous.filter(x -> !isKeyPressed(key).get());
    }
    
    private Input() {}
}

package com.jakespringer.reagne.test;

import com.jakespringer.reagne.Reagne;
import com.jakespringer.reagne.gfx.LWJGLWindow;

public class PositionTest {
    public static void main(String[] args) throws InterruptedException {
        final LWJGLWindow window = new LWJGLWindow();
        Reagne.initialize.forEach(x -> window.initialize(1200, 800, "Reagen Test"));
        Reagne.continuous.forEach(x -> window.update());
        
//        Input.whileKeyPressed(Keyboard.KEY_0).forEach(x -> System.out.println("Time elapsed: " + x));
        Player p = new Player();
        
        Reagne.run();
    }
}

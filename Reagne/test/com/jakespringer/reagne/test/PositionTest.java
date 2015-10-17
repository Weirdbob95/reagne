package com.jakespringer.reagne.test;

import com.jakespringer.reagne.Reagne;
import com.jakespringer.reagne.game.World;
import com.jakespringer.reagne.gfx.LWJGLWindow;

public class PositionTest {
    public static void main(String[] args) throws InterruptedException {
        final World world = new World();
        
        final LWJGLWindow window = new LWJGLWindow();
        Reagne.initialize.forEach(x -> window.initialize(1200, 800, "Reagen Test"));
        Reagne.continuous.forEach(x -> window.update());
        
//        Input.whileKeyPressed(Keyboard.KEY_0).forEach(x -> System.out.println("Time elapsed: " + x));
        world.add(new Player());
        
        Reagne.run();
    }
}

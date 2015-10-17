package com.jakespringer.reagne.test;

import org.lwjgl.input.Keyboard;
import com.jakespringer.reagne.Reagne;
import com.jakespringer.reagne.Signal;
import com.jakespringer.reagne.game.Entity;
import com.jakespringer.reagne.gfx.Graphics;
import com.jakespringer.reagne.input.Input;
import com.jakespringer.reagne.math.Color4;
import com.jakespringer.reagne.math.Vec2;

public class Player implements Entity {
    private final double SPEED_COEFFICIENT = 0.002;
    
    private Signal<Vec2> velocity;
    private Signal<Vec2> position;
    private Signal<Double> graphics;
    
    @Override
    public void create() {
        Input.whenKeyPressed(Keyboard.KEY_Q).forEach(x -> destroy());
        velocity = Movement.makeWASDVelocitySystem(SPEED_COEFFICIENT).combine(
                Movement.makeFrictionSystem());

        position = new Signal<>(new Vec2())
                .sendOn(Reagne.continuous, (dt, x) -> x.add(velocity.get().multiply(dt)));
        
        graphics = Reagne.continuous.forEach(dt -> {
            Graphics.drawCircle(position.get().x, position.get().y, 16.0, new Color4(1.0, 0.0, 0.0));
        });
    }
    
    @Override
    public void destroy() {
        position.remove();
        graphics.remove();
        velocity.remove();
    }
}

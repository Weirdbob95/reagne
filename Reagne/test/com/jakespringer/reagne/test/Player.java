package com.jakespringer.reagne.test;

import org.lwjgl.input.Keyboard;
import com.jakespringer.reagne.Reagne;
import com.jakespringer.reagne.Stream;
import com.jakespringer.reagne.gfx.Graphics;
import com.jakespringer.reagne.input.Input;
import com.jakespringer.reagne.math.Color4;
import com.jakespringer.reagne.math.Vec2;

public class Player implements Entity {
    private final double SPEED_COEFFICIENT = 0.002;
    
    private Stream<Vec2> velocity;
    private Stream<Vec2> position;
    private Stream<Double> graphics;
    
    @Override
    public void create() {
        Input.whenKeyPressed(Keyboard.KEY_Q).forEach(x -> destroy());
        velocity = new Stream<>(new Vec2())
                .sendOn(Input.whileKeyPressed(Keyboard.KEY_W), (dt, x) -> x.add(new Vec2(0.0, dt*SPEED_COEFFICIENT)))
                .sendOn(Input.whileKeyPressed(Keyboard.KEY_D), (dt, x) -> x.add(new Vec2(dt*SPEED_COEFFICIENT, 0.0)))
                .sendOn(Input.whileKeyPressed(Keyboard.KEY_A), (dt, x) -> x.add(new Vec2(-dt*SPEED_COEFFICIENT, 0.0)))
                .sendOn(Input.whileKeyPressed(Keyboard.KEY_S), (dt, x) -> x.add(new Vec2(0.0, -dt*SPEED_COEFFICIENT)))
                .sendOn(Reagne.continuous, (dt, x) -> x.multiply(0.9));

        position = new Stream<>(new Vec2())
                .sendOn(Reagne.continuous, (dt, x) -> x.add(velocity.get().multiply(dt)));
        
        graphics = Reagne.continuous.forEach(dt -> {
            Graphics.drawCircle(position.get().x, position.get().y, 10.0, new Color4(1.0, 0.0, 0.0));
        });
    }
    
    @Override
    public void destroy() {
        position.remove();
        graphics.remove();
        velocity.remove();
    }
}

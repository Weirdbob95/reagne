package com.jakespringer.reagne.test;

import com.jakespringer.reagne.Signal;
import com.jakespringer.reagne.game.AbstractEntity;
import com.jakespringer.reagne.gfx.Graphics2D;
import com.jakespringer.reagne.input.Input;
import com.jakespringer.reagne.math.Color4;
import com.jakespringer.reagne.math.Vec2;
import org.lwjgl.input.Keyboard;

public class Player extends AbstractEntity {

    private final double SPEED_COEFFICIENT = 0.002;

    private Signal<Vec2> velocity;
    private Signal<Vec2> position;

    @Override
    public void create() {
        //Position and velocity
        velocity = Movement.makeWASDVelocitySystem(SPEED_COEFFICIENT).combine(Movement.makeFrictionSystem());
        position = Movement.makePositionUpdateSystem(velocity);
        add(velocity, position);

        //Graphics
        onUpdate(dt -> Graphics2D.fillEllipse(position.get(), new Vec2(16, 16), Color4.RED, 20));

        //Destroying self
        add(Input.whenKeyPressed(Keyboard.KEY_Q).forEach(x -> destroy()));
    }
}

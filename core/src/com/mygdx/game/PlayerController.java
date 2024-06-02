package com.mygdx.game;

import java.util.function.BooleanSupplier;

import com.badlogic.gdx.ai.steer.behaviors.Jump;
import org.libsdl.SDL;
import org.libsdl.SDL_Error;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

import uk.co.electronstudio.sdl2gdx.SDL2Controller;
import uk.co.electronstudio.sdl2gdx.SDL2ControllerManager;


public class PlayerController {

    /**
     * Key Binds Class
     */
    private static class ControlAction {
        private final BooleanSupplier condition;
        private final Runnable action;
    
        public ControlAction(BooleanSupplier condition, Runnable action) {
            this.condition = condition;
            this.action = action;
        }
    
        public void checkAndPerform() {
            if (condition.getAsBoolean()) {
                action.run();
            }
        }
    }


    public enum ControllerType {Keyboard, Keyboard2, Controller};
    private static final float MAX_VELOCITY_GROUNDED = 0.4f; // Should become character specific
    private static final float MAX_VELOCITY_AIRBORNE = 0.2f; // Should become character specific
    private static final long JUMP_DEBOUNCE = 125; // milliseconds
    private static final float AXIS_DEADZONE = 0.2f;
    
    private final RayCastCallback m_callback = new RayCastCallback() {
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            // if the fixture is the ground, apply a jump impulse
            if (fixture.getUserData() == "ground") {
                m_isGrounded = true;
                return 0;
            }
            return -1;   
        }
    };

    private final Fighter m_fighter;

    private final ControlAction[] m_bindings;
    private SDL2Controller m_controller;

    private boolean m_isGrounded;
    private boolean m_hasDoubleJump;
    private boolean m_isFalling;
    private long m_lastJump;
    private boolean m_isFacingRight;

    private float m_previousY;
    private float m_previousTime;
    private float m_deltaTime;

    private float m_endLag;
    private long m_previousAttackTime;

    /**
     * Constructor for the Controller Class.
     * @param fighter 
     * @param controllerType
     */
    public PlayerController(Fighter fighter, ControllerType controllerType) {
        m_fighter = fighter;
        m_isGrounded = false;
        m_hasDoubleJump = false;
        m_isFalling = false;
        m_isFacingRight = false;
        m_previousY = 0;
        m_deltaTime = 0;
        m_previousTime = 0;
        m_endLag = 0;

        // init bindings
        switch(controllerType) {
            case Keyboard:
                m_bindings = new ControlAction[] {
                        new ControlAction(() -> Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D), () -> moveXAxis(-1)),
                        new ControlAction(() -> Gdx.input.isKeyPressed(Keys.D) && !Gdx.input.isKeyPressed(Keys.A), () -> moveXAxis(1)),
                        new ControlAction(() -> Gdx.input.isKeyJustPressed(Keys.SPACE), this::jump),
                        new ControlAction(() -> Gdx.input.isKeyJustPressed(Keys.J), () -> attack(ControllerType.Keyboard, false)),
                        new ControlAction(() -> Gdx.input.isKeyJustPressed(Keys.K), () -> attack(ControllerType.Keyboard, true))
                };
                break;

            case Keyboard2:
                m_bindings = new ControlAction[] {
                    new ControlAction(() -> Gdx.input.isKeyPressed(Keys.LEFT) && !Gdx.input.isKeyPressed(Keys.RIGHT), () -> moveXAxis(-1)),
                    new ControlAction(() -> Gdx.input.isKeyPressed(Keys.RIGHT) && !Gdx.input.isKeyPressed(Keys.LEFT), () -> moveXAxis(1)),
                    new ControlAction(() -> Gdx.input.isKeyJustPressed(Keys.UP), this::jump)
                };
                break;

            case Controller:
                try {
                    m_controller = new SDL2Controller(new SDL2ControllerManager(), 0);   
                } catch (SDL_Error e) {
                    m_controller = null;
                    m_bindings = new ControlAction[0];
                    return;
                }

                m_bindings = new ControlAction[] {
                        new ControlAction(() -> Math.abs(m_controller.getAxis(SDL.SDL_CONTROLLER_AXIS_LEFTX)) > AXIS_DEADZONE,
                                () -> moveXAxis(m_controller.getAxis(SDL.SDL_CONTROLLER_AXIS_LEFTX))),
                        new ControlAction(() -> m_controller.getButton(SDL.SDL_CONTROLLER_BUTTON_A), this::jump),
                        new ControlAction(() -> m_controller.getButton(SDL.SDL_CONTROLLER_BUTTON_X), () -> attack(ControllerType.Controller, false)),
                        new ControlAction(() -> m_controller.getButton(SDL.SDL_CONTROLLER_BUTTON_Y), () -> attack(ControllerType.Controller, true))
                };
                break;

            default:
                m_bindings = new ControlAction[0];
                m_controller = null;
                break;
        }
    }


    /**
     * Move the Fighter on the X-Axis.
     * @param modifier value to modify the move direction by
     */
    public void moveXAxis(float modifier) {
        Body body = m_fighter.getBody();
        Vector2 pos = body.getPosition();
        Vector2 vel = body.getLinearVelocity();
        float maxVelocity = m_isGrounded ? MAX_VELOCITY_GROUNDED : MAX_VELOCITY_AIRBORNE;

        if (Math.abs(vel.x) < maxVelocity) {
            body.applyLinearImpulse(m_fighter.getRunSpeed() * modifier, 0, pos.x, pos.y, true);
        }

        m_isFacingRight = Math.signum(modifier) >= 0;
    }


    public void jump() {
        Body body = m_fighter.getBody();
        Vector2 pos = body.getPosition();
        if (m_isGrounded && System.currentTimeMillis() - m_lastJump > JUMP_DEBOUNCE) {
            body.applyLinearImpulse(0, m_fighter.getJumpForce(), pos.x, pos.y, true);
            m_lastJump = System.currentTimeMillis();
        }
        else if (!m_isGrounded && m_hasDoubleJump && System.currentTimeMillis() - m_lastJump > JUMP_DEBOUNCE) {
            // body.applyLinearImpulse(0, m_fighter.getJumpForce() * (m_isFalling ? 3f : 1.35f), pos.x, pos.y, true);
            body.applyLinearImpulse(0, m_fighter.getJumpForce() * 1.35f, pos.x, pos.y, true);
            m_lastJump = System.currentTimeMillis();
            m_hasDoubleJump = false;
        }
    }


    public void attack(ControllerType ct, boolean isSpecial) {
        // Don't Attack, if still in EndLag.
        if (System.currentTimeMillis() - m_previousAttackTime <= m_endLag) return;

        boolean left;
        boolean right;
        boolean up;
        boolean down;

        switch(ct) {
            case Controller: {
                left = m_controller.getAxis(SDL.SDL_CONTROLLER_AXIS_LEFTX) < -AXIS_DEADZONE;
                right = m_controller.getAxis(SDL.SDL_CONTROLLER_AXIS_LEFTX) > AXIS_DEADZONE;
                up = m_controller.getAxis(SDL.SDL_CONTROLLER_AXIS_LEFTY) > AXIS_DEADZONE;
                down = m_controller.getAxis(SDL.SDL_CONTROLLER_AXIS_LEFTY) < -AXIS_DEADZONE;
                break;
            }

            default: { // Keyboard1 binds
                left = Gdx.input.isKeyPressed(Keys.A);
                right = Gdx.input.isKeyPressed(Keys.D);
                up = Gdx.input.isKeyPressed(Keys.W);
                down = Gdx.input.isKeyPressed(Keys.S);
                break;
            }
        }

        Attack.direction direction = Attack.direction.Neutral;
        if (left || right && !(up || down)) {
            direction = Attack.direction.Side;
        }
        else if (up && !(right || down)) {
            direction = Attack.direction.Up;
        }
        else if (down && !(up || right)) {
            direction = Attack.direction.Down;
        }

        m_endLag = m_fighter.attack(direction, m_isGrounded, m_isFacingRight, isSpecial);
        m_previousAttackTime = System.currentTimeMillis();
    }


    public void update() {
        Body body = m_fighter.getBody();
        Vector2 pos = body.getPosition();

        // raytrace to check if the fighter is on the ground
        m_isGrounded = false;
        Vector2 from = new Vector2(pos.x, pos.y);
        Vector2 to = new Vector2(pos.x, pos.y - m_fighter.getDimensions().y / 2 - 0.3f);
        MyGdxGame.WORLD.rayCast(m_callback, from, to);

        m_isFalling = false;
        m_deltaTime = System.currentTimeMillis() - m_previousTime;
        float fallSpeed = Math.abs(m_previousY) - Math.abs(pos.y);
        if (fallSpeed < -0.4) {
            m_isFalling = true;
        }

        // Bindings
        for (ControlAction action : m_bindings) {
            action.checkAndPerform();
        }

        if (m_isGrounded) {
            m_hasDoubleJump = true;
        }

        m_previousY = pos.y;
        m_previousTime = System.currentTimeMillis();
    }


    public Fighter getFighter() {
        return m_fighter;
    }
}

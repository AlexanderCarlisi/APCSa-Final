package com.mygdx.game;

import java.util.function.BooleanSupplier;

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
    private class ControlAction {
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


    public static enum ControllerType {Keyboard, Keyboard2, Controller};
    private static final float MAX_VELOCITY_GROUNDED = 0.4f;
    private static final float MAX_VELOCITY_AIRBORNE = 0.2f;
    private static final long JUMP_DEBOUNCE = 300; // milliseconds
    private static final float AXIS_DEADZONE = 0.5f;
    
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
    // private boolean m_hasDoubleJump;
    private long m_lastJump;


    /**
     * Constructor for the Controller Class.
     * @param fighter 
     * @param controllerType
     */
    public PlayerController(Fighter fighter, ControllerType controllerType) {
        m_fighter = fighter;
        m_isGrounded = false;
        // m_hasDoubleJump = false;

        // init bindings
        switch(controllerType) {
            case Keyboard:
                m_bindings = new ControlAction[] {
                    new ControlAction(() -> Gdx.input.isKeyPressed(Keys.A), () -> moveXAxis(-1)),
                    new ControlAction(() -> Gdx.input.isKeyPressed(Keys.D), () -> moveXAxis(1)),
                    new ControlAction(() -> Gdx.input.isKeyJustPressed(Keys.W) || Gdx.input.isKeyJustPressed(Keys.SPACE), () -> jump())
                };
                break;

            case Keyboard2:
                m_bindings = new ControlAction[] {
                    new ControlAction(() -> Gdx.input.isKeyPressed(Keys.LEFT), () -> moveXAxis(-1)),
                    new ControlAction(() -> Gdx.input.isKeyPressed(Keys.RIGHT), () -> moveXAxis(1)),
                    new ControlAction(() -> Gdx.input.isKeyJustPressed(Keys.UP), () -> jump())
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
                    new ControlAction(() -> m_controller.getAxis(SDL.SDL_CONTROLLER_AXIS_LEFTX) < -AXIS_DEADZONE, () -> moveXAxis(-1)),
                    new ControlAction(() -> m_controller.getAxis(SDL.SDL_CONTROLLER_AXIS_LEFTX) > AXIS_DEADZONE, () -> moveXAxis(1)),
                    new ControlAction(() -> m_controller.getButton(SDL.SDL_CONTROLLER_BUTTON_A), () -> jump())
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
     * @param signum : Either 1 or -1. -1 is Left, 1 is Right.
     */
    public void moveXAxis(int signum) {
        Body body = m_fighter.getBody();
        Vector2 pos = body.getPosition();
        Vector2 vel = body.getLinearVelocity();
        float maxVelocity = m_isGrounded ? MAX_VELOCITY_GROUNDED : MAX_VELOCITY_AIRBORNE;

        if (signum == -1 && vel.x > -maxVelocity) {
            body.applyLinearImpulse(-m_fighter.getRunSpeed(), 0, pos.x, pos.y, true);
        } else if (signum == 1 && vel.x < maxVelocity) {
            body.applyLinearImpulse(m_fighter.getRunSpeed(), 0, pos.x, pos.y, true);
        }
    }


    public void jump() {
        Body body = m_fighter.getBody();
        Vector2 pos = body.getPosition();
        if (m_isGrounded && System.currentTimeMillis() - m_lastJump > JUMP_DEBOUNCE) {
            body.applyLinearImpulse(0, m_fighter.getJumpForce(), pos.x, pos.y, true);
            m_lastJump = System.currentTimeMillis();
        }
    }


    public void update() {
        Body body = m_fighter.getBody();
        Vector2 pos = body.getPosition();

        // Get height for Ground Raycast
        PolygonShape shape = (PolygonShape) m_fighter.getFixture().getShape();
        float height = 0;
        int vertexCount = shape.getVertexCount();
        Vector2 vertex = new Vector2();
        for (int i = 0; i < vertexCount; i++) {
            shape.getVertex(i, vertex);
            if (vertex.y > height) {
                height = vertex.y;
            }
        }

        // raytrace to check if the fighter is on the ground
        m_isGrounded = false;
        Vector2 from = new Vector2(pos.x, pos.y);
        Vector2 to = new Vector2(pos.x, pos.y - height / 2 - 0.3f);
        MyGdxGame.WORLD.rayCast(m_callback, from, to);
        
        // Bindings
        for (ControlAction action : m_bindings) {
            action.checkAndPerform();
        }
    }


    public Fighter getFighter() {
        return m_fighter;
    }
}

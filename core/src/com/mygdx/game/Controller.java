package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class Controller {
    private static final float MAX_VELOCITY_GROUNDED = 0.4f;
    private static final float MAX_VELOCITY_AIRBORNE = 0.2f;
    private static final long JUMP_DEBOUNCE = 300; // milliseconds

    private final Fighter m_fighter;
    private boolean m_isGrounded;
    // private boolean m_hasDoubleJump;
    private long m_lastJump;
    
    private final RayCastCallback callback = new RayCastCallback() {
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


    /**
     * Constructor for the Controller Class.
     * @param fighter 
     */
    public Controller(Fighter fighter) {
        m_fighter = fighter;
        m_isGrounded = false;
        // m_hasDoubleJump = false;
    }


    public void update() {
        Body body = m_fighter.getBody();
        Vector2 vel = body.getLinearVelocity();
        Vector2 pos = body.getPosition();

        // raytrace to check if the fighter is on the ground
        m_isGrounded = false;
        Vector2 from = new Vector2(pos.x, pos.y);
        Vector2 to = new Vector2(pos.x, pos.y - 0.3f);
        Battle.WORLD.rayCast(callback, from, to);
        
        float maxVelocity = m_isGrounded ? MAX_VELOCITY_GROUNDED : MAX_VELOCITY_AIRBORNE;

        // apply left impulse, but only if max velocity is not reached yet
        if (Gdx.input.isKeyPressed(Keys.A) && vel.x > -maxVelocity) {			
            body.applyLinearImpulse(-0.05f, 0, pos.x, pos.y, true);
        }

        // apply right impulse, but only if max velocity is not reached yet
        if (Gdx.input.isKeyPressed(Keys.D) && vel.x < maxVelocity) {
            body.applyLinearImpulse(0.05f, 0, pos.x, pos.y, true);
        }

        // apply jump impulse, but only if the fighter is on the ground raytrace
        if ((Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.SPACE)) && (m_isGrounded /*|| m_hasDoubleJump*/) && System.currentTimeMillis() - m_lastJump > JUMP_DEBOUNCE) {
            body.applyLinearImpulse(0, 0.3f, pos.x, pos.y, true);
            m_lastJump = System.currentTimeMillis();
            // if (m_hasDoubleJump && !m_isGrounded) {
            //     m_hasDoubleJump = false;
            // }
        }

        // if (m_isGrounded) {
        //     m_hasDoubleJump = true;
        // }
    }


    public Fighter getFighter() {
        return m_fighter;
    }
}

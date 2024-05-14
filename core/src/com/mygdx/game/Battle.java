package com.mygdx.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;


/**
 * The Battle Class
 * 
 * This class is initialized once the Fighters are selected and the players start the game.
 * <p>
 * Handles the physics, controls, and logic of the game.
 */
public class Battle {
    /** World Object, handles all Physics, needs to be declared first so bodies don't throw an error. */
    public static final World WORLD = new World(new Vector2(0f, -1f), true);
    public static final Camera CAMERA = new OrthographicCamera(GDXHelper.PTM(1280), GDXHelper.PTM(720));

    private static final float TIME_STEP = 1/60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private static final Vector2 STARTING_POSITION = new Vector2(GDXHelper.PTM(10), GDXHelper.PTM(10));

    /** General Body Definition for Fighters. */
    private static final BodyDef FIGHTER_BODY_DEF = GDXHelper.generateBodyDef(BodyType.DynamicBody, STARTING_POSITION);
    private static final FixtureDef FIGHTER_FIXTURE_DEF = GDXHelper.generateFixtureDef(1f, 4f, 0f, GDXHelper.PTM(15f), GDXHelper.PTM(25f));

    /** Render vars */
    private final Box2DDebugRenderer m_debugRenderer = new Box2DDebugRenderer();
    private float m_accumulator = 0;
    private float m_previousTime = 0;

    /** Controllers for each player. */
    private final Controller[] m_controllers;
    private final Fighter[] m_fighters;

    /** World Objects */
    private final Arena m_arena;


    /**
     * Constructor for the Battle Class.
     * @param fighters 
     */
    public Battle(Fighter[] fighters) {
        m_fighters = fighters;
        m_controllers = new Controller[m_fighters.length];
        for (int i = 0; i < m_fighters.length; i++) {
            m_fighters[i].setBody(WORLD.createBody(FIGHTER_BODY_DEF));
            m_fighters[i].generateFixture(FIGHTER_FIXTURE_DEF);
            m_controllers[i] = new Controller(m_fighters[i]);
        }

        // Will eventually be set with an index to determine the Arena.
        m_arena = new Arena();
    }


    /**
     *  Update method, run periodically in the Render Loop.
     */
    public void update() {
        float currentTime = System.nanoTime();
        physicsStep(currentTime - m_previousTime);
        m_previousTime = currentTime;

        for (Controller controller : m_controllers) {
            controller.update();
        }
    }


    private void physicsStep(float deltaTime) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        m_accumulator += frameTime;
        while (m_accumulator >= TIME_STEP) {
            WORLD.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            m_accumulator -= TIME_STEP;
        }
    }
    

    /**
     * Objects to update in Render Loop. 
     * @param spriteBatch
     */
    public void draw(SpriteBatch spriteBatch) {
        // spriteBatch.begin();
        // for (Controller controller : m_controllers) {
        //     Fighter fighter = controller.getFighter();
        //     spriteBatch.draw(fighter.getTexture(), fighter.getXPos(), fighter.getYPos());
        // }
        // spriteBatch.end();

        m_debugRenderer.render(WORLD, CAMERA.combined);
    }


    /**
     *  Objects to dispose in Dispose.
     */
    public void dispose() {

        for (Fighter fighter : m_fighters) {
            fighter.getFixture().getShape().dispose();
        }

        m_arena.getGroundFixture().getShape().dispose();

        m_debugRenderer.dispose();
    }
}

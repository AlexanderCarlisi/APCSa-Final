package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.PlayerController.ControllerType;


/**
 * The Battle Class
 * 
 * This class is initialized once the Fighters are selected and the players start the game.
 * <p>
 * Handles the physics, controls, and logic of the game.
 */
public class Battle {

    /** Controllers for each player. */
    private final PlayerController[] m_controllers;
    private final Fighter[] m_fighters;

    /** World Objects */
    private final Arena m_arena;


    /**
     * Constructor for the Battle Class.
     * @param fighters 
     * @param controllers
     */
    public Battle(Fighter[] fighters, ControllerType[] controllers) {
        m_arena = new Arena(); // Will eventually be set with an index to determine the Arena.
        m_fighters = fighters;
        m_controllers = new PlayerController[m_fighters.length];
        Vector2[] startingPositions = m_arena.getStartingPositions();
        for (int i = 0; i < m_fighters.length; i++) {
            m_controllers[i] = new PlayerController(m_fighters[i], controllers[i]);
            m_fighters[i].getBody().setTransform(startingPositions[i], 0);
        }
    }


    /**
     *  Update method, run periodically in the Render Loop.
     */
    public void update() {
        for (PlayerController controller : m_controllers) {
            controller.update();
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
    }


    /**
     *  Objects to dispose in Dispose.
     */
    public void dispose() {

        for (Fighter fighter : m_fighters) {
            fighter.getFixture().getShape().dispose();
        }

        m_arena.getGroundFixture().getShape().dispose();
    }
}

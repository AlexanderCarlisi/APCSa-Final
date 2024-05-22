package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
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

    /** World Objects */
    private final Arena m_arena;

    // Battle Data
    private final BattleConfig m_config;
    private final Fighter[] m_fighters;
    private final int[] m_stocks;
    private final float m_startTime;


    /**
     * Constructor for the Battle Class.
     * @param fighters 
     * @param controllers
     */
    public Battle(Fighter[] fighters, ControllerType[] controllers, BattleConfig config) {
        m_config = config;
        m_arena = new Arena(fighters.length); // Will eventually be set with an index to determine the Arena.
        m_fighters = fighters;
        m_controllers = new PlayerController[m_fighters.length];
        m_stocks = new int[m_fighters.length];
        Vector2[] startingPositions = m_arena.getStartingPositions();
        for (int i = 0; i < m_fighters.length; i++) {
            m_controllers[i] = new PlayerController(m_fighters[i], controllers[i]);
            m_fighters[i].getBody().setTransform(startingPositions[i], 0);
            m_stocks[i] = m_config.stocks;
        }

        m_startTime = (m_config.timeLimit == -1) ? 0 : System.currentTimeMillis();
    }


    /**
     *  Update method, run periodically in the Render Loop.
     *  For Logic, not Graphics
     */
    public void update() {
        for (PlayerController controller : m_controllers) {
            controller.update();
        }
        m_arena.update(m_fighters, m_stocks);

        // Death Checks
        for (int i = 0; i < m_fighters.length; i++) {
            if (m_stocks[i] > 0 || m_stocks[i] == -1) {
                Fighter fighter = m_fighters[i];
                Vector2 pos = fighter.getBody().getPosition();
                if (Math.abs(pos.x) > Arena.BOUNDS.x || Math.abs(pos.y) > Arena.BOUNDS.y) {
                    if (m_stocks[i] > 0) m_stocks[i] -= 1;
                    if (m_stocks[i] > 0 || m_stocks[i] == -1) {
                        fighter.setHealth(0);
                        fighter.getBody().setTransform(m_arena.getStartingPositions()[i], 0);
                    }
                    System.out.println("Out of Bounds");
                }
            }
        }

        // End Battle Checks
        // Timer
        if (m_config.timeLimit != -1) {
            if (m_startTime + m_config.timeLimit < System.currentTimeMillis()) {
                // End Battle
                System.out.println("Battle Ended");
            }
        }

        // Stocks
        if (m_config.stocks != -1) {
            if (m_config.gamemode == BattleConfig.GameMode.FreeForAll) {
                int alive = 0;
                for (int i = 0; i < m_stocks.length; i++) {
                    if (m_stocks[i] > 0) alive++;
                }
                if (alive <= 1) {
                    // End Battle
                    System.out.println("Battle Ended");
                }
            }
        }
    }
    

    /**
     * Objects to update in Render Loop.
     * For Graphics, not Logic
     * @param spriteBatch
     */
    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        // spriteBatch.begin();
        // spriteBatch.end();
        shapeRenderer.begin();
        for (int i = 0; i < m_fighters.length; i++) {
            if (m_stocks[i] > 0 || m_stocks[i] == -1) {
                Fighter fighter = m_fighters[i];
                Vector2 pos = fighter.getBody().getPosition();
                Vector2 size = fighter.getDimensions();
                GDXHelper.drawRect(shapeRenderer, pos.x, pos.y, size.x, size.y);
            }
        }
        m_arena.draw(shapeRenderer);
        shapeRenderer.end();
    }


    /**
     *  Objects to dispose in Dispose.
     */
    public void dispose() {

        for (Fighter fighter : m_fighters) {
            fighter.getFixture().getShape().dispose();
        }
        m_arena.dispose();
    }
}

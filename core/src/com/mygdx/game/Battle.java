package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.PlayerController.ControllerType;


/**
 * The Battle Class
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

    /** If the startTime in Arena has been set. */
    private boolean m_setStartTime;

    /** If the Battle has Concluded */
    public boolean isFinished;


    /**
     * Constructor for the Battle Class.
     * @param fighters 
     * @param controllers
     */
    public Battle(Fighter[] fighters, PlayerController[] controllers, BattleConfig config) {
        // Setup Battle
        m_config = config;
        m_arena = new Arena(fighters.length); // Will eventually be set with an index to determine the Arena.
        m_fighters = fighters;
        m_controllers = controllers;
        m_stocks = new int[m_fighters.length];
        Vector2[] startingPositions = m_arena.getStartingPositions();

        // Setup Fighters
        for (int i = 0; i < m_fighters.length; i++) {
            m_fighters[i].getBody().setTransform(startingPositions[i], 0);
            m_stocks[i] = m_config.stocks;
        }

        m_startTime = (m_config.timeLimit == -1) ? 0 : System.nanoTime();
        m_setStartTime = false;
    }


    /**
     *  Update method, run periodically in the Render Loop.
     *  For Logic, not Graphics
     */
    public void update() {
        if (!m_setStartTime) {
            m_setStartTime = true;
            m_arena.setStartTime(System.nanoTime());
        }
        for (PlayerController controller : m_controllers) {
            if (!controller.getFighter().isDead) controller.update();
        }
        m_arena.update(m_fighters, m_stocks, m_config.timeLimit);

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
                        fighter.getBody().setLinearVelocity(0, 0);
                    }
                }
            }
            else if (m_stocks[i] == 0 && !m_fighters[i].isDead) {
                m_fighters[i].isDead = true;
                m_fighters[i].getFixture().setUserData("MARKED FOR DELETION");
                System.out.println("Killed Fighter");
            }
        }

        // End Battle Checks
        // Timer
        if (m_config.timeLimit != -1) {
            if ((m_config.timeLimit - (System.nanoTime() - m_startTime) / 1000000000.0) <= 0) {
                // End Battle
                System.out.println("Battle Ended");
                isFinished = true;
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
                    isFinished = true;
                }
            }
        }

        // Remove Dead Players, or Attacks.
        Array<Body> bodies = new Array<>();
        MyGdxGame.WORLD.getBodies(bodies);
        for (Body body : bodies) {
            for (Fixture fixture : body.getFixtureList()) {
                if (fixture.getUserData() instanceof MyGdxGame.entityCategory && fixture.getUserData().equals(MyGdxGame.entityCategory.Destroy)) {
                    MyGdxGame.WORLD.destroyBody(body);
                }
                else if (fixture.getUserData() instanceof Attack.AttackInfo) {
                    Attack.AttackInfo info = (Attack.AttackInfo) fixture.getUserData();
                    if (System.currentTimeMillis() > info.lifeTime) {
                        MyGdxGame.WORLD.destroyBody(body);
                    }
                }
            }
        }
    }
    

    /**
     * Objects to update in Render Loop.
     * For Graphics, not Logic
     * @param spriteRenderer
     */
    public void draw(SpriteBatch spriteRenderer, ShapeRenderer shapeRenderer) {

        spriteRenderer.begin();
        m_arena.drawWorld(spriteRenderer);
        for (int i = 0; i < m_fighters.length; i++) {
            Animation<TextureRegion> animation = m_controllers[i].getCurrentAnimation();
            if (animation != null) {
                TextureRegion currentFrame = animation.getKeyFrame(m_controllers[i].getStateTime(), true);
                Vector2 pos = m_fighters[i].getBody().getPosition();
                Vector2 size = m_fighters[i].getDimensions();

                if (!m_controllers[i].isFacingRight()) // Flip Orientation
                    size.x = -size.x;
                spriteRenderer.draw(
                        currentFrame,
                        GDXHelper.convertBox2dPos(pos.x, size.x),
                        GDXHelper.convertBox2dPos(pos.y, size.y),
                        GDXHelper.convertBox2dSize(size.x),
                        GDXHelper.convertBox2dSize(size.y));
            }
        }
        spriteRenderer.end();

        shapeRenderer.begin();
        for (int i = 0; i < m_fighters.length; i++) {
            if (m_stocks[i] > 0 || m_stocks[i] == -1) {
                Fighter fighter = m_fighters[i];
                Vector2 pos = fighter.getBody().getPosition();
                Vector2 size = fighter.getDimensions();
                // GDXHelper.drawRect(shapeRenderer, pos.x, pos.y, size.x, size.y);
                if (fighter.getController().isGuarding()) {
                    GDXHelper.drawCircle(shapeRenderer, pos.x + size.x / 2, pos.y + size.y / 2, size.y * (fighter.getController().getGuardPercent() / 100));
                }
            }
        }
        shapeRenderer.end();

        m_arena.drawUI();
    }


    /**
     * PRECONDITION: Battle has ended
     * Works only for FREE FOR ALL modes
     *
     * @return Name/Text who won / victory status
     */
    public String getWinner() {
        if (!isFinished) return null;

        Fighter winner = null;
        for (int i = 0; i < m_fighters.length; i++) {
            if (m_stocks[i] > 0 && winner == null) {
                winner = m_fighters[i];
            } else if (winner != null && m_stocks[i] > 0) {
                return "Game ended from Timer!";
            }
        }

        if (winner != null) {
            return winner.getName() + " Won the Battle!";
        }

        return "No Winner";
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

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


/**
 * Area class, this is the space where Fighters fight.
 * 
 * Has methods for updating the Ground, and Background.
 * This class is primarily for the visual aspects of the game, the physics gets handled by the World in Battle.
 * 
 * This class's implementation should mostly be in the Battle class.
 */
public class Arena {
    /** Arena Bounds, if a Fighter is outside of these bounds they will die. */
    public static final Vector2 BOUNDS = new Vector2(GDXHelper.PTM(1500), GDXHelper.PTM(1500));

    /** Constant position that every Arena's ground should be at. */
    private static final Vector2 GROUND_POSITION = new Vector2(GDXHelper.PTM(0), GDXHelper.PTM(-400));

    private static final float GROUND_WIDTH = GDXHelper.PTM(500);
    private static final float GROUND_HEIGHT = GDXHelper.PTM(200);
    private static final Skin FONT = new Skin(Gdx.files.internal("FontSkins\\default\\skin\\uiskin.json"));

    private final Vector2[] m_startingPositions;

    // World Objects
    private final Body m_groundBody = MyGdxGame.WORLD.createBody(GDXHelper.generateBodyDef(BodyType.StaticBody, GROUND_POSITION));
    private final Fixture m_groundFixture = m_groundBody.createFixture(
        GDXHelper.generateFixtureDef(1, 0.3f, 0, GROUND_WIDTH, GROUND_HEIGHT,
            MyGdxGame.entityCategory.Ground.getID(), MyGdxGame.entityCategory.Fighter.getID()));
    private final Body m_bedrockBody = MyGdxGame.WORLD.createBody(GDXHelper.generateBodyDef(BodyType.StaticBody, new Vector2(0, -5000)));
    private final Fixture m_bedrockFixture = m_bedrockBody.createFixture(
            GDXHelper.generateFixtureDef(1, 10, 0, 10000, 0.01f,
                    MyGdxGame.entityCategory.Ground.getID(), MyGdxGame.entityCategory.Fighter.getID()));

    private final Texture m_background;
    private final Texture m_ground;

    // UI
    private final Stage m_stage = new Stage();
    private final Label[] m_healthLabels;
    private final Label[] m_stockLabels;
    private final Label m_timerLabel;
    private float m_startTime;


    /**
     * Constructor for the Arena Class.
     */
    public Arena(int numOfFighters) {
        m_groundFixture.setUserData(MyGdxGame.entityCategory.Ground);
        m_startingPositions = new Vector2[] {
            new Vector2(GDXHelper.PTM(10), GDXHelper.PTM(10)), new Vector2(GDXHelper.PTM(7), GDXHelper.PTM(10))
        };

        m_healthLabels = new Label[numOfFighters];
        m_stockLabels = new Label[numOfFighters];
        float labelPosX = 100;
        float labelPosY = 60;
        for (int i = 0; i < numOfFighters; i++) {
            m_healthLabels[i] = new Label("0%", FONT);
            m_stage.addActor(m_healthLabels[i]);
            m_healthLabels[i].setPosition(labelPosX, labelPosY);
            m_stockLabels[i] = new Label("0", FONT);
            m_stage.addActor(m_stockLabels[i]);
            m_stockLabels[i].setPosition(labelPosX, labelPosY - 25);
            labelPosX += 200;
        }

        m_startTime = System.currentTimeMillis();
        m_timerLabel = new Label(Float.toString(m_startTime), FONT);
        m_stage.addActor(m_timerLabel);
        m_timerLabel.setPosition(1200, 600);

        m_background = new Texture(Gdx.files.internal("Arenas/background0.png"));
        m_ground = new Texture(Gdx.files.internal("Arenas/ground0.png"));
    }

    public Body getGroundBody() {
        return m_groundBody;
    }

    public Fixture getGroundFixture() {
        return m_groundFixture;
    }

    public Vector2[] getStartingPositions() {
        return m_startingPositions;
    }

    /**
     * Render elements of the Arena. Including UI, Background, and Ground.
     */
    public void drawWorld(SpriteBatch spriteRenderer) {
        spriteRenderer.draw(m_background, -640 / 100f, -360 / 100f, GDXHelper.PTM(1280), GDXHelper.PTM(720));
        spriteRenderer.draw(m_ground, -500 / 100f, -400 / 100f, GDXHelper.PTM(1000), GDXHelper.PTM(200));
        //m_stage.draw();
        // GDXHelper.drawRect(shapeRenderer, GROUND_POSITION.x, GROUND_POSITION.y, GROUND_WIDTH, GROUND_HEIGHT);
    }

    public void drawUI() {
        m_stage.draw();
    }

    public void dispose() {
        m_stage.dispose();
        m_groundFixture.getShape().dispose();
        m_bedrockFixture.getShape().dispose();
    }
    
    public void update(Fighter[] fighters, int[] stocks, float timeLimit) {
        for (int i = 0; i < fighters.length; i++) {
            m_healthLabels[i].setText(fighters[i].getName() + ": " + String.format("%.1f",fighters[i].getHealth()) + "%" + "\nUlt: " + String.format("%.1f", fighters[i].getUltMeter()) + "%");
            m_stockLabels[i].setText((stocks[i] == -1) ? "inf" : Integer.toString(stocks[i]));
        }

        // update timer
        if (timeLimit == -1) { // Count up
            m_timerLabel.setText(String.format("%.2f", (System.nanoTime() - m_startTime) / 1000000000.0));
        } else { // Count Down from TimeLimit
            m_timerLabel.setText(String.format("%.2f", (timeLimit - (System.nanoTime() - m_startTime) / 1000000000.0)));
        }

        // m_stage.act();
    }


    /**
     * Set the StartTime when the battle begins.
     * @param time in NanoSeconds
     */
    public void setStartTime(float time) {
        m_startTime = time;
    }
    
}

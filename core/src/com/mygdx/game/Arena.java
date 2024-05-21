package com.mygdx.game;

import com.badlogic.gdx.files.FileHandle;
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
    private static final Vector2 GROUND_POSITION = new Vector2(GDXHelper.PTM(0), GDXHelper.PTM(-200));

    private static final float GROUND_WIDTH = GDXHelper.PTM(500);
    private static final float GROUND_HEIGHT = GDXHelper.PTM(3);

    private final Vector2[] m_startingPositions;

    // World Objects
    private final Body m_groundBody = MyGdxGame.WORLD.createBody(GDXHelper.generateBodyDef(BodyType.StaticBody, GROUND_POSITION));
    private final Fixture m_groundFixture = m_groundBody.createFixture(
        GDXHelper.generateFixtureDef(1, 0.1f, 0, GROUND_WIDTH, GROUND_HEIGHT,
            MyGdxGame.entityCategory.Ground.getID(), MyGdxGame.entityCategory.Fighter.getID()));
    private final Body m_bedrockBody = MyGdxGame.WORLD.createBody(GDXHelper.generateBodyDef(BodyType.StaticBody, new Vector2(0, -5000)));
    private final Fixture m_bedrockFixture = m_bedrockBody.createFixture(
            GDXHelper.generateFixtureDef(1, 10, 0, 10000, 0.01f,
                    MyGdxGame.entityCategory.Ground.getID(), MyGdxGame.entityCategory.Fighter.getID()));

    // UI
    private final Stage m_stage = new Stage();
    private final Label[] m_healthLabels;


    /**
     * Constructor for the Arena Class.
     */
    public Arena(int numOfFighters) {
        m_groundFixture.setUserData("ground");
        m_startingPositions = new Vector2[] {
            new Vector2(GDXHelper.PTM(10), GDXHelper.PTM(10)), new Vector2(GDXHelper.PTM(7), GDXHelper.PTM(10))
        };

        m_healthLabels = new Label[numOfFighters];
        float labelPosX = 0;
        float labelPosY = 10;
        for (int i = 0; i < numOfFighters; i++) {
            m_healthLabels[i] = new Label("0%", new Skin(new FileHandle("C:\\Users\\alexh\\Desktop\\test\\core\\src\\com\\mygdx\\game\\FontSkins\\default\\skin\\uiskin.json")));
            m_stage.addActor(m_healthLabels[i]);
            m_healthLabels[i].setPosition(labelPosX, labelPosY);
            labelPosX += 100;
        }

        // m_stage.setDebugAll(true);

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
    public void draw(ShapeRenderer shapeRenderer) {
        m_stage.draw();
        GDXHelper.drawRect(shapeRenderer, GROUND_POSITION.x, GROUND_POSITION.y, GROUND_WIDTH, GROUND_HEIGHT);
    }

    public void dispose() {
        m_stage.dispose();
        m_groundFixture.getShape().dispose();
        m_bedrockFixture.getShape().dispose();
    }
    
    public void update(Fighter[] fighters) {
        for (int i = 0; i < fighters.length; i++) {
            m_healthLabels[i].setText(fighters[i].getName() + ": " + fighters[i].getHealth() + "%");
            // Vector2 pos = fighters[i].getBody().getPosition();
            // Vector2 dim = fighters[i].getDimensions();
            // m_healthLabels[i].setPosition(pos.x + dim.x, pos.y + dim.y);
        }

        m_stage.act();
    }
    
}

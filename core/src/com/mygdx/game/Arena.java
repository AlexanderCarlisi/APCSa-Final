package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;


/**
 * Area class, this is the space where Fighters fight.
 * 
 * Has methods for updating the Ground, and Background.
 * This class is primarily for the visual aspects of the game, the physics gets handled by the World in Battle.
 * 
 * This class's implementation should mostly be in the Battle class.
 */
public class Arena {
    /** Constant position that every Arena's ground should be at. */
    private static final Vector2 GROUND_POSITION = new Vector2(0, GDXHelper.PTM(-200));

    private final Body m_groundBody = Battle.WORLD.createBody(GDXHelper.generateBodyDef(BodyType.StaticBody, GROUND_POSITION));
    private final Fixture m_groundFixture = m_groundBody.createFixture(GDXHelper.generateFixtureDef(1, 0.1f, 0, GDXHelper.PTM(500), GDXHelper.PTM(3)));

    /**
     * Constructor for the Arena Class.
     */
    public Arena() {
        m_groundFixture.setUserData("ground");
    }

    public Body getGroundBody() {
        return m_groundBody;
    }

    public Fixture getGroundFixture() {
        return m_groundFixture;
    }

    // Might not be needed
    // public void draw() {

    // }

    // For if there was ever anything wanting real-time updates. Like a moving background.
    // public void update() {

    // }
    
}

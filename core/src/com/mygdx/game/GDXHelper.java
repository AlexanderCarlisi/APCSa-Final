package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * I'm not gonna say LibGDX sucks, but there's a reason this file exists.
 * 
 * A class for generalized helper methods to cope with the lack of constructors in LibGDX.
 */
public class GDXHelper {

    /**
     * Pixels to Meters Method.
     * Box2D uses meters for everything, so this method is used to convert pixels to meters.
     * 
     * Converts pixels to meters.
     * @param pixels 
     * @return 
     */
    public static float PTM(float pixels) {
        return pixels / 100f;
    }

    /**
     * Generates a BodyDef object with the given type and position.
     * 
     * For some reason LibGDX doesn't have a constructor for this. It's annoying.
     * @param type 
     * @param position 
     * @return 
     */
    public static BodyDef generateBodyDef(BodyDef.BodyType type, Vector2 position) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(position);
        bodyDef.fixedRotation = true;

        return bodyDef;
    }

    /**
     * Generates a FixtureDef object with the given density, friction, restitution, width, and height.
     * 
     * For some reason LibGDX doesn't have a constructor for this. It's annoying.
     * @param density
     * @param friction
     * @param restitution
     * @param width
     * @param height
     * @param catagoryBits
     * @param maskBits
     * @return
     */
    public static FixtureDef generateFixtureDef(float density, float friction, float restitution, float width, float height, short catagoryBits, short maskBits) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;

        // fixtureDef.shape = new PolygonShape();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width, height);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = catagoryBits;
        fixtureDef.filter.maskBits = maskBits;

        return fixtureDef;
    }
}

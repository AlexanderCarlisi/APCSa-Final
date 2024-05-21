package com.mygdx.game;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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


    /**
     * Converts a position in Box2D to its position in LibGDX, for Drawing objects to the screen.
     * @param box2dPos x cord or y cord
     * @param dimension either the Width(Box2D x cord) or Height(Box2D y cord) of the Fixture.
     * @return Position for LibGDX components
     */
    public static float convertBox2dPos(float box2dPos, float dimension) {
        return box2dPos - dimension;
    }


    /**
     * The Transform between Box2d and LibGDX is weird, this will convert LibGDX drawing
     * pos to Box2d's drawing pose. This uses Box2d's position as the real one, and LibGDX as
     * the overlay.
     *
     * <p>
     * Requires shapeRenderer to have already been begun!
     *
     * @param posX
     * @param posY
     * @param width
     * @param height
     */
    public static void drawRect(ShapeRenderer shapeRenderer, float posX, float posY, float width, float height) {
        shapeRenderer.rect(posX - width, posY - height, width * 2, height * 2);
    }
}

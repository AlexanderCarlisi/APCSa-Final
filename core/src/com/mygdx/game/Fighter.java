package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import java.util.HashMap;

/**
 * Fighter Class
 * 
 */
public class Fighter {

    public static class AttackConfig {
        public final Vector2 offset;
        public final boolean isSideDependent;
        public final float damage;
        public final float ultPercent;
        public final float force;
        public final Vector2 size;
        public final Vector2 impulse;
        public final boolean isProjectile;
        public final Attack.direction direction;
        public final boolean isGroundAttack;
        public final Attack.attackType attackType;
        public final boolean bringFighter;
        public final long lifeTime;
        public final float endLag;

        public AttackConfig(Attack.attackType attackType, Attack.direction direction, float damage, float ultPercent, float force, Vector2 offset, Vector2 size, boolean isSideDependent, boolean isGroundAttack, float endLag) {
            this.attackType = attackType;
            this.direction = direction;
            this.damage = damage;
            this.ultPercent = ultPercent;
            this.force = force;
            this.offset = offset;
            this.size = size;
            this.impulse = null;
            this.isProjectile = false;
            this.isSideDependent = isSideDependent;
            this.isGroundAttack = isGroundAttack;
            this.bringFighter = false;
            this.endLag = endLag;
            this.lifeTime = 0;
        }

        public AttackConfig(Attack.attackType attackType, Attack.direction direction, float damage, float ultPercent, float force, long lifeTime, Vector2 offset, Vector2 size, Vector2 impulse, boolean isGroundAttack, boolean bringFighter, float endLag) {
            this.isProjectile = true;
            this.attackType = attackType;
            this.direction = direction;
            this.damage = damage;
            this.ultPercent = ultPercent;
            this.force = force;
            this.lifeTime = lifeTime;
            this.offset = offset;
            this.size = size;
            this.impulse = impulse;
            this.isSideDependent = false;
            this.isGroundAttack = isGroundAttack;
            this.bringFighter = bringFighter;
            this.endLag = endLag;
        }
    }

    /** Body Definition used for each Fighter. */
    private final BodyDef BODY_DEF = GDXHelper.generateBodyDef(BodyType.DynamicBody, new Vector2(0, 0));

    /** Name of the Character. */
    private final String m_name;

    /** The amount of force to apply to the Y Axis. */
    private final float m_jumpForce;

    /** Weight, effects Launch percent. Damage Calcs */
    private final float m_weight;

    /** Amount of Pixels to Move per Frame */
    private final float m_runSpeed;

    /** Height of the Fighter, same value fed into the Fixture Def. */
    private final float m_height;

    /** Width of the Fighter, same value fed into the Fixture Def. */
    private final float m_width;

    /** Body of the Fighter generated by the World. */
    private final Body m_body;

    /** Fixture of the Fighter generated by the Body. */
    private final Fixture m_fixture;

    /** Final Array of all Attacks/Specials of the Fighter. */
    private final AttackConfig[] m_attackConfigs;

    private PlayerController m_controller;

    /** Current health in Percent. */
    private float m_health;

    /** Current Value of the Ultimate Meter. */
    private float m_ultMeter;

    /** If the Fighter is still in the Battle. */
    public boolean isDead;
    

    /**
     * Constructor for the Fighter Class.
     * @param name 
     * @param runSpeed
     * @param jumpForce
     * @param weight
     * @param fixtureDef
     */
    public Fighter(String name, float runSpeed, float jumpForce, float weight, FixtureDef fixtureDef, float width, float height, AttackConfig[] attackConfigs) {
        m_name = name;
        m_jumpForce = jumpForce;
        m_weight = weight;
        m_runSpeed = runSpeed;
        m_height = height;
        m_width = width;
        isDead = false;
        m_body = MyGdxGame.WORLD.createBody(BODY_DEF);
        m_fixture = m_body.createFixture(fixtureDef);
        m_fixture.setUserData(this); // Collider identifier
        m_attackConfigs = attackConfigs;

        m_body.setGravityScale(0.1f);
    }

    public String getName() {
        return m_name;
    }
    
    public float getHealth() {
        return m_health;
    }

    public void setHealth(float health) {
        m_health = health;
    }

    public float getUltMeter() {
        return m_ultMeter;
    }

    public void setUltMeter(float value) {
        m_ultMeter = value;
    }

    public float getJumpForce() {
        return m_jumpForce;
    }

    public float getWeight() {
        return m_weight;
    }

    public float getRunSpeed() {
        return m_runSpeed;
    }

    public Body getBody() {
        return m_body;
    }

    public Fixture getFixture() {
        return m_fixture;
    }

    public Vector2 getDimensions() {
        return new Vector2(m_width, m_height);
    }

    public void setController(PlayerController controller) {
        m_controller = controller;
    }

    public PlayerController getController() {
        return m_controller;
    }

    /**
     * Performs an Attack for the Fighter.
     *
     * @param direction : Direction given by the PlayerController.
     * @param facingRight : The Direction the Fighter is facing.
     * @return EndLag of the used Move.
     */
    public float attack(Attack.attackType attackType, Attack.direction direction, boolean onGround, boolean facingRight) {
        Vector2 pos = m_body.getPosition();
        for (AttackConfig config : m_attackConfigs) {
            if (config.attackType == attackType && (attackType == Attack.attackType.Special || (attackType == Attack.attackType.Basic || attackType == Attack.attackType.Ultimate && onGround == config.isGroundAttack) || (attackType == Attack.attackType.Smash && onGround)) && config.direction == direction) {
                if (!config.isProjectile)
                    new Attack(
                            this,
                            config.damage, config.ultPercent, config.force,
                            config.isSideDependent ?
                                    facingRight ?
                                            new Vector2(pos.x + config.offset.x, pos.y + config.offset.y) :
                                            new Vector2(pos.x - config.offset.x, pos.y + config.offset.y)
                                    : new Vector2(pos.x + config.offset.x, pos.y + config.offset.y),
                            config.size,
                            direction,
                            facingRight);
                else
                    new Attack(
                            this,
                            config.damage, config.ultPercent, config.force, config.lifeTime, config.bringFighter,
                            facingRight ?
                                    new Vector2(pos.x + config.offset.x, pos.y + config.offset.y) :
                                    new Vector2(pos.x - config.offset.x, pos.y + config.offset.y),
                            config.impulse,
                            config.size,
                            direction,
                            facingRight);
                return config.endLag;
            }
        }
        return 0;
    }
}

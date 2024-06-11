package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import java.io.IOException;
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


        public AttackConfig() {
            this.attackType = null;
            this.direction = null;
            this.damage = 0;
            this.ultPercent = 0;
            this.force = 0;
            this.offset = null;
            this.size = null;
            this.impulse = null;
            this.isProjectile = false;
            this.isSideDependent = false;
            this.isGroundAttack = false;
            this.bringFighter = false;
            this.endLag = 0;
            this.lifeTime = 0;
        }


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


    public static class FighterConfig {
        public final String name;
        public final float jumpForce;
        public final float runSpeed;
        public final float weight;
        public final float width;
        public final float height;
        public final AttackConfig[] attackConfigs;

        public FighterConfig() {
            this.name = null;
            this.jumpForce = 0;
            this.weight = 0;
            this.runSpeed = 0;
            this.width = 0;
            this.height = 0;
            this.attackConfigs = null;
        }

        public FighterConfig(String name, float runSpeed, float jumpForce, float weight, float width, float height, AttackConfig[] attackConfigs) {
            this.name = name;
            this.jumpForce = jumpForce;
            this.weight = weight;
            this.runSpeed = runSpeed;
            this.width = width;
            this.height = height;
            this.attackConfigs = attackConfigs;
        }
    }

    public enum Animations {
        Idle("idle", 0),
        Run("run", -1),
        Jump("jump", 1),
        GroundNeutral("groundNeutral", 2),
        GroundSide("groundSide", 2),
        GroundUp("groundUp", 2),
        GroundDown("groundDown", 2),
        AirNeutral("airNeutral", 2),
        AirSide("airSide", 2),
        AirUp("airUp", 2),
        AirDown("airDown", 2),
        SpecialNeutral("specialNeutral", 2),
        SpecialSide("specialSide", 2),
        SpecialUp("specialUp", 2),
        SpecialDown("specialDown", 2),
        SmashSide("smashSide", 2),
        SmashUp("smashUp", 2),
        SmashDown("smashDown", 2),
        Ultimate("ultimate", 3),
        Shield("shield", 4),
        ShieldBreak("shieldBreak", 5);

        public final String path;
        public final int priority;

        Animations(String path, int priority) {
            this.path = path;
            this.priority = priority;
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

    private final HashMap<Animations, Animation<TextureRegion>> m_animations;

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
     */
    public Fighter(String name, float runSpeed, float jumpForce, float weight, float width, float height, AttackConfig[] attackConfigs) {
        m_name = name;
        m_jumpForce = jumpForce;
        m_weight = weight;
        m_runSpeed = runSpeed;
        m_height = height;
        m_width = width;
        isDead = false;
        m_body = MyGdxGame.WORLD.createBody(BODY_DEF);
        m_fixture = m_body.createFixture(GDXHelper.generateFixtureDef(1f, 4f, 0f, width, height,
                MyGdxGame.entityCategory.Fighter.id, MyGdxGame.entityCategory.Ground.id));
        m_fixture.setUserData(this); // Collider identifier
        m_attackConfigs = attackConfigs;

        m_animations = null;
        m_body.setGravityScale(0.1f);
    }

    public Fighter(FighterConfig config) {
        m_name = config.name;
        m_jumpForce = config.jumpForce;
        m_weight = config.weight;
        m_runSpeed = config.runSpeed;
        m_height = config.height;
        m_width = config.width;
        isDead = false;
        m_body = MyGdxGame.WORLD.createBody(BODY_DEF);
        m_fixture = m_body.createFixture(GDXHelper.generateFixtureDef(1f, 4f, 0f, m_width, m_height,
                MyGdxGame.entityCategory.Fighter.id, MyGdxGame.entityCategory.Ground.id));
        m_fixture.setUserData(this); // Collider identifier
        m_attackConfigs = config.attackConfigs;

        m_animations = new HashMap<>();
        String path = "Animations/"+m_name+"/";
        for (Animations num : Animations.values()) {
            try {
                m_animations.put(num, GDXHelper.generateAnimation(new Texture(Gdx.files.internal(path+num.path+".png")), 1/4f));
            } catch (Exception e) {}
        }

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

    public HashMap<Animations, Animation<TextureRegion>> getAnimations() {
        return m_animations;
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

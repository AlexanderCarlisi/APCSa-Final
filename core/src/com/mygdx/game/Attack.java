package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Attack {

    public static class AttackInfo {
        public Fighter user;
        public Attack attack;
        public long lifeTime;

        public AttackInfo(Fighter fighter, Attack _attack, long time) {
            user = fighter;
            attack = _attack;
            lifeTime = System.currentTimeMillis() + time;
        }
    }


    public enum direction {
        Neutral,
        Side,
        Up,
        Down
    }

    public enum attackType {
        Basic,
        Special,
        Smash,
        Ultimate
    }

    public final float m_damage;
    public final Fixture m_fixture;
    public final Body m_body;
    public final float m_force;
    public final direction dir;
    public final boolean isFacingRight;


    public Attack(Fighter user, float damage, float force, Vector2 pos, Vector2 size, direction dir, boolean isFacingRight) {
        m_damage = damage;
        m_body = MyGdxGame.WORLD.createBody(GDXHelper.generateBodyDef(BodyDef.BodyType.DynamicBody, pos));
        m_fixture = m_body.createFixture(GDXHelper.generateFixtureDef(0, 0, 0, size.x, size.y,
                MyGdxGame.entityCategory.Attack.getID(), MyGdxGame.entityCategory.Fighter.getID()));
        m_fixture.setSensor(true);
        m_fixture.setUserData(new AttackInfo(user, this, 50));
        m_force = force;
        this.dir = dir;
        this.isFacingRight = isFacingRight;
        m_body.setGravityScale(0);
    }


    public Attack(Fighter user, float damage, float force, long lifeTime, boolean bringFighter, Vector2 startingPos, Vector2 impulse, Vector2 size, direction dir, boolean isFacingRight) {
        m_damage = damage;
        m_body = MyGdxGame.WORLD.createBody(GDXHelper.generateBodyDef(BodyDef.BodyType.DynamicBody, startingPos));
        m_fixture = m_body.createFixture(GDXHelper.generateFixtureDef(0, 0, 0, size.x, size.y,
            MyGdxGame.entityCategory.Attack.getID(), MyGdxGame.entityCategory.Fighter.getID()));
        m_fixture.setSensor(true);
        m_fixture.setUserData(new AttackInfo(user, this, lifeTime));
        m_force = force;
        this.dir = dir;
        this.isFacingRight = isFacingRight;
        m_body.setGravityScale(0);

        m_body.setGravityScale(0);
        Vector2 updatedImpulse = new Vector2(isFacingRight ? impulse.x : -impulse.x, impulse.y);
        m_body.applyLinearImpulse(updatedImpulse, startingPos, true);

        if (bringFighter) {
            user.getBody().applyLinearImpulse(updatedImpulse, startingPos, true);
        }
    }

    public void dispose() {
         m_fixture.setUserData(MyGdxGame.entityCategory.Destroy);
    }
}

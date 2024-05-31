package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Attack {

    public static class AttackCollisionListener implements ContactListener {

        @Override
        public void beginContact(Contact contact) {
            // Fixture A is Fighter contacted with
            // Fixture B is AttackInfo
            if (contact.getFixtureA().getUserData() instanceof Fighter && contact.getFixtureB().getUserData() instanceof AttackInfo) {
                AttackInfo attackInfo = (AttackInfo) contact.getFixtureB().getUserData();
                Fighter target = (Fighter) contact.getFixtureA().getUserData();

                if (attackInfo.user != target) {
                    target.setHealth(target.getHealth() + attackInfo.attack.m_damage);

                    // Calculate the direction from the attack to the target
                    Vector2 attackPosition = contact.getFixtureB().getBody().getPosition();
                    Vector2 targetPosition = contact.getFixtureA().getBody().getPosition();
                    Vector2 direction = targetPosition.sub(attackPosition).nor();

                    // Apply an impulse to the target's body in the calculated direction
                    float impulseMagnitude = (target.getHealth() / 100) * (attackInfo.attack.m_force / target.getWeight());
                    Vector2 impulse = direction.scl(impulseMagnitude);
                    target.getBody().applyLinearImpulse(impulse, target.getBody().getWorldCenter(), true);
                    
                    attackInfo.attack.dispose();
                }
            }
        }

        @Override
        public void endContact(Contact contact) {}

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {
            // Have Collision Detection, but no Physical Collisions for Attacks
            if (contact.getFixtureB().getUserData() instanceof AttackInfo) {
                contact.setEnabled(false);
            }
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {}
    }


    public static class AttackInfo {
        public Fighter user;
        public Attack attack;
        public float lifeTime;

        public AttackInfo(Fighter fighter, Attack _attack, float time) {
            user = fighter;
            attack = _attack;
            lifeTime = System.nanoTime() + time;
        }
    }


    public enum direction {
        Neutral,
        Side,
        Up,
        Down
    }

    private final float m_damage;
    private final Fixture m_fixture;
    private final Body m_body;
    private final float m_force;
    // private final float m_radius;

    public Attack(Fighter user, float damage, float force, Vector2 pos, float width, float height, boolean isProjectile) {
        m_damage = damage;
        m_body = MyGdxGame.WORLD.createBody(GDXHelper.generateBodyDef(BodyDef.BodyType.StaticBody, pos));
        m_fixture = m_body.createFixture(GDXHelper.generateFixtureDef(0, 0, 0, width, height,
                MyGdxGame.entityCategory.Attack.getID(), MyGdxGame.entityCategory.Fighter.getID()));
        m_fixture.setUserData(new AttackInfo(user, this, isProjectile ? 5 : 0.1f));
        m_force = force;
    }

    public void dispose() {
         m_fixture.setUserData("MARKED FOR DELETION");
    }
}

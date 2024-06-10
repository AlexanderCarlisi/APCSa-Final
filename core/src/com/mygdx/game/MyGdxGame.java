package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.PlayerController.ControllerType;

/**
 *
 * MyGdxGame class
 */
public class MyGdxGame extends ApplicationAdapter {

	public static class WorldContactListener implements ContactListener {
		@Override
		public void beginContact(Contact contact) {
			// Grounding Fighters
			// Fixture A is the Fighter
			// Fixture B is the Ground
			if (contact.getFixtureB().getUserData() instanceof MyGdxGame.entityCategory && contact.getFixtureA().getUserData() instanceof Fighter) {
				MyGdxGame.entityCategory ground = (MyGdxGame.entityCategory) contact.getFixtureB().getUserData();
				Fighter fighter = (Fighter) contact.getFixtureA().getUserData();
				if (ground == MyGdxGame.entityCategory.Ground) {
					fighter.getController().setGrounded(true);
				}
			}

			// Attacking Fighters
			// Fixture A is Fighter contacted with
			// Fixture B is AttackInfo
			if (contact.getFixtureA().getUserData() instanceof Fighter && contact.getFixtureB().getUserData() instanceof Attack.AttackInfo) {
				Attack.AttackInfo attackInfo = (Attack.AttackInfo) contact.getFixtureB().getUserData();
				Fighter target = (Fighter) contact.getFixtureA().getUserData();

				if (attackInfo.user != target) {
					if (!target.getController().isGuarding()) {
						target.setHealth(target.getHealth() + attackInfo.attack.m_damage);
						attackInfo.user.setUltMeter(attackInfo.user.getUltMeter() + attackInfo.attack.ultPercent);

						// Apply an impulse to the target's body in the calculated direction
						float impulseMagnitude = attackInfo.attack.m_force + (attackInfo.attack.m_force * (target.getHealth() / 100 / target.getWeight()));
						Vector2 impulse = new Vector2(impulseMagnitude, impulseMagnitude);

						switch(attackInfo.attack.dir) {
							case Neutral:

							case Side: {
								if (attackInfo.attack.isFacingRight) {
									impulse.set(impulse.x, impulse.y / 2);
								} else {
									impulse.set(-impulse.x, impulse.y / 2);
								}
								break;
							}

							case Up: {
								if (attackInfo.attack.isFacingRight) {
									impulse.set(impulse.x / 4, impulse.y * 1.5f);
								} else {
									impulse.set(-impulse.x / 4, impulse.y * 1.5f);
								}
								break;
							}

							case Down: {
								if (attackInfo.attack.isFacingRight) {
									impulse.set(impulse.x / 4, -impulse.y * 1.5f);
								} else {
									impulse.set(-impulse.x / 4, -impulse.y * 1.5f);
								}
								break;
							}
						}

						target.getBody().applyLinearImpulse(impulse, target.getBody().getWorldCenter(), true);
					}
					attackInfo.attack.dispose();
				}
			}
		}

		@Override
		public void endContact(Contact contact) {
			// Grounding Fighters
			// Fixture A is the Fighter
			// Fixture B is the Ground
			if (contact.getFixtureB().getUserData() instanceof MyGdxGame.entityCategory && contact.getFixtureA().getUserData() instanceof Fighter) {
				MyGdxGame.entityCategory ground = (MyGdxGame.entityCategory) contact.getFixtureB().getUserData();
				Fighter fighter = (Fighter) contact.getFixtureA().getUserData();
				if (ground == MyGdxGame.entityCategory.Ground) {
					fighter.getController().setGrounded(false);
				}
			}
		}

		@Override
		public void preSolve(Contact contact, Manifold oldManifold) {
			// Could potentially be removed, but if it ain't broke don't fix it.
			// Have Collision Detection, but no Physical Collisions for Attacks
			if (contact.getFixtureB().getUserData() instanceof Attack.AttackInfo) {
				contact.setEnabled(false);
			}
		}

		@Override
		public void postSolve(Contact contact, ContactImpulse impulse) {}
	}


	/**
	 * Entity Categories, for MaskBits and Identification of Fixtures.
	 */
	public enum entityCategory {
		Default((short) 0),
		Ground((short) 1),
		Fighter((short) 2),
		Attack((short) 3),
		Destroy((short) 4);

		private final short id;
		private entityCategory(short id) {
			this.id = id;
		}

		public short getID() {
			return this.id;
		}
	}

	// Constants
	private static final float TIME_STEP = 1/60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;

	/** World Object, handles all Physics, needs to be declared first so bodies don't throw an error. */
    public static World WORLD;
	public static Camera CAMERA;

	/** Render vars */
    private Box2DDebugRenderer m_debugRenderer;
	private float m_accumulator = 0;
	private float m_previousTime = 0;

	private SpriteBatch m_spriteBatch;
	private ShapeRenderer m_shapeRenderer;

	private CharacterSelect m_characterSelector;
	private Battle m_battle;
	

	@Override
	public void create () { // Start of the Program
		WORLD = new World(new Vector2(0f, -1f), true);
		CAMERA = new OrthographicCamera(GDXHelper.PTM(1280), GDXHelper.PTM(720));
		m_debugRenderer = new Box2DDebugRenderer();

		m_spriteBatch = new SpriteBatch();
		m_shapeRenderer = new ShapeRenderer();
		m_shapeRenderer.setAutoShapeType(true);
		WORLD.setContactListener(new WorldContactListener()); // Collision Listener

		// Should eventually be moved to Render method once properly implemented.
		m_characterSelector = new CharacterSelect();
	}


	@Override
	public void render () { // During the Program
		ScreenUtils.clear(0, 0, 0, 1); // values range from 0-1 instead of 0-255

		// Game state Updates/Checks
		if (m_battle == null && m_characterSelector != null && m_characterSelector.isFinished()) {
			Fighter[] fighters = m_characterSelector.getFighters();
			PlayerController[] controllers = m_characterSelector.getControllers(fighters);
			m_battle = new Battle(fighters, controllers, new BattleConfig());
		}

		// Physics Step
		float currentTime = System.nanoTime();
        physicsStep(currentTime - m_previousTime);
        m_previousTime = currentTime;

		// Update Environments
		CAMERA.update();
		if (m_battle != null && !m_battle.isFinished) m_battle.update();

		// Draw Environments
		m_debugRenderer.render(WORLD, CAMERA.combined); // See Collision Boxes, to be removed
		m_spriteBatch.setProjectionMatrix(CAMERA.combined); // Matrix for Sprites
		m_shapeRenderer.setProjectionMatrix(CAMERA.combined); // Matrix for GDXShapes
		if (m_battle != null && !m_battle.isFinished) m_battle.draw(m_spriteBatch, m_shapeRenderer);
	}

	
	@Override
	public void dispose () { // End of the Program
		m_spriteBatch.dispose();
		m_debugRenderer.dispose();
		m_battle.dispose();
	}

	
	/**
	 * 
	 * @param deltaTime 
	 */
    private void physicsStep(float deltaTime) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        m_accumulator += frameTime;
        while (m_accumulator >= TIME_STEP) {
            WORLD.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            m_accumulator -= TIME_STEP;
        }
    }
}

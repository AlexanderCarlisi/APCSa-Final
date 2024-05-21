package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.PlayerController.ControllerType;

/**
 *
 * MyGdxGame class
 */
public class MyGdxGame extends ApplicationAdapter {

	public static enum entityCategory {
		Default((short) 0),
		Ground((short) 1),
		Fighter((short) 2);

		private final short id;
		private entityCategory(short id) {
			this.id = id;
		}

		public short getID() {
			return this.id;
		}
	}

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
	private Battle m_battle;
	

	@Override
	public void create () { // Start of the Program
		WORLD = new World(new Vector2(0f, -1f), true);
		CAMERA = new OrthographicCamera(GDXHelper.PTM(1280), GDXHelper.PTM(720));
		m_debugRenderer = new Box2DDebugRenderer();

		m_spriteBatch = new SpriteBatch();
		m_shapeRenderer = new ShapeRenderer();
		m_shapeRenderer.setAutoShapeType(true);

		// Will be gotten in the Main Menu, but for now declared here.
		Fighter[] fighters = new Fighter[] {

			new Fighter("Test", 0.05f, 0.3f, 10f, 
				GDXHelper.generateFixtureDef(
					1f, 4f, 0f, 
					GDXHelper.PTM(15f), GDXHelper.PTM(25f), 
					entityCategory.Fighter.getID(), entityCategory.Ground.getID()), GDXHelper.PTM(15f), GDXHelper.PTM(25f)),

			new Fighter("Test2", 0.1f, 0.7f, 10f, 
				GDXHelper.generateFixtureDef(
					1f, 4f, 0f, 
					GDXHelper.PTM(25f), GDXHelper.PTM(35f), 
					entityCategory.Fighter.getID(), entityCategory.Ground.getID()), GDXHelper.PTM(25f), GDXHelper.PTM(35f))};

		ControllerType[] controllers = new ControllerType[] {ControllerType.Keyboard, ControllerType.Keyboard2};
		
		m_battle = new Battle(fighters, controllers, new BattleConfig());
	}


	@Override
	public void render () { // During the Program
		ScreenUtils.clear(0, 0, 0, 1); // values range from 0-1 instead of 0-255

		float currentTime = System.nanoTime();
        physicsStep(currentTime - m_previousTime);
        m_previousTime = currentTime;

		CAMERA.update();
		m_battle.update();

		// Draw Characters
		m_debugRenderer.render(WORLD, CAMERA.combined);
		m_spriteBatch.setProjectionMatrix(CAMERA.combined);
		m_shapeRenderer.setProjectionMatrix(CAMERA.combined);
		m_battle.draw(m_spriteBatch, m_shapeRenderer);

		// Draw Background


		// Draw UI


		// m_frameCount++;
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

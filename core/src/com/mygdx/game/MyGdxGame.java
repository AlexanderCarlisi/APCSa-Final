package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch m_spriteBatch;
	Battle m_battle;

	
	@Override
	public void create () { // Start of the Program
		m_spriteBatch = new SpriteBatch();

		Fighter fighter1 = new Fighter("Test", 0.05f, 0.3f, 10f);
		Fighter[] fighters = new Fighter[] {fighter1};
		
		m_battle = new Battle(fighters);
	}


	@Override
	public void render () { // During the Program
		ScreenUtils.clear(0, 0, 0, 1); // values range from 0-1 instead of 0-255
		m_battle.update(); // Update Battle each Frame

		// Draw Characters
		m_battle.draw(m_spriteBatch);

		// Draw Background


		// Draw UI


		// m_frameCount++;
	}

	
	@Override
	public void dispose () { // End of the Program
		m_spriteBatch.dispose();
	}
}

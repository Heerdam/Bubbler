package com.game.bubbler;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public interface Ball {

	public void checkSolution();

	public void delete();

	public boolean clicked(Vector2 click);

	public void step(SpriteBatch batch);

	public void destroy();
	
	public boolean isAnmiationFinished();

}
package com.game.bubbler;

import java.util.Iterator;

import box2dLight.PointLight;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class RenderSystem extends EntitySystem implements InputProcessor {
	
	protected Iterator<Ball> iterator;
	//protected Polygon viewport;
	protected Vector3 clickPoint;
	
	protected PointLight centre;
	
	protected GameScreen screen;
	protected Vector2 gravity;
	
	protected Game game;
	
	public RenderSystem(Game game){
		this.game = game;
	}
	
	public void removedFromEngine (Engine engine) {
		game.multiplexer.removeProcessor(this);
		centre.dispose();			
	}
	
	public void addedToEngine(Engine engine) {
		gravity = new Vector2();
		screen = game.game;
		game.multiplexer.addProcessor(this);
		clickPoint = new Vector3();
		centre = new PointLight(game.ray, 50, Color.WHITE, 15, game.camera.position.x, game.camera.position.y);
		centre.setXray(true);

		game.world.setContactListener(new ContactListener(){

			@Override
			public void beginContact(Contact contact) {
				
			}

			@Override
			public void endContact(Contact contact) {
							
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
					
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				if(!game.paused){
					if(contact.getFixtureA().getBody().getUserData() != null){
						BubbleSpezial temp = (BubbleSpezial) contact.getFixtureA().getBody().getUserData();
						temp.changeLight();
					}else if(contact.getFixtureB().getBody().getUserData() != null){
						BubbleSpezial temp = (BubbleSpezial) contact.getFixtureB().getBody().getUserData();
						temp.changeLight();
					}
				}
			}
			
		});
		
	}

	public void update(float deltaTime) {		
		if(game.hasCompass){
			game.world.setGravity(gravity.set((float)(-1*Math.sin(Gdx.input.getPitch()*MathUtils.degreesToRadians)*4), (float)(Math.sin(Gdx.input.getRoll()*MathUtils.degreesToRadians)*4)));		
		}

		game.batch.begin();
		iterator = game.game.maths.bubbles.iterator();
		//System.out.println(game.game.maths.bubbles.size);
		while(iterator.hasNext()){
			Ball b = iterator.next();	
			if(b.clicked(new Vector2(clickPoint.x, clickPoint.y)) && game.clicked && !game.paused){
				b.checkSolution();		
				game.clicked = false;
			}
	
			if(b.isAnmiationFinished()){
				b.destroy();
				iterator.remove();
			}else{
				b.step(game.batch);
			}
		}
		game.batch.end();
	if(game.clicked){
		game.clicked = false;
		}	
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.ESCAPE){		
			if(game.paused){
				game.paused = false;
			}else{
				game.paused = true;
			}
			screen.showMenu();
		}else if(keycode == Keys.W){
			game.game.maths.createExcercise();
		}else if(keycode == Keys.C){
			game.options.addPoints(-game.options.getPoints()*2);
		}else if(keycode == Keys.S){
			game.game.maths.spawnSpezial();
		} 
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			clickPoint = (game.camera.unproject(new Vector3(screenX, screenY, 0)));
			game.clicked = true;
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}

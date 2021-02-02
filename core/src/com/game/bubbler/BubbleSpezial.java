package com.game.bubbler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import box2dLight.PointLight;

public class BubbleSpezial implements Ball{
	
	protected Body body;
	protected PointLight light;
	protected Circle circle;
	protected BitmapFont font;
	protected int length;
	protected long spawnTime;
	protected long deathTime;
	protected Color color;
	
	protected boolean delete;
	protected boolean animationFinished;
	
	protected Game game;

	public BubbleSpezial(Vector2 position, PointLight light, float size, BitmapFont font, Game game){
		this.game = game;
		color = new Color();
		changeLight();
		circle = new Circle(position.x*Game.Box2World, position.y*Game.Box2World, (float)game.scaleAndroid(size*Game.Box2World));
		body = createBody(new Vector2(position).scl(Game.World2Box), (float)game.scaleAndroid(size));
		length =  (int) (font.getCapHeight());
		
		this.font = font;
		this.light = light;
		this.light.setColor(color);
		this.light.setDistance((float)game.scaleAndroid(size)*3f);
		this.light.setIgnoreAttachedBody(true);
		this.light.attachToBody(body);
		this.light.setActive(true);
		
		spawnTime = System.nanoTime()/1000000;
		deathTime = MathUtils.random(5, 10) * 1000;
		game.options.ballsSpezial++;
	}

	@Override
	public void checkSolution() {
		delete = true;	
		animationFinished = true;
		Gdx.input.vibrate(250);
		game.options.addSpezial();
	}

	@Override
	public void delete(){
		light.setActive(false);
		game.game.maths.lights.add(light);
		if(!animationFinished && !delete){			
			game.world.destroyBody(body);
		}	
	}

	@Override
	public boolean clicked(Vector2 click) {
		if(circle.contains(click) && !delete){
			return true;
		}else{
			return false;	
		}
	}

	@Override
	public void step(SpriteBatch batch) {
		if(game.paused){		
			spawnTime = spawnTime + (long)(Gdx.graphics.getDeltaTime()*1000);
			long time = System.nanoTime()/1000000 - spawnTime;
			font.draw(batch, (int)(deathTime - time)/1000 + "", circle.x -length, circle.y + length/2);
		}else{
			long time = System.nanoTime()/1000000 - spawnTime;
			if(time > deathTime){
				delete();
			}
			if(!delete){
				circle.setPosition(body.getPosition().x * Game.Box2World, body.getPosition().y * Game.Box2World);
				font.setColor(color);
				light.setColor(color);
				font.draw(batch, (int)(deathTime - time)/1000 + "", circle.x -length, circle.y + length/2);
			}
		}
	}

	@Override
	public void destroy() {
		light.setActive(false);
		game.game.maths.lights.add(light);
		game.world.destroyBody(body);
		game.options.ballsSpezial--;
	}

	@Override
	public boolean isAnmiationFinished() {
		return animationFinished;
	}
	
	protected Body createBody(Vector2 position, float size){
		BodyDef bubbleDef = new BodyDef();
		bubbleDef.type = BodyType.DynamicBody;
		bubbleDef.fixedRotation = false;
		bubbleDef.awake = true;
		bubbleDef.position.set(position);
		
		Body bubble  = game.world.createBody(bubbleDef);
		
		CircleShape shape = new CircleShape();
		shape.setRadius((float)game.scaleAndroid(size));
		
		FixtureDef bubbleFixDef = new FixtureDef();
		bubbleFixDef.shape = shape;
		bubbleFixDef.friction = 0.1f;
		bubbleFixDef.restitution = 1f;
		bubbleFixDef.density = 1;
		bubbleFixDef.isSensor = false;
		
		bubble.createFixture(bubbleFixDef);
		bubble.setUserData(this);

		return bubble;
	}
	
	public void changeLight(){
		color.set(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1);
	}
}

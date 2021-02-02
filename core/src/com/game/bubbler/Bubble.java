package com.game.bubbler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import box2dLight.PointLight;

public class Bubble implements Ball {
	
	public boolean isMouse;
	
	public Object userData;
	
	protected Color color;
	
	protected PointLight light;
	public Body body;
	public Circle circle;	
	protected BitmapFont font;
	
	public double solution;
	public String text;
	public boolean isEx;
	protected float size;
	
	public boolean delete;
	public boolean animationFinished;
	public Bubble parent;
	
	protected Vector2 deathEnd;
	protected Vector2 deathStart;
	protected boolean negative;
	protected float interpolator;
	
	protected Array<Bubble> siblings;
	protected Bubble main;
	
	protected boolean decoration;
	protected boolean drawFont;
	
	protected long born;
	protected long points;
	protected long bonus;
	protected double factor;
	protected Game game;
	
	public Bubble(Vector3 position, PointLight light, boolean isMouse, Game game){
		this.game = game;
		int size = (int) game.scaleAndroid(MathUtils.random(25, 100));
		circle = new Circle(position.x, position.y, (float) game.scaleAndroid(size));
		body = createBody(new Vector2(position.x, position.y), size * Game.World2Box);

		this.light = light;
		changeLight();
		this.light.setDistance((float) game.scaleAndroid(size * Game.World2Box * 3.5f));
		this.light.setIgnoreAttachedBody(true);
		this.light.attachToBody(body);
		this.light.setActive(true);
		
		this.decoration = true;
		this.isMouse = isMouse;
		
		born = System.nanoTime();
	}
	
	public Bubble(Vector2 position, PointLight light, Game game){
		this.game = game;
		int size = (int) game.scaleAndroid(MathUtils.random(25, 100));
		circle = new Circle(position, size);
		body = createBody(new Vector2(position).scl(Game.World2Box), size * Game.World2Box);

		this.light = light;
		changeLight();
		this.light.setDistance((float) game.scaleAndroid(size * Game.World2Box * 3.5f));
		this.light.setIgnoreAttachedBody(true);
		this.light.attachToBody(body);
		this.light.setActive(true);
		
		this.decoration = true;
		born = System.nanoTime();
	}
	
	public Bubble(Vector2 position, Color color, double number, Bubble parent, PointLight light, BitmapFont font, Vector2 death, Bubble main, double factor, Game game){
		this.game = game;
		this.font = font;
		this.font.setColor(color);
		
		siblings = new Array<Bubble>();		
		
		if(number - (int)number > 0){
			this.text = " " + Double.toString(number) + " ";
		}else{
			this.text = " " + Integer.toString((int) number) + " ";		
		}	
		
		float base = (int) (text.length() * font.getCapHeight());
		//size = MathUtils.randomTriangular(base/3, base/2);
		size = base/3;
		
		body = createBody(new Vector2(position).scl(Game.World2Box), size * Game.World2Box);
		circle = new Circle(position, size);
		
		this.light = light;
		this.light.setColor(color);
		this.light.setDistance(size * Game.World2Box*2.5f);
		this.light.setIgnoreAttachedBody(true);
		this.light.attachToBody(body);
		this.light.setActive(true);
		
		this.isEx = false;
		this.parent = parent;
		this.solution = number; 
		this.deathEnd = new Vector2(death).scl(Game.World2Box);
		this.main = main;
		this.drawFont = true;
		this.color = color;
		born = System.nanoTime();
		this.factor = factor;
	}
	
	public Bubble(Vector2 position, Color color, String excercise, double solution, PointLight light, BitmapFont font, Vector2 death, Bubble main, double factor, long points, Game game){
		this.game = game;
		this.font = font;
		this.font.setColor(color);

		siblings = new Array<Bubble>();
		
		float base = (int) (excercise.length() * font.getCapHeight());
		size = base/2.9f;
		
		this.text = excercise;
		body = createBody(new Vector2(position).scl(Game.World2Box), size * Game.World2Box);
		circle = new Circle(position, size * Game.World2Box);
		
		this.light = light;
		this.light.setColor(color);
		this.light.setDistance((float) game.scaleAndroid(size * Game.World2Box*2.5f));
		this.light.setIgnoreAttachedBody(true);
		this.light.attachToBody(body);
		this.light.setActive(true);
		
		this.isEx = true;
		this.solution = solution;	
		this.deathEnd = new Vector2(death).scl(Game.World2Box);
		this.main = main;	
		this.drawFont = true;
		this.color = color;
		born = System.nanoTime();
		this.factor = factor;
		this.points = points;
	}
	
	public Ball changeLight(){
		light.setColor(new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1));
		return this;
	}
	
	public void setDynamic(){
		body.setType(BodyType.DynamicBody);
	}
	
	public void addSibling(Bubble bubble){
		siblings.add(bubble);
	}
	
	@Override
	public void checkSolution(){
		delete = true;
		//correct solution
		if(!isEx && parent != null){
			Gdx.input.vibrate(50);
			game.options.addCurrentSolves(1);
			game.options.changed = true;
			parent.deleteA();
			for(Bubble b : siblings){
				b.deleteA();
			}
			deleteA();	
			//wrong solution
		}else if(!isEx && parent == null){
			game.options.addCurrentSolves(-1 - game.options.level);
			Gdx.input.vibrate(new long[] { 0, 50, 150, 200}, -1); 
			deleteB();
		}
	}
	
	public void deleteA(){
		//System.out.println("A");
		if(isEx){
			game.options.posCombo++;
			game.options.activeEX--;
			game.options.solvesCorrect++;
			//System.out.println(1d/(double)((System.nanoTime() - born)/1000000l) * game.options.meanSpeed * (1 + factor));
			double temp = (double)((1d/(double)(((System.nanoTime() - born)/1000000))) * (double)(game.options.meanSpeed));
			double bonusFactor = temp* (double)(1 + game.options.combo/10);
			bonus = (long)(bonusFactor * (double)points * (1 + game.options.level));
			game.options.addPoints(points + bonus);
			game.options.changeSpeed(-250);
		}
		game.world.destroyBody(body);
		drawFont = false;
		light.attachToBody(null);
		delete = true;
		animationFinished = false;
		deathStart = new Vector2(circle.x, circle.y).scl(Game.World2Box);
	}
	
	public void deleteB(){
		//System.out.println("B");
		game.world.destroyBody(body);
		drawFont = false;
		if(!isEx){
			game.options.posCombo = 0;
			game.options.solvesFalse++;
			bonus = (long)((solution + solution * game.options.negCombo) * factor);
			//System.out.println(bonus);
			points = (long)(solution * factor);
			game.options.addPoints(- points - bonus);
			game.options.negCombo++;
		}
		light.attachToBody(null);
		delete = true;
		animationFinished = false;
		deathStart = new Vector2(circle.x, circle.y).scl(Game.World2Box);
		negative = true;
		main.removeSibling(this);
	}
	
	@Override
	public void delete(){
		if(game.options.inGame) {
			light.setActive(false);
			game.game.maths.lights.add(light);
			if(!animationFinished && !delete){			
				game.world.destroyBody(body);
			}	
		}else{
			delete = true;
			animationFinished = true;
		}
	}
	
	public void removeSibling(Bubble bubble){
		siblings.removeValue(bubble, false);
	}
	
	@Override
	public boolean clicked(Vector2 click){
		if(circle.contains(click) && !isEx && !delete){
			return true;
		}else{
			return false;	
		}
	}

	protected Body createBody(Vector2 position, float size){
		BodyDef bubbleDef = new BodyDef();
		bubbleDef.type = BodyType.DynamicBody;
		bubbleDef.fixedRotation = false;
		bubbleDef.awake = true;
		bubbleDef.position.set(position);
		
		Body bubble  = game.world.createBody(bubbleDef);
		
		CircleShape shape = new CircleShape();
		shape.setRadius((float) game.scaleAndroid(size));
		//shape.setPosition(new Vector2(0, 0));
		
		FixtureDef bubbleFixDef = new FixtureDef();
		bubbleFixDef.shape = shape;
		bubbleFixDef.friction = 0.1f;
		bubbleFixDef.restitution = 0.1f;
		bubbleFixDef.density = 1;
		bubbleFixDef.isSensor = false;
		
		bubble.createFixture(bubbleFixDef);
		shape.dispose();
		return bubble;
	}
	
	@Override
	public void step(SpriteBatch batch){	
		if(!delete && !decoration){
			circle.setPosition(body.getPosition().x * Game.Box2World, body.getPosition().y * Game.Box2World);
			if(drawFont){
				font.setColor(color);
				font.draw(batch, text, body.getPosition().x* Game.Box2World  -size/2, body.getPosition().y * Game.Box2World +font.getCapHeight()/2, size, Align.center, false);
			}
			//label.setPosition(body.getPosition().x * Parameters.Box2World + Gdx.graphics.getWidth()/2 - label.getWidth()/2, body.getPosition().y * Parameters.Box2World + Gdx.graphics.getHeight()/2 - label.getHeight()/2);
		}else if(!animationFinished && !decoration){
			if(!negative){
				deathAnimationA(batch);
			}else{
				deathAnimationB(batch);
			}	
		}else{
			if(!delete){
				circle.setPosition(body.getPosition().x * Game.Box2World, body.getPosition().y * Game.Box2World);		
			}
		}
	}
	
	protected void deathAnimationA(SpriteBatch batch){
		if(isEx){
			if(interpolator > 1f && interpolator < 3f){
				font.setColor(Color.GREEN);
				font.draw(batch, points + "+ (Bonus: " + bonus + ")", deathStart.x* Game.Box2World - (float)game.scaleAndroid(90), (deathStart.y + (float)game.scaleAndroid(interpolator)*0.5f)*Game.Box2World, (float)game.scaleAndroid(80  + 20*(game.options.combo/0.5f)), Align.left, false);
			}
		}
		if(interpolator <= 1f + 0.05f){
			light.setPosition(deathStart.x + Interpolation.circle.apply(0, deathEnd.x - deathStart.x, interpolator), deathStart.y + Interpolation.circle.apply(0, deathEnd.y - deathStart.y, interpolator));
			if(!game.paused){
				interpolator += 0.05f;
			}
		}else if(interpolator <= 1.3f){
			light.setColor(Color.WHITE);
			light.setDistance(light.getDistance() + 0.25f);
			if(!game.paused){
				interpolator += 0.05f;
			}
		}else if(interpolator > 3f){
			if(isEx){
			animationFinished = true;
			}
		}
		if(!game.paused){
			interpolator += 0.05f;
		}
	}
	
	protected void deathAnimationB(SpriteBatch batch){
		if(interpolator > 1f && interpolator < 3f){
			font.setColor(Color.RED);
			font.draw(batch, solution + " + (Bonus: " + bonus + ")", deathStart.x* Game.Box2World - (float)game.scaleAndroid(90), (deathStart.y + (float)game.scaleAndroid(interpolator)*0.5f)*Game.Box2World, (float)game.scaleAndroid(80), Align.left, false);
		}
		if(interpolator <= 1f + 0.05f){
			light.setPosition(deathStart.x + Interpolation.circle.apply(0, deathEnd.x - deathStart.x, interpolator), deathStart.y + Interpolation.circle.apply(0, deathEnd.y - deathStart.y, interpolator));			
		}else if(interpolator <= 1.3f){
			light.setColor(Color.RED);
			light.setDistance(light.getDistance() + 0.25f);
		}else if(interpolator > 3f){
			animationFinished = true;
		}
		if(!game.paused){
			interpolator += 0.05f;
		}
	}
	
	@Override
	public void destroy(){
		if(decoration){
			light.attachToBody(null);
			light.setActive(false);
			game.menu.lights.add(light);
			game.world.destroyBody(body);		
		}else{
			light.setActive(false);
			game.game.maths.lights.add(light);
			//game.world.destroyBody(body);
		}

	}

	@Override
	public boolean isAnmiationFinished() {
		return animationFinished;
	}
}
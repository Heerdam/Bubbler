package com.game.bubbler;

import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.utils.Array;

public class MathsProcessor {

	public Array<PointLight> lights;
	public Array<Ball> bubbles;
	
	protected BitmapFont baseSmall;
	protected BitmapFont baseBig;

	public Vector2 death;
	
	public Game game;
	
	public void reset(){
		//System.out.println(bubbles.size);
		game.options.start.clear();
		game.options.createStartMessage();
	}
	
	public MathsProcessor(Game game){
		this.game = game;
		bubbles = new Array<Ball>();
		death = new Vector2().set(0, 0);
		baseSmall = requestFont(Color.WHITE, (int)game.scaleAndroid(45));
		baseBig = requestFont(Color.WHITE, (int)game.scaleAndroid(60));
		
		lights = new Array<PointLight>();
		
		createBorder();
	}
	
	public void addSystems(){
		game.engine.addSystem(new RenderSystem(game));
		game.engine.addSystem(new UiSystemGame(game));
		game.engine.addSystem(new ScreenMessageSystem(game));
	}
		
	protected PointLight getLight(){
		if(game.menu.lights.size > 0){
			return game.menu.lights.pop();
		}else{
			return new PointLight(game.ray, 12);
		}
	}
	
	public void createExcercise(){
		Results temp = OperationsPicker.resultBuilder(OperationsPicker.pickOperations(game.options), game.options);
		Color color = new Color(MathUtils.random(0.1f, 1f), MathUtils.random(0.1f, 1f), MathUtils.random(0.1f, 1f), 1);	
		Bubble parent = new Bubble(new Vector2(game.camera.position.x + MathUtils.random(-100, 100), game.camera.position.y + MathUtils.random(-100, 100)), color, temp.string, temp.result, getLight(), baseBig, death, null, temp.factor, temp.points, game);		
		Bubble main = new Bubble(new Vector2(game.camera.position.x + MathUtils.random(-100, 100), game.camera.position.y + MathUtils.random(-100, 100)), color, parent.solution, parent, getLight(), baseSmall, death, null, temp.factor, game);				
		for(int i = 0; i < MathUtils.random(3, 8); i++){
			int neg = MathUtils.randomSign();
			Bubble b = new Bubble(new Vector2(game.camera.position.x + MathUtils.random(-100, 100), game.camera.position.y + MathUtils.random(-100, 100)), color, parent.solution + MathUtils.random(1, 6)*neg, null, getLight(), baseSmall, death, main, temp.factor, game);
			main.addSibling(b);
			bubbles.add(b);
		}
		bubbles.add(parent);
		bubbles.add(main);
		if(MathUtils.randomBoolean(0.001f)){
			spawnSpezial();
		}
	}
	
	public void spawnSpezial(){
		bubbles.add(new BubbleSpezial(new Vector2(game.camera.position.x + MathUtils.random(-100, 100), game.camera.position.y + MathUtils.random(-100, 100)), getLight(), 4f, baseBig, game));
	}
	
	public BitmapFont requestFont(Color color, int size){
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("arial.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = (int) game.scaleAndroid(size);
		parameter.color = color;
		parameter.borderWidth = (float)game.scaleAndroid(1);
		parameter.borderStraight = false;
		parameter.borderColor = Color.DARK_GRAY;
		BitmapFont temp = generator.generateFont(parameter);
		generator.dispose();
		return temp;
	}
	
	protected void createBorder(){
		BodyDef bordereDef = new BodyDef();
		bordereDef.type = BodyType.StaticBody;
		bordereDef.position.set(0, 0);
		
		Body border = game.world.createBody(bordereDef);
		
		ChainShape shape = new ChainShape();
		shape.createChain(new float[]{
				-Gdx.graphics.getWidth()/2 * Game.World2Box, -Gdx.graphics.getHeight()/2 * Game.World2Box,
				Gdx.graphics.getWidth()/2 * Game.World2Box, -Gdx.graphics.getHeight()/2 * Game.World2Box,
				Gdx.graphics.getWidth()/2 * Game.World2Box, (float) ((Gdx.graphics.getHeight()/2 - game.scaleAndroid(70)) * Game.World2Box),
				-Gdx.graphics.getWidth()/2 * Game.World2Box, (float) ((Gdx.graphics.getHeight()/2 - game.scaleAndroid(70)) * Game.World2Box),
				-Gdx.graphics.getWidth()/2 * Game.World2Box, -Gdx.graphics.getHeight()/2 * Game.World2Box,
		});
		
		FixtureDef bubbleFixDef = new FixtureDef();
		bubbleFixDef.shape = shape;
		bubbleFixDef.friction = 1f;
		bubbleFixDef.restitution = 0.1f;
		bubbleFixDef.density = 1;
		bubbleFixDef.isSensor = false;
		
		border.createFixture(bubbleFixDef);
	}
	
	public void dispose(){
		lights.clear();
		bubbles.clear();	
	}
}
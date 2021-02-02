package com.game.bubbler;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

public class Game extends ApplicationAdapter{
	
	public Engine engine;
	//public ShapeRenderer shape;
	public SpriteBatch batch;
	public OrthographicCamera camera;
	public OrthographicCamera cameraBox;
	public World world;
	//public Box2DDebugRenderer debug;
	public Stage stage;
	public RayHandler ray;
	public InputMultiplexer multiplexer;
	
	public GameScreen game;
	public MenuScreen menu;
	
	public static final float Box2World = 25;
	public static final float World2Box = 0.04f;
	
	public boolean paused;
	public boolean clicked;
	public boolean wasPaused;
	
	public boolean hasCompass;
	protected static double scale;
	
	public Sprite splash;
	public Sprite bg;
	public boolean loaded;
	public long splashTime;
	
	public Options options;
	public boolean exit;
	
	protected boolean adsLoaded;
	protected boolean firstLoad;
	
	public boolean background;
	public Array<ErrorMessage> errors;
	
	public static IActivityRequestHandler adControler;

    public Game(IActivityRequestHandler handler) {
    	adControler = handler;
    }
    
	@Override
	public void create () {
		options = new Options(this);
		if(options.firstLoad){
			firstLoad = true;
			options.firstLoad = false;
			splashTime = System.nanoTime()/1000000;
			splash = new Sprite(new Texture(Gdx.files.internal("splash.png")));
			splash.setBounds(-Gdx.graphics.getWidth()/2, -Gdx.graphics.getHeight()/2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
		bg = new Sprite(new Texture(Gdx.files.internal("bg.png")));
		bg.setBounds(-Gdx.graphics.getWidth()/2, -Gdx.graphics.getHeight()/2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		if(Gdx.graphics.getHeight() < Gdx.graphics.getWidth()){
			scale = 1;
			scale = scale / 800;
			scale *= Gdx.graphics.getHeight();
		}else{
			scale = 1;
			scale = scale / 800;
			scale *= Gdx.graphics.getWidth();
		}		
		hasCompass = Gdx.input.isPeripheralAvailable(Peripheral.Compass);	
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0, 0, 0);		
		cameraBox = new OrthographicCamera();
		cameraBox.setToOrtho(false, camera.viewportWidth*World2Box, camera.viewportHeight*World2Box);
		cameraBox.position.set(0, 0, 0);
		batch = new SpriteBatch();
		stage = new Stage();	
		errors = new Array<ErrorMessage>();
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(stage);		
		Gdx.input.setInputProcessor(multiplexer);
		world = new World(new Vector2(0, 0), true);
		ray = new RayHandler(world);	
		engine = new Engine();
		//debug = new Box2DDebugRenderer();
		menu = new MenuScreen(this);
	}
	
	@Override
	public void pause () {
		paused = true;
	}

	@Override
	public void resume () {
		paused = false;
	}

	public void startGame(Table gameMenu){
		game = new GameScreen(this);
		game.create(gameMenu);	
		game.maths.addSystems();
		game.loaded();	
	}
	
	@Override
	public void render () {	
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		if(loaded && adsLoaded){
			
			if(errors.size > 0){
				errors.reverse();
				for(ErrorMessage e : errors){
					stage.addActor(UiElement.createErrorWindow(e.message, e.exit, this));
				}
				errors.clear();
			}
			
			cameraBox.update();
			ray.setCombinedMatrix(cameraBox);
			
			ray.updateAndRender();

			engine.update(Gdx.graphics.getDeltaTime());
			if(background){
				renderBackground();
			}
			stage.act();
			stage.draw();
			//debug.render(world, cameraBox.combined);
			if(!paused && !options.gameOver){
				world.step(1/60f, 6, 2);
			}	
		}else if((System.nanoTime()/1000000 - splashTime) < 3500 && firstLoad){
			batch.begin();
			splash.draw(batch);
			batch.end();
		}else{
			adsLoaded = true;
		}
	}
	
	public void renderBackground(){
		batch.begin();
		bg.draw(batch);
		batch.end();
	}
	
	@Override
	public void dispose(){
		if(!options.inGame){
			adActions(Ads.destroy);
		}
		world.dispose();
		batch.dispose();
		stage.dispose();
		ray.dispose();
	}
	
	public double scaleAndroid(double input){
		return input * scale * options.zoom;
	}
	
	public static void adActions(Ads action){
		if(adControler != null){
			adControler.showAds(action);
		}
	}
	
	public static interface IActivityRequestHandler {
		   public void showAds(Ads action);
		}
	
	public static enum Ads{
		show, hide, destroy, help
	}
}

package com.game.bubbler;

import java.util.Iterator;

import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.game.bubbler.Game.Ads;
import com.game.bubbler.Options.RunnableInterface;

public class MenuScreen implements InputProcessor, GestureListener{

	protected TextButton options;
	protected TextButton exit;
	public TextButton resume;
	public TextButton restart;
	public TextButton help;
	protected Table optionsT;
	
	protected TextButton start;
	protected TextButton highscore;

	protected Vector3 position;
	public boolean clicked;
	
	public Polygon viewport;
	public Array<Bubble> bubbles;
	public Array<PointLight> lights;
	public Array<Button> buttons;
	
	Table gameMenu;
	public Table menuMenu;
	public boolean gameStarted;
	
	public Highscore high;
	
	public Game game;

	public MenuScreen(Game game){
		this.game = game;
		Gdx.input.setCursorImage(new Pixmap(Gdx.files.internal("normal.png")), 0, 0);	
		game.world.setGravity(new Vector2(0, -3));
		game.multiplexer.addProcessor(this);
		bubbles = new Array<Bubble>();
		lights = new Array<PointLight>();
		buttons = new Array<Button>();
		position = new Vector3();
		
		viewport = new Polygon();	
		viewport.setVertices(new float[]{
				-Gdx.graphics.getWidth()/2*2f, -Gdx.graphics.getHeight()/2 * 2f,
				Gdx.graphics.getWidth()/2*2f, -Gdx.graphics.getHeight()/2 * 2f,
				Gdx.graphics.getWidth()/2*2f, Gdx.graphics.getHeight()/2 * 2f,
				-Gdx.graphics.getWidth()/2*2f, Gdx.graphics.getHeight()/2 * 2f,
		});
		viewport.setPosition(game.camera.position.x, game.camera.position.y);
		loadMenu();
		high = new Highscore(game);
		game.engine.addSystem(new UiSystemMenu(this, game));
		game.loaded = true;
	}
	
	protected synchronized void loadMenu(){
		int width = (int)game.scaleAndroid(400);
		int height = (int)game.scaleAndroid(100);
	
		TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
		style.font = UiElement.requestFont(Color.LIGHT_GRAY, (int)game.scaleAndroid(50), game);
		style.up = UiElement.createDrawble(400, 100, UiElement.background, 5, UiElement.backgroundBorder, true);
		style.checkedOverFontColor = Color.LIGHT_GRAY;
		style.overFontColor = Color.WHITE;
		style.fontColor = Color.LIGHT_GRAY;	
		style.disabledFontColor = Color.DARK_GRAY;
				
		gameMenu = new Table();
		gameMenu.setWidth(width);
		
		menuMenu = new Table();
		menuMenu.setWidth(width);
		//table.debugAll();
		
		resume = new TextButton("Resume", style);
		resume.setSize(width, height);
		resume.setVisible(false);
		resume.setName("resume");
		
		restart = new TextButton("Restart", style);
		restart.setSize(width, height);
		restart.setVisible(false);
		restart.setName("restart");
		
		start = new TextButton("Start", style);
		start.setSize(width, height);
		start.setVisible(true);
		start.addListener(new ClickListener(){
			
			@Override
			public void clicked (InputEvent event, float x, float y) {
				game.options.reset();
				Gdx.input.vibrate(50);
				options.remove();
				exit.remove();
				Game.adActions(Ads.hide);
				int padding = (int)game.scaleAndroid(20);
				gameMenu.add(resume).padBottom(padding).row();
				gameMenu.add(restart).padBottom(padding).row();
				gameMenu.add(highscore).padBottom(padding).row();
				gameMenu.add(options).padBottom(padding).row();
				gameMenu.add(exit).row();
				gameMenu.pack();
				gameMenu.setPosition(Gdx.graphics.getWidth()/2 - gameMenu.getWidth()/2, Gdx.graphics.getHeight()/2 - gameMenu.getHeight()/2);		
				menuMenu.remove();
				game.startGame(gameMenu);
				gameStarted = true;
			}
			
		});
		
		highscore = new TextButton("Highscore", style);
		highscore.setSize(width, height);
		highscore.setVisible(true);
		highscore.addListener(new ClickListener(){
			
			@Override
			public void clicked (InputEvent event, float x, float y) {
				//if(!Options.inGame){
				//	Game.adActions(Ads.hide);
				//}
				Gdx.input.vibrate(50);
				if(game.options.inGame){
					gameMenu.remove();
				}else{
					//Game.adActions(Ads.hide);
					menuMenu.remove();
				}
				game.engine.addSystem(high);
			}
			
		});
		
		help = new TextButton("Help", style);
		help.setSize(width, height);
		help.addListener(new ClickListener(){
			
			@Override
			public void clicked (InputEvent event, float x, float y) {
				Gdx.input.vibrate(125);
				Game.adActions(Ads.help);
			}
			
		});
		
		options = new TextButton("Options", style);
		options.setSize(width, height);
		options.setVisible(true);
		options.addListener(new ClickListener(){
			
			@Override
			public void clicked (InputEvent event, float x, float y) {
				Gdx.input.vibrate(50);
				if(optionsT == null){
					createOptions();
				}
				if(game.options.inGame){
					game.background = true;
					gameMenu.remove();
				}else{
					//Game.adActions(Ads.hide);
					menuMenu.remove();
				}
				
				if(buttons.get(0).isChecked() != game.options.addition){
					buttons.get(0).toggle();
				}
				if(buttons.get(1).isChecked() != game.options.subtraction){
					buttons.get(1).toggle();
				}
				if(buttons.get(2).isChecked() != game.options.multiplication){
					buttons.get(2).toggle();
				}
				if(buttons.get(3).isChecked() != game.options.division){
					buttons.get(3).toggle();
				}
				optionsT.setVisible(true);
				event.getStage().addActor(optionsT);
			}
			
		});
		
		exit = new TextButton("Exit", style);
		exit.setSize(width, height);
		exit.setVisible(true);
		exit.addListener(new ClickListener(){
			
			@Override
			public void clicked (InputEvent event, float x, float y) {
				Gdx.input.vibrate(50);
				game.options.writeFile();
				Game.adActions(Ads.destroy);
				game.exit = true;
				Gdx.app.exit();
			}
			
		});

		int padding = (int)game.scaleAndroid(20);
		menuMenu.add(start).padBottom(padding).row();
		menuMenu.add(help).padBottom(padding).row();
		menuMenu.add(highscore).padBottom(padding).row();
		menuMenu.add(options).padBottom(padding).row();
		menuMenu.add(exit).row();
		menuMenu.pack();
		menuMenu.setPosition(Gdx.graphics.getWidth()/2 - menuMenu.getWidth()/2, Gdx.graphics.getHeight()/2 - menuMenu.getHeight()/2);
		menuMenu.setVisible(true);
		game.stage.addActor(menuMenu);
		game.loaded = true;
	}
	
	protected void createOptions(){	
		int width = (int)game.scaleAndroid(300);
		int height = (int)game.scaleAndroid(160);
		float scale = 1.5f;
		
		Array<clickStruct> operations = new Array<clickStruct>();
		operations.add(new clickStruct("addition", new RunnableInterface(){

			@Override
			public void runUp(UserObject object) {
	
			}

			@Override
			public void runDown(UserObject object) {

			}

			@Override
			public void runBoolean(boolean clicked) {
				game.options.addition = clicked;	
			}
			
		}, game.options.addition));
		operations.add(new clickStruct("subtraction", new RunnableInterface(){

			@Override
			public void runUp(UserObject object) {
	
			}

			@Override
			public void runDown(UserObject object) {

			}

			@Override
			public void runBoolean(boolean clicked) {
				game.options.subtraction = clicked;
			}
			
		}, game.options.subtraction));
		operations.add(new clickStruct("multiplication", new RunnableInterface(){

			@Override
			public void runUp(UserObject object) {
	
			}

			@Override
			public void runDown(UserObject object) {

			}

			@Override
			public void runBoolean(boolean clicked) {
				game.options.multiplication = clicked;
			}
			
		}, game.options.multiplication));
		operations.add(new clickStruct("division", new RunnableInterface(){

			@Override
			public void runUp(UserObject object) {
	
			}

			@Override
			public void runDown(UserObject object) {

			}

			@Override
			public void runBoolean(boolean clicked) {
				game.options.division = clicked;
			}
			
		}, game.options.division));
						
		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = UiElement.requestFont(Color.LIGHT_GRAY, (int)game.scaleAndroid(30), game);
		
		//SpriteDrawable bg = UiElement.createDrawble(width, height, UiElement.background, (int)Game.scaleAndroid(5), UiElement.backgroundBorder, true);
		
		TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
		buttonStyle.checkedOverFontColor = Color.LIGHT_GRAY;
		buttonStyle.overFontColor = Color.WHITE;
		buttonStyle.fontColor = Color.LIGHT_GRAY;	
		buttonStyle.font = UiElement.requestFont(Color.LIGHT_GRAY, (int)game.scaleAndroid(50), game);
		
		Table operation = UiElement.createPicker(width, (int)(height*scale), operations, this, game);
		//operation.debug();

		Table lower = UiElement.constructTableArrow(width, height, game.options.lowerLimit, " - Lower Limit", -1000, game.options.upperLimit, true, new RunnableInterface(){

			@Override
			public void runUp(UserObject object) {
				object.value++;
				object.upperLimit = game.options.upperLimit - 1;
				game.options.lowerLimit = (int) object.value;	
			}

			@Override
			public void runDown(UserObject object) {
				object.value--;
				object.upperLimit = game.options.upperLimit - 1;
				game.options.lowerLimit = (int) object.value;
			}

			@Override
			public void runBoolean(boolean clicked) {
				// TODO Auto-generated method stub
				
			}
			
		}, labelStyle, buttonStyle, game);
		Table factors = UiElement.constructTableArrow(width, height, game.options.factors, " - Factors", 2, 3, true, new RunnableInterface(){

			@Override
			public void runUp(UserObject object) {
				object.value++;
				game.options.factors = (int) object.value;
			}

			@Override
			public void runDown(UserObject object) {
				object.value--;
				game.options.factors = (int) object.value;
			}

			@Override
			public void runBoolean(boolean clicked) {
				// TODO Auto-generated method stub
				
			}
			
		}, labelStyle, buttonStyle, game);
		Table upper = UiElement.constructTableArrow(width, height, game.options.upperLimit, " - Upper Limit", game.options.lowerLimit, 1000, true, new RunnableInterface(){

			@Override
			public void runUp(UserObject object) {
				object.value++;
				object.lowerLimit = game.options.lowerLimit + 1;
				game.options.upperLimit = (int) object.value;
			}

			@Override
			public void runDown(UserObject object) {
				object.value--;
				object.lowerLimit = game.options.lowerLimit + 1;
				game.options.upperLimit = (int) object.value;
			}

			@Override
			public void runBoolean(boolean clicked) {
				// TODO Auto-generated method stub
				
			}
			
		}, labelStyle, buttonStyle, game);
		Table zoom = UiElement.constructTableArrow(width, height, game.options.zoom, " - Zoom", 0.5f, 2f, true, new RunnableInterface(){

			@Override
			public void runUp(UserObject object) {
				object.value += 0.1f;
				object.value = (double)((int)(object.value*10))/10;
				object.zoom = game.options.zoom + 0.1f;
				game.options.zoom = object.value;
			}

			@Override
			public void runDown(UserObject object) {
				object.value -= 0.1f;
				object.value = (double)((int)(object.value*10))/10;
				object.zoom = game.options.zoom - 0.1f;
				game.options.zoom = object.value;
			}

			@Override
			public void runBoolean(boolean clicked) {
				// TODO Auto-generated method stub
				
			}
			
		}, labelStyle, buttonStyle, game);
		
		TextButton close = UiElement.createButton((int)game.scaleAndroid(150), (int)game.scaleAndroid(75), "Close", (int)game.scaleAndroid(35), true, game);
		close.addListener(new ClickListener(){
			
			@Override
			public void clicked (InputEvent event, float x, float y) {
				Gdx.input.vibrate(50);
				if(game.options.inGame){
					game.stage.addActor(gameMenu);
					game.background = false;
				}else{
					game.stage.addActor(menuMenu);
				}	
				optionsT.remove();
				game.options.writeFile();
			}
			
		});
		
		Table exitT = new Table();
		exitT.add(close).expand().align(Align.bottomRight).pad((int)game.scaleAndroid(4));		
		//exitT.debug();
		exitT.pack();

		optionsT = new Table();
		//optionsT.debug();
		int space = (int)game.scaleAndroid(4);
		optionsT.add(operation).width(width).height((int)game.scaleAndroid(height*scale)).space(space);
		optionsT.add(zoom).width(width).space(space).row();
		optionsT.add(lower).width(width).height(height).space(space);
		optionsT.add(factors).width(width).height(height).space(space).row();
		optionsT.add(upper).width(width).height(height).space(space);
		optionsT.add(exitT).width(width).height(height).space(space).row();
		
		//optionsT.pack();	
		optionsT.setPosition(Gdx.graphics.getWidth()/2 - optionsT.getWidth()/2, Gdx.graphics.getHeight()/2 - optionsT.getHeight()/2);
	}
	
	
	public void clear(){
		Iterator<Bubble> iterator = bubbles.iterator();
		while(iterator.hasNext()){
			Ball b = iterator.next();	
			b.destroy();
			iterator.remove();
		}
	}
	
	public Array<PointLight> dispose(){
		clear();
		game.world.setGravity(new Vector2(0, -2));
		game.multiplexer.removeProcessor(this);
		bubbles.clear();
		bubbles = null;		
		return lights;
	}
	

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		position.set(game.camera.unproject(position.set(screenX, screenY, 0))).scl(Game.World2Box);
		PointLight light;
		if(game.menu.lights.size < 500){
			light = new PointLight(game.ray, 8);
		}else{
			light = game.menu.lights.pop();
		}
		bubbles.add(new Bubble(position, light, true, game));
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {	
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		position.set(game.camera.unproject(position.set(screenX, screenY, 0))).scl(Game.World2Box);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		position.set(game.camera.unproject(position.set(screenX, screenY, 0))).scl(Game.World2Box);
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public class clickStruct{
		public String input;
		public RunnableInterface run;
		public boolean down;
		public clickStruct(String input, RunnableInterface run, boolean down) {
			this.input = input;
			this.run = run;
			this.down = down;
		}
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}
}

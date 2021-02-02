package com.game.bubbler;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.game.bubbler.Game.Ads;

public class UiSystemGame extends EntitySystem{

	protected Table ui;
	protected Label points;
	protected Slider bar;
	protected Slider sideBar;
	protected TextButton menu;
	protected TextButton resume;
	protected TextButton pauseB;
	protected TextButton plus;
	
	protected Label min;
	protected Label max;

	protected long startTime;
	protected long lastSpawn;
	protected long nextSpawn;

	protected long lastSolve;
	protected boolean firstSpawn;

	protected long heartbeat;
	
	protected boolean removed;
	
	protected long lock;
	protected Game game;
	
	protected Label speedIndicator;
	protected Label level;
	protected String string;

	public UiSystemGame(Game game){
		this.game = game;
	}
	
	@Override
	public void addedToEngine(Engine engine){
		ui = UiElement.createGameUi(game);
		
		for(Actor a : ui.getChildren()){		
			if(a.getName() == "points"){
				points = (Label) a;
			}else if(a.getName() == "difBar"){
				Table temp = (Table) a;
				for(Actor b : temp.getChildren()){
					if(b.getName() == "bar"){
						bar = (Slider) b;
					}	
				}
			}else if(a.getName() == "buttons"){
				Table buttons = (Table) a;
				for(Actor c : buttons.getChildren()){
					if(c.getName() == "menu"){
						menu = (TextButton) c;
					}else if(c.getName() == "pause"){
						pauseB = (TextButton) c;
					}else if(c.getName() == "plus"){
						plus = (TextButton) c;
					}
				}
			}else if(a.getName() == "minMax"){
				Table minMax = (Table) a;
				for(Actor d : minMax.getChildren()){
					if(d.getName() == "min"){
						min = (Label) d;
					}else if(d.getName() == "max"){
						max = (Label) d;
					}
				}
			}else if(a.getName() == "sideBarTable"){
				Table sideBarTable = (Table) a;
				for(Actor d : sideBarTable.getChildren()){
					if(d.getName() == "sideBar"){
						sideBar = (Slider) d;
					}
				}
			}
		}
		
		resume = game.menu.resume;
		
		game.stage.addActor(ui);
		
		resume.addListener(new ClickListener(){
			
			@Override
			public void clicked (InputEvent event, float x, float y) {
				Game.adActions(Ads.hide);
				plus.setTouchable(Touchable.enabled);
				Gdx.input.vibrate(50);
				pauseB.setTouchable(Touchable.enabled);
				menu.setTouchable(Touchable.enabled);
				game.paused = false;
				game.game.showMenu();
			}
			
		});

		plus.addListener(new ClickListener(){
			
			@Override
			public void clicked (InputEvent event, float x, float y) {
				Gdx.input.vibrate(50);
				game.game.maths.createExcercise();
				game.options.addPoints(250 + 50*game.options.level);
				game.options.activeEX++;
			}
			
		});
		plus.setTouchable(Touchable.disabled);
		
		menu.addListener(new ClickListener(){
			
			@Override
			public void clicked (InputEvent event, float x, float y) {
				Game.adActions(Ads.show);
				Gdx.input.vibrate(50);
				plus.setTouchable(Touchable.disabled);
				pauseB.setTouchable(Touchable.disabled);
				menu.setTouchable(Touchable.disabled);
				if(pauseB.isChecked()){
					pauseB.toggle();
				}
				game.paused = true;
				game.game.showMenu();	
			}
			
		});
		
		pauseB.addListener(new ClickListener(){
			
			@Override
			public void clicked (InputEvent event, float x, float y) {
				Gdx.input.vibrate(50);
				if(!game.paused){
					plus.setTouchable(Touchable.disabled);
					game.paused = true;
					game.options.addPoints(-250 - 50*game.options.level);
					game.options.addCurrentSolves(-game.options.level);
					game.options.createStartMessage("Paused", 2000, Color.RED);
				}else if(!game.options.gameOver){
					plus.setTouchable(Touchable.enabled);
					game.paused = false;
				}	
			}
			
		});	
		
		speedIndicator = UiElement.createLabel("---|+++", (int)game.scaleAndroid(40), game);
		Vector2 position = new Vector2().set(bar.localToStageCoordinates(new Vector2(0, 0)));
		speedIndicator.setPosition(position.x, position.y);
		speedIndicator.setWidth(bar.getWidth());
		speedIndicator.setHeight(bar.getHeight());
		speedIndicator.setAlignment(Align.center);
		speedIndicator.setZIndex(10);
		game.stage.addActor(speedIndicator);
		
		position = new Vector2().set(sideBar.localToStageCoordinates(new Vector2(0, 0)));
		level = UiElement.createLabel(game.options.level + "", (int)game.scaleAndroid(40), game);
		level.setPosition(position.x, position.y + sideBar.getHeight() - level.getHeight());
		level.setWidth(sideBar.getWidth());
		level.setAlignment(Align.center);
		game.stage.addActor(level);
		game.options.sideBar = sideBar;
		
		
		heartbeat = System.nanoTime()/1000000;
		createBorder((-plus.getWidth()*1.5f + plus.getX())*Game.World2Box, (-Gdx.graphics.getHeight()/2 + plus.getY())*Game.World2Box, plus.getWidth()*3*Game.World2Box, plus.getHeight()*Game.World2Box);
		createBorder((position.x-Gdx.graphics.getWidth()/2) * Game.World2Box, (position.y-Gdx.graphics.getHeight()/2) * Game.World2Box, sideBar.getWidth() * Game.World2Box, sideBar.getHeight() * Game.World2Box);
		lock = System.nanoTime()/1000000;
		game.options.createStartMessage();
		game.options.inGame = true;
	}
	
	protected void createBorder(float x, float y, float width, float height){
		BodyDef bordereDef = new BodyDef();
		bordereDef.type = BodyType.StaticBody;
		bordereDef.position.set(x, y);
		
		Body border = game.world.createBody(bordereDef);
		
		ChainShape shape = new ChainShape();
		shape.createChain(new float[]{
				0, 0,
				width, 0,
				width, height,
				0, height,
				0, 0
		});
		
		FixtureDef bubbleFixDef = new FixtureDef();
		bubbleFixDef.shape = shape;
		bubbleFixDef.friction = 1f;
		bubbleFixDef.restitution = 0.1f;
		bubbleFixDef.density = 1;
		bubbleFixDef.isSensor = false;
		
		border.createFixture(bubbleFixDef);
	}
	
	public void heartbeat(){
		if(System.nanoTime()/1000000 - heartbeat > 2000 && !game.paused){
			if(game.options.currentSpeed <= game.options.meanSpeed){
				game.options.changeSpeed(25 + game.options.activeEX * 25); 
			}else{
				game.options.changeSpeed(-50 - game.options.activeEX * 25); 
			}
			heartbeat = System.nanoTime()/1000000;
			
			if(game.options.activeEX > 10){
				game.options.addCurrentSolves(-game.options.activeEX-10);
			}
		}
	}

	@Override
	public void update(float deltaTime) {	
		if(!game.options.startText && !game.paused){
			if(System.nanoTime()/1000000 - lock > 2500){
				game.options.startText = true;
			}
		}else if(!game.options.startText && game.paused){
			lock = lock + (long)(Gdx.graphics.getDeltaTime()*1000);
		}
			
		if(game.options.reset){
			for(Ball b : game.game.maths.bubbles){
				b.delete();
			}
			game.game.maths.bubbles.clear();
			game.options.stopThePress = true;
			removed = false;
			game.options.reset = false;
			game.options.startText = false;
			sideBar.setColor(Color.WHITE);
			bar.setColor(Color.WHITE);
			pauseB.setTouchable(Touchable.enabled);
			menu.setTouchable(Touchable.enabled);
			lock = System.nanoTime()/1000000;
		}
		if(game.options.startText && !removed){
			removed = true;
			plus.setTouchable(Touchable.enabled);
		}
		if(removed && !game.options.gameOver){
			if(game.options.getPoints() < 0 || game.options.callGameOver){
				gameOver();
			}
			heartbeat();
			updateDifficulty(deltaTime);
			spawnBubbles(deltaTime);
			levelControl();
					

			if(game.options.getPoints() >= 0){
				points.setColor(Color.GREEN);
			}else{
				points.setColor(Color.RED);
			}
			points.setText("Points: " + game.options.getPoints());	
			//points.setText(Gdx.graphics.getFramesPerSecond() + " fps");
			min.setText(game.options.minPoints + "");
			max.setText("+" + game.options.maxPoints);
		}
	}
		
	protected void gameOver(){
		sideBar.setColor(Color.RED);
		bar.setColor(Color.RED);
		game.options.start.clear();
		game.options.createStartMessage("Game Over!", 2000, Color.RED);
		game.options.gameOver = true;
		pauseB.setTouchable(Touchable.disabled);
		game.menu.resume.setDisabled(true);
		game.options.writeFile();
	}
	
	protected void levelControl(){
		sideBar.setValue(game.options.currentSolves);
		level.setText(game.options.level + "");
	}
	
	protected void updateDifficulty(float deltaTime){	
		if(game.options.changed){
			game.options.getMean(System.nanoTime()/1000000 - lastSolve);
			lastSolve = System.nanoTime()/1000000;
			game.options.changed = false;			
		}
	}

	protected void spawnBubbles(float deltaTime){	
		if(!firstSpawn){
			startTime = System.nanoTime()/1000000;
			lastSpawn = startTime;
			lastSolve = startTime;
			firstSpawn = true;	
			nextSpawn = 2000;
			bar.setRange(0, nextSpawn);
			bar.setValue(nextSpawn);
		}else{		
			if(game.paused){
				lastSpawn =  lastSpawn + (long)(Gdx.graphics.getDeltaTime()*1000);
				//nextSpawn = nextSpawn + (long)(Gdx.graphics.getDeltaTime()*1000);
				lastSolve = lastSolve  + (long)(Gdx.graphics.getDeltaTime()*1000);
			}else{
				bar.setValue(bar.getValue() - deltaTime*1000);
				if(System.nanoTime()/1000000 > (nextSpawn + lastSpawn) || game.options.activeEX == 0){
					game.game.maths.createExcercise();	
					game.options.activeEX++;
					
					lastSpawn = System.nanoTime()/1000000;
					
					nextSpawn = (long)((double)game.options.currentSpeed * (1d + game.options.difficulty));
					//System.out.println(game.options.difficulty + " | " + nextSpawn);
					bar.setRange(0, nextSpawn);
					bar.setValue(nextSpawn);
					long delta = game.options.currentSpeed - game.options.oldSpeed;
					//System.out.println(delta);
					string = "";
					if(delta > 0){
						speedIndicator.setColor(Color.GREEN);
						string = "-";
						if(delta > 0 && delta > 500 && delta < 1000){
							string = "--";
						}else if(delta >= 1000){
							string = "---";
						}
						speedIndicator.setText(string);
					}else if(delta < 0){
						speedIndicator.setColor(Color.RED);
						string = "+";
						if(delta < 0 && delta < -500 && delta > -1000){
							string = "++";
						}else if(delta <= -1000){
							string = "+++";
						}
						speedIndicator.setText(string);
					}else{
						speedIndicator.setColor(Color.WHITE);
						speedIndicator.setText("|");
					}		
				}			
			}
		}
	}
}
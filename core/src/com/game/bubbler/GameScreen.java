package com.game.bubbler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.bubbler.Game.Ads;

public class GameScreen {

	public MathsProcessor maths;	
	public boolean loaded;
	public Table gameMenu;
	public Game game;
	
	public GameScreen(Game game){
		this.game = game;
	}
	
	public void create(Table gameMenu){	
		this.gameMenu = gameMenu;
		game.menu.resume.setVisible(true);
		game.menu.restart.setVisible(true);
		game.menu.restart.addListener(new ClickListener(){
			
			@Override
			public void clicked (InputEvent event, float x, float y) {
				Game.adActions(Ads.hide);
				game.options.writeFile();
				Gdx.input.vibrate(50);
				restart();
			}
			
		});
		
		maths = new MathsProcessor(game);
		maths.lights.addAll(game.menu.dispose()); 
	}

	public void showMenu(){
		if(game.paused){
			game.stage.addActor(gameMenu);
		}else{
			gameMenu.remove();
		}
	}
	
	public void restart(){
		game.menu.resume.setDisabled(false);
		gameMenu.remove();
		game.options.reset = true;
		game.options.reset();
		game.options.gameOver = false;
		maths.reset();
	}
	
	public void dispose(){
		maths.dispose();
	}
	
	public void loaded(){
		loaded = true;
		//Game.load.dispose();
	}
}

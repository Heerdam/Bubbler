package com.game.bubbler;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class Highscore extends EntitySystem{
	
	protected int width;
	protected int height;
	
	protected int gridWidth;
	protected int gridHeight;
	protected int dx;
	protected int dy;
	
	protected Vector2[][] grid;
	
	protected BitmapFont fontSmall;
	protected BitmapFont fontBig;
	
	protected Table window;
	protected Vector2 position;
	
	protected Array<TextColor> head;
	
	protected Game game;
	
	public Highscore(Game game){
		this.game = game;
	}
	
	@Override
	public void addedToEngine (Engine engine) {
		head = new Array<TextColor>();
		head.add(new TextColor("Points", Color.LIGHT_GRAY));
		head.add(new TextColor("Time", Color.LIGHT_GRAY));
		head.add(new TextColor("Solves", Color.LIGHT_GRAY));
		head.add(new TextColor("Level",  Color.LIGHT_GRAY));
		
		dx = (int)game.scaleAndroid(250);
		dy = (int)game.scaleAndroid(-50);
		width = 5*dx;
		height = -14*dy;
		gridWidth = 4;
		
		gridHeight = game.options.highscore.size+1;
		
		createHighscore();
		
		grid = createGrid(gridWidth, gridHeight);

		fontSmall = UiElement.requestFont(Color.WHITE, 25, game);
		fontBig = UiElement.requestFont(Color.WHITE, 45, game);
	}
	
	@Override
	public void update(float deltaTime) {
		game.batch.begin();
		game.bg.draw(game.batch);
		for(int x = 0;  x < gridWidth; x++){
			fontBig.setColor(head.get(x).color);
			fontBig.draw(game.batch, head.get(x).text, grid[0][x].x, grid[0][x].y);
		}
		
		for(int y = 1;  y < gridHeight; y++){
			HighscoreItem temp = game.options.highscore.get(y-1);
			if(temp.lastEntry){
				fontSmall.setColor(Color.YELLOW);
				fontSmall.draw(game.batch, temp.points + "", grid[y][0].x, grid[y][0].y);	
				fontSmall.draw(game.batch, temp.time/1000/60 + " min " + game.options.highscore.get(y-1).time/1000%60 + " s", grid[y][1].x, grid[y][1].y);
				fontSmall.draw(game.batch, temp.solvesCorrect + "/ " + temp.solvesFalse, grid[y][2].x, grid[y][2].y);
				fontSmall.draw(game.batch, temp.level + "", grid[y][3].x, grid[y][3].y);	
			}else{
				fontSmall.setColor(Color.WHITE);
				fontSmall.draw(game.batch, temp.points + "", grid[y][0].x, grid[y][0].y);	
				fontSmall.draw(game.batch, temp.time/1000/60 + " min " + game.options.highscore.get(y-1).time/1000%60 + " s", grid[y][1].x, grid[y][1].y);
				if(temp.solvesCorrect - temp.solvesFalse >= 0){
					fontSmall.setColor(Color.GREEN);
				}else{
					fontSmall.setColor(Color.RED);
				}
				fontSmall.draw(game.batch, temp.solvesCorrect + "/ " + temp.solvesFalse, grid[y][2].x, grid[y][2].y);
				fontSmall.setColor(Color.WHITE);
				fontSmall.draw(game.batch, temp.level + "", grid[y][3].x, grid[y][3].y);
			}
		}
		game.batch.end();
	}

	@Override
	public void removedFromEngine (Engine engine) {
		
	}

	
	protected void createHighscore(){
		window = new Table();
		window.setSize(width, height);
		//window.setBackground(UiElement.createDrawble(width, height, UiElement.background, 4, UiElement.backgroundBorder, true));
		//window.debug();
		
		TextButton exit = UiElement.createButton(150, 75, "Close", 35, true, game);
		exit.addListener(new ClickListener(){
			
			@Override
			public void clicked (InputEvent event, float x, float y) {
				window.remove();
				Gdx.input.vibrate(50);
				//if(!Options.inGame){
				//	Game.adActions(Ads.show);
				//}
				if(game.options.inGame){
					game.stage.addActor(game.game.gameMenu);
				}else{
					game.stage.addActor(game.menu.menuMenu);
				}	
				game.engine.removeSystem(game.engine.getSystem(Highscore.class));
			}
			
		});
		
		window.add(exit).expand().align(Align.bottomRight);
		window.setPosition(Gdx.graphics.getWidth()/2 - window.getWidth()/2, Gdx.graphics.getHeight()/2 - window.getHeight()/2);
		//window.pack();
		position = new Vector2().set(-2*dx, -5*dy);
		game.stage.addActor(window);
	}
	
	protected Vector2[][] createGrid(int width, int height){
		Vector2[][] temp = new Vector2[height][width];
		
		for(int y = 0;  y < gridHeight; y++){
			for(int x = 0;  x < gridWidth; x++){
				temp[y][x] = new Vector2(x*dx, y*dy).add(position);
			}	
		}
		
		return temp;
	}
	
	protected class TextColor{
		protected String text;
		protected Color color;
	protected TextColor(String text, Color color){
		this.text = text;
		this.color = color;
	}
	}
	
}

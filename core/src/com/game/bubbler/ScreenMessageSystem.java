package com.game.bubbler;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class ScreenMessageSystem extends EntitySystem{

	protected Label label;
	protected Label.LabelStyle style;

	protected ScreenMessage currentText;
	
	protected long startTime;
	protected long endTime;
	protected long deltaTime;
	
	protected boolean delta;
	protected boolean finished;
	
	protected Game game;
	
	protected ScreenMessageSystem(Game game){
		this.game = game;
	}
	
	public void addedToEngine(Engine engine) {
		style = new Label.LabelStyle();
		style.font = UiElement.requestFont(Color.WHITE, (int)game.scaleAndroid(60), game);
		
		label = new Label("", style);
		label.setPosition(Gdx.graphics.getWidth()/2 - label.getWidth()/2, Gdx.graphics.getHeight()/2 + label.getHeight()/2);
		finished = true;	
	}
	
	public void update(float deltaTime) {
		if(game.options.start.size > 0 && finished){
			finished = false;
			currentText = game.options.start.first();
			game.options.start.removeIndex(0);
			startTime = System.nanoTime()/1000000;
			endTime = startTime + currentText.time;
			label.setText(currentText.message);
			style.fontColor = currentText.color;
			game.stage.addActor(label);
		}
		if(game.options.stopThePress){
			game.options.stopThePress = false;
			finished = true;
			label.remove();
		}else if(!finished){	
			if(game.paused){
				if(!delta){
					this.deltaTime = endTime - System.nanoTime()/1000000;
					delta = true;
				}else{
					this.endTime = System.nanoTime()/1000000 + this.deltaTime;
				}	
			}			
			if(System.nanoTime()/1000000 < endTime){
				label.setText(currentText.message);
				label.pack();
				label.setPosition(Gdx.graphics.getWidth()/2 - label.getWidth()/2, Gdx.graphics.getHeight()/2 + label.getHeight()/2);
			}else{
				if(game.options.start.size > 0){
					currentText = game.options.start.first();
					game.options.start.removeIndex(0);
					startTime = System.nanoTime()/1000000;
					endTime = startTime + currentText.time;
					style.fontColor = currentText.color;
				}else{
					finished = true;
					label.remove();
				}		
			}	
		}
	}
}

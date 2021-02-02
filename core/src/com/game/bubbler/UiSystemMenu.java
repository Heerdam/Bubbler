package com.game.bubbler;

import java.util.Iterator;

import box2dLight.PointLight;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class UiSystemMenu extends EntitySystem{
	
	protected ImmutableArray<Entity> entities;
	
	protected MenuScreen menu;
	protected Game game;
	protected Iterator<Bubble> iterator;
	protected Vector2 position;
	protected int pos;
	
	protected long lastSpawn;
	
	public UiSystemMenu(MenuScreen menu, Game game){
		this.menu = menu;	
		this.game = game;
	}

	public void addedToEngine(Engine engine) {
		entities = engine.getEntities();
		lastSpawn = System.currentTimeMillis();
		position = new Vector2();
		pos = 1;
	}
	
	public void update(float deltaTime) {
		updateUI();

		if(!menu.gameStarted){
			if(menu.bubbles.size < 100){
				spawnBubble();
			}
			//Game.shape.begin(ShapeType.Line);
			//Game.shape.polygon(menu.viewport.getTransformedVertices());
			game.batch.begin();
			iterator = menu.bubbles.iterator();
			while(iterator.hasNext()){
				Bubble b = iterator.next();	
				if(!menu.viewport.contains(b.circle.x, b.circle.y)){
					b.delete();
				}
				if(b.animationFinished){
					b.destroy();
					iterator.remove();
				}else{
					b.step(game.batch);
				}
				//Game.shape.circle(b.circle.x, b.circle.y, b.circle.radius);
			}
			game.batch.end();
		}
	}
	
	protected void updateUI(){	
		for(Entity e : entities){
			LabelComponent l = Mapper.labels.get(e);
			if(l != null){
				if(l.object.firstRun || (System.currentTimeMillis() - l.object.clickStart) > 250){
					l.object.firstRun = false;
					if(l.object.hasLimits){
						if(l.object.isUp){
							if(l.object.value < l.object.upperLimit){
								//l.object.value++;
								Gdx.input.vibrate(50);
								l.object.run.runUp(l.object);
								l.object.label.setText(l.object.value + "");	
							}	
						}else{
							if(l.object.value > l.object.lowerLimit){
								//l.object.value--;
								Gdx.input.vibrate(50);
								l.object.run.runDown(l.object);
								l.object.label.setText(l.object.value + "");	
							}
						}				
					}else{
						if(l.object.isUp){
							//l.object.value++;
							l.object.run.runUp(l.object);
							l.object.label.setText(l.object.value + "");	
						}else{
							//l.object.value--;
							l.object.run.runDown(l.object);
							l.object.label.setText(l.object.value + "");	
						}	
					}	
				}	
				
			}
		}
	}
	
	protected void spawnBubble(){
		if(System.currentTimeMillis() - lastSpawn > 500 && !game.paused){
			//System.out.println(menu.bubbles.size);
			PointLight light;
			if(menu.lights.size < 500){
				light = new PointLight(game.ray, 15);
			}else{
				light = menu.lights.pop();
			}
			
			position.set(MathUtils.random((float) game.scaleAndroid(50), menu.viewport.getBoundingRectangle().width/2), (float) (menu.viewport.getBoundingRectangle().height/2-game.scaleAndroid(50))).scl(pos, 1);
			menu.bubbles.add(new Bubble(position, light, game));
			lastSpawn = System.currentTimeMillis();
			pos *= -1;
		}
	}
}

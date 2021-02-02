package com.game.bubbler;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.game.bubbler.Options.RunnableInterface;

public class UserObject{
	public Label label;
	public double value;
	public double upperLimit;
	public double lowerLimit;
	public boolean hasLimits;
	public boolean isUp;		
	public long clickStart;
	public boolean firstRun;
	public double zoom;
	
	public Entity entity;
	public LabelComponent comp;
	
	public RunnableInterface run;
	
	public UserObject(double value, Label label){
		this.value = value;
		this.label = label;
	}
}

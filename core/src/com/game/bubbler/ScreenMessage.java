package com.game.bubbler;

import com.badlogic.gdx.graphics.Color;

public class ScreenMessage {

	public String message;
	public long time;
	public Color color;
	
	public ScreenMessage(String message, long time, Color color) {
		this.message = message;
		this.time = time;
		this.color = color;
	}
}

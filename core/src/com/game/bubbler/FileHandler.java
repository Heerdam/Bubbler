package com.game.bubbler;

import java.io.IOException;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class FileHandler {
	
	public Game game;
	public Options options;
	
	protected FileHandle handle;
	
	FileHandler(Game game, Options options){
		this.game = game;
		this.options = options;
		initialize();
	}
	
	public void initialize(){
		handle = Gdx.files.local("preferences.m");
		if(!handle.exists()){
			try {
				handle.file().createNewFile();
				handle.writeString("12;0;2;1.0;true;false;false;false;true%", false);
			} catch (IOException e) {
				game.errors.add(new ErrorMessage(false, "Unable to initialize File! Reason: " + e.getMessage()));
				e.printStackTrace();
			}
		}
	}

	public void load(){
		try{
			String input = handle.readString();
			String[] foo = input.split("%");			
			String[] temp0 = foo[0].split(";");			
			this.options.upperLimit = Integer.parseInt(temp0[0]);
			this.options.lowerLimit = Integer.parseInt(temp0[1]);			
			this.options.factors = Integer.parseInt(temp0[2]);
			this.options.zoom =  Double.parseDouble(temp0[3]);
			this.options.addition = Boolean.parseBoolean(temp0[4]);
			this.options.subtraction = Boolean.parseBoolean(temp0[5]);
			this.options.multiplication = Boolean.parseBoolean(temp0[6]);
			this.options.division = Boolean.parseBoolean(temp0[7]);	
			this.options.firstLoad = Boolean.parseBoolean(temp0[8]);	
			
			options.highscore.clear();
			if(foo.length > 1){	
				String[] temp1 = foo[1].split(";");
				if(temp1.length > 1){
					for(int i = 0; i < temp1.length-5; i += 6){
						this.options.highscore.add(new HighscoreItem(
								(long)Integer.parseInt(temp1[i]), 
								(long)Integer.parseInt(temp1[i + 1]), 
								(long)Integer.parseInt(temp1[i + 2]), 
								(long)Integer.parseInt(temp1[i + 3]), 
								(long)Integer.parseInt(temp1[i + 4]), 
								Boolean.parseBoolean(temp1[i + 5])));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
				game.errors.add(new ErrorMessage(false, "Savegame corrupted. Attempting to fix it..."));
			if(fixFile()){
				game.errors.add(new ErrorMessage(false, "Savegame fixed!"));
			}else{
				game.errors.add(new ErrorMessage(true, "Unable to fix the file. Shutting down..."));
			}
		}
	}
	
	public void write(){
		try{
			if(!options.saved && options.inGame){
				for(HighscoreItem i : options.highscore){
					i.lastEntry = false;
				}
				options.highscore.add(new HighscoreItem(options.maxPoints, (System.nanoTime()/1000000 - options.gameStart), options.solvesCorrect, options.solvesFalse, options.activeLevel, true));
				options.highscore.sort(new HighscoreComperator());
				options.highscore.truncate(10);
				options.saved = true;
			}	
			String temp = "";
			for(HighscoreItem h : options.highscore){
				temp = temp + (h.points + ";" +  h.time + ";" +  h.solvesCorrect + ";" +  h.solvesFalse + ";" + h.level + ";" + h.lastEntry + ";");
			}
			String options = "";
			if(!this.options.addition && !this.options.subtraction && !this.options.multiplication && !this.options.division){
				options = this.options.upperLimit + ";" + this.options.lowerLimit + ";" + this.options.factors + ";" + this.options.zoom + ";" + "true;false;false;false;" + this.options.firstLoad;
			}else{
				options = this.options.upperLimit + ";" + this.options.lowerLimit + ";" + this.options.factors + ";" + this.options.zoom + ";" + this.options.addition + ";" + this.options.subtraction + ";" + this.options.multiplication + ";" +this.options.division + ";" + this.options.firstLoad;
			}
			
			temp = options + "%" + temp;
			handle.writeString(temp, false);
		} catch (Exception e) {
				game.errors.add(new ErrorMessage(false, "Savegame corrupted. Attempting to fix it..."));
			if(fixFile()){
				game.errors.add(new ErrorMessage(false, "Savegame fixed!"));
			}else{
				game.errors.add(new ErrorMessage(true, "Unable to fix the file. Shutting down..."));
			}
		}
	}
	
	protected boolean fixFile(){
		if(!handle.exists()){
			try {
				handle.file().createNewFile();
				handle.writeString("12;0;2;1.0;true;false;false;false;true%", false);
			} catch (IOException e) {
				return false;
			}
			return true;
		}else{
			handle.writeString("12;0;2;1.0;true;false;false;false;true%", false);
			return true;
		}
	}
	
	protected class HighscoreComperator implements Comparator<HighscoreItem>{

		@Override
		public int compare(HighscoreItem o1, HighscoreItem o2) {
			if(o1.points > o2.points){
				return -1;
			}else if(o1.points < o2.points){
				return 1;
			}else{
				return 0;
			}
		}

	}
}

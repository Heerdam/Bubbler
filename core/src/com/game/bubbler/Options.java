package com.game.bubbler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.utils.Array;

public class Options {

	public int upperLimit;
	public int lowerLimit;
	
	public int factors;
	
	public int upperLimitD;
	public int lowerLimitD;
	
	public int factorsD;
	
	public boolean addition;
	public boolean subtraction;
	public boolean multiplication;
	public boolean division;
	
	public boolean additionD;
	public boolean subtractionD;
	public boolean multiplicationD;
	public boolean divisionD;

	public long gameStart;
	public long points;
	public long maxPoints;
	public long minPoints;
	public boolean changed;
	
	public long solvesCorrect;
	public long solvesFalse;
	
	public boolean gameOver;
	public boolean callGameOver;
	
	//read only. use goal for adjusting speed.
	public long currentSpeed;	
	public long nextSpeed;
	public long oldSpeed;
	public long meanSpeed;
	
	public int level;
	public int currentSolves;
	public Slider sideBar;
	
	public float a;
	public boolean inGame;
	
	public int volume;
	public boolean muted;
	
	public boolean startText;
	
	public boolean isSandbox;
	public boolean reset;
	public boolean stopThePress;
	
	public long[] solvesMean;
	public long[] solvesMeanNorm;
	public int solvesMeanLength;
	public int solvesMeanNormLength;
	public int meanIterator;
	public int meanIteratorNorm;
	public double difficulty;
	public int activeLevel;
	
	public int combo;
	
	public int negCombo;
	public int posCombo;
	
	public int activeEX;

	public int ballsSpezial;
	
	public double zoom = 1;
	
	public boolean saved;
	
	public Array<ScreenMessage> start;
	public Array<HighscoreItem> highscore;
	
	public Game game;
	public FileHandler handler;
	
	public boolean firstLoad;
	
	public Options(Game game){
		handler = new FileHandler(game, this);
		this.game = game;
		start = new Array<ScreenMessage>();
		highscore = new Array<HighscoreItem>();
		solvesMeanLength = 5;
		solvesMeanNormLength = 50;
		reset();
	}
	
	public void changeSpeed(long speed){
		nextSpeed += speed;	
	}
	
	public long getMean(long solve){
		negCombo = 0;	
		if(solve <=  currentSpeed){
			combo++;
		}else{
			combo = 0;
		}
		if(solve + nextSpeed >= 10000){
			solvesMean[meanIterator] = 10000;
			solvesMeanNorm[meanIteratorNorm] = 10000;
		}else if(solve + nextSpeed < 250){
			solvesMean[meanIterator] = 250;
			solvesMeanNorm[meanIteratorNorm] = 250;
		}else{
			solvesMean[meanIterator] = solve + nextSpeed;
			solvesMeanNorm[meanIteratorNorm] = solve;
		}
		if(meanIterator < solvesMeanLength-1){
			meanIterator++;
		}else{
			meanIterator = 0;
		}	
		if(meanIteratorNorm < solvesMeanNormLength-1){
			meanIteratorNorm++;
		}else{
			meanIteratorNorm = 0;
		}	
		//System.out.println(nextSpeed + " / " + (oldSpeed - currentSpeed) + " / " + meanSpeed + " / " +  currentSpeed);

		nextSpeed = 0;
		oldSpeed = currentSpeed;
		currentSpeed = 0;
		meanSpeed = 0;
		for(int i = 0; i < solvesMean.length; i++){
			currentSpeed += solvesMean[i];
		} 
		for(int i = 0; i < solvesMeanNorm.length; i++){
			meanSpeed += solvesMeanNorm[i];
		} 
		
		meanSpeed = (long)(meanSpeed  / solvesMeanNorm.length);
		return currentSpeed = (long)(((double)currentSpeed / (double)solvesMean.length));
	}
	
	public void addPoints(long input){
		points += input;
		if(input > 0){
			maxPoints += input;
		}else{
			minPoints += input;
		}
	}
	
	public long getPoints(){
		return points;
	}
	
	public long currentSpeed(){
		return currentSpeed;
	}
	
	public long nextSpeed(){
		return nextSpeed;
	}
	
	public void createStartMessage(String input, long time, Color color){
		start.add(new ScreenMessage(input, time, color));
	}
		
	public void writeFile(){
		handler.write();
	}
	
	public void loadFile(){
		handler.load();
	}
	
	public void calculateInitialDificulty(){	
		difficulty = 0d;
		if(addition){
			difficulty += 1d;
		}
		if(subtraction){
			difficulty += 1.5d;
		}
		if(multiplication){
			difficulty += 2d;
		}
		if(division){
			difficulty += 2.5d;
		}
		difficulty = ((difficulty*(factors-1d)) * ((1d/12d)*(upperLimit - lowerLimit))/10);
	}
	
	public void reset(){
		loadFile();
		
		upperLimitD = upperLimit;
		lowerLimitD = lowerLimit;
		factorsD = factors;
		
		additionD = addition;
		subtractionD = subtraction;
		multiplicationD = multiplication;
		divisionD = division;
		
		currentSpeed = 2000;
		oldSpeed = currentSpeed;
		nextSpeed = 0;
		meanSpeed = currentSpeed;

		points =  0;
		maxPoints = 0;
		minPoints = 0;
				
		activeEX = 0;
		
		gameOver = false;
		game.paused = false;
		callGameOver = false;
		
		
		solvesMean = new long[solvesMeanLength];
		for(int i = 0; i < solvesMeanLength; i++){
			solvesMean[i] = currentSpeed;
		}
		
		solvesMeanNorm = new long[solvesMeanNormLength];
		for(int i = 0; i < solvesMeanNormLength; i++){
			solvesMeanNorm[i] = currentSpeed;
		}
		
		meanIteratorNorm = 0;
		meanIterator = 0;
		
		negCombo = 0;
		
		solvesCorrect = 0;
		solvesFalse = 0;
		ballsSpezial = 0;
		saved = false;
		level = 0;
		currentSolves = 0;
		activeLevel = 0;
		gameStart = System.nanoTime()/1000000;
	}
	
	public void addCurrentSolves(int solve){
		currentSolves += solve;
		if(currentSolves > (15 + level*2)){		
			currentSolves = 0;
			level++;
			sideBar.setRange(0, 15 + level*2);
			addDifficulty();
		}else if(currentSolves < 0){			
			level--;
			sideBar.setRange(0, 15 + level*2);
			if(level < 0){
				callGameOver = true;
			}
			currentSolves = 15 + level*2;
		}
	}
	
	public void changeDificulty(){
		game.game.maths.spawnSpezial();
		addDifficulty();
	}

	public void addDifficulty(){
		switch(level){
			case 1:
				if(level > activeLevel){
					activeLevel++;
					if(additionD && !subtractionD && !multiplicationD && !divisionD){
						createStartMessage("Additions already active! Magic Ball spawned instead!", 2000, Color.BLUE);
						game.game.maths.spawnSpezial();
					}else if(!additionD){
						additionD = true;
						createStartMessage("Additions activated!", 2000, Color.RED);
					}
				}
			break;
			case 2:
				if(level > activeLevel){
					activeLevel++;
					int delta = MathUtils.random(2, 4);
					createStartMessage("Upper limit increased by "+ delta +"!", 2000, Color.RED);
					upperLimitD += delta;
				}
			break;
			case 3:
				if(level > activeLevel){
					activeLevel++;
					createStartMessage("Magic ball spawned!", 2000, Color.MAGENTA);
					game.game.maths.spawnSpezial();
				}
			break;
			case 4:
				if(level > activeLevel){
					activeLevel++;
					int delta2 = -1*MathUtils.random(2, 4);
					createStartMessage("Lower limit increased by "+ delta2 +"!", 2000, Color.RED);
					lowerLimitD += delta2;
				}
			break;
			case 5:
				if(level > activeLevel){
					activeLevel++;
					if(!additionD && subtractionD && !multiplicationD && !divisionD){
						createStartMessage("Subtraction already active! Magic Ball spawned instead!", 2000, Color.BLUE);
						game.game.maths.spawnSpezial();
					}else if(!subtractionD){
						subtractionD = true;
						createStartMessage("Subtraction activated!", 2000, Color.RED);
					}	
				}
			break;
			case 6:
				if(level > activeLevel){
					activeLevel++;
					createStartMessage("Magic ball has spawned!", 2000, Color.MAGENTA);
					game.game.maths.spawnSpezial();
				}
			break;
			case 7:
				if(level > activeLevel){
					activeLevel++;
					if(!additionD && !subtractionD && multiplicationD && !divisionD){
						createStartMessage("Multiplication already active! Magic Ball spawned instead!", 2000, Color.BLUE);
						game.game.maths.spawnSpezial();
					}else if(!multiplicationD){
						multiplicationD = true;
						createStartMessage("Multiplication activated!", 2000, Color.RED);
					}
				}
			break;
			case 8:
				if(level > activeLevel){
					activeLevel++;
					int delta1 = MathUtils.random(2, 4);
					createStartMessage("Upper limit increased by "+ delta1 +"!", 2000, Color.RED);
					upperLimitD += delta1;
				}
			break;
			case 9:
				if(level > activeLevel){
					activeLevel++;
					if(!additionD && !subtractionD && !multiplicationD && divisionD){
						createStartMessage("Division already active! Magic Ball spawned instead!", 2000, Color.BLUE);
						game.game.maths.spawnSpezial();
					}else if(!divisionD){
						divisionD = true;
						createStartMessage("Division activated!", 2000, Color.RED);
					}
				}
			break;
			case 10:
				if(level > activeLevel){
					activeLevel++;
					createStartMessage("Factors increased by 1!", 2000, Color.RED);
					factorsD += 1;
				}
			break;
			
		}
		if(level > 15 && level > activeLevel){
				switch((int)MathUtils.randomTriangular(0, 3, 1)){
				case 0:
					activeLevel++;
					int delta = MathUtils.random(1, 5);
					createStartMessage("Upper limit increased by "+ delta +"!", 2000, Color.RED);
					upperLimitD += delta;
				break;
				case 1:
					activeLevel++;
					createStartMessage("Magic ball has spawned!", 2000, Color.MAGENTA);
					game.game.maths.spawnSpezial();
				break;
				case 2:
					activeLevel++;
					int delta2 = -1*MathUtils.random(1, 5);
					createStartMessage("Lower limit increased by "+ delta2 +"!", 2000, Color.RED);
					lowerLimitD += delta2;
				break;
				case 3:
					Gdx.input.vibrate(50);
					createStartMessage("Extra task spawned!", 2000, Color.RED);
					game.game.maths.createExcercise();
					game.options.activeEX++;
				break;
			}
		}
	}
	
	public void addSpezial(){
		switch((int)MathUtils.random(1)){
		case 0:
			long temp = (long) MathUtils.randomTriangular(-5000, 5000, 2500);
			if(temp > 0){
				createStartMessage(temp + " points awarded!", 2000, Color.GREEN);
				points += temp;	
			}else if(temp < 0 && points*2 > temp){
				createStartMessage(temp + " points lost!", 2000, Color.RED);
				if(points + temp < 0){
					points = 0;
				}else{
					points += temp;	
				}	
			}else{
				createStartMessage("More luck next time!", 2000, Color.BLUE);
			}
			break;
		case 1:
			long temp1 = (long) MathUtils.randomTriangular(-2500, 5000, 2500);
			if(temp1 > 0){
				createStartMessage("Speed decreased!", 2000, Color.GREEN);
				changeSpeed(temp1);
			}else if(temp1 < 0){
				createStartMessage("Speed increased!", 2000, Color.RED);
				changeSpeed(temp1);
			}else{
				createStartMessage("More luck next time!", 2000, Color.BLUE);
			}	
			break;
		}
	}
	
	public interface RunnableInterface{
		public void runUp(UserObject object);
		public void runDown(UserObject object);
		public void runBoolean(boolean clicked);
	}
	
	public void createStartMessage(){
		start.add(new ScreenMessage("3", 750, Color.RED));
		start.add(new ScreenMessage("2", 750, Color.RED));
		start.add(new ScreenMessage("1", 1000, Color.RED));
	}
}
package com.game.bubbler;

public class HighscoreItem{
	public long points;
	public long time;
	public long solvesCorrect;
	public long solvesFalse;
	public long level;
	public boolean lastEntry;
	
	public HighscoreItem (long points, long time, long solvesCorrect, long solvesFalse, long level, boolean lastEntry) {
		this.points = points;
		this.time = time;
		this.solvesCorrect = solvesCorrect;
		this.solvesFalse = solvesFalse;
		this.level = level;
		this.lastEntry = lastEntry;
	}
}
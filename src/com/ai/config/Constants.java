package com.ai.config;

/*Contains all configuration parameters required for the Game*/
public class Constants {

	public static final int MAX_DEPTH = 10;
	public static final int MAX_DEPTH_EASY = 10;
	public static final int MAX_DEPTH_NOVICE = 51;
	public static final int MAX_DEPTH_HARD = 99;
	public static final int MAX_DEPTH_VERY_DIFFICULT = 20;
	public static final String EMPTY_SYMBOL = "x";
	public static final String WHITE_SYMBOL = "w";
	public static final String BLACK_SYMBOL = "b";
	public static final int MAX_UTILITY = 100; //black wins
	public static final int MIN_UTILITY = -100; //white wins
	public static final int DRAW_UTILITY = 0; //Draw
	public static final int CUTOFF_TIME = 10; //in ten seconds
}

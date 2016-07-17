package com.ai.model;

import java.util.List;

import com.ai.config.Constants;

/*The game class is who manages play for Human and CPU*/
/*By doing this we can have multiple games running on server as long as there is enough CPU Horse power to crank the alpha beta search:)*/
public class Game 
{
	private Master master;
	private Board board;
	private static int totalDepth =Constants.MAX_DEPTH_HARD;
	
	/*Game constructor*/
	public Game(int size, int diff)
	{
		if(diff == 0)
			totalDepth = Constants.MAX_DEPTH_EASY;
		else if(diff==1)
			totalDepth = Constants.MAX_DEPTH_NOVICE;
		else
			totalDepth = Constants.MAX_DEPTH_HARD;
		board = new Board(size);
		master = new Master(totalDepth,size);
	}
	
	/*API that starts the AI*/
	public boolean cpuPlay()
	{
		boolean ret = false;
		if(getTurn() == board.getCPUPlayer())
		{
			Move move =  master.alphaBetaSearch(board);
			if(move != null)
				ret = board.makeMove(move.src, move.dest);
		}
		System.out.println(board.dumpCurrentBoard());
		return ret;
	}
	
	public PlayerType getTurn()
	{
		return board.getCurrentTurn();
	}
	
	/*API to make a move on board*/
	public boolean makeMove(Point src, Point dest)
	{
		boolean ret= false;
		ret=  board.makeMove(src, dest);
		System.out.println(board.dumpCurrentBoard());
		return ret;
	}
	
	public void initializeHumanPlayerType (PlayerType humanPlayerType)
	{
		System.out.println("Game human is "+humanPlayerType.name());
		board.initializeHumanPlayer(humanPlayerType);
	}
	
	public int getSize()
	{
		return board.getSize();
	}
	
	public PlayerType getCPUPlayer()
	{
		return board.getCPUPlayer();
	}
	
	public List<Point> getLegalMovesOfHuman(Point p)
	{
		return board.generateAllPossibleMoves(p);
	}
	
	public PlayerType getHumanPlayer()
	{
		return board.getHumanPlayer();
	}
	public void printBoard()
	{
		System.out.println(board);
	}
	
	public String getJSONString()
	{
		return board.toJsonString();
	}
	
	/*Returns 1 if CPU won or 0 if human won and 2 if draw*/
	public int whoWon()
	{
		int ret = 2; //2 means no one won
		boolean isHumanGame = master.isGame(board.getHumanPlayer(),board);
		boolean isCPUGame = master.isGame(board.getCPUPlayer(), board);
		if(isHumanGame && isCPUGame)
		{
			System.out.println("CLASH!!");
			if(board.getCurrentTurn() == board.getHumanPlayer())
				ret = 1;//it means CPU made the move before
			else if(board.getCurrentTurn() == board.getCPUPlayer())
				ret = 0;
		}
		else 
		{
			if(isHumanGame)
			{
				ret = 0;
				System.out.println("HUman won !");
			}
			else if(isCPUGame)
			{
				ret=1;
				System.out.println("CPU won !!");
			}
		}
		if(board.getCount(board.getHumanPlayer()) == 0)
			ret = 1;//means cpu won
		else if (board.getCount(board.getCPUPlayer()) == 0)
			ret = 0;//means human won
		
		if(board.getCount(board.getHumanPlayer()) == 1)
			ret = 0; //means human won
		else if(board.getCount(board.getCPUPlayer()) == 1)
			ret = 1; //means cpu won 
		return ret;
	}
	
}

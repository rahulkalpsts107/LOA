package com.ai.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import com.ai.config.Constants;

/**
 * Rule master will determine if certain moves are legal or illegal
 * It is the AI engine that applies utility function to generate utility values.
 * */

public class Master 
{
	public int maxDepth;
    //PriorityQueue<Node> allStates = new PriorityQueue<Node>(20,Collections.reverseOrder());//priority queue will not ignore duplicates
	TreeSet<Node> allUniqueStates = new TreeSet<>(); // Tree set is unique and orders node based on overridden compareTo fn
	
	/*stat variables - used to display stats after search*/
	private int maxDepthReached;
	private int numberOfNodes;
	private int numberTimesEvalMax;
	private int numberTimesEvalMin;
	private int numberTimesPrunMax;
	private int numberTimesPrunMin;
	private long startTime;
	private long endTime;
	private int boardSize;
	private int quadModel[][];//quad numbers from outer to inner quad in decreasing size
	
    public class Node implements Comparable<Node> {
        Board board;
        int value;

        public Node(Board board, int value) {
            this.board = board;
            this.value = value;

        }

        @Override
        public int compareTo(Node n) {
            return this.value - n.value ;

        }
    }
    
    public boolean isCuttoffTest(Board state, int currentDepth)
    {
    	boolean ret =false;
    	long now = new Date().getTime();
    	long diff = Math.abs(now -startTime) /1000; //to seconds
    	//System.out.println("diff is " + diff + " depth "+currentDepth);
    	if(currentDepth >= maxDepth) //depth check
    	{
    		ret = true;
    	}
    	if(diff >=Constants.CUTOFF_TIME) //Cutoff is 10 seconds
    	{
    		ret = true;
    	}
    	return ret;
    }
    
    private int getConnectedComponents(Board state, PlayerType playertype)
    {
    	 ArrayList<Point> visited = new ArrayList<Point>();
         int groups = 0;
         for (int i = 0; i < state.getSize(); i++) {
             for (int j = 0; j < state.getSize(); j++) {
                 if (!visited.contains(new Point(i, j)) && state.getCurrentBoard()[i][j].playerType == playertype) {
                	 dfsExplore(i, j, playertype, state.getSize(), visited, state.getCurrentBoard());
                     groups++;
                 }
             }
         }
         return groups;
    }
    
    private int evalFunction(Board state)
    {
    	int cpu = getQuadCount(state.getCPUPlayer(), state.getCurrentBoard());
    	int human = getQuadCount(state.getHumanPlayer(), state.getCurrentBoard());
    	int cpuGroups = getConnectedComponents(state, state.getCPUPlayer());
    	int humanGroups = getConnectedComponents(state, state.getHumanPlayer());
    	if(cpuGroups == 1)
    		return Constants.MAX_UTILITY;
    	if(humanGroups == 1)
    		return Constants.MIN_UTILITY;
    	return (cpu -human) + (humanGroups - cpuGroups);
    }
    
    private int utility(Board state)
    {
    	return evalFunction(state);
    }
    
    private int getQuadCount(PlayerType playerType, Checker[][] board){
    	int count=0;
    	for(int i=0; i<boardSize; i++)
    		for(int j=0; j<boardSize; j++)
    			if(playerType == board[i][j].playerType)
    				count+=quadModel[i][j];
    	return count;
    	
    }
    
    private void dfsExplore(int rowP, int colP, PlayerType playerType, int boardSize, List<Point >visited, Checker[][] board)
    {
    	visited.add(new Point(rowP,colP)); //we need to track repeated states
    	
    	//One step right
    	if(colP+1 < boardSize && board[rowP][colP+1].playerType == playerType && !visited.contains(new Point(rowP,colP+1)) )
    		dfsExplore(rowP,colP+1,playerType,boardSize,visited,board);
    	//One step left
    	if(colP-1 >= 0 && board[rowP][colP-1].playerType == playerType && !visited.contains(new Point(rowP,colP-1)))
    		dfsExplore(rowP,colP-1,playerType,boardSize,visited,board);
    	//One step up
    	if(rowP+1 < boardSize && board[rowP+1][colP].playerType == playerType && !visited.contains(new Point(rowP+1,colP)))
    		dfsExplore(rowP+1,colP,playerType,boardSize,visited,board);
    	//One step down
    	if(rowP-1 >= 0 && board[rowP-1][colP].playerType == playerType && !visited.contains(new Point(rowP-1,colP)))
    		dfsExplore(rowP-1,colP,playerType,boardSize,visited,board);
    	//One step diagonally -45 down
    	if(rowP+1 < boardSize && colP+1 < boardSize && board[rowP+1][colP+1].playerType == playerType && !visited.contains(new Point(rowP+1,colP+1)))
    		dfsExplore(rowP+1,colP+1,playerType,boardSize,visited,board);
    	//One step diagonally -45 down
    	if(rowP-1 >= 0 && colP-1 >= 0 && board[rowP-1][colP-1].playerType == playerType && !visited.contains(new Point(rowP-1,colP-1)))
    		dfsExplore(rowP-1,colP-1,playerType,boardSize,visited,board);
    	//One step diagonally +45 down
    	if(rowP+1 < boardSize && colP-1 >= 0 && board[rowP+1][colP-1].playerType == playerType && !visited.contains(new Point(rowP+1,colP-1)))
    		dfsExplore(rowP+1,colP-1,playerType,boardSize,visited,board);
    	//One step diagonally +45 up
    	if(rowP-1 >= 0 && colP+1 < boardSize && board[rowP-1][colP+1].playerType == playerType && !visited.contains(new Point(rowP-1,colP+1)))
    		dfsExplore(rowP-1,colP+1,playerType,boardSize,visited,board);
    }
    
    //Implement detection of connected group
    public boolean isConnected(int rowP, int colP, PlayerType playerType, Board state)
    {
    	boolean ret = false;
    	List<Point> visited = new ArrayList<>();
    	dfsExplore(rowP, colP, playerType, state.getSize(), visited, state.getCurrentBoard());
    	if(visited.size() == state.getCount(playerType))
    		ret =true;
    	return ret;
    }
    
    //
    public boolean isGame(PlayerType playerType, Board state)
    {
    	Checker [][] currentBoard = state.getCurrentBoard();
    	boolean ret =false;
    	int rowP ,colP;
    	rowP = colP = -1;
    	//get any piece of player and start dfs
    	for(int row=0; row < state.getSize(); row++)
    	{
    		for(int col=0; col < state.getSize(); col++)
    		{
    			if(currentBoard[row][col].playerType == playerType)
    			{
    				rowP=row;
    				colP=col;
    				break;
    			}
    		}
    		if(rowP != -1 && colP !=-1)
    			break;
    	}
    	
    	if(rowP != -1 && colP !=-1)
    	{
    		ret = isConnected(rowP,colP,playerType,state);
    	}
    	return ret;
    }
    
  	public boolean isTerminalTest(Board state)
  	{
  		boolean ret = false;
  		//need to check for cpu and human if they are winners
  		boolean isCpuWinner =isGame(state.getCPUPlayer(), state);
  		boolean isHumanWinner =isGame(state.getHumanPlayer(), state);
  		
  		ret = isCpuWinner? true: isHumanWinner ?true:false;
  		return ret;
  	}
    
	public Master(int depth , int boardSize)
	{
		this.maxDepth = depth;
		this.boardSize = boardSize;
		quadModel = new int[boardSize][boardSize];
		for (int i = 0; i < boardSize/2+1; i++) { //this outer loop works for 5 and  6 boardsize
            for (int j = i; j < boardSize - i; j++) {
                for (int j2 = i; j2 < boardSize - i; j2++) {
                	quadModel[j][j2] = i;
                }
            }
        }
	}
	
	private Move fetchBestAction(Board state)
	{
		//System.out.println("fetch best action "+allUniqueStates.size());
		Move m =null;
        ArrayList<Point> change = new ArrayList<Point>();
        while (true) {
        	if(allUniqueStates.size() == 0)
        		return m;
        	
        	Node node = allUniqueStates.last(); 
        	
            for (int i = 0; i < node.board.getCurrentBoard().length; i++) {
                for (int j = 0; j < node.board.getCurrentBoard().length; j++) {
                    if (node.board.getCurrentBoard()[i][j] != state.getCurrentBoard()[i][j]) {
                        change.add(new Point(i, j));
                    }
                }
            }
            for (int i = 0; i < change.size(); i++) {
                List<Point> compare = state.generateAllPossibleMoves(change.get(i));
                for (int j = 0; j < compare.size(); j++) 
                {
                    if (change.contains(compare.get(j))) 
                    {
                    	m = new Move(change.get(i),compare.get(j));
                        return m;
                    }
                }
            }
            allUniqueStates.pollLast();//Get rid of the value and choose the next highest
        }
	}
	
	private void resetStats()
	{
		maxDepthReached =-1; //start with root node
		numberOfNodes= 0;
		numberTimesEvalMax=0;
		numberTimesEvalMin=0;
		numberTimesPrunMax=0;
		numberTimesPrunMin=0;
		startTime=new Date().getTime();
		endTime=0;
	}
	
	private void printStats()
	{
		System.out.println("Search ended ! <!=== STATS =====!>");
		System.out.println("Max depth reached = "+maxDepthReached);
		System.out.println("The total number of nodes generated(including root node) "+numberOfNodes);
		System.out.println("The number of times eval function was called within Max-Value "+numberTimesEvalMax);
		System.out.println("The number of times eval function was called within Min-Value "+numberTimesEvalMin);
		System.out.println("The number of times pruning occured within Max-value "+numberTimesPrunMax);
		System.out.println("The number of times pruning occured within Min-value "+numberTimesPrunMin);
		System.out.println("The total time in seconds "+ Math.abs(endTime-startTime)/1000);
	}
	
	public Move alphaBetaSearch(Board state)
	{
		resetStats();
		numberOfNodes++;
		MaxValue(state,Constants.MIN_UTILITY, Constants.MAX_UTILITY, 0);
		//MaxValue(state,Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
		Move move = fetchBestAction(state);
		endTime = new Date().getTime();
		printStats();
		return move;
	}
	
	//Returns a utility value
	public int MaxValue(Board state, int alpha, int beta, int depth)
	{
		//System.out.println("max");
		if(isTerminalTest(state))
		{
			//System.out.println("reached terminal");
			int val = utility(state);//need to plug util function
			allUniqueStates.add(new Node(state,val));
			return val;
		}
		if(isCuttoffTest(state,depth))
		{
			//System.out.println("reached cutoff "+depth);
			int val = evalFunction(state);//NEED TO PLUG EVALUATION FUNCTION
			allUniqueStates.add(new Node(state,val));
			numberTimesEvalMax++;
			return val;
		}
		maxDepthReached = Math.max(maxDepthReached,depth);
		
		int v = Constants.MIN_UTILITY;
		List<Board> actions = getAllStatesInStateSpace(state);//return board instances after making all legal moves for each checker
		//System.out.println("List of actions "+actions.size() + " at depth "+depth);
//		for(Board b: actions)
//			System.out.println(b);
		if(actions != null)
			numberOfNodes+=actions.size(); //track number of nodes
		for(Board childState: actions)
		{
			v = Math.max(v, MinValue(childState, alpha, beta, depth+1));
			if(v>=beta) { numberTimesPrunMax++; allUniqueStates.add(new Node(childState,v)); return v;} //prune
			alpha = Math.max(alpha,v);
		}
		return v;
	}
	
	//Returns a utility value
	public int MinValue(Board state, int alpha, int beta, int depth)
	{
		//System.out.println("min");
		if(isTerminalTest(state))
		{
			//System.out.println("reached terminal");
			int val = utility(state);//need to plug util function
			allUniqueStates.add(new Node(state,val));
			return val;
		}
		if(isCuttoffTest(state,depth))
		{
			//System.out.println("reached cutoff");
			int val = evalFunction(state);//NEED TO PLUG EVALUATION FUNCTION
			allUniqueStates.add(new Node(state,val));
			numberTimesEvalMin++;
			return val;
		}
		maxDepthReached = Math.max(maxDepthReached,depth);
		
		int v = Constants.MAX_UTILITY;
		List<Board> actions = getAllStatesInStateSpace(state);
		//System.out.println("List of actions "+actions.size() + " at depth "+depth);
		if(actions != null)
			numberOfNodes+=actions.size(); //track number of nodes
		for(Board childState: actions)
		{
			v = Math.min(v, MaxValue(childState, alpha, beta, depth+1));
			if(v<=alpha){numberTimesPrunMin++; allUniqueStates.add(new Node(childState,v)); return v;}// prune
			beta = Math.min(beta,v);
		}
		return v;
	}

	
	private ArrayList<Board> getAllStatesInStateSpace(Board board) {
        //board.togglePlayer();//Really need this ?
        ArrayList<Board> listOfBoards = new ArrayList<Board>(); //Contains all legal state space for this player
        for (int i = 0; i < board.getSize(); i++) 
        {
            for (int j = 0; j < board.getSize(); j++) 
            {
                if (board.getCurrentBoard()[i][j].playerType == board.getCurrentTurn()) //only interested for pieces of current player 
                {
                    List<Point> moves = board.generateAllPossibleMoves(new Point(i, j));
                    for (int eachMove = 0; eachMove < moves.size(); eachMove++) 
                    {
                        Board childBoard = new Board(board);
                        childBoard.makeMove(new Point(i, j), moves.get(eachMove));
                        listOfBoards.add(childBoard);
                        //System.out.println(childBoard);
                    }
                }
            }
        }
        //board.togglePlayer();
        return listOfBoards;
    }
	
	public static void main(String args[])
	{
		Master m = new Master(Constants.MAX_DEPTH_HARD,5);
		Board b = new Board(6);
		b.initializeHumanPlayer(PlayerType.BLACK);
		System.out.println("after move \n"+b.dumpCurrentBoard());
		Move move;
		//System.out.println("****************************************");
		//move = m.alphaBetaSearch(b);
		//System.out.println(move);
		//b.makeMove(move.src, move.dest);//cpu making the move
		//System.out.println(b.dumpCurrentBoard());
		b.makeMove(new Point(0,4), new Point(2,4));//human making the move
		System.out.println(b.dumpCurrentBoard());
		
		move = m.alphaBetaSearch(b);
		b.makeMove(move.src, move.dest);//cpu making the move
		System.out.println(move);
		System.out.println(b.dumpCurrentBoard());
		
		List<Point> p = b.generateAllPossibleMoves(new Point(2,4));
		System.out.println(p);
		
		b.makeMove(new Point(2,4), new Point(5,1));
		System.out.println(b.dumpCurrentBoard());
		return ;
//		b.makeMove(new Point(5,3), new Point(3,5));//human making the move
//		System.out.println(b.dumpCurrentBoard());
//		
//		move = m.alphaBetaSearch(b);
//		b.makeMove(move.src, move.dest);//cpu making the move
//		System.out.println(b.dumpCurrentBoard());

//		move = m.alphaBetaSearch(b);
//		b.makeMove(move.src, move.dest);//cpu making the move
//		System.out.println(b.dumpCurrentBoard());
//		
	
	}
}

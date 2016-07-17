package com.ai.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Board class represents the state of the Lines of action board
 * Its a 2D array of #Checker object
 * @author rrk
 *
 */
public class Board {
	private Checker [][] board;
	private PlayerType humanPlayer;
	private PlayerType cpuPlayer;
	private int boardSize;
	public long cuttofTime = 0L;
	private int []checkersCount;//black is 0th index and white is 1st index
	private Checker [][]backup;
	
	private PlayerType nextPlayersTurn;
	
	/*Constructor*/
	public Board(int boardSize)
	{
		this.boardSize = boardSize;
		board = new Checker[boardSize][boardSize];
		preInitializeBoard(); //First initialize all pieces to empty
		checkersCount = new int[2];
		checkersCount[PlayerType.BLACK.ordinal()] = checkersCount[PlayerType.WHITE.ordinal()] = (boardSize-2)*2;
		backup = null;
		nextPlayersTurn = PlayerType.BLACK;
	}

	/*Copy constructor*/
	public Board(Board copyBoard)
	{
		boardSize = copyBoard.boardSize;
		board = new Checker[boardSize][boardSize];
		checkersCount = new int[2];
		for(int i=0 ;i< boardSize;i++)
		{
			for(int j=0; j<boardSize; j++)
			{
				board[i][j] = new Checker(copyBoard.board[i][j]);
			}
		}
		cuttofTime = copyBoard.cuttofTime;
		checkersCount[PlayerType.BLACK.ordinal()] = copyBoard.checkersCount[PlayerType.BLACK.ordinal()];
		checkersCount[PlayerType.WHITE.ordinal()] = copyBoard.checkersCount[PlayerType.WHITE.ordinal()];
		initializeHumanPlayer(copyBoard.humanPlayer);
		nextPlayersTurn = copyBoard.nextPlayersTurn;
		backup=null;
	}
	
	/*returns the number of pieces remaining for a player*/
	public int getCount(PlayerType playerType)
	{
		return checkersCount[playerType.ordinal()];
	}
	
	/*returns the human player type*/
	public PlayerType getHumanPlayer()
	{
		return humanPlayer;
	}
	
	/*returns the cpu player type*/
	public PlayerType getCPUPlayer()
	{
		return cpuPlayer;
	}
	
	private void preInitializeBoard()
	{
		for(int i=0 ;i< boardSize;i++)
		{
			for(int j=0; j<boardSize; j++)
			{
				board[i][j] = new Checker(i,j);
			}
		}
	}
	
	/*Initializes the board on game creation*/
	private void initializePlayersOnBoard() //This function will initialize board based on human player selection
	{
		for(int i=0; i<=boardSize; i+=boardSize-1) //Always put human player front facing
		{
			for(int j=1; j<=boardSize-2; j++)
			{
				board[i][j].setPlayer(humanPlayer);
			}
		}
		
		for(int j=0; j<= boardSize; j+=boardSize-1) //Always put cpu player side facing
		{
			for(int i=1; i<=boardSize-2; i++)
			{
				board[i][j].setPlayer(cpuPlayer);;
			}
		}
		
		//We know who is going to move first .
		if(humanPlayer == PlayerType.BLACK)
			nextPlayersTurn = humanPlayer;// for this frontend will send a new move
		else
			nextPlayersTurn = cpuPlayer; //for this AI will trigger a move
	}
	
	public void togglePlayer()
	{
		nextPlayersTurn = nextPlayersTurn == humanPlayer ? cpuPlayer : humanPlayer;
	}
	
	public void initializeHumanPlayer(PlayerType player)
	{
		humanPlayer = player;
		cpuPlayer = (humanPlayer == PlayerType.BLACK) ? PlayerType.WHITE:PlayerType.BLACK;
		//System.out.println("The human player is "+player.name() + "= "+player.ordinal());
		//System.out.println("The cpu player is "+cpuPlayer.name() + "= "+cpuPlayer.ordinal());
		initializePlayersOnBoard();
	}
	
	/*Makes a move from one Point to another and modifying the board accordingly*/
	/*Any enemy pieces will be captured*/
	public boolean makeMove(Point sourcePoint, Point destPoint)
	{
		Checker sourceChecker = board[sourcePoint.getX()][sourcePoint.getY()];
		Checker destChecker = board[destPoint.getX()][destPoint.getY()];
//		System.out.println("source checker type is "+ sourceChecker.playerType.name());
//		System.out.println("dest checker type is "+ destChecker.playerType.name());
//		System.out.println("move from "+sourcePoint.getX()+","+sourcePoint.getY()+ " to "+ destPoint.getX() +","+ destPoint.getY());
		if(sourceChecker.playerType == nextPlayersTurn && sourceChecker.playerType != PlayerType.EMPTY && sourceChecker != destChecker)
		{
			createBackup();
			List<Point> moves = generateAllPossibleMoves(sourcePoint);
			//for(Point p : moves)
				//System.out.println(p);
	        if (moves.contains(destPoint)) { //check if this move made by client is possible
	        	//We come here it means destination point is present in the list
	            if (sourceChecker.playerType == PlayerType.WHITE
	                    && destChecker.playerType == PlayerType.BLACK) {
	            	//tackle capture
	            	checkersCount[destChecker.playerType.ordinal()]--;
	            	//System.out.println(sourceChecker.playerType.name()+" captured a piece of "+destChecker.playerType.name());
	                
	            }
	            if (sourceChecker.playerType == PlayerType.BLACK
	                    && destChecker.playerType == PlayerType.WHITE) {
	            	//tackle capture
	            	checkersCount[destChecker.playerType.ordinal()]--;
	            	//System.out.println(sourceChecker.playerType.name()+" captured a piece of "+destChecker.playerType.name());
	            }
	            destChecker.setPlayer(sourceChecker.playerType);
	            destChecker.symb = sourceChecker.symb;
	            sourceChecker.setPlayer(PlayerType.EMPTY);
	            //If move succeeds , then we toggle player
	            //System.out.println("Move made by " + sourceChecker.playerType.name());
	            togglePlayer();
	            return true;
	        }
	        else
	        	return false;
		}
		else
		{
			//System.out.println("Invalid turn by : " + sourceChecker.playerType.name());
			return false;
		}
	}
	
	private void createBackup()
	{
		if(backup == null)
			backup = new Checker[boardSize][boardSize];
		for (int row = 0; row < boardSize; row++) 
		{
            for (int col = 0; col < boardSize; col += 1) 
            {
            	backup[row][col] = board[row][col];
            }
        }
	}
	
	public void restore()
	{
		if(backup != null)
		{
			for (int row = 0; row < boardSize; row++) 
			{
	            for (int col = 0; col < boardSize; col += 1) 
	            {
	            	board[row][col] = backup[row][col];
	            }
	        }
		}
	}
	
	/*Checks any enemy checkers in the path*/
	public boolean anyEnemyCheckers(Point start, Point end) {
        if (board[start.getX()][start.getY()].equals(board[end.getX()][end.getY()]))
            return false;
        if (board[start.getX()][start.getY()].playerType == board[end.getX()][end.getY()].playerType)//avoid crashing to our mates
			return false;
        int dx = end.getX() - start.getX();
        int dy = end.getY() - start.getY();
        
        int stepx = 0; /*calculate how many steps for both x and y*/
        if (dx != 0) {
            stepx = dx / Math.abs(dx);
        }
        int stepy = 0;
        if (dy != 0)
            stepy = dy / Math.abs(dy);
        Point temp = new Point(start.getX(), start.getY());
        PlayerType sourcePlayerType = board[start.getX()][start.getY()].playerType;
        PlayerType insideType;

        while (!temp.equals(end)) {
        	insideType = board[temp.getX()][temp.getY()].playerType;
        	//If we find any pieces that is not ours then skip
            if (insideType != PlayerType.EMPTY && insideType != sourcePlayerType) {
                return false;
            }
            temp.setY(temp.getY() + stepy);
            temp.setX(temp.getX() + stepx);
        }
        return true;
    }
	
	
	/*Generated all legal moves from one point */
	public List<Point> generateAllPossibleMoves(Point sourcePoint)
	{
		Checker sourceChecker = board[sourcePoint.getX()][sourcePoint.getY()];
		//System.out.println(sourceChecker.playerType.name());
		ArrayList<Point> moves = new ArrayList<Point>();
		if(sourceChecker.playerType == nextPlayersTurn && sourceChecker.playerType != PlayerType.EMPTY)
		{
			int rowCount = getPossibleMovesCountOnColumn(sourcePoint);
			int colCount =  getPossibleMovesCountOnRow(sourcePoint);
			int plus45Count = getAllPossibleMovesPlus45degreeCount(sourcePoint);
			int minus45Count = getAllPossibleMovesMinus45degreeCount(sourcePoint);
//			System.out.println("Get all possible moves counts on row "+colCount);
//			System.out.println("Get all possible moves counts on column "+rowCount);
//			System.out.println("Get all possible moves counts on +45 "+plus45Count);
//			System.out.println("Get all possible moves counts on -45 "+minus45Count);
			
			if (sourcePoint.getX() + rowCount < boardSize
	                && anyEnemyCheckers(sourcePoint,
	                        new Point(sourcePoint.getX() + rowCount, sourcePoint.getY()))) 
			{
	            moves.add(new Point(sourcePoint.getX() + rowCount, sourcePoint.getY() ));
	        }
	        if (sourcePoint.getX() - rowCount >= 0
	                && anyEnemyCheckers(sourcePoint,
	                        new Point(sourcePoint.getX() - rowCount, sourcePoint.getY()))) 
	        {
	            moves.add(new Point(sourcePoint.getX() - rowCount, sourcePoint.getY() ));
	        }
	        if (sourcePoint.getY() + colCount < boardSize
	                && anyEnemyCheckers(sourcePoint, new Point(sourcePoint.getX(), sourcePoint.getY()
	                        + colCount))) 
	        {
	            moves.add(new Point(sourcePoint.getX(), sourcePoint.getY() + colCount ));
	        }
	        if (sourcePoint.getY() - colCount >= 0
	                && anyEnemyCheckers(sourcePoint, new Point(sourcePoint.getX(), sourcePoint.getY()
	                        - colCount))) 
	        {
	            moves.add(new Point(sourcePoint.getX(), sourcePoint.getY() - colCount ));
	        }
	        if (sourcePoint.getX() - plus45Count >= 0 && sourceChecker.getY() + plus45Count < boardSize
	                && anyEnemyCheckers(sourcePoint, new Point(sourceChecker.getX() - plus45Count,
	                		sourceChecker.getY() + plus45Count))) 
	        {
	            moves.add(new Point(sourceChecker.getX()
	                    - plus45Count, sourceChecker.getY() + plus45Count ));
	        }
	        if (sourcePoint.getX() + plus45Count < boardSize
	                && sourcePoint.getY() - plus45Count >= 0
	                && anyEnemyCheckers(sourcePoint, new Point(sourcePoint.getX() + plus45Count,
	                		sourcePoint.getY() - plus45Count))) 
	        {
	            moves.add(new Point(sourcePoint.getX()
	                    + plus45Count, sourcePoint.getY() - plus45Count ));
	        }
	        if (sourcePoint.getX() + minus45Count < boardSize
	                && sourcePoint.getY() + minus45Count < boardSize
	                && anyEnemyCheckers(sourcePoint, new Point(sourcePoint.getX() + minus45Count,
	                		sourcePoint.getY() + minus45Count))) 
	        {
	            moves.add(new Point(sourcePoint.getX()
	                    + minus45Count, sourcePoint.getY() + minus45Count ));
	        }
	        if (sourcePoint.getX() - minus45Count >= 0
	                && sourcePoint.getY() - minus45Count >= 0
	                && anyEnemyCheckers(sourcePoint, new Point(sourcePoint.getX() - minus45Count,
	                		sourcePoint.getY() - minus45Count))) 
	        {
	            moves.add(new Point(sourcePoint.getX()
	                    - minus45Count, sourcePoint.getY() - minus45Count ));
	        }
		}
		else
		{
			//System.out.println("Invalid move by : " + sourceChecker.playerType.name());
		}
		return moves;
	}
	
	/* get all counts of pieces on row*/
	public int getPossibleMovesCountOnRow(Point start)
	{
		int count = 0;
        for (int col = 0; col < boardSize; col++) {
            if (board[start.getX()][col].playerType == PlayerType.BLACK
                    || board[start.getX()][col].playerType == PlayerType.WHITE) {
            	count++;
            }
        }
        return count;
	}
	
	/* get all counts of pieces on column*/
	public int getPossibleMovesCountOnColumn(Point start)
	{
		int count = 0;
        for (int row = 0; row < boardSize; row++) {
            if (board[row][start.getY()].playerType == PlayerType.BLACK
                    || board[row][start.getY()].playerType == PlayerType.WHITE) {
            	count++;
            }
        }
        return count;
	}
	
	public PlayerType getCurrentTurn()
	{
		return nextPlayersTurn;
	}
	
	public void setCurrentTurn(PlayerType turn)
	{
		this.nextPlayersTurn = turn;
	}
	
	//Get possible moves count diagonally +45
	//  			*
	//			*
	//		*
	//	*
	public int getAllPossibleMovesPlus45degreeCount(Point point) {
        int sum = point.getX() + point.getY();
        int ystart = -1;
        int xstart = -1;
        if (sum >= boardSize) {
            ystart = boardSize - 1;
            xstart = sum - boardSize + 1;
        } else {
            ystart = sum;
            xstart = 0;
        }
        int counter = 0;
        Point startPoint = new Point(xstart, ystart); //start point of diagonal that cuts across start
        Point endPoint = new Point(ystart, xstart);//end point of diagonal that cuts across start
        while (true) {
            if (startPoint.equals(endPoint)) { //this is end condition
                if (board[startPoint.getX()][startPoint.getY()].playerType != PlayerType.EMPTY)
                    counter++;
                break;
            }
            if (board[startPoint.getX()][startPoint.getY()].playerType != PlayerType.EMPTY)
                counter++;
            startPoint.setX(startPoint.getX() + 1);
            startPoint.setY(startPoint.getY() - 1);
        }
        return counter;
    }
	
	//Get possible moves count diagonally -45
	//	*
	//		*
	//			*
	//				*
	public int getAllPossibleMovesMinus45degreeCount(Point point) {
		int startx = -1;
        int starty = -1;
        Point endPoint = null;
        int counter = 0;
        if (point.getX() >= point.getY()) {
            startx = point.getX() - point.getY();
            starty = 0;

        } else {
            startx = 0;
            starty = point.getY() - point.getX();
        }
        endPoint = new Point(boardSize - starty - 1, boardSize - startx - 1);
        Point startPoint = new Point(startx, starty);
        while (true) {
            if (endPoint.equals(startPoint)) {
                if (board[startPoint.getX()][startPoint.getY()].playerType != PlayerType.EMPTY)
                    counter++;
                break;
            }
            if (board[startPoint.getX()][startPoint.getY()].playerType != PlayerType.EMPTY)
                counter++;
            startPoint.setX(startPoint.getX() + 1);
            startPoint.setY(startPoint.getY() + 1);
        }
        return counter;
    }
		
	public String dumpCurrentBoard() //Function for testing purpose to check the current board config
	{
		StringBuilder builder = new StringBuilder();
		for(int i=0 ; i<boardSize; i++)
		{
			for(int j=0 ; j<boardSize; j++)
			{
				builder.append(board[i][j] + " ");
			}
			builder.append("\n");
		}
		return builder.toString();
	}
	
	
	@Override
	public String toString() {
		return dumpCurrentBoard();
	}
	
	public String toJsonString(){
		StringBuilder builder = new StringBuilder();
		builder.append("{\"data\":[");
		for(int i=0 ; i<boardSize; i++)
		{
			builder.append("[");
			for(int j=0 ; j<boardSize; j++)
			{
				builder.append(board[i][j].playerType.ordinal());
				if(j != boardSize-1)
					builder.append(",");
			}
			builder.append("]");
			if(i != boardSize-1)
				builder.append(",");
		}
		builder.append("]}");
		return builder.toString();
	}
	
	public int getSize(){return boardSize;}
	
	public Checker[][] getCurrentBoard(){return board;}
	
	/*Test boad functions*/
	public static void main(String args[])
	{
		Board b = new Board(6);
		b.initializeHumanPlayer(PlayerType.BLACK);
		System.out.println("Initial config \n"+b.dumpCurrentBoard());
		b.makeMove(new Point(0,4), new Point(2,4)); //valid
		System.out.println(b.dumpCurrentBoard());
		List<Point> p = b.generateAllPossibleMoves(new Point(2,4));
		System.out.println(p);
		Board b1 = new Board(b);
		System.out.println(b1.dumpCurrentBoard());
		return ;
//		b.makeMove(new Point(0,0), new Point(2,5));//Invlaid move EMTPY
//		b.makeMove(new Point(6,0), new Point(2,5));//Invlaid move Not ur move
//		System.out.println(b.makeMove(new Point(0,1), new Point(0,7)));
//		System.out.println(b.dumpCurrentBoard());
//		System.out.println(b.makeMove(new Point(6,0), new Point(4,2)));
//		System.out.println(b.dumpCurrentBoard());
//		System.out.println(b.makeMove(new Point(0,7), new Point(1,6)));
//		System.out.println(b.dumpCurrentBoard());
//		System.out.println(b.makeMove(new Point(3,7), new Point(7,7)));
//		System.out.println(b.dumpCurrentBoard());
//		System.out.println(b.makeMove(new Point(4,2), new Point(7,7)));
//		System.out.println(b.dumpCurrentBoard());
	}
	
}

package com.ai.model;

import com.ai.config.Constants;

public class Checker 
{
	public PlayerType playerType;
	public boolean isCaptured;
	public Point p;
	public String symb;
	
	public Checker(int x, int y)
	{
		this.playerType = PlayerType.EMPTY;
		isCaptured = false;
		p = new Point(x, y);
		setSymb();
	}
	
	public Checker(Checker copyChecker)
	{
		this.playerType = copyChecker.playerType;
		this.isCaptured = false;
		this.p = new Point(copyChecker.getX(), copyChecker.getY());
		this.symb = copyChecker.symb;
	}
	
	private void setSymb()
	{
		if(playerType == PlayerType.EMPTY)
			symb = Constants.EMPTY_SYMBOL;
		if(playerType == PlayerType.WHITE)
			symb = Constants.WHITE_SYMBOL;
		if(playerType == PlayerType.BLACK)
			symb = Constants.BLACK_SYMBOL;
	}
	
	public void manipulate(int x, int y)
	{
		if(!isCaptured)
		{
			p.setX(x);
			p.setY(y);
		}
		else
			System.out.println("Checker is already captured");
	}
	
	public void setPlayer(PlayerType playerType)
	{
		this.playerType = playerType;
		setSymb();
	}
	
	public void capture()
	{
		isCaptured = true;
	}
	
	public int getX()
	{return p.getX();}
	
	public int getY()
	{return p.getY();}
	
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(symb);
		return buf.toString();
	}
	
	@Override
    public boolean equals(Object p) {
        return this.equals((Checker) p);
    }
	
	//unfortunate hack
	public boolean equals(Checker point) {
		// TODO Auto-generated method stub
		return this.p.getX() == point.getX() && this.p.getY() == point.getY();
	}
}

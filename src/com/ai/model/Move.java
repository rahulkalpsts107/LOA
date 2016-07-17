package com.ai.model;

/***
 * Class describes a move from src to destinaion
 * @author rrk
 *
 */
public class Move 
{	
	Point src;
	Point dest;
	
	public Move(Point moveFrom, Point moveTo)
	{
		src = moveFrom;
		dest = moveTo;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return src.getX() + " "+ src.getY() + " : " + dest.getX() + " " + dest.getY() + "\n";
	}
}

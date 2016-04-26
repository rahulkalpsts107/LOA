package com.ai.controller;

import javax.xml.bind.annotation.XmlRootElement;

import com.ai.model.Point;

@XmlRootElement
public class PointParam {
	private int x;
	private int y;
	
	public PointParam(int x2, int y2) {
		// TODO Auto-generated constructor stub
		this.x=x2;
		this.y=y2;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public Point toPoint()
	{
		return new Point(x,y);
	}
	
	public String toString()
	{
		return "x " +x + "y "+y; 
	}
}

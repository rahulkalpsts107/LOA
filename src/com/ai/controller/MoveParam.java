package com.ai.controller;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MoveParam {

	private int srcX;
	private int srcY;
	private int destX;
	private int destY;
	
	
	public MoveParam(int srcX, int srcY, int destX, int destY) {
		super();
		this.srcX = srcX;
		this.srcY = srcY;
		this.destX = destX;
		this.destY = destY;
	}
	
	public int getSrcX() {
		return srcX;
	}
	public void setSrcX(int srcX) {
		this.srcX = srcX;
	}
	public int getSrcY() {
		return srcY;
	}
	public void setSrcY(int srcY) {
		this.srcY = srcY;
	}
	public int getDestX() {
		return destX;
	}
	public void setDestX(int destX) {
		this.destX = destX;
	}
	public int getDestY() {
		return destY;
	}
	public void setDestY(int destY) {
		this.destY = destY;
	}
	
	@Override
	public String toString() 
	{
		return "from ["+srcX+"]["+srcY+"] to "+ "["+destX+"]"+"["+destY+"]";
	}
	
}

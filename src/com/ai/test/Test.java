package com.ai.test;

import com.ai.config.Constants;
import com.ai.model.Game;
import com.ai.model.PlayerType;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Game newGame = new Game(6,Constants.MAX_DEPTH_NOVICE);
		newGame.initializeHumanPlayerType(PlayerType.BLACK);
		//if black waitfor user input
		//else make ur cpu move since cpu is black
		newGame.printBoard();
	}

}
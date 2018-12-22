package app;

import java.util.ArrayList;
import java.util.Random;

public class RandomStrategy implements GameStrategy{

	private Board board;
	private int symbol;
	
	public RandomStrategy(Board inBoard, int inSymbol){
		this.board = inBoard;
		this.symbol = inSymbol;
	}
	
	@Override
	public int nextMove() {
		ArrayList<Integer> free = board.getFreeCells();
		Random rand = new Random();
		return free.get(rand.nextInt(free.size()));
	}

	@Override
	public int getSymbol() {
		return this.symbol;
	}

}

package app;

import java.util.ArrayList;
import java.util.Random;

public class CallousStrategy implements GameStrategy{

	private Board board;
	private int symbol;
	
	public CallousStrategy(Board inBoard, int inSymbol){
		//System.out.println("CallousStrategy :: constructor called");
		this.board = inBoard;
		this.symbol = inSymbol;
	}
	
	@Override
	public int nextMove() {
		ArrayList<Integer> free = board.getFreeCells();
		int ret = free.get(0);
		int maxPt = Integer.MIN_VALUE;
		
		//find max(actPt)
		for(int c : free){
			//System.out.println("CallousStrategy :: inspection for c=" + c);
			//inspect horizontal and vertical
			int h = evaluateDirection(c, 1, 0);
			int v = evaluateDirection(c, 0, 1);
			int d = 0;
			//diagonal should only be inspected for cells on either diagonals:
			if(c % 2 == 0)
				d = evaluateDirection(c, 1, 1);
			
			int actPt = h + v + d;
			
			//System.out.println("  inspection results: h=" + h + ", v=" + v + ", d=" + d);
			
			//update when new max found
			if( actPt > maxPt ){
				maxPt = actPt;
				ret = c;
				//System.out.println("  inspection new MAX: maxPt=" + maxPt);
			}
		}//next free cell
		
		return ret;
	}
	
	private int evaluateDirection(int inStartCell, int inHorizDir, int inVertDir){
		int ownSymbol = 0;
		int oppSymbol = 0;
		
		int actRow = Board.getCellRow(inStartCell) - 2 * inVertDir;
		int actCol = Board.getCellColumn(inStartCell) - 2 * inHorizDir;
		/*
		System.out.println("CallousStrategy :: evaluateDirection called for c=(" 
				+ (actRow + 2 * inVertDir) + ", " + (actCol + 2 * inHorizDir) + ") , "
				+ "inHorizDir=" + inHorizDir + ", inVertDir=" + inVertDir);
		*/
		for(int i=0; i<5; i++){
			if(   actRow < 0 || actRow >= Board.ROWNUM    // assert inspected cell is on board
			   || actCol < 0 || actCol >= Board.COLNUM){
				//do nothing
			} else {
				if(board.getCellValue(actRow, actCol) == this.symbol){
					ownSymbol++;
					//System.out.println("   --> ownSymbol found at actRow=" + actRow + ", actCol=" + actCol);
				} else if(board.getCellValue(actRow, actCol) > 0
						  && board.getCellValue(actRow, actCol) != this.symbol){
					oppSymbol++;
					//System.out.println("   --> oppSymbol found at actRow=" + actRow + ", actCol=" + actCol);
				}
			}//endif
			
			//step forward
			actRow += inVertDir;
			actCol += inHorizDir;
		}//next cell
		
		//System.out.println("   --> ownSymbol=" + ownSymbol + ", oppSymbol=" + oppSymbol);
		
		if(ownSymbol == 2) 
			return 100;
		else if(oppSymbol == 2)
			return 5;
		else 
			return (ownSymbol - oppSymbol);
	}

	@Override
	public int getSymbol() {
		return this.symbol;
	}

}


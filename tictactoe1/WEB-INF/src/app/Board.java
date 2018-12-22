package app;

import java.util.ArrayList;

public class Board {

	public static final int CIRCLE = 1;
	public static final int CROSS = 2;
	
	public static final int ROWNUM = 3;
	public static final int COLNUM = 3;
	
	private int[][] board; // row, column
	
	private static final int[][] _boardcheck = new int[][]{
			 new int[]{0,0,0,1}  //1st row
			,new int[]{1,0,0,1}  //2nd row
			,new int[]{2,0,0,1}  //3rd row
			,new int[]{0,0,1,0}  //1st col
			,new int[]{0,1,1,0}  //2nd col
			,new int[]{0,2,1,0}  //3rd col
			,new int[]{0,0,1,1}  //diag TopLeft-BottomRight
			,new int[]{2,0,-1,1}  //diag BottomLeft-TopRight
		};
	
	
	public Board(){
		board = new int[ROWNUM][COLNUM];
	}
	
	public void setCell(int inSymbol, int inRow, int inCol){
		if(0 <= inRow && inRow < board.length){
			if(0 <= inCol && inCol < board[inRow].length){
				board[inRow][inCol] = inSymbol;
			}
		}
	}
	
	public int getWinner(){
		//proof if there is three consecutive signs in row/col/diagonal
		int ret = 0;
		for(int[] p : _boardcheck){
			ret = checkConsecutiveCells(p[0],p[1],p[2],p[3]);
			if(ret > 0) return ret;
		}
		return ret;
	}
	
	private int checkConsecutiveCells(int inStartRow, int inStartCol, int inHorizDir, int inVertDir){
		/*System.out.println("checkConsecutiveCells :: inStartRow=" + inStartRow
				          + ", inStartCol=" + inStartCol + ", inHorizDir=" + inHorizDir + ", inVertDir=" + inVertDir);
		 */
		int ret = board[inStartRow][inStartCol];
		if(ret > 0){
			if(    ret == board[inStartRow+inHorizDir][inStartCol+inVertDir] 
				&& ret == board[inStartRow+2*inHorizDir][inStartCol+2*inVertDir]) return ret;
			else return 0;
		} else return ret;
	}
	
	public boolean isFull(){
		for(int r=0; r<board.length; r++){
			for(int c=0; c<board[r].length; c++){
				if( board[r][c] == 0 ) return false;
			}
		}
		return true;
	}
	
	public int getCellValue(int inRow, int inCol){
		return board[inRow][inCol];
	}
	
	public ArrayList<Integer> getFreeCells(){
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for(int i=0; i<ROWNUM*COLNUM; i++){
			int col = i % ROWNUM;
			int row = i / COLNUM;
			if(board[row][col] == 0) ret.add(i);
		}
		return ret;
	}
	
}

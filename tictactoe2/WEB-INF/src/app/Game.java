package app;

import java.util.ArrayList;

public class Game {
	
	private Board board;
	private GameStrategy computer;
	private boolean gameFinished; //board is full OR a player has won
	private int cntMove;
	private long startTime;
	
	private int[] lastGame;
	private int humanSymbol;
	private int computerStrategyType;
	private boolean humanStarts;
	
	private ArrayList<GameStatistics> stats;
	
	public Game(){
		stats = new ArrayList<GameStatistics>();
		//reset();
	}
	
	public void setHumanSymbol(int inHumanSymbol){
		humanSymbol = inHumanSymbol;
	}
	
	public void setComputerStrategyType(int inCompStratType){
		computerStrategyType = inCompStratType;
	}
	
	public int getComputerStrategyType(){
		return computerStrategyType;
	}
	
	public void setHumanStarts(String inHumanStarts){
		humanStarts = (inHumanStarts != null && !(inHumanStarts.equalsIgnoreCase("")));
	}
	
	public String getSettings(){
		return    "humanSymbol=" + humanSymbol 
				+ ", computerStrategyType=" + computerStrategyType 
				+ ", humanStarts=" + humanStarts;
	}
	
	public void reset(){
		board = new Board();
		int compSymbol = (humanSymbol == Board.CIRCLE ? Board.CROSS : Board.CIRCLE);
		computer = GameStrategyFactory.getStrategy(computerStrategyType, board, compSymbol);		
		gameFinished = false;
		cntMove = 0;
		startTime = System.currentTimeMillis();
		
		if(!humanStarts)
			this.computerMove();
	}
	
	public boolean isBoardFull(){
		return board.isFull();
	}
	
	public int getWinner(){
		return ( board==null ? -1 : board.getWinner() );
	}
	
	public boolean isGameFinished(){
		return this.gameFinished;
	}
	
	public int getCellValue(int inRow, int inCol){
		return board.getCellValue(inRow, inCol);
	}
	
	public int getMoveCount(){return cntMove;}
	
	public void manageTurn(int inHumanCell){
		//manage moves
		if(inHumanCell >=0 && inHumanCell < Board.ROWNUM * Board.COLNUM){
			humanMove(inHumanCell);
			
			//check if game is not over => if not, Computer's turn
			if( !isGameFinished() ){
				computerMove();
			}
		}
		//update lastGame when finished + save statistics
		if( isGameFinished() ){
			updateLastGame();
			stats.add( this.getStatistics() );
		}
	}
	
	private void computerMove(){
		int selCell = computer.nextMove();
		handleMove(computer.getSymbol(), selCell / Board.COLNUM, selCell % Board.ROWNUM);
	}
	
	private void humanMove(int inCell){
		handleMove(humanSymbol, inCell / Board.COLNUM, inCell % Board.ROWNUM);
	}
	
	private void handleMove(int inSymbol, int inRow, int inCol){
		board.setCell(inSymbol, inRow, inCol);
		gameFinished = board.isFull() || board.getWinner() > 0;
		cntMove++;
	}
	
	public GameStatistics getStatistics(){
		long elapstim = System.currentTimeMillis() - startTime;
		int bwinner = board.getWinner();
		
		String winner = "";
		if(bwinner==0)
			winner = "Draw";
		else if(bwinner == computer.getSymbol())
			winner = "Computer (" + GameStrategyFactory.getStrategyName(computerStrategyType) + ")";
		else if(bwinner != computer.getSymbol())
			winner = "Human";
		
		return new GameStatistics(elapstim, cntMove/2+1, winner);
	}
	
	public void updateLastGame(){
		if(lastGame == null)
			lastGame = new int[Board.ROWNUM * Board.COLNUM];
		
		int[] b = board.getBoardArray();
		//copy board array
		for(int i=0; i < b.length; i++){
			lastGame[i] = b[i];
		}
	}
	
	public int[] getLastGame(){return lastGame;}
	
	public ArrayList<GameStatistics> getGameStatistics(){return stats;}
	
	public String toString(){
		String bs = "";
		if(board != null){
			for(int r=0; r<Board.ROWNUM; r++){
				for(int c=0; c<Board.COLNUM; c++){
					bs += ( (bs.equalsIgnoreCase("") ? "" : "," ) + board.getCellValue(r, c) );
				}
			}
		} else {
			bs = "NULL";
		}
		
		return "Game :: " + getSettings() 
		     + ", finished=" + this.isGameFinished() 
		     + ", winner=" + this.getWinner() 
		     + ", board: " + bs
		     ;
	}
}

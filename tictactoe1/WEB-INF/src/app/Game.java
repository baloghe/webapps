package app;

public class Game {
	
	private Board board;
	private GameStrategy computer;
	private boolean gameFinished; //board is full OR a player has won
	int cntMove;
	long startTime;
	
	public Game(){
		board = new Board();
		computer = new RandomStrategy(board, Board.CIRCLE);
		gameFinished = false;
		cntMove = 0;
		startTime = System.currentTimeMillis();
	}
	
	public boolean isBoardFull(){
		return board.isFull();
	}
	
	public int getWinner(){
		return board.getWinner();
	}
	
	public boolean isGameFinished(){
		return this.gameFinished;
	}
	
	public int getCellValue(int inRow, int inCol){
		return board.getCellValue(inRow, inCol);
	}
	
	public int getMoveCount(){return cntMove;}
	
	public void computerMove(){
		int selCell = computer.nextMove();
		handleMove(computer.getSymbol(), selCell / Board.COLNUM, selCell % Board.ROWNUM);
	}
	
	public void handleMove(int inSymbol, int inRow, int inCol){
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
			winner = "Computer";
		else if(bwinner != computer.getSymbol())
			winner = "Human";
		
		return new GameStatistics(elapstim, cntMove/2+1, winner);
	}
}

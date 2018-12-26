package app;

public class GameStatistics {
	
	private long durationMillis;
	private int turns;
	private String winner;
	
	public GameStatistics(long inDurmillis, int inTurns, String inWinner){
		this.durationMillis = inDurmillis;
		this.turns = inTurns;
		this.winner = inWinner;
	}
	
	public long getDurationMillis(){return durationMillis;}
	public int getTurns(){return turns;}
	public String getWinner(){return winner;}
}

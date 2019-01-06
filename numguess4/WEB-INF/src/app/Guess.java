package app;

public class Guess {

	private int guess;
	private int hint;
	
	public Guess(int inGuess, int inHint){
		this.guess = inGuess;
		this.hint = inHint;
	}
	
	public int getGuess(){return this.guess;}
	public int getHint(){return this.hint;}
}

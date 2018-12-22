package app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * taken from: http://www.ntu.edu.sg/home/ehchua/programming/java/JavaServerPages.html
 * page accessed: 21st December, 2018
 */
public class NumberGuessBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static int HINT_NUMFORMATERROR = 0;
	public static int HINT_GUESS_TOO_BIG = -1;
	public static int HINT_GUESS_TOO_SMALL = 1;
	
	private int answer;
	private boolean success;
	private int cntGuess;
	private Random random;
	private int hint;
	
	private ArrayList<Guess> prvGuesses;
	private HashMap<Integer,Guess> prvGuessMap;
	
	private static int _num_base_ = 100;
	
	// Constructor
	public NumberGuessBean() {
		random = new Random();
		reset();
	}
	
	public void reset() {
		answer = random.nextInt( _num_base_ ) + 1;
		success = false;
		cntGuess = 0;
		prvGuesses = new ArrayList<Guess>();
		prvGuessMap = new HashMap<Integer,Guess>();
	}
	
	public void setGuess(String inGuess){
		++cntGuess;
		
		int numIn;
		try {
			 numIn = Integer.parseInt(inGuess);
		} catch (NumberFormatException e) {
			hint = HINT_NUMFORMATERROR;
			return;
		}
		
		if (numIn == answer) {
			success = true;
		} else if (numIn < answer) {
			hint = HINT_GUESS_TOO_SMALL;
		} else if (numIn > answer) {
			hint = HINT_GUESS_TOO_BIG;
		}
		
		//remember former guesses only if a valid integer has been provided
		if(!success){
			Guess g = new Guess(numIn, hint);
			prvGuesses.add( g );
			prvGuessMap.put(numIn, g);
		}
	}
	
	public boolean isSuccess() { return success; }
	public int getHint() { return hint; }
	public int getCntGuess() { return cntGuess; }
	
	public ArrayList<Guess> getPrvGuesses(){return prvGuesses;}
	
	public static int getNumBase(){return _num_base_;}
	
	public int guessedAlready(int inNum){
		Guess g = prvGuessMap.get(inNum);
		if(g==null)
			return 0;
		return g.getHint();
	}
}

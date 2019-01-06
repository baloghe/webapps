package app;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.faces.application.FacesMessage;
/* this one is originally under j2ee7\glassfish4\glassfish\modules\javax.faces.jar 
 *   -> copied under tomcat7\lib so that tomcat could use it as well */
import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/**
 * taken from: http://www.ntu.edu.sg/home/ehchua/programming/java/JavaServerPages.html
 * page accessed: 21st December, 2018
 */
@ManagedBean(name="numGuess")
@SessionScoped
public class NumberGuessBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static int HINT_NUMFORMATERROR = 0;
	public static int HINT_GUESS_TOO_BIG = -1;
	public static int HINT_GUESS_TOO_SMALL = 1;
	
	private int answer;
	private String userGuess;
	private boolean success;
	private int cntGuess;
	private Random random;
	private int hint;
	
	private int lowerBarrier;
	private int upperBarrier;
	
	private ArrayList<Integer> availableNums;
	
	private ArrayList<Guess> prvGuesses;
	private HashMap<Integer,Guess> prvGuessMap;
	
	private static int numBaseMin = 1;
	private static int numBaseMax = 100;
	
	// Constructor
	public NumberGuessBean() {
		random = new Random();
		reset();
	}
	
	public void reset() {
		answer = random.nextInt( numBaseMax ) + numBaseMin;
		success = false;
		cntGuess = 0;
		prvGuesses = new ArrayList<Guess>();
		prvGuessMap = new HashMap<Integer,Guess>();
		availableNums = new ArrayList<Integer>();
		for(int i=numBaseMin; i<=numBaseMax; i++)
			availableNums.add(i);
		userGuess = "";
		lowerBarrier = numBaseMin - 1;
		upperBarrier = numBaseMax + 1;
	}
	
	public int getNumBaseMax(){return numBaseMax;}
	
	public int getNumBaseMin(){return numBaseMin;}
	
	public String getGuess(){return userGuess;}
	
	public int getLowerBarrier(){return lowerBarrier;}
	
	public int getUpperBarrier(){return upperBarrier;}
	
	public ArrayList<Integer> getAvailableNums(){return availableNums;}
	
	public void setGuess(String inGuess){
		++cntGuess;
		userGuess = inGuess;
	}
	
	public void guess(Integer i){
		++cntGuess;
		userGuess = Integer.toString(i);
		evaluate();
	}
	
	public void guessAjax(Integer i){
		++cntGuess;
		userGuess = Integer.toString(i);
		evaluate();
	}
	
	public boolean getSuccess(){return success;}
	
	public void evaluate(){
		//figure out if it is a number		
		int numIn;
		try {
			 numIn = Integer.parseInt(userGuess);
		} catch (NumberFormatException e) {
			hint = HINT_NUMFORMATERROR;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"This is not a number!",""));
			return;
		}
		
		//if it is a number, compare it to the expected answer
		String msg = "";
		if (numIn == answer) {
			success = true;
			msg = "Wow! Number (" + answer + ") guessed correctly after " + cntGuess + " tries.";
		} else if (numIn < answer) {
			hint = HINT_GUESS_TOO_SMALL;
			lowerBarrier = numIn;
			msg = "Try a bigger number than " + numIn + "!";
		} else if (numIn > answer) {
			hint = HINT_GUESS_TOO_BIG;
			upperBarrier = numIn;
			msg = "Try a smaller number than " + numIn + "!";
		}
		//remove elements
		ArrayList<Integer> toRem = new ArrayList<Integer>();
		for(Integer i : availableNums){
			if(i <= lowerBarrier || i >= upperBarrier)
				toRem.add(i);
		}
		availableNums.removeAll(toRem);
		
		//remember former guesses only if a valid integer has been provided
		if(!success){
			Guess g = new Guess(numIn, hint);
			prvGuesses.add( g );
			prvGuessMap.put(numIn, g);
		}
		
		//send back a message
		FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO,msg,"");
		FacesContext.getCurrentInstance().addMessage(null, facesMsg);
	}	
	
}

package app;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.faces.application.FacesMessage;
/* this one is originally under j2ee7\glassfish4\glassfish\modules\javax.faces.jar 
 *   -> copied under tomcat7\lib so that tomcat could use it as well */
import javax.faces.bean.*;
import javax.faces.context.FacesContext;

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
	
	private ArrayList<Guess> prvGuesses;
	private HashMap<Integer,Guess> prvGuessMap;
	
	private static int numBase = 100;
	
	// Constructor
	public NumberGuessBean() {
		random = new Random();
		reset();
	}
	
	public void reset() {
		answer = random.nextInt( numBase ) + 1;
		success = false;
		cntGuess = 0;
		prvGuesses = new ArrayList<Guess>();
		prvGuessMap = new HashMap<Integer,Guess>();
		userGuess = "";
	}
	
	public int getNumBase(){return numBase;}
	
	public String getGuess(){return userGuess;}
	
	public void setGuess(String inGuess){
		++cntGuess;
		userGuess = inGuess;
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
			msg = "Wow! Number guessed correctly after " + cntGuess + " tries.";
		} else if (numIn < answer) {
			hint = HINT_GUESS_TOO_SMALL;
			msg = "Try a bigger number!";
		} else if (numIn > answer) {
			hint = HINT_GUESS_TOO_BIG;
			msg = "Try a smaller number!";
		}
		
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

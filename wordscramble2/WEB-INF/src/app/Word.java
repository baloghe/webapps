package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Word {

	private String original;
	private String scramble;
	
	public Word(String inOriginal){
		this.original = inOriginal;
		this.scramble = Word.mixUp(this.original);
	}
	
	public static String mixUp(String inStr){
		StringBuilder builder = new StringBuilder();
		
		//Taken from: https://stackoverflow.com/questions/27254000/java-mix-up-letters
		List<Character> letters = new ArrayList<Character>();
		for (char letter : inStr.toCharArray()) {
            letters.add(letter);
        }
		Collections.shuffle(letters);
		for (char letter : letters) {
			builder.append(letter);
        }
		
		return builder.toString();
	}
	
	public String getOriginal(){ return this.original; }
	public String getScramble(){ return this.scramble; }
	
	public boolean proof(String inAnswer){
		char[] orig = this.original.toCharArray();
		char[] answ = inAnswer.toCharArray();
		if( orig.length == answ.length ){
			for(int i=0; i<orig.length; i++){
				if(orig[i] != answ[i]) return false;
			}
			return true;
		} else return false;
	}
}

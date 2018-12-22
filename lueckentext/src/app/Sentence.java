package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Sentence {

	private String origString;
	private HashMap<Word, Boolean> hmWords;
	private ArrayList<Word> arrWords;
	
	public Sentence (String inString, String inSubject) {
		this.origString = inString;
		
		this.generateWords();
	}
	
	public Iterator<Word> getIterator(){
		return arrWords.iterator();
	}
	
	public boolean isFixedWord(Word inWord){
		return hmWords.get(inWord);
	}
	
	private void generateWords(){
		
		/* Split up original string to word literals */
		String[] warr = Util.splitText(this.origString);
		
		/* generate Word objects */
		hmWords = new HashMap<Word, Boolean>();
		arrWords = new ArrayList<Word>();
		boolean bFixed = false;
		for(int i=0; i<warr.length; i++){
			String look1 = (i < warr.length-1 ? warr[i+1] : null);
			String act = warr[i];
			
			if( bFixed == false && act.equalsIgnoreCase("[") && look1 != null && look1.equalsIgnoreCase("/") ){
				//the following word is not subject to test => exception => turn Fixed on
				bFixed = true;
				i++;
			}
			else if( bFixed == true && act.equalsIgnoreCase("/") && look1 != null && look1.equalsIgnoreCase("]") ){
				//end of exception => turn Fixed off
				bFixed = false;
				i++;
			} else {
				Word tmpw = new Word(act);
				hmWords.put( tmpw, bFixed);
				arrWords.add(tmpw);
			}//endif
		}//next i
	}
	
}

package cc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class LetterProviderModel {

	private String htmlID;

	private Map<String, String> nextLetterMap;
	private Map<String, Alphabet> alphabetMap;
	private ArrayList<ArrayList<String>>  layout;

	public LetterProviderModel(String inHtmlID){
		this.htmlID = inHtmlID;

		this.nextLetterMap = new LinkedHashMap<String, String>();
		this.alphabetMap   = new LinkedHashMap<String, Alphabet>();
		setLayout(new ArrayList<ArrayList<String>>());
	}

	public void addLetter(String inLetterID, Alphabet inAlphabet){
		this.alphabetMap.put(inLetterID, inAlphabet);
		this.nextLetterMap.put(inLetterID, this.getNextLetter(inLetterID));
	}

	public ArrayList<ArrayList<String>> getLayout() {
		return this.layout;
	}

	public void setLayout(ArrayList<ArrayList<String>> layout) {
		this.layout = layout;
	}

	private String getNextLetter(String inID){
		Alphabet alphi = this.alphabetMap.get(inID);
		return alphi.getRandomLetter();
	}

	public String getHtmlID() {
		return this.htmlID;
	}

	public void generateNextLetters(){
		for(String letterID : this.alphabetMap.keySet()){
			this.nextLetterMap.put(letterID, this.getNextLetter(letterID));
		}
	}

	public Map<String, String> getNextLetterMap() {
		return nextLetterMap;
	}
}


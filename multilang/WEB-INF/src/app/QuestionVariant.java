package app;

import java.util.ArrayList;

public class QuestionVariant {

	private String lang;
	private String text;
	private ArrayList<String> hints;
	private Question parent;
	
	public QuestionVariant(String inLang, String inText, Question inParent){
		this.lang = inLang;
		this.text = inText;
		this.parent = inParent;
		this.hints = new ArrayList<>();
	}
	
	public void addHint(String inHintText){
		this.hints.add(inHintText);
	}
	
	public int getTimeSec(){
		return parent.getTimeSec();
	}
	
	public String getText(){
		return this.text;
	}
	
	public String getHintsStr(){
		String ret = "";
		for(String s : this.hints){
			if(ret.length()==0)
				ret += s;
			else ret += ("," + s);
		}
		return ret;
	}
	
	public ArrayList<String> getHints(){return this.hints;}
	
	public String toString(){
		String hintTxt = "";
		for(String s : hints){
			if(hintTxt.length()==0)
				hintTxt += s;
			else hintTxt += (", " + s);
		}
		return this.lang + " :: " + this.text + " -> " + hintTxt;
	}
}

package app;

import java.util.HashMap;

public class Question {

	private String id;
	private int timeSec;
	private HashMap<String, QuestionVariant> variants;
	
	public Question(String inID, int inTimeSec){
		this.id = inID;
		this.timeSec = inTimeSec;
		this.variants = new HashMap<>();
	}
	
	public void addVariant(String inLang, QuestionVariant inVariant){
		variants.put(inLang, inVariant);
	}
	
	public QuestionVariant getVariant(String inLang){
		return variants.get(inLang);
	}
	
	public int getTimeSec(){return this.timeSec;}
	
	public String toString(){
		String vars = "";
		for(QuestionVariant v : variants.values()){
			if(vars.length()==0)
				vars += ("(" + v.toString() + ")");
			else vars += (", (" + v.toString() + ")");
		}
		return "[" + this.id + " (" + this.timeSec + " sec) :: " + vars + "]";
	}
}

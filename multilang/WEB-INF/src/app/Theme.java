package app;

import java.util.ArrayList;

public class Theme {

	private String id;
	private String label;
	private ArrayList<Question> questions;
	
	public Theme(String inID, String inLabel){
		this.id = inID;
		this.label = inLabel;
		this.questions = new ArrayList<>();
	}
	
	public String getId(){return this.id;}
	public String getLabel(){return this.label;}
	
	public int getQuestionNumber(){return questions.size();}
	
	public void addQuestion(Question inQuestion){
		this.questions.add(inQuestion);
	}
	
	public ArrayList<QuestionVariant> getQuestionVariants(String[] inLangSelection){
		ArrayList<QuestionVariant> ret = new ArrayList<>();
		for(Question q : questions){
			for(String s : inLangSelection){
				if(s!=null && !s.equalsIgnoreCase("")){
					//don't add non-existing variant!
					QuestionVariant x = q.getVariant(s);
					if(x != null)
						ret.add( x );
				}
			}//next lang
		}//next question
		return ret;
	}
}

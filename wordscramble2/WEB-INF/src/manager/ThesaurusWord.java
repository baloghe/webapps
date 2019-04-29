package manager;

import java.util.ArrayList;

public class ThesaurusWord {

	private String word;
	private long score;
	private ArrayList<String> tags;
	
	public ThesaurusWord(){}
	
	public ThesaurusWord(String word, long score){
		this.word = word;
		this.score = score;
		this.tags = null;
	}
	
	public String toString(){
		return "("+word+", "+score+", ["+(tags==null ? "NULL" : "len="+tags.size() )+"])";
	}
	
	public String getWord() {
		return word;
	}
	public void setWord(String literal) {
		this.word = literal;
	}
	public long getScore() {
		return score;
	}
	public void setScore(long score) {
		this.score = score;
	}
	public ArrayList<String> getTags() {
		return tags;
	}
	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
	
}

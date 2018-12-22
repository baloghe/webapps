package app;

public class Word {

	private String literal;
	private String type;
	
	public Word(String inLiteral){
		this.literal = inLiteral;
		this.type = WordType.getWordType(this.literal);
	}
	
	public String type(){return this.type;}
	
	public String literal(){return this.literal;}
	
}

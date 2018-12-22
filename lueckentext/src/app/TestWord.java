package app;

import javax.servlet.http.HttpServletRequest;

public class TestWord {
	
	private Word underlyingWord;
	private boolean bEligible;
	private boolean bSelected;
	private String blankTextID;
	private String userText;
	
	public TestWord(Word inWord){
		this.underlyingWord = inWord;
		this.bEligible = true;
		this.bSelected = false;
		this.blankTextID = null;
		this.userText = "";
	}
	
	public String literalToString(){
		return this.underlyingWord.literal();
	}
	
	public boolean getResult(String inUserReply){
		return (   this.blankTextID == null 
				|| this.underlyingWord.literal().equals( inUserReply ));
	}
	
	public boolean getResult(HttpServletRequest inRequest){
		this.userText = inRequest.getParameter( this.blankTextID ).trim();
		return (   this.blankTextID == null 
				|| this.underlyingWord.literal().equals( this.userText ));
	}
	
	public String resultToString(){
		if(this.blankTextID==null)
			return this.literalToString();
		return this.getResult( this.userText ) ? 
			"<span class='goodAnswer'>"+this.userText+"</span>" 
				: "<span class='badAnswer'>"+this.userText+"</span><span class='goodAnswer'>["+this.literalToString()+"]</span>"
			;
	}
	
	public void resetUserText(){this.userText = "";}
		
	public void setEligible(boolean inEligible){this.bEligible = inEligible;}
	
	public void setSelected(boolean inSelected){this.bSelected = inSelected;}
	
	public boolean eligible(){return this.bEligible;}
	
	public boolean selected(){return this.bSelected;}
	
	public String type(){return this.underlyingWord.type();}
	
	public void setHtmlID(String inID){this.blankTextID = inID;}
	
	public String getHtmlID(){return this.blankTextID;}
}

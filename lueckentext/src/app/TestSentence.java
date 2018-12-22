package app;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

public class TestSentence {

	private Sentence underlyingSentence;
	private ArrayList<TestWord> arrTestWords;
	private int sentenceNum;
	private int cntGoodAns;
	private int cntBadAns;
	
	public TestSentence(Sentence inSentence, int inSentenceNum){
		this.underlyingSentence = inSentence;
		this.sentenceNum = inSentenceNum;
		this.cntGoodAns = 0;
		this.cntBadAns = 0;
		arrTestWords = null;
	}
	
	public void resetUserText(){
		this.cntGoodAns = 0;
		this.cntBadAns = 0;
		for(TestWord tw : arrTestWords){
			tw.resetUserText();
		}
	}
	
	public void generateTestSentence(HashSet<String> inEnabledTypes, double inBlankRatio){
		arrTestWords = new ArrayList<TestWord>();
		
		//1: generate Word objects
		int tmpFixedNum = 0;
		Iterator<Word> it = underlyingSentence.getIterator();
		while(it.hasNext()){
			Word w = it.next();
			TestWord nv = new TestWord( w );
			if( this.underlyingSentence.isFixedWord(w) ){
				nv.setEligible(false);
				tmpFixedNum++;
			}
			this.arrTestWords.add( nv );
		}//wend
		
		//2: select TEST words
		int expSelNum = (int) Math.floor( inBlankRatio * this.arrTestWords.size() );
		int tmpSelNum = 0;
		int tmpFree = this.arrTestWords.size() - tmpFixedNum;
		Random rand = new Random();
		
		while(   tmpSelNum < expSelNum
				  && tmpFree > 0){
			
			for(int i=0; i<this.arrTestWords.size() && tmpSelNum < expSelNum; i++){
				TestWord actword = this.arrTestWords.get(i);
				//already chosen?
				if(  ( !(actword.selected()) )
		  		   && actword.eligible()       ){
					//Type OK?
					String actWType = actword.type();
					boolean isTypeOK = (inEnabledTypes==null || inEnabledTypes.size()==0 || inEnabledTypes.contains(actWType));
					
					//Type not enabled => make word uneligible
					if( !isTypeOK ){
						actword.setEligible(false);
						tmpFree--;
						//System.out.println("TestSentence :: uneligible: " + actword.literalToString());
					} else {
						//decide if actual word should be subject to test
						double rnd = rand.nextDouble();
						double p = (double)expSelNum / (double)this.arrTestWords.size();
						if(rnd <= p ){
							actword.setSelected(true);
							
							String tmpID = "txtLck"+this.sentenceNum+"_"+i;
							actword.setHtmlID(tmpID);
							//System.out.println("TestSentence :: tmpID=" + tmpID + ", literal=" + actword.literalToString() + ", p=" + p + ", rnd=" + rnd);
							
							tmpSelNum++;
							tmpFree--;
						}
					}//endif
				}//endif
			}//next i
			
		}//wend
	}
	
	public String testToString(){
		//create input fields in text
		String ret = "";
		for(int i=0; i<this.arrTestWords.size(); i++){
			TestWord actword = this.arrTestWords.get(i);
			ret += ( (i==0 || actword.type().equalsIgnoreCase("ZEICH") ? "" : " ")
			        + (actword.selected() ?
						"<input type='text' name='"+ actword.getHtmlID() +"' />"
							: actword.literalToString()
							)
					);
			
		}
		
		//alert("TestSentence.testToString :: ret=" + ret);
		
		return ret;
	}
	
	public void calcGoodBadAns(HttpServletRequest inRequest){
		for( TestWord actword : this.arrTestWords ){
			if( actword.selected() ){
				if( actword.getResult(inRequest) ){
					this.cntGoodAns++;
				} else {
					this.cntBadAns++;
				}
			}//endif
		}//next i
	}
	
	public String resultToString(){
		String ret = "";
		boolean isFirst = true;
		for( TestWord actword : this.arrTestWords ){
			ret += (  (isFirst || actword.type().equalsIgnoreCase("ZEICH") ? "" : " ") + actword.resultToString() );
			isFirst = false;
		}
		return ret + (this.cntBadAns==0 ? 
						"<span class='goodAnswer'>&#10004;</span>" 
							: "<span class='badAnswer'>&#8224;</span>" );
	}
	
	public int getGoodAns(){return this.cntGoodAns;}
	public int getBadAns(){return this.cntBadAns;}
}

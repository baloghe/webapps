package app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

public class ClozeTest {

	private ArrayList<TestSentence> testsntc;
	private int pointer;
	
	private HashSet<String> enabledWT;
	private ArrayList<String> subjects;
	private long startTime;
	private long elapsedTimeMillis;
	private Statistics statistics;

	public ClozeTest(HashSet<String> inEnabledWordTypes, ArrayList<String> inSubjects, double inBlankRatio){
		enabledWT = inEnabledWordTypes;
		subjects = inSubjects;
		
		testsntc = new ArrayList<TestSentence>();
		
		int cnt = 0;
		for(String sb : subjects){
			for(String se : Util.getSubjectSentences(sb)){
				TestSentence ts = new TestSentence( new Sentence(se, sb) , cnt++);
				ts.generateTestSentence(enabledWT, inBlankRatio);
				testsntc.add( ts );
			}//next sentence
		}//next subject
		
		pointer = 0;
		startTime = System.currentTimeMillis();
		statistics = null;
	}
	
	public void start(){
		pointer = 0;
		for(TestSentence ts : testsntc){
			ts.resetUserText();
		}
		startTime = System.currentTimeMillis();
		statistics = null;
	}
	
	public int getLength(){return testsntc.size();}
	
	public boolean isFinished(){
		return pointer >= testsntc.size();
	}
	
	public TestSentence next(){
		if( isFinished() ) return null;
		
		TestSentence ret = testsntc.get(pointer);
		pointer++;
		return ret;
	}
	
	public int getPointer(){return pointer;}
	
	public void evaluateAnswer(HttpServletRequest request){
		TestSentence ts = testsntc.get(pointer-1);
		ts.calcGoodBadAns(request);
	}
	
	private void calcElapsedTime(){
		long endTime = System.currentTimeMillis();
		elapsedTimeMillis = endTime - startTime;
	}
	
	public long getElapsedTimeInMillis(){return elapsedTimeMillis;}
	
	public Statistics calcStatistics(){
		calcElapsedTime();
				
		int cntGoodAns = 0;
		int cntBadAns = 0;
		int cntGoodSentences = 0;
		int cntTestSentences = testsntc.size();
		for(TestSentence ts : testsntc){
			cntGoodAns += ts.getGoodAns();
			cntBadAns += ts.getBadAns();
			cntGoodSentences += (ts.getBadAns() > 0 ? 0 : 1);
		}//next i
		int cntBadSentences = cntTestSentences - cntGoodSentences;
		
		statistics = new Statistics(elapsedTimeMillis, cntGoodAns, cntBadAns, cntGoodSentences, cntBadSentences);
		
		return statistics;
	}
	
	public String resultsToString(){
		SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss.SSS");
		String elapsedTimeFormatted = formatter.format(elapsedTimeMillis);
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		String ret = "";
		for(TestSentence ts : testsntc){
			ret += ( "<br/>" + ts.resultToString() );
		}//next i
		
		int cntGoodAns = statistics.goodAns;
		int cntBadAns = statistics.badAns;
		int cntGoodSentences = statistics.goodSntc;
		int cntTestSentences = testsntc.size();
		int cntAllAns = cntBadAns + cntGoodAns;
		int cntBadSentences = cntTestSentences - cntGoodSentences;
		
		ret = "<span class='timeElapsed'>time elapsed: "+elapsedTimeFormatted+" </span>,"
	    + " results: answers <span class='goodAnswer'>"+cntGoodAns+"</span> / "+cntAllAns+" &rarr; errors: <span class='badAnswer'>"+cntBadAns+"</span>," 
	    + " sentences <span class='goodAnswer'>"+cntGoodSentences+"</span> / "+cntTestSentences+" &rarr; errors: <span class='badAnswer'>"+cntBadSentences+"</span>," 
		+ ret
		;
		
		return ret;
	}
}

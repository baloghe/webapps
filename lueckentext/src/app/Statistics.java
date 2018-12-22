package app;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Statistics {

	long solveMillis;
	int goodAns;
	int badAns;
	int goodSntc;
	int badSntc;
	
	static SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
	
	static{
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	public Statistics(long inSolveMillis, int inGoodAns, int inBadAns, int inGoodSentences, int inBadSentences){
		solveMillis = inSolveMillis;
		goodAns = inGoodAns;
		badAns = inBadAns;
		goodSntc = inGoodSentences;
		badSntc = inBadSentences;
	}
	
	public Statistics copy(){
		return new Statistics(this.solveMillis, this.goodAns, this.badAns, this.goodSntc, this.badSntc);
	}
	
	public String toHtmlString(){
		int cntAns = this.goodAns + this.badAns;
		int cntSen = this.goodSntc + this.badSntc;
		int ratAns = (int)Math.floor( (double)100 * (double)this.goodAns / (double)cntAns );
		int ratSen = (int)Math.floor( (double)100 * (double)this.goodSntc / (double)cntSen );
		
		String ret = 	 
				 "<tr>"
				+"<td>"+cntSen+"</td>"
				+"<td>"+formatter.format(this.solveMillis)+"</td>"
				+"<td>"+this.goodAns+"</td>"
				+"<td>"+this.badAns+"</td>"
				+"<td>"+ratAns+" %</td>"
				+"<td>"+this.goodSntc+"</td>"
				+"<td>"+this.badSntc+"</td>"
				+"<td>"+ratSen+" %</td>"
				+"</tr>"
				;
				
		return ret;
	}
	
}

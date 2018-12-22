package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WordType {

/* WordType - Start */
	
	//ZEICH
	public static final String[] arrZeich = new String[]{
	 "\\.",",",":",";","!","\\?","\\(","\\)","\\-","/","\\[","\\]","\'","\""
	};
	
	public static Pattern[] patZeich;
	
	private static HashMap<String,ArrayList<Wordbag>> wordbags;
	
	
	static{
		patZeich = new Pattern[arrZeich.length];
		for(int i=0; i<arrZeich.length; i++){
			patZeich[i] = Pattern.compile(arrZeich[i]);
		}
	}
	
	/* WordType - End */
	
	public static void setGrammars(HashMap<String,ArrayList<Wordbag>> inGrammars){
		wordbags = inGrammars;
	}
	
	public static String[] getTypeArr(){
		String lang = Util.getLanguage();
		ArrayList<Wordbag> wbs = wordbags.get(lang);
		int len = wbs.size();
		String[] ret = new String[len+1];
		
		ret[0] = "ZEICH";
		for(int i=0; i<wbs.size(); i++){
			ret[i+1] = wbs.get(i).getID();
		}
		
		return ret;
	}
	
	public static String getTypeName(String inWordType){
		if(inWordType.equalsIgnoreCase("ZEICH"))
			return Util.translateLabel("ZEICH");
		
		String lang = Util.getLanguage();
		ArrayList<Wordbag> wbs = wordbags.get(lang);
		for(Wordbag wb : wbs){
			if(wb.getID().equalsIgnoreCase(inWordType)){
				return wb.getLabel();
			}
		}
		
		return Util.translateLabel("Unknown");
	}
	
	public static String getWordType(String inWord){
		
		//1. Delimiter?
		for(Pattern p : patZeich){
			Matcher m = p.matcher(inWord);
			if(m.matches()) return "ZEICH";
		}
		
		//2. Any non-default type?
		Wordbag defaultBag = null;
		for(Wordbag wb : wordbags.get(Util.getLanguage())){
			if(wb.isDefault()){
				defaultBag = wb;
			} else {
				String tp = wb.getID();
				if(wb.contains(inWord)) return tp;
			}
		}//next wb
		
		//3. then return the default type
		if(defaultBag != null) return defaultBag.getID();
		
		//4. finally...
		return "UNKNOWN";
	}	
	
}

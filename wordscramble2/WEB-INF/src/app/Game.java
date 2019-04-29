package app;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Game {

	private static ConcurrentHashMap<String, Boolean> staticBag;
	private HashMap<String, Boolean> dynamicBag;
	
	private Word actualScramble;
	
	static{
		staticBag = new ConcurrentHashMap<String, Boolean>();
		staticBag.put("apple", Boolean.TRUE);
		staticBag.put("certitude", Boolean.TRUE);
		staticBag.put("duel", Boolean.TRUE);
		staticBag.put("elegant", Boolean.TRUE);
		staticBag.put("feminism", Boolean.TRUE);
		staticBag.put("generous", Boolean.TRUE);
		staticBag.put("hedonistic", Boolean.TRUE);
		staticBag.put("immaculate", Boolean.TRUE);
		staticBag.put("jingoistic", Boolean.TRUE);
		staticBag.put("kangaroo", Boolean.TRUE);
		staticBag.put("latitude", Boolean.TRUE);
		staticBag.put("marshmallow", Boolean.TRUE);
		staticBag.put("neglect", Boolean.TRUE);
		staticBag.put("obituary", Boolean.TRUE);
		staticBag.put("perfume", Boolean.TRUE);
		staticBag.put("quarrel", Boolean.TRUE);
		staticBag.put("rectitude", Boolean.TRUE);
		staticBag.put("sullen", Boolean.TRUE);
		staticBag.put("tease", Boolean.TRUE);
		staticBag.put("ubiquitous", Boolean.TRUE);
		staticBag.put("vulgar", Boolean.TRUE);
		staticBag.put("without", Boolean.TRUE);
	}
	
	public Game(){
		actualScramble = null;
		dynamicBag = new HashMap<>();
	}
	
	public void setActualScramble(Word inWord){
		actualScramble = inWord;
	}
	
	public Word getActualScramble(){
		return actualScramble;
	}
	
	public Word getRandomWord(){
		return chooseWord ( dynamicBag.size()==0 ? staticBag : dynamicBag );
	}
	
	private Word chooseWord(Map<String, Boolean> inMap){
		int size = inMap.size();
		int item = new Random().nextInt(size);
		int i=0;
		for(String s : inMap.keySet()){
			if(i == item)
				return new Word(s);
			i++;
		}
		return null;
	}
	
	public void addWords(Collection<String> inWords){
		for(String s : inWords){
			dynamicBag.put(s, Boolean.TRUE);
		}
	}
	
	public boolean proof(String inAnswer){
		return actualScramble.proof(inAnswer);
	}
	
	public void setNewScramble(){
		this.setActualScramble( getRandomWord() );
	}
	
}

package app;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Game {

	private static ConcurrentHashMap<String, Boolean> wordBag;
	
	private static Word actualScramble;
	
	static{
		wordBag = new ConcurrentHashMap<String, Boolean>();
		wordBag.put("apple", Boolean.TRUE);
		wordBag.put("certitude", Boolean.TRUE);
		wordBag.put("duel", Boolean.TRUE);
		wordBag.put("elegant", Boolean.TRUE);
		wordBag.put("feminism", Boolean.TRUE);
		wordBag.put("generous", Boolean.TRUE);
		wordBag.put("hedonistic", Boolean.TRUE);
		wordBag.put("immaculate", Boolean.TRUE);
		wordBag.put("jingoistic", Boolean.TRUE);
		wordBag.put("kangaroo", Boolean.TRUE);
		wordBag.put("latitude", Boolean.TRUE);
		wordBag.put("marshmallow", Boolean.TRUE);
		wordBag.put("neglect", Boolean.TRUE);
		wordBag.put("obituary", Boolean.TRUE);
		wordBag.put("perfume", Boolean.TRUE);
		wordBag.put("quarrel", Boolean.TRUE);
		wordBag.put("rectitude", Boolean.TRUE);
		wordBag.put("sullen", Boolean.TRUE);
		wordBag.put("tease", Boolean.TRUE);
		wordBag.put("ubiquitous", Boolean.TRUE);
		wordBag.put("vulgar", Boolean.TRUE);
		wordBag.put("without", Boolean.TRUE);
	}
	
	public Game(){
		actualScramble = null;
	}
	
	public void setActualScramble(Word inWord){
		actualScramble = inWord;
	}
	
	public Word getActualScramble(){
		return actualScramble;
	}
	
	public Word getRandomWord(){
		int size = wordBag.size();
		int item = new Random().nextInt(size);
		int i=0;
		for(String s : wordBag.keySet()){
			if(i == item)
				return new Word(s);
			i++;
		}
		return null;
	}
	
	public void addWords(Collection<String> inWords){
		for(String s : inWords){
			wordBag.put(s, Boolean.TRUE);
		}
	}
	
	public boolean proof(String inAnswer){
		return actualScramble.proof(inAnswer);
	}
	
}

package cc;

import java.util.ArrayList;
import java.util.Random;

public class Alphabet {

	public static String STANDARD_ALPHABET = "['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']";
	public static String STANDARD_VOCALS = "['A','E','I','O','U','Y']";
	public static String STANDARD_CONSONANTS = "['B','C','D','F','G','H','J','K','L','M','N','P','Q','R','S','T','V','W','X','Z']";

	private String jsonString;
	private String[] arr;
	private ArrayList<String> list;

	public Alphabet(String inJSONString){

		this.jsonString = inJSONString;

		String x = inJSONString.replace("[", "").replace("]", "").replace("'", "");
		this.arr = x.split(",");

		this.list = new ArrayList<String>();
		for(String h : this.arr){
			this.list.add(h);
		}

	}

	public String getJsonString() {
		return jsonString;
	}

	public String[] getArr() {
		return arr;
	}

	public ArrayList<String> getList() {
		return list;
	}

	public String getRandomLetter(){
		Random r = new Random();
		return this.list.get(r.nextInt(this.list.size()));
	}

}


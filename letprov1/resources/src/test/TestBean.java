package test;

import cc.Alphabet;
import cc.LetterProviderModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.PrimeFaces;

import com.google.gson.Gson;

@ManagedBean
@SessionScoped
public class TestBean {

	private static Alphabet alphVocals, alphConsonants, alphFull;
	static{
		alphVocals 		= new Alphabet(Alphabet.STANDARD_VOCALS);
		alphConsonants 	= new Alphabet(Alphabet.STANDARD_CONSONANTS);
		alphFull 		= new Alphabet(Alphabet.STANDARD_ALPHABET);
	}

	private Map<String, LetterProviderModel> modelMap;

	public TestBean(){

		//here we build the model(s) of what should appear on screen

		modelMap = new LinkedHashMap<String, LetterProviderModel>();

		LetterProviderModel model = new LetterProviderModel("lp1");

		modelMap.put("lp1", model);

		model.addLetter("fc1", alphVocals);
		model.addLetter("fc2", alphFull);
		model.addLetter("fc3", alphFull);
		model.addLetter("fc4", alphFull);
		model.addLetter("fc5", alphConsonants);
		model.addLetter("fc6", alphConsonants);

		ArrayList<ArrayList<String>> layout = new ArrayList<ArrayList<String>>();

		ArrayList<String> line1 = new ArrayList<String>();
			line1.add("fc1");

		ArrayList<String> line2 = new ArrayList<String>();
			line2.add("fc2");
			line2.add("fc3");
			line2.add("fc4");

		ArrayList<String> line3 = new ArrayList<String>();
			line3.add("fc5");
			line3.add("fc6");

		layout.add(line1);
		layout.add(line2);
		layout.add(line3);

		model.setLayout(layout);
	}

	public void registerFlipCard(String inCardID){
		//not really needed...
	}

	public void registerLetterProvider(String inID){
		//not really needed...
	}

	public ArrayList<ArrayList<String>> getLayout(String inHtmlID){
		ArrayList<ArrayList<String>> ret = null;
		if(this.modelMap.containsKey(inHtmlID)){
			ret = this.modelMap.get(inHtmlID).getLayout();
		}
		return ret;
	}

	public String getExpectedValues(String inHtmlID){
		String ret = "<br/>NULL";
		if(this.modelMap.containsKey(inHtmlID)){
			Map<String,String> mp = this.modelMap.get(inHtmlID).getNextLetterMap();
			ArrayList<ArrayList<String>> ly = this.modelMap.get(inHtmlID).getLayout();
			ret = "";
			for(ArrayList<String> row : ly){
				String line = "<br/>";
				for(String col : row){
					String letter = mp.get(col);
					if(line.equalsIgnoreCase("")){
						line += letter;
					} else {
						line += (" " + letter);
					}
				}
				if(ret.equalsIgnoreCase("")){
					ret += line;
				} else {
					ret += ("<br/>" + line);
				}
			}//next row
		}
		return ret;
	}

	public void animStart(String inHtmlID){
		//generate new letters
		this.generateNextLetters(inHtmlID);

		//collect actual next letters 
		Map<String, String> cards = this.getNextLetters(inHtmlID);
		String jsonNextLetters = (new Gson()).toJson(cards);
		//System.out.println("@MB :: animStart: next letters=" + getNextLetters());

		//Primefaces: add callback param
		PrimeFaces.current().ajax().addCallbackParam("nextLettersJSON", jsonNextLetters);

	}

	private Map<String, String> getNextLetters(String inHtmlID){
		LetterProviderModel model = this.modelMap.get(inHtmlID);
		return model.getNextLetterMap();
	}

	private void generateNextLetters(String inHtmlID){
		LetterProviderModel model = this.modelMap.get(inHtmlID);
		model.generateNextLetters();
	}
}


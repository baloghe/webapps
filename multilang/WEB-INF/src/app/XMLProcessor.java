package app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public final class XMLProcessor {

	private static ArrayList<Theme> themes;
	private static HashMap<String, Theme> idToTheme;
	
	private static LinkedHashMap<String, String> langsMap;
	
	static{
		langsMap = new LinkedHashMap<>();
		langsMap.put("HU", "magyar");
		langsMap.put("EN", "english");
		langsMap.put("DE", "deutsch");
		langsMap.put("FR", "français");
	}
	
	public static LinkedHashMap<String, String> getLangsMap(){
		LinkedHashMap<String, String> ret = new LinkedHashMap<>();
		for(Entry<String, String> e : langsMap.entrySet()){
			//reverse the whole thing so that Html selection shows labels and returns IDs
			ret.put(e.getValue(), e.getKey());
		}
		return ret;
	}
	
	public static LinkedHashMap<String, String> getThemesMap(){
		LinkedHashMap<String, String> ret = new LinkedHashMap<>();
		for(Theme t : themes){
			//reverse the whole thing so that Html selection shows labels and returns IDs
			ret.put(t.getLabel() + " (" + t.getQuestionNumber() + ")", t.getId());
		}
		return ret;
	}
	
	public static ArrayList<QuestionVariant> getQuestionVariants(String[] inThemeSelection, String[] inLangSelection){
		ArrayList<QuestionVariant> ret = new ArrayList<QuestionVariant>();
		
		for(String s : inThemeSelection){
			if(s!=null && !s.equalsIgnoreCase("")){
				Theme t = idToTheme.get(s);
				for(QuestionVariant v : t.getQuestionVariants(inLangSelection)){
					ret.add(v);
				}//next variant in theme
			}//
		}//next theme
		
		return ret;
	}
	
	public static void loadTest(String inRealPath, String inXmlFileName){
		
		themes = new ArrayList<>();
		idToTheme = new HashMap<>();
		
		String fname = (inRealPath==null ? "" : inRealPath + "/") + inXmlFileName;
		
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			//Document doc = dBuilder.parse(fXmlFile);
			Document doc = dBuilder.parse( fname );
			
			Node root = doc.getDocumentElement();
			
			root.normalize();
			
			NodeList themeList = doc.getElementsByTagName("theme");
			
			for(int i=0; i<themeList.getLength(); i++){
				Node nd = themeList.item(i);
				
				if (nd.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) nd;
					String strID = e.getAttribute("id");
					String strLabel = e.getAttribute("label");
					
					Theme actTheme = new Theme(strID, strLabel);
					processTheme(actTheme, nd);
					
					themes.add(actTheme);
					idToTheme.put(strID, actTheme);
					
				}//endif :: ELEMENT_NODE
			}
			
		} catch (IOException e){
			System.out.println("IO exception for FileName=" + inXmlFileName);
			e.printStackTrace();
		} catch (SAXException e){
			System.out.println("SAX exception for FileName=" + inXmlFileName);
			e.printStackTrace();
		} catch (ParserConfigurationException e){
			System.out.println("ParserConfiguration exception for FileName=" + inXmlFileName);
			e.printStackTrace();
		}
	}
	
	private static void processTheme(Theme inTheme, Node inThemeNode){
		NodeList questionList = inThemeNode.getChildNodes();
		for(int j=0; j<questionList.getLength(); j++){
			Node nd = questionList.item(j);
			if(nd.getNodeType() == Node.ELEMENT_NODE){
				Element e = (Element) nd;
				String strID = e.getAttribute("id");
				String strTimeSec = e.getAttribute("timeSec");
				boolean valid = true;
				Question q = null;
				try {
					int intTimeSec = Integer.parseInt(strTimeSec);
					q = new Question(strID, intTimeSec);
					processQuestion(q, nd);
					
					inTheme.addQuestion(q);
				} catch(NumberFormatException ex){
					valid = false;
					ex.printStackTrace();
				}
				if(valid){
					System.out.println("XMLProcessor :: Question added=" + q.toString());
				} else {
					System.out.println("XMLProcessor :: Question discarded, ID=" + strID);
				}
			}//endif :: ELEMENT_NODE
		}//next j
	}
	
	private static void processQuestion(Question inQuestion, Node inQuestionNode){
		NodeList variantList = inQuestionNode.getChildNodes();
		for(int j=0; j<variantList.getLength(); j++){
			Node nd = variantList.item(j);
			if(nd.getNodeType() == Node.ELEMENT_NODE){
				Element e = (Element) nd;
				String strLang = e.getAttribute("lang");
				String strTxt = e.getAttribute("txt");
				
				if( !langsMap.containsKey(strLang) ){
					langsMap.put(strLang, strLang);
				}
				
				QuestionVariant v = new QuestionVariant(strLang, strTxt, inQuestion);
				processVariant(v, nd);
				
				inQuestion.addVariant(strLang, v);
			}//endif :: ELEMENT_NODE
		}//next j
	}
	
	private static void processVariant(QuestionVariant inVariant, Node inVariantNode){
		NodeList hintList = inVariantNode.getChildNodes();
		for(int j=0; j<hintList.getLength(); j++){
			Node nd = hintList.item(j);
			if(nd.getNodeType() == Node.ELEMENT_NODE){
				Element e = (Element) nd;
				String strTxt = e.getAttribute("txt");
				inVariant.addHint(strTxt);
			}//endif :: ELEMENT_NODE
		}//next j
	}
	
}

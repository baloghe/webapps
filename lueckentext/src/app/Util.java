package app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public final class Util {

	private static ArrayList<String> subjects;
	private static HashMap<String,String> subjectNames;
	private static HashMap<String,ArrayList<String>> sentences;
	private static String language;
	
	private static HashMap<String,String> dict;
	
	private static ArrayList<Statistics> stats;
	
	
	
	public static void setLanguage(String inLanguage){
		language = inLanguage;
	}
	public static String getLanguage(){return language;}
	
	public static String translateLabel(String inStr){
		String ret = dict.get(language + "#" + inStr);
		return (ret==null ? "?? inStr ??" : ret);
	}
	
	public static ArrayList<String> getSubjects(){return subjects;}
	public static String getSubjectName(String inSubjID){
		return subjectNames.get(inSubjID);
	}
	public static ArrayList<String> getSubjectSentences(String inSubjID){
		return sentences.get(inSubjID);
	}
	
	public static void addStatistics(Statistics inStat){
		stats.add(inStat.copy());
	}
	
	public static String statisticsToHtmlString(){
		String ret = "<table id='TBL_RESULTLOG'><thead><tr><td># Sent</td><td>Secs</td><td>Good W</td><td>Bad W</td><td>Rate W (%)</td><td>Good S</td><td>Bad S</td><td>Rate S (%)</td></tr></thead><tbody>";
		for(Statistics s : stats){
			ret += s.toHtmlString();
		}
		ret += "</tbody></table>";
		return ret;
	}
	
	public static void init(String contextPath){
		
		loadDict(contextPath + "/"+"dict.xml");
		loadGrammar(contextPath + "/"+"grammar.xml");
		loadTests(contextPath + "/"+language+".xml");
		
		stats = new ArrayList<Statistics>();
	}
	
	public static void loadDict(String inXmlFileName){
		dict = new HashMap<String,String>();
		
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			//Document doc = dBuilder.parse(fXmlFile);
			Document doc = dBuilder.parse( inXmlFileName );
			
			Node root = doc.getDocumentElement();
			
			root.normalize();
			
			NodeList labs = doc.getElementsByTagName("label");
			
			for(int i=0; i<labs.getLength(); i++){
				Node nd = labs.item(i);
				
				if (nd.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) nd;
					String strID = e.getAttribute("id");
					
					NodeList trans = e.getChildNodes();
					for(int j=0; j<trans.getLength(); j++){
						Node tr = trans.item(j);
						
						if(tr.getNodeType() == Node.ELEMENT_NODE){
							Element te = (Element) tr;
							String strLang = te.getAttribute("lang");
							String strTxt = te.getAttribute("txt");
							
							dict.put(strLang + "#" + strID, strTxt);
						}//endif :: ELEMENT_NODE
					}//next j
				}//endif :: ELEMENT_NODE
				
			}//next i
			
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
	
	public static void loadGrammar(String inXmlFileName){
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			//Document doc = dBuilder.parse(fXmlFile);
			Document doc = dBuilder.parse( inXmlFileName );
			
			Node root = doc.getDocumentElement();
			
			root.normalize();
			
			NodeList grammars = doc.getElementsByTagName("grammar");
			
			HashMap<String,ArrayList<Wordbag>> grs = new HashMap<String,ArrayList<Wordbag>>();
			
			for(int i=0; i<grammars.getLength(); i++){
				Node nd = grammars.item(i);
				
				if (nd.getNodeType() == Node.ELEMENT_NODE) {
					String strLang = ((Element)nd).getAttribute("lang");
					PriorityQueue<Wordbag> bags = parseGrammar((Element)nd);
					
					ArrayList<Wordbag> arrLst = new ArrayList<Wordbag>();
					while(!bags.isEmpty()){
						arrLst.add( bags.poll() );
					}
					
					grs.put(strLang, arrLst);
				}//endif :: ELEMENT_NODE
			}//next i
			
			WordType.setGrammars(grs);
			
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
	
	private static PriorityQueue<Wordbag> parseGrammar(Element inGrammarNode){
		PriorityQueue<Wordbag> ret = new PriorityQueue<Wordbag>();
		
		NodeList wts = inGrammarNode.getChildNodes();
		for(int i=0; i<wts.getLength(); i++){
			Node nd = wts.item(i);
			if(   nd.getNodeType() == Node.ELEMENT_NODE
			   && nd.getNodeName() == "wordtype"       ){
				Element wte = (Element)nd;
				
				String strID = wte.getAttribute("id");
				String strLabel = wte.getAttribute("label");
				int ord = Integer.parseInt(wte.getAttribute("order"));
				boolean isDefault = (wte.getAttribute("default") != null && wte.getAttribute("default").equalsIgnoreCase("Y") ? true : false);
				
				Wordbag wb = new Wordbag(strID, strLabel, ord, isDefault);
				
				NodeList wbs = wte.getChildNodes();
				for(int j=0; j<wbs.getLength(); j++){
					Node nd2 = wbs.item(j);
					if(   nd2.getNodeType() == Node.ELEMENT_NODE
					   && nd2.getNodeName() == "wordbag"        ){
						Element wbe = (Element)nd2;
						
						NodeList wl = wbe.getChildNodes();
						for(int k=0; k<wl.getLength(); k++){
							Node nd3 = wl.item(k);
							if(   nd3.getNodeType() == Node.ELEMENT_NODE
							   && nd3.getNodeName() == "word"           ){
								Element we = (Element)nd3;
								String strTxt = we.getAttribute("txt");
								wb.addWord(strTxt);
								
							}//endif :: ELEMENT_NODE
						}//next k
						
					}//endif :: ELEMENT_NODE
					
				}//next j
				
				ret.add(wb);
				
			}//endif :: ELEMENT_NODE
		}//next i
		
		return ret;
	}
	
	public static void loadTests(String inXmlFileName){
		
		System.out.println("Util :: init inXmlFileName="+inXmlFileName);
		
		subjects = new ArrayList<String>();
		subjectNames = new HashMap<String,String>();
		sentences = new HashMap<String,ArrayList<String>>();
		
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			//Document doc = dBuilder.parse(fXmlFile);
			Document doc = dBuilder.parse( inXmlFileName );
			
			Node root = doc.getDocumentElement();
			
			root.normalize();
			
			NodeList subjs = doc.getElementsByTagName("subject");
			NodeList sents = doc.getElementsByTagName("sentence");
			
			//subjects
			for(int i=0; i<subjs.getLength(); i++){
				Node nd = subjs.item(i);
				
				if (nd.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) nd;
					String strSubject = e.getAttribute("id");
					String strName = e.getAttribute("name");
				
					subjects.add(strSubject);
					subjectNames.put(strSubject, strName);
					System.out.println(" Subject added ID="+strSubject + ", name=" + strName);
				}//endif :: ELEMENT_NODE
				
			}//next i
			System.out.println(" Subject num="+subjectNames.size());
			
			//sentences
			for(int i=0; i<sents.getLength(); i++){
				Node nd = sents.item(i);
				
				if (nd.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) nd;
					String strSubject = e.getAttribute("subjectID");
					String strTxt = e.getAttribute("txt");
				
					ArrayList<String> lst = null;
					ArrayList<String> act = sentences.get(strSubject);
					if(act==null){
						lst = new ArrayList<String>();
					} else {
						lst = act;
					}
					
					lst.add(strTxt);
					sentences.put(strSubject, lst);
				}//endif :: ELEMENT_NODE
				
			}//next i
			
			
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
	
	public static String[] splitText(String inStr){
		
		String tmpstr = inStr;
		for(String z : WordType.arrZeich){
			tmpstr = tmpstr.replaceAll(z, " "+z+" ");
			//System.out.println("splitText :: z=" + z + "  -> tmpstr=" + tmpstr);
		}
		tmpstr = tmpstr.replaceAll("\\s+", " ");
		
		return tmpstr.split(" ");
		
	}
	
}

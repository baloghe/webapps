package test;
 
import java.util.LinkedHashMap;
import java.util.Map;
 
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
 
import org.primefaces.PrimeFaces;
 
import com.google.gson.Gson;
 
@ManagedBean
@SessionScoped
public class TestBean2 {
 
               private Map<String, String> cards;
               private static String[] fixedLetterOrder = {"A","B","C"};
               private int fixedLetterPointer = 0;
              
               public TestBean2(){
                              cards = new LinkedHashMap<String, String>();
                              fixedLetterPointer = 0;
               }
              
               /**
               * registers a FlipCard
               * @param inID
               */
               public void registerCard(String inID){
                              if(!cards.containsKey(inID)){
                                            cards.put(inID, getNextLetter());
                                            //System.out.println("@MB :: Registered: " + inID);
                              }
               }
              
               /**
               * 1: generates the next expected outcome for all registered FlipCards,
                * 2: pushes back this result in JSON format to the Client side via PrimeFaces' addCallbackParam
               */
               public void animStart(){
                              //reset new letters
                              for(String id : cards.keySet()){
                                            cards.put(id, getNextLetter());
                              }
                              //System.out.println("@MB :: animStart: next letters=" + getNextLetters());
                             
                              //Primefaces: add callback param
                              PrimeFaces.current().ajax().addCallbackParam("nextLettersJSON", getNextLettersJSON());
               }
              
               /**
               * returns the next letter for a single FlipCard registered to this bean
               * @param inID DOM id of the FlipCard element
               * @return letter = String of length 1
               */
               public String getExpectedValue(String inID){
                              return cards.get(inID);
               }
              
               /**
               * for logging purposes
               * @return description of the {FlipCard -> next letter} map
               */
               public String getNextLetters(){
                              String ret = "";
                              for(Map.Entry<String, String> e : cards.entrySet()){
                                            if(ret.length()>0){
                                                           ret += ", ";
                                            }
                                            ret += (e.getKey() + " -> " + e.getValue());
                              }
                              return ret;
               }
              
               private String getNextLettersJSON(){
                              String ret = (new Gson()).toJson(cards);
                              //System.out.println("getNextLettersJSON :: returned=" + ret);
                              return ret;
               }
              
               /**
               * generates the next letter. In our case it simply loops through a predefined (fixed) array
                * @return letter = String of length 1
               */
               protected String getNextLetter(){
                              String ret = TestBean2.fixedLetterOrder[fixedLetterPointer++];
                              fixedLetterPointer = fixedLetterPointer % TestBean2.fixedLetterOrder.length;
                              return ret;
               }
}
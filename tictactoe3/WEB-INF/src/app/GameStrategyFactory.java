package app;

import java.util.HashMap;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="strategyBean")
@SessionScoped
public class GameStrategyFactory {
	
	public static final int STRATEGY_RANDOM = 1;
	public static final int STRATEGY_CALLOUS = 2;
	
	public static final HashMap<String, Integer> STRATEGIES;
	public static final HashMap<Integer, String> REVERSE_STRATEGIES;
	static{
		STRATEGIES = new HashMap<String, Integer>();
		REVERSE_STRATEGIES = new HashMap<Integer, String>();
		STRATEGIES.put("Random shooter", STRATEGY_RANDOM);
		REVERSE_STRATEGIES.put(STRATEGY_RANDOM, "Random shooter");
		STRATEGIES.put("Callous challenger", STRATEGY_CALLOUS);
		REVERSE_STRATEGIES.put(STRATEGY_CALLOUS, "Callous challenger");
	}
	
	//empty constructor for fulfill ManagedBean requirements 
	public GameStrategyFactory(){}
	
	//to be called from web
	public HashMap<String, Integer> getStrategies(){return STRATEGIES;}
	
	public static GameStrategy getStrategy(int inStrategyType, Board inBoard, int inSymbol){
		//TBD welcome new strategy types
		//for now: a single Random strategy will be generated
		switch(inStrategyType){
			case STRATEGY_RANDOM: 
				return new RandomStrategy(inBoard, inSymbol);
			case STRATEGY_CALLOUS: 
				return new CallousStrategy(inBoard, inSymbol);
			default: 
				return null;
		}
	}
	
	public static String getStrategyName(int inStrategyType){
		String ret = REVERSE_STRATEGIES.get(inStrategyType);
		if(ret == null)
			return "Unknown";
		else return ret;
	}
}

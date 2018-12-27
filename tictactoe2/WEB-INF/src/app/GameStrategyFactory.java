package app;

import java.util.HashMap;

public class GameStrategyFactory {
	
	public static final int STRATEGY_RANDOM = 1;
	public static final int STRATEGY_CALLOUS = 2;
	
	public static final HashMap<Integer, String> STRATEGIES;
	static{
		STRATEGIES = new HashMap<Integer, String>();
		STRATEGIES.put(STRATEGY_RANDOM, "Random shooter");
		STRATEGIES.put(STRATEGY_CALLOUS, "Callous challenger");
	}
	
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
		String ret = STRATEGIES.get(inStrategyType);
		if(ret == null)
			return "Unknown";
		else return ret;
	}
}

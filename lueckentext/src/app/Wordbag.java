package app;

import java.util.HashSet;

public class Wordbag implements Comparable<Wordbag> {
	
	private HashSet<String> bag;
	private String id;
	private String label;
	private int order;
	private boolean isdefault;
	
	public Wordbag(String inID, String inLabel, int inOrder, boolean inIsDefault){
		this.bag = new HashSet<String>();			
		this.id = inID;
		this.label = inLabel;
		this.order = inOrder;
		this.isdefault = inIsDefault;
	}
	
	public void addWord(String inWord){
		bag.add( inWord.toUpperCase() );
	}
	
	public boolean contains(String inWord){
		return bag.contains( inWord.toUpperCase() );
	}
	
	public String getID(){return this.id;}
	public String getLabel(){return this.label;}
	public HashSet<String> getBag(){return this.bag;}
	public boolean isDefault(){return this.isdefault;}

	@Override
	public int compareTo(Wordbag inWB) {
		return this.order - inWB.order;
	}
			 
}

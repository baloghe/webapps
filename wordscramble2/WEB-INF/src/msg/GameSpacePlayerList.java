package msg;

import java.util.ArrayList;

import manager.Player;

public class GameSpacePlayerList extends Message {
	private int count;
	private ArrayList<PlayerDescriptor> list;
	
	public GameSpacePlayerList(String inType, String inSender, String inContent){
		super(inType, inSender, inContent);
		count = 0;
		list = new ArrayList<>();
	}
	
	public void setCount(int inCount){this.count = inCount;}
	public int getCount(){return this.count;}
	
	public void setList(ArrayList<PlayerDescriptor> inList){this.list = inList;}
	public ArrayList<PlayerDescriptor> getList(){return this.list;}
	
	public void addPlayerDescriptor(PlayerDescriptor inPD){
		this.list.add(inPD);
	}
}

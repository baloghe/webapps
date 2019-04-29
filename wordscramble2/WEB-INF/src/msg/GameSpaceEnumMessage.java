package msg;

import java.util.ArrayList;

public class GameSpaceEnumMessage extends Message {
	private int count;
	private ArrayList<GameSpaceDescriptor> list;
	
	public GameSpaceEnumMessage(String inType, String inSender, String inContent){
		super(inType, inSender, inContent);
		count = 0;
		list = new ArrayList<>();
	}
	
	public void addDescriptor(GameSpaceDescriptor inDescriptor){this.list.add(inDescriptor);}
	
	public void setCount(int inCount){this.count = inCount;}
	public int getCount(){return this.count;}
	
	public void setList(ArrayList<GameSpaceDescriptor> inList){this.list = inList;}
	public ArrayList<GameSpaceDescriptor> getList(){return this.list;}
}

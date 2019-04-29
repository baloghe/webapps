package msg;

public class PlayerDescriptorMessage extends Message {
	private PlayerDescriptor player;
	
	public PlayerDescriptorMessage(String inType, String inSender, String inContent){
		super(inType, inSender, inContent);
	}
	
	public void setPlayer(PlayerDescriptor inDescriptor){
		this.player = inDescriptor;
	}
	
	public PlayerDescriptor getPlayer(){return this.player;}
}

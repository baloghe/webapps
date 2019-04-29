package msg;

public class GameSpaceDescriptor {
	private long id;
	private String title;
	private int players;
	
	public GameSpaceDescriptor(){}
	
	public void setId(long inId){this.id = inId;}
	public long getId(){return this.id;}
	
	public void setTitle(String inTitle){this.title = inTitle;}
	public String getTitle(){return this.title;}
	
	public void setPlayers(int inPlayers){this.players = inPlayers;}
	public int getPlayers(){return this.players;}
}

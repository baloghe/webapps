package app;

import javax.websocket.Session;

public class Player {

	private Session session;
	private String name;
	private int score;
	
	public Player(Session inSession){
		this.session = inSession;
		this.score = 0;
	}
	
	public Session getSession(){return this.session;}
	
	public void setName(String s){this.name = s;}
	public String getName(){return this.name;}
	
	public void incScore(){this.score++;}
	public int getScore(){return this.score;}
	
	public void notifyPlayerJoined(Player inOtherPlayer){
		//send a message to the own client
		
	}
	
}

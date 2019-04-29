package manager;

import javax.websocket.Session;

public class Player {

	private Session session;
	private String name;
	private int score;
	
	public Player(Session inSession){
		this.session = inSession;
		resetScore();
	}
	
	public Session getSession(){return this.session;}
	
	public void setName(String s){this.name = s;}
	public String getName(){return this.name;}
	
	public void incScore(){this.score++;}
	public int getScore(){return this.score;}
	public void resetScore(){this.score = 0;}
	
	public void notifyPlayerJoined(Player inOtherPlayer){
		//send a message to the own client
		
	}
	
}

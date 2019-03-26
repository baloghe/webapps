package app;

import java.io.EOFException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;

import msg.*;


@ServerEndpoint(
		value = "/srv"/*,
		decoders = { MessageDecoder.class }*/,
		encoders = { MessageEncoder.class }
	)
public class GameManager {

	private static final Map<String, Player> players = new ConcurrentHashMap<>();
	private static int cntConnected = 0;
	
	private Session session;
	private Player player;   //https://techblog.bozho.net/websocket-and-java/
	
	private static Game game;
	
	@OnOpen
	public void onOpen(Session inSession){
		System.out.println("onOpen called");
		cntConnected++;
		this.session = inSession;
		Player player = new Player(session);
	    players.put(session.getId(), player);
    	connConfirm(session);
    	
	    
	    /* first user instantiates the game object */
	    if(game==null){
	    	game = new Game();
	    	System.out.println("new Game object created");
	    	setNewScramble();
	    }
	    
	    /* send the actual scramble to the new user */
	    this.sendScramble(this.session, game.getActualScramble().getScramble());
	}
	
	/*
	@OnMessage
	public void digestMessage(Message inMessage, Session inSession){
		
		System.out.println("digestMessage called");
		
		switch(inMessage.getType()){
			case Message.JOIN :
				System.out.println("incoming JOIN message");
				join(inMessage.getSender(), inSession);
				break;
		}
		
	}
	*/
	
	@OnMessage
	public void digestMessage(String inMessage, Session inSession){
		
		System.out.println("digestMessage called, txt=" + inMessage);
				
		Gson gson = new Gson();
		Message message = gson.fromJson(inMessage, Message.class);
		
		switch(message.getType()){
			case Message.JOIN :
				System.out.println("incoming JOIN message, sender=" + message.getSender());
				this.join(message.getSender(), inSession);
				break;
			case Message.ANSWER :
				System.out.println("incoming ANSWER message, sender=" + message.getSender() + ", content=" + message.getContent());
				this.answer(inSession, message.getContent());
				break;
		}//end switch
	}
	
	
	@OnClose
    public void onClose(Session session){
		if(players.containsKey(session.getId())){
			String sid = session.getId();
			players.remove(sid);
			broadcastQuit(session);
		}
	}
	
	@OnError
    public void onError(Throwable t) throws Throwable {
		//source: https://www.programcreek.com/java-api-examples/?api=javax.websocket.OnError
	    int count = 0;
	    Throwable root = t;
	    while (root.getCause() != null && count < 20) {
	        root = root.getCause();
	        count ++;
	    }
	    if (root instanceof EOFException) {
	        // Assume this is triggered by the user closing their browser and
	        // ignore it.
	    } else {
	        throw t;
	    }
	}
	
	public void connConfirm(Session inSession){
		Message msg = new Message(Message.CONN_CONFIRM, "SRV", "");
		msg.setSessionID(inSession.getId());
		try{
			inSession.getBasicRemote().sendObject( msg );
			System.out.println("CONN_CONFIRM message sent to sessionID=" + inSession.getId());
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void join(String inPlayerName, Session inSession){
		//set player name
		players.get(inSession.getId()).setName(inPlayerName);
		System.out.println("join :: name set");
		
		//send a JOIN message to the other players' clients
		//and notify our client of already connected users
		for(Entry<String, Player> e : players.entrySet()){
			
			if( !(e.getKey().equalsIgnoreCase(inSession.getId()) ) ){
				
				//send a JOIN message to the other players' clients
				Message msg = new Message(Message.JOIN, "SRV", inPlayerName);
				msg.setSessionID(inSession.getId());
				Session targetSession = e.getValue().getSession();
				try{
					targetSession.getBasicRemote().sendObject( msg );
					System.out.println("JOIN message sent to " + e.getValue().getName());
				} catch(Exception ex){
					ex.printStackTrace();
				}
				
				//notify our client of already connected users
				msg = new Message(Message.JOIN, "SRV", e.getValue().getName());
				msg.setSessionID(e.getValue().getSession().getId());
				targetSession = inSession;
				try{
					targetSession.getBasicRemote().sendObject( msg );
					System.out.println("JOIN message sent to " + inPlayerName);
				} catch(Exception ex){
					ex.printStackTrace();
				}
				
			}//endif
		}//next e
		
	}
	
	public void answer(Session inSession, String inAnswer){
		boolean b = game.proof(inAnswer);
		//confirm answer
		confirmAnswer(inAnswer, this.session, b);
		
		//if OK move on
		if( b ){
			//increase client score
			Player p = players.get(inSession.getId());
			if(p != null)
				p.incScore();
			else System.out.println("answer :: Player at sessionID=" + inSession.getId() + " is NULL !!!");
			
			//ask for new scramble
			this.setNewScramble();
			
			//broadcast new scramble
			this.broadcastScramble();
			
			//broadcast client score
			this.broadcastScore();
			
		}
	}
	
	public void broadcastScramble(){
		String scramble = game.getActualScramble().getScramble();
		for(Entry<String, Player> e : players.entrySet()){
			sendScramble(e.getValue().getSession(), scramble);			
		}
	}
	
	public void sendScramble(Session inTargetSession, String inScramble){
		Message msg = new Message(Message.SCRAMBLE, "SRV", inScramble);
		msg.setSessionID(inTargetSession.getId());
		try{
			inTargetSession.getBasicRemote().sendObject( msg );
			System.out.println("SCRAMBLE message sent to sessionID=" + inTargetSession.getId());
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void broadcastScore(){
		int userScore = players.get(session.getId()).getScore();
		Session userSession = session;
		for(Entry<String, Player> e : players.entrySet()){
			Session targetSession = e.getValue().getSession();
			sendScore(targetSession, userSession, userScore);
		}		
	}
	
	public void sendScore(Session inTargetSession, Session inUserSession, int inScore){
		Message msg = new Message(Message.SCORE, inUserSession.getId(), Integer.toString(inScore));
		msg.setSessionID(inTargetSession.getId());
		try{
			inTargetSession.getBasicRemote().sendObject( msg );
			System.out.println("SCORE message sent to sessionID=" + inTargetSession.getId() + ", sender ID=" + inUserSession.getId());
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void broadcastQuit(Session inUserSession){
		for(Entry<String, Player> e : players.entrySet()){
			Session targetSession = e.getValue().getSession();
			sendQuit(targetSession, inUserSession);
		}
	}
	
	public void sendQuit(Session inTargetSession, Session inUserSession){
		Message msg = new Message(Message.QUIT, inUserSession.getId(), "");
		msg.setSessionID(inTargetSession.getId());
		try{
			inTargetSession.getBasicRemote().sendObject( msg );
			System.out.println("QUIT message sent to sessionID=" + inTargetSession.getId() + ", sender ID=" + inUserSession.getId());
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void confirmAnswer(String inAnswer, Session inSession, boolean inProof){
		Message msg = new Message(Message.ANS_CONFIRM, inAnswer, (inProof ? "YES" : "NO"));
		msg.setSessionID(inSession.getId());
		try{
			inSession.getBasicRemote().sendObject( msg );
			System.out.println("ANS_CONFIRM message sent to sessionID=" + inSession.getId());
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void setNewScramble(){
		Word w = game.getRandomWord();
    	game.setActualScramble( w );
    	System.out.println("first scramble set to " + w.getScramble() + " <--> " + w.getOriginal());
	}
	
}

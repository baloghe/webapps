package manager;

import java.io.EOFException;
import java.util.ArrayList;
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
import app.*;


@ServerEndpoint(
		value = "/srv"/*,
		decoders = { MessageDecoder.class }*/,
		encoders = { MessageEncoder.class, GameSpaceEnumMessageEncoder.class, GameSpacePlayerListEncoder.class, PlayerDescriptorMessageEncoder.class }
	)
public class GameManager {

	private static final Map<Long, GameSpace> gameSpaces = new ConcurrentHashMap<>();
	private static final Map<String, Player> players = new ConcurrentHashMap<>();//players may be connected to the Server but not joined to any Space
	private static int cntConnected = 0;
	
	private static final int CNT_MAX_GAMESPACES = 3;
	private static final int G_CNT_MAX_USER_PER_GAMESPACE = 5;
	
	private Session session;
	private Player player;   //https://techblog.bozho.net/websocket-and-java/
	private GameSpace gameSpace;
	
	@OnOpen
	public void onOpen(Session inSession){
		System.out.println("onOpen called");
		cntConnected++;
		this.session = inSession;
		this.player = new Player(session);
		players.put(session.getId(), player);
		System.out.println("Player added: " + player.getSession().getId() + ", players.size=" + players.size());
    	connConfirm(session);
    	//set everything else to NULL
    	gameSpace = null;
	}
	
	@OnMessage
	public void digestMessage(String inMessage, Session inSession){
		
		System.out.println("SessionID="+this.session.getId()+": digestMessage called, txt=" + inMessage);
				
		Gson gson = new Gson();
		Message message = gson.fromJson(inMessage, Message.class);
		
		switch(message.getType()){
			case Message.JOIN_SERVER :
				System.out.println("SessionID="+this.session.getId()+": incoming JOIN_SERVER message, sender=" + message.getSender());
				this.joinServer(message.getSender(), inSession);
				break;
			case Message.JOIN_SPACE :
				System.out.println("SessionID="+this.session.getId()+": incoming JOIN_SPACE message, sender=" + message.getSender());
				long spaceID = Long.parseLong(message.getContent());
				this.joinSpace(inSession, spaceID);
				break;
			case Message.CREATE_SPACE :
				System.out.println("SessionID="+this.session.getId()+": incoming CREATE_SPACE message, sender=" + message.getSender());
				String[] lst = (message.getContent().split(","));
				this.createSpace(inSession, lst);
				break;
			case Message.QUIT_SPACE :
				System.out.println("SessionID="+this.session.getId()+": incoming QUIT_SPACE message, sender=" + message.getSender());
				this.quitSpace(inSession);
				break;
			case Message.ANSWER :
				System.out.println("SessionID="+this.session.getId()+": incoming ANSWER message, sender=" + message.getSender() + ", content=" + message.getContent());
				this.answer(inSession, message.getContent());
				break;
		}//end switch
	}
	
	
	@OnClose
    public void onClose(Session session){
		long gameSpaceID = gameSpace.getID();
		if(gameSpaces.containsKey(gameSpaceID)){
			//remove player from the GameSpace
			GameSpace gs = gameSpaces.get(gameSpaceID);
			String sid = session.getId();
			gs.removePlayer(sid);
			//remove player from broadcast list
			players.remove(session.getId());
			//broadcast change in users
			broadcastDisconnect(session);
			//abandon empty GameSpace
			if(gs.getPlayerNumber()==0){
				gameSpaces.remove(gs.getID());
				broadcastGameSpaces();
			}
		}
		//set them to NULL
		gameSpace = null;
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
	
	public Player getPlayer(){return this.player;}
	
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
	
	public void joinServer(String inPlayerName, Session inSession){
		//set player name
		player.setName(inPlayerName);
		System.out.println("join :: name set");
		
		//enumerate available GameSpaces and send them over to the just joined user
		GameSpaceEnumMessage msg = getGameSpaceEnumMessageTemplate();
		Session targetSession = this.player.getSession();
		msg.setSessionID(targetSession.getId());
		sendMessageToWebClient(targetSession, msg);
		
	}
	
	public void answer(Session inSession, String inAnswer){
		boolean b = gameSpace.proof(inAnswer);
		//confirm answer
		gameSpace.confirmAnswer(inAnswer, b, this.session);
		
		//if OK move on
		if( b ){
			//increase client score
			player.incScore();
			
			//ask for new scramble
			gameSpace.setNewScramble();
			
			//broadcast client score
			gameSpace.broadcastScore(this.player);
			
		}
	}
	
	public void sendMessageToWebClient(Session inTargetSession, Message inMsg){
		try{
			if(inTargetSession.isOpen()){
				inTargetSession.getBasicRemote().sendObject( inMsg );
				System.out.println("sendMessageToWebClient :: sessionID="+this.session.getId()+" sent type="+inMsg.getType()+" to sessionID="+inTargetSession.getId());
			} else {
				System.out.println("---Attempted to send message to SessionID=" + inTargetSession.getId() + " already being closed!");
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private GameSpaceEnumMessage getGameSpaceEnumMessageTemplate(){
		GameSpaceEnumMessage msg = new GameSpaceEnumMessage(Message.GAME_SPACE_ENUM, player.getSession().getId(), "");
		msg.setCount(gameSpaces.size());
		for(GameSpace gs : gameSpaces.values()){
			GameSpaceDescriptor gsd = new GameSpaceDescriptor();
			gsd.setId(gs.getID());
			gsd.setTitle(gs.getTitle());
			gsd.setPlayers(gs.getPlayerNumber());
			msg.addDescriptor(gsd);
		}
		return msg;
	}
	/*
	private GameSpacePlayerList getGameSpacePlayerList(){
		GameSpacePlayerList msg = new GameSpacePlayerList(Message.GAME_SPACE_ENUM, player.getSession().getId(), "");
		msg.setCount(this.gameSpace.getPlayerNumber());
		for(Player p : gameSpace.getPlayerList()){
			PlayerDescriptor pd = new PlayerDescriptor(p.getName(), p.getSession().getId(), p.getScore());
			msg.addPlayerDescriptor(pd);
		}
		return msg;
	}
	*/
	
	public void broadcastGameSpaces(){
		GameSpaceEnumMessage msg = getGameSpaceEnumMessageTemplate();
		for(Entry<String, Player> e : players.entrySet()){
			Session targetSession = e.getValue().getSession();
			msg.setSessionID(targetSession.getId());
			sendMessageToWebClient(targetSession, msg);
		}
	}
	
	public void broadcastDisconnect(Session inUserSession){
		for(Entry<String, Player> e : players.entrySet()){
			Session targetSession = e.getValue().getSession();
			//message itself
			Message msg = new Message(Message.QUIT_SERVER, inUserSession.getId(), "");
			msg.setSessionID(targetSession.getId());
			sendMessageToWebClient(targetSession, msg);
		}
	}
	
	public void joinSpace(Session inSession, long spaceID){
		if(gameSpaces.containsKey(spaceID)){
			this.gameSpace = gameSpaces.get(spaceID);
			if( this.gameSpace.getPlayerNumber() < GameManager.G_CNT_MAX_USER_PER_GAMESPACE ){
				//confirm join
				Message msg = new Message(Message.JOIN_SPACE_CONFIRM, this.player.getSession().getId(), Long.toString(this.gameSpace.getID()) );
				msg.setSessionID(this.player.getSession().getId());
				sendMessageToWebClient(this.player.getSession(), msg);
				
				//register player in the GameSpace
				//send join message to other players in the room
				this.gameSpace.join(player);
				
				//send actual opponent list to the user
				//this.gameSpace.sendSelfOtherPlayers();
				
				//send current scramble to the user
				this.gameSpace.sendSelfActualScramble(this.player.getSession());
				
				//send gamespace info to other users --> done as last step after IF-ELSE
				//broadcastGameSpaces();
				
				//log
				System.out.println("SessionID="+this.session.getId()+" succesfully joined to gamespace "+spaceID);				
			} else {
				//reject join
				Message msg = new Message(Message.JOIN_SPACE_REJECT, this.player.getSession().getId(), "" );
				msg.setSessionID(this.player.getSession().getId());
				sendMessageToWebClient(this.player.getSession(), msg);
				//log
				System.out.println("Join to gamespace "+spaceID+" rejected");
			}
		}
		broadcastGameSpaces();
	}
	
	public void createSpace(Session inSession, String[] inKeyWords){
		if( gameSpaces.size() < CNT_MAX_GAMESPACES ){
			this.gameSpace = new GameSpace(this);
			gameSpaces.put(this.gameSpace.getID(), this.gameSpace);
			for(String kw : inKeyWords){
				this.gameSpace.addKeyWord(kw);
			}
			//add this user to the gamespace
			this.gameSpace.addPlayer(this.player.getSession(), this.player);
			//confirm creation
			Message msg = new Message(Message.CREATE_SPACE_CONFIRM, this.player.getSession().getId(), Long.toString(this.gameSpace.getID()) );
			msg.setSessionID(this.player.getSession().getId());
			sendMessageToWebClient(this.player.getSession(), msg);
			
			//send gamespace info to other users
			broadcastGameSpaces();
			
			//gameSpace has just been open => start the game automatically
			gameSpace.setNewScramble();
			gameSpace.startSpace();
						
			//log
			System.out.println("New space created (ID="+this.gameSpace.getID()+"). Number of spaces=" + gameSpaces.size());
		} else {
			//reject creation
			Message msg = new Message(Message.CREATE_SPACE_REJECT, this.player.getSession().getId(), "" );
			msg.setSessionID(this.player.getSession().getId());
			sendMessageToWebClient(this.player.getSession(), msg);
			//log
			System.out.println("New space creation rejected");
		}
	}
	
	public void quitSpace(Session inSession){
		this.gameSpace.removePlayer(inSession.getId());
		//remove GameSpace if nobody is present
		if(this.gameSpace.getPlayerNumber()==0){
			gameSpaces.remove(gameSpace.getID());
			gameSpace.stop();
		}
		this.gameSpace = null;
		broadcastGameSpaces();
	}
	
}

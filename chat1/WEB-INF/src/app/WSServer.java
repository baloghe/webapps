package app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/srv", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class WSServer {

	public enum UserNames{
		ALICE, BRENDON, CECIL, DOMINIQUE;
		private static UserNames[] vals = values();
		public UserNames next(){
			return vals[(this.ordinal()+1) % vals.length];
		}
	}
	
	//[^!@#$%^&*]*(foobar|foobaz|foofii)[^!@#$%^&*]*
	//taken from https://stackoverflow.com/questions/8319136/profanity-filter-using-a-regular-expression-list-of-100-words
	private static final String dictionary = "(?)[^!@#$%^&*]*(fuck|bitch|prick|bastard|bellend|arse|cunt)[^!@#$%^&*]*";
	private static final Pattern swPattern = Pattern.compile(dictionary);
	private Matcher matcher;
	
	private static int MAX_USER_NUMBER = 2;
	
	private static UserNames ptrNextName = UserNames.ALICE;
	private static final Set<WSServer> serverEndpoints = new CopyOnWriteArraySet<>();
	
	private Session session;
	private UserNames username;
	
	static int cntConnected = 0;
	
	@OnOpen
	public void onOpen(Session inSession){
		//maximize number of participants
		if( cntConnected < MAX_USER_NUMBER ){
			cntConnected++;
			this.session = inSession;
			this.username = ptrNextName;
			serverEndpoints.add(this);
			sendNameRemote();
			broadcast("msgConnect", this.username + " connected", "Connected");
			enumConnectedUsers(null);
			
			System.out.println(session.getId() + " -> " + this.username);
			ptrNextName = this.username.next(); //step pointer to the next predefined username
		} else {
			//notify user he/she couldn't join
			try{
				Message m = new Message();
	    		m.setType("msgConnRefused");
	    		m.setFrom("Server");
	    		m.setTo(null);
	    		m.setContent("Already " + cntConnected + " participants! Connection refused.");
				inSession.getBasicRemote().sendObject(m);
				inSession.close();
			} catch(IOException | EncodeException e){
				System.out.println("IOException or EncodeException at onOpen / close session");
				e.printStackTrace();
			}
		}
	}
	
	@OnMessage
	public void dispatchMessage(String inMessage, Session inSession){
		//filter received text for dictionary
		String msg = inMessage;
		matcher = swPattern.matcher(inMessage);
		boolean rebuke = false;
		if(matcher.find()){
			msg = "[message failed on profanity check]";
			rebuke = true;
		}
		//broadcast message in name of the sender
		sendSimpleMessage(msg);
		//rebuke user if necessary
		if(rebuke){
			sendRebukeRemote();
		}
	}
	
	private void sendSimpleMessage(String inText){
		for(WSServer endpoint : serverEndpoints){
			Message m = new Message();
			m.setType("msgSimple");
			m.setFrom(this.username.name());
			m.setTo(endpoint.username.name());
			m.setContent(inText);
			synchronized (endpoint) {
				try {
					endpoint.session.getBasicRemote().sendObject(m);
				} catch (IOException | EncodeException e) {
                	System.out.println("IOException at sendSimpleMessage");
                    e.printStackTrace();
                }
			}//end synchronized
		}
	}
	
	@OnClose
    public void onClose(Session session){
		serverEndpoints.remove(this);
		cntConnected--;
		broadcast("msgClose", this.username + " quit", "Disconnected");
		enumConnectedUsers(null);
	}
	
	private void broadcast(String inMessageType, String inMessage, String inSelfMessage){
		for(WSServer endpoint : serverEndpoints){
			Message m = new Message();
    		m.setType(inMessageType);
    		m.setFrom("Server");
    		m.setTo(endpoint.username.name());
    		synchronized (endpoint) {
                try {
                	if( endpoint != this ){
                		m.setContent(inMessage);
                		endpoint.session.getBasicRemote().sendObject(m);
                	} else if( inSelfMessage != null ){
                		m.setContent(inSelfMessage);
                		this.session.getBasicRemote().sendObject(m);
                	}
                } catch (IOException | EncodeException e) {
                	System.out.println("IOException at broadcast");
                    e.printStackTrace();
                }	
    		}//end synchronized
    	}
	}
	
	private void sendRebukeRemote(){
		Message m = new Message();
		m.setType("msgRebuke");
		m.setFrom("Server");
		m.setTo(this.username.name());
		m.setContent("Please don't use swear words!");
		try {
			this.session.getBasicRemote().sendObject(m);
		} catch (IOException | EncodeException e) {
			e.printStackTrace();
		}
	}
	
	private void sendNameRemote(){
		Message m = new Message();
		m.setType("msgSelfName");
		m.setFrom("Server");
		m.setTo(this.username.name());
		m.setContent(this.username.name());
		try {
			this.session.getBasicRemote().sendObject(m);
		} catch (IOException | EncodeException e) {
			e.printStackTrace();
		}
	}
	
	private void enumConnectedUsers(WSServer inRecipientEndpoint){
		Message m = new Message();
		m.setType("msgConnectedUsers");
		m.setFrom("Server");
		ArrayList<String> arr = new ArrayList<String>();
		for(WSServer ep : serverEndpoints){
			synchronized (ep) {
				arr.add(ep.username.name());
			}
		}
		m.setStringArray(arr);
        try {
        	if(inRecipientEndpoint == null){
        		//send it to everyone
        		for(WSServer ep : serverEndpoints){
        			synchronized (ep) {
        				m.setTo(ep.username.name());
        				ep.session.getBasicRemote().sendObject(m);
        			}
        		}
        	} else {
        		//send only to the dedicated recipient
        		m.setTo(inRecipientEndpoint.username.name());
        		inRecipientEndpoint.session.getBasicRemote().sendObject(m);
        	}
         } catch (IOException | EncodeException e) {
        	 e.printStackTrace();
         }

}
	
}

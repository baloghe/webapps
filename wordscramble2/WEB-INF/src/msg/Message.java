package msg;


public class Message {
	/* Message types */
	//server-level connection
	public static final String JOIN_SERVER="JOIN_SERVER";
	public static final String QUIT_SERVER="QUIT_SERVER";
	public static final String CONN_CONFIRM="CONN_CONFIRM";
	
	//gamespace create/join/quit
	public static final String QUIT_SPACE="QUIT_SPACE";
	
	public static final String JOIN_SPACE="JOIN_SPACE";
	public static final String JOIN_SPACE_CONFIRM="JOIN_SPACE_CONFIRM";
	public static final String JOIN_SPACE_REJECT="JOIN_SPACE_REJECT";
	
	public static final String CREATE_SPACE="CREATE_SPACE";
	public static final String CREATE_SPACE_CONFIRM="CREATE_SPACE_CONFIRM";
	public static final String CREATE_SPACE_REJECT="CREATE_SPACE_REJECT";
	
	public static final String GAME_SPACE_ENUM="GAME_SPACE_ENUM";
	public static final String GAME_SPACE_PLAYER_ENUM="GAME_SPACE_PLAYER_ENUM";
	public static final String CLOCK="CLOCK";
	
	//actual game-related
	public static final String SCRAMBLE="SCRAMBLE";
	public static final String ANSWER="ANSWER";
	public static final String ANS_CONFIRM="ANS_CONFIRM";
	public static final String SCORE="SCORE";
	
	
	//Message attributes
	private String type;
	private String sender;
	private String sessionID;
	private String content;
	
	public Message(){}
	
	public Message(String inType, String inSender, String inContent){
		this.type = inType;
		this.sender = inSender;
		this.content = inContent;
	}
	
    public String getType() {return this.type;}
    public void setType(String type) {this.type = type;}
	
    public String getSender() {return this.sender;}
    public void setSender(String sender) {this.sender = sender;}
	
    public String getSessionID() {return this.sessionID;}
    public void setSessionID(String sessionID) {this.sessionID = sessionID;}
	
    public String getContent() {return this.content;}
    public void setContent(String content) {this.content = content;}
	
}

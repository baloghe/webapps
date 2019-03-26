package msg;

public class Message {

	public static final String JOIN="JOIN";
	public static final String CONN_CONFIRM="CONN_CONFIRM";
	public static final String SCRAMBLE="SCRAMBLE";
	public static final String ANSWER="ANSWER";
	public static final String ANS_CONFIRM="ANS_CONFIRM";
	public static final String SCORE="SCORE";
	public static final String QUIT="QUIT";
	
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

package msg;

public class PlayerDescriptor {

	private String name;
	private String sessionId;
	private int score;
	
	public PlayerDescriptor(String inName, String inSessionId, int inScore){
		this.name = inName;
		this.sessionId = inSessionId;
		this.score = inScore;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
}

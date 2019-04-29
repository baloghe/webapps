package manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.DecodeException;
import javax.websocket.Session;

import msg.GameSpacePlayerList;
import msg.Message;
import msg.PlayerDescriptor;
import msg.PlayerDescriptorMessage;
import app.Game;
import app.Word;

public class GameSpace extends TimerTask {

	private static long SEQ_ID = 0;
	public static int SOLVE_SCRAMBLE_SECS = 10;
	public static int WORDS_PER_KW_TO_RETRIEVE = 20;
	
	private long id;
	private Map<String, Player> players = new ConcurrentHashMap<>();/* SessionID -> Player */
	private Game game;
	private GameManager gameManager;
	private String title;
	private int remainingSecs;
	private Timer timer;
	
	
	public GameSpace(GameManager inGameManager){
		id = getNewID();
		game = new Game();
		gameManager = inGameManager;
		title = "";
	}
	
	public void addKeyWord(String inStr){
		
		boolean retrieveSuccess = true; //let's hope for it
		//enlarge title
		if(title.length()==0){
			title = inStr;
		} else {
			title +=(", " + inStr);
		}
		
		//retrieve words
		HashSet<String> bag = new HashSet<>();
		if(retrieveSuccess){
			List<ThesaurusWord> lst = null;
			try{
				lst = getWordList(inStr);
				for(ThesaurusWord wd : lst){
					System.out.println("  addKeyWord("+inStr+") -> "+wd.getWord());
					if( !wd.getWord().contains(" ") ){
						bag.add(wd.getWord());
					} else {
						System.out.println("  ----discarded for SPACE");
					}
				}
			} catch(Exception e){
				//do nothing
			}
			retrieveSuccess = ( lst != null );
		} else {
			//do nothing
		}
		
		//add them to Game
		if(retrieveSuccess){
			game.addWords(bag);
		}
	}
	
	private static List<ThesaurusWord> getWordList(String inKeyWord) throws IOException{
		
		InputStream is = new URL("https://api.datamuse.com/words?ml="+inKeyWord.trim()+"&max="+WORDS_PER_KW_TO_RETRIEVE).openStream();
		try {
			BufferedReader rd = new BufferedReader( new InputStreamReader(is, Charset.forName("UTF-8")) );
			StringBuilder sb = new StringBuilder();
		    int cp;
		    while ((cp = rd.read()) != -1) {
		    	sb.append((char) cp);
		    }
		    String jsonText = sb.toString();
			ThesaurusWordListDecoder d = new ThesaurusWordListDecoder();
			List<ThesaurusWord> lst = d.decode(jsonText);
			return lst;
		} catch(DecodeException de){
			System.out.println("ERROR: getWordList :: DecodeException for "+inKeyWord);
			return null;
		} finally {
			is.close();
		}
	}
	
	public void startSpace(){
		timer = new Timer(true);
		timer.scheduleAtFixedRate(this, 0, 1000);
	}
	
	public void stop(){
		timer.cancel();
	}
	
	public String getTitle(){return this.title;}
	
	public Game getGame(){return this.game;}
	
	public int getPlayerNumber(){return players.size();}
	
	public void addPlayer(Session inSession, Player inPlayer){
		players.put(inSession.getId(), inPlayer);
	}
	
	public Collection<Player> getPlayerList(){
		return this.players.values();
	}
	
	public long getID(){return this.id;}

	public void removePlayer(String inSessionID){
		Player p = players.remove(inSessionID);
		p.resetScore();
		broadcastQuitSpace(p);
	}
	
	private static synchronized long getNewID(){
		return ++SEQ_ID;
	}
	
	public void setNewScramble(){
		game.setNewScramble();
		remainingSecs = SOLVE_SCRAMBLE_SECS;
		
		//broadcast new scramble
		//clock would be broadcasted by thread.run() anyway
		broadcastScramble();
		//broadcastClock();
	}
	
	public boolean proof(String inAnswer){
		return game.proof(inAnswer);
	}
	
	public void broadcastScramble(){
		String scramble = game.getActualScramble().getScramble();
		for(Entry<String, Player> e : players.entrySet()){
			//message to send
			Message msg = new Message(Message.SCRAMBLE, "SRV", scramble);
			Session targetSession = e.getValue().getSession();
			msg.setSessionID(targetSession.getId());
			gameManager.sendMessageToWebClient(targetSession, msg);
		}
	}
	
	public void sendSelfActualScramble(Session inSession){
		Message msg = new Message(Message.SCRAMBLE, "SRV", game.getActualScramble().getScramble());
		Session targetSession = inSession;
		msg.setSessionID(targetSession.getId());
		gameManager.sendMessageToWebClient(targetSession, msg);
	}
	
	public void sendSelfOtherPlayers(){
		GameSpacePlayerList msg = new GameSpacePlayerList(Message.GAME_SPACE_PLAYER_ENUM, "SRV", "");
		Session targetSession = gameManager.getPlayer().getSession();
		msg.setSessionID(targetSession.getId());
		int cnt = 0;
		for(Player p : this.players.values()){
			if( !p.getSession().getId().equalsIgnoreCase(targetSession.getId()) ){
				msg.addPlayerDescriptor( new PlayerDescriptor(p.getName(), p.getSession().getId(), p.getScore()) );
				cnt++;
			}
		}
		msg.setCount(cnt);
		System.out.println("SessionID="+targetSession.getId()+": sendSelfOtherPlayers() called, cnt=" + cnt);
		gameManager.sendMessageToWebClient(targetSession, msg);
	}
	
	public void broadcastScore(Player p){
		int userScore = p.getScore();
		Session userSession = p.getSession();
		for(Entry<String, Player> e : players.entrySet()){
			Session targetSession = e.getValue().getSession();
			//message to send
			Message msg = new Message(Message.SCORE, userSession.getId(), Integer.toString(userScore));
			msg.setSessionID(targetSession.getId());
			gameManager.sendMessageToWebClient(targetSession, msg);
		}
	}
	
	public void broadcastQuitSpace(Player p){
		Session userSession = p.getSession();
		for(Entry<String, Player> e : players.entrySet()){
			Session targetSession = e.getValue().getSession();
			//message to send
			Message msg = new Message(Message.QUIT_SPACE, userSession.getId(), "");
			msg.setSessionID(targetSession.getId());
			gameManager.sendMessageToWebClient(targetSession, msg);
		}
	}
	
	public void confirmAnswer(String inAnswer, boolean inProof, Session inSession){
		Session targetSession = inSession;
		//message to send
		Message msg = new Message(Message.ANS_CONFIRM, inAnswer, (inProof ? "YES" : "NO"));
		msg.setSessionID(targetSession.getId());
		gameManager.sendMessageToWebClient(targetSession, msg);
	}
	
	public void join(Player inPlayer){
		Session userSession = inPlayer.getSession();
		this.addPlayer(userSession, inPlayer);
		
		//send a JOIN message to the other players' clients
		//and notify our client of already connected users
		for(Entry<String, Player> e : players.entrySet()){
			
			if( !(e.getKey().equalsIgnoreCase(userSession.getId()) ) ){
				
				System.out.println("SessionID="+this.gameManager.getPlayer().getSession().getId()+" join(): e.sessionID=" + e.getKey() + ", userSession="+userSession.getId());
				
				//send a JOIN message to the other players' clients
				PlayerDescriptorMessage msg = new PlayerDescriptorMessage(Message.JOIN_SPACE, "SRV", inPlayer.getName());
				msg.setSessionID(userSession.getId());
				msg.setPlayer(new PlayerDescriptor(inPlayer.getName(), inPlayer.getSession().getId(), inPlayer.getScore()));
				Session targetSession = e.getValue().getSession();
				gameManager.sendMessageToWebClient(targetSession, msg);
				
				//notify our client of already connected users
				msg = new PlayerDescriptorMessage(Message.JOIN_SPACE, "SRV", e.getValue().getName());
				msg.setSessionID(e.getValue().getSession().getId());
				msg.setPlayer(new PlayerDescriptor(e.getValue().getName(), e.getValue().getSession().getId(), e.getValue().getScore()));
				targetSession = userSession;
				gameManager.sendMessageToWebClient(targetSession, msg);
				
			}//endif
		}//next e
	}
	
	public void broadcastClock(){
		Message msg = new Message(Message.CLOCK, "SRV", Integer.toString(remainingSecs));
		for(Entry<String, Player> e : players.entrySet()){
			//message to send
			Session targetSession = e.getValue().getSession();
			msg.setSessionID(targetSession.getId());
			gameManager.sendMessageToWebClient(targetSession, msg);
		}
	}
		
	@Override
	public void run() {
		//try {
			//Thread.sleep(1000); //wait 1 sec
			this.remainingSecs--;
			broadcastClock();
			if(this.remainingSecs == 0){
				setNewScramble();
			}
		//} catch (InterruptedException e) {
		//	e.printStackTrace();
		//}
	}
}

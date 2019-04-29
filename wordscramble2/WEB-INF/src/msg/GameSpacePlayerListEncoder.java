package msg;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;

public class GameSpacePlayerListEncoder implements Encoder.Text<GameSpacePlayerList> {


	private static Gson gson = new Gson();

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(EndpointConfig arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String encode(GameSpacePlayerList inMsg) throws EncodeException {
        String json = gson.toJson(inMsg);
        return json;
	}
}

package manager;

import java.util.List;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ThesaurusWordListDecoder implements Decoder.Text<List<ThesaurusWord>> {

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
	public List<ThesaurusWord> decode(String s) throws DecodeException {
		System.out.println("ThesaurusWordListDecoder.decode :: s=" + s);
		List<ThesaurusWord> ret = gson.fromJson(s, new TypeToken<List<ThesaurusWord>>(){}.getType());
		System.out.println("  -> decoded: " + ret.toString());
        return ret;
	}

	@Override
	public boolean willDecode(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}

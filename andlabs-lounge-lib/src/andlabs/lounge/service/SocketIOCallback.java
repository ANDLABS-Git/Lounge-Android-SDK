package andlabs.lounge.service;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

public class SocketIOCallback implements IOCallback {

	private SocketIO mSocketIO;


	public SocketIOCallback(SocketIO pSocketIO) {
		mSocketIO = pSocketIO;
	}


	@Override
	public void onMessage(JSONObject arg0, IOAcknowledge arg1) {
		Log.d("LoungeService", String.format("IOCallback.onMessage(): arg0 = %s", arg0));

	}


	@Override
	public void onMessage(String arg0, IOAcknowledge arg1) {
		Log.d("LoungeService", String.format("IOCallback.onMessage(): arg0 = %s", arg0));

	}


	@Override
	public void onError(SocketIOException arg0) {
		Log.e("LoungeService", String.format("IOCallback.onError(): caught exception while connecting"), arg0);

	}


	@Override
	public void onDisconnect() {
		Log.d("LoungeService", String.format("IOCallback.onDisconnect():"));

	}


	@Override
	public void onConnect() {
		Log.d("LoungeService", String.format("IOCallback.onConnect():"));
		try {
			mSocketIO.emit("login", new JSONObject().put("playerID", "kodra"));
		} catch (JSONException e) {
			Log.e("LoungeService", "caught exception while sending login", e);
		}
	}


	@Override
	public void on(String arg0, IOAcknowledge arg1, Object... arg2) {
		Log.d("LoungeService", String.format("IOCallback.on(): arg0 = %s, arg1 = %s, arg2 = %s", arg0, arg1, Arrays.toString(arg2)));
		
	}

}

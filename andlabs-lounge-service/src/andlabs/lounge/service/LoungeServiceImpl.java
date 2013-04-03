package andlabs.lounge.service;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONException;
import org.json.JSONObject;

import andlabs.lounge.model.Game;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;


public class LoungeServiceImpl extends LoungeServiceDef.Stub {

	private Messenger mMessenger;
	private SocketIO mSocketIO;
	private IOCallback mSocketIOCallback = new IOCallback() {

		@Override
		public void onMessage(JSONObject arg0, IOAcknowledge arg1) {
			Log.d("LoungeServiceImpl", String.format("IOCallback.onMessage(): arg0 = %s", arg0));

		}


		@Override
		public void onMessage(String arg0, IOAcknowledge arg1) {
			Log.d("LoungeServiceImpl", String.format("IOCallback.onMessage(): arg0 = %s", arg0));

		}


		@Override
		public void onError(SocketIOException arg0) {
			Log.e("LoungeServiceImpl", String.format("IOCallback.onError(): caught exception while connecting"), arg0);

		}


		@Override
		public void onDisconnect() {
			Log.d("LoungeServiceImpl", String.format("IOCallback.onDisconnect():"));

		}


		@Override
		public void onConnect() {
			Log.d("LoungeServiceImpl", String.format("IOCallback.onConnect():"));
			try {
				Message message = new Message();
				message.what = 1;
				message.setData(Bundle.EMPTY);
				mMessenger.send(message);
			} catch (RemoteException e) {
				Log.e("LoungeServiceImpl", "caught exception while sending message", e);
			}
		}


		@Override
		public void on(String arg0, IOAcknowledge arg1, Object... arg2) {
			Log.v("LoungeServiceImpl", String.format("IOCallback.on(): arg0 = %s, arg1 = %s, arg2 = %s", arg0, arg1, Arrays.toString(arg2)));
			mLoungeMessageProcessor.processMessage(arg0, arg2);
		}

	};

	private LoungeMessageProcessor mLoungeMessageProcessor = new LoungeMessageProcessor() {

		@Override
		public void triggerUpdate(ArrayList<Game> pGames) {
			Log.v("LoungeServiceImpl", String.format("LoungeMessageProcessor.triggerUpdate(): pGames = %s", pGames));
			Message message = new Message();
			message.what = 7;
			Bundle bundle = new Bundle();
			bundle.putSerializable("gameList", pGames);
			message.setData(bundle);
			try {
				mMessenger.send(message);
			} catch (RemoteException e) {
				Log.e("LoungeServiceImpl", "LoungeMessageProcessor.triggerUpdate(): caught exception while sending message", e);
			}
		};

	};

	public LoungeServiceImpl(Intent intent) {
		super();
		Log.v("LoungeServiceImpl", "LoungeServiceImpl():");
		mMessenger = intent.getParcelableExtra("client-messenger");
		try {
			Message message = new Message();
			message.what = 42;
			message.setData(Bundle.EMPTY);
			mMessenger.send(message);
		} catch (RemoteException e) {
			Log.e("LoungeServiceImpl", "LoungeServiceImpl(): caught exception while sending message", e);
		}

	}


	@Override
	public void connect() throws RemoteException {
		Log.v("LoungeServiceImpl", "connect():");
		try {
			mSocketIO = new SocketIO("http://lounge-server.jit.su/");
			mSocketIO.connect(mSocketIOCallback);
		} catch (MalformedURLException e) {
			Log.e("LoungeService", "caught exception while creating/connecting SocketIO", e);
		}
	}


	@Override
	public void disconnect() throws RemoteException {
		Log.v("LoungeServiceImpl", "disconnect():");
		if (mSocketIO != null) {
			mSocketIO.disconnect();
		}
	}


	@Override
	public void login(String playerId) throws RemoteException {
		Log.v("LoungeServiceImpl", "login(): playerId = " + playerId);
		try {
			mSocketIO.emit("login", new JSONObject().put("playerID", playerId));
		} catch (JSONException e) {
			Log.e("LoungeService", "caught exception while sending login", e);
		}
	}


	@Override
	public void chat(String message) throws RemoteException {
		// TODO Auto-generated method stub

	}


	@Override
	public void openMatch(String pPackageId, String pDisplayName) throws RemoteException {
		Log.v("LoungeServiceImpl", String.format("openMatch(): pPackageId = %s, pDisplayName = %s", pPackageId, pDisplayName));
		try {
			// PAYLOAD {gameID: "packageID", gameName: ”AppName”, maximumPlayers: “MaximumAllowedPlayersInGame” , gameType: “move/stream”}
			JSONObject payload = new JSONObject().put("gameID", pPackageId).put("gameName", pDisplayName);
			payload.put("MaximumAllowedPlayersInGame", 2).put("gameType", "move");
			mSocketIO.emit("join", payload);
		} catch (JSONException e) {
			Log.e("LoungeService", "caught exception while sending join", e);
		}
	}

}

package andlabs.lounge.service;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

import roboguice.util.Ln;

import andlabs.lounge.model.Game;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;


public class LoungeServiceImpl extends LoungeServiceDef.Stub {

	private Messenger mMessenger;
	private SocketIO mSocketIO;
	private IOCallback mSocketIOCallback = new IOCallback() {

		@Override
		public void onMessage(JSONObject arg0, IOAcknowledge arg1) {
			Ln.d("IOCallback.onMessage(): arg0 = %s", arg0);

		}


		@Override
		public void onMessage(String arg0, IOAcknowledge arg1) {
			Ln.d("IOCallback.onMessage(): arg0 = %s", arg0);

		}


		@Override
		public void onError(SocketIOException arg0) {
			Ln.e(arg0, "IOCallback.onError(): caught exception while connecting");

		}


		@Override
		public void onDisconnect() {
			Ln.d("IOCallback.onDisconnect():");

		}


		@Override
		public void onConnect() {
			Ln.d("IOCallback.onConnect():");
			try {
				Message message = new Message();
				message.what = 1;
				message.setData(Bundle.EMPTY);
				mMessenger.send(message);
			} catch (RemoteException e) {
				Ln.e(e, "IOCallback.onConnect(): caught exception while sending message");
			}
		}


		@Override
		public void on(String arg0, IOAcknowledge arg1, Object... arg2) {
			Ln.v("IOCallback.on(): arg0 = %s, arg1 = %s, arg2 = %s", arg0, arg1, Arrays.toString(arg2));
			mLoungeMessageProcessor.processMessage(arg0, arg2);
		}

	};

	private LoungeMessageProcessor mLoungeMessageProcessor = new LoungeMessageProcessor() {

		@Override
		public void triggerUpdate(HashMap<String, Game> pInvolvedGames, HashMap<String, Game> pOpenGames) {
			Ln.v("LoungeMessageProcessor.triggerUpdate(): pInvolvedGames = %s, pOpenGames = %s", pInvolvedGames, pOpenGames);
			Message message = new Message();
			message.what = 7;
			Bundle bundle = new Bundle();
            bundle.putSerializable("involvedGameList", pInvolvedGames);
            bundle.putSerializable("openGameList", pOpenGames);
			message.setData(bundle);
			try {
				mMessenger.send(message);
			} catch (RemoteException e) {
				Ln.e(e, "LoungeMessageProcessor.triggerUpdate(): caught exception while sending message");
			}
		}

        @Override
        public void onGameMove(String pMatchID, Bundle pParams) {
			Ln.v("LoungeMessageProcessor.onGameMove(): pMatchID = %s, pParams = %s", pMatchID, pParams);

        }

	};


	public LoungeServiceImpl(Intent intent) {
		super();
		Ln.v("LoungeServiceImpl():");
		mMessenger = intent.getParcelableExtra("client-messenger");
		try {
			Message message = new Message();
			message.what = 42;
			message.setData(Bundle.EMPTY);
			mMessenger.send(message);
		} catch (RemoteException e) {
			Ln.e(e, "LoungeServiceImpl(): caught exception while sending message 42");
		}
	}


	@Override
	public void connect() throws RemoteException {
		Ln.v("connect():");
		try {
			mSocketIO = new SocketIO("http://lounge-server.jit.su/");
			mSocketIO.connect(mSocketIOCallback);
		} catch (MalformedURLException e) {
			Ln.e(e, "connect(): caught exception while creating/connecting SocketIO");
		}
	}


	@Override
	public void disconnect() throws RemoteException {
		Ln.v("disconnect():");
		if (mSocketIO != null) {
			mSocketIO.disconnect();
		}
	}


	@Override
	public void login(String playerId) throws RemoteException {
		Ln.v("login(): playerId = %s", playerId);
		try {
			mLoungeMessageProcessor.setMyPlayerId(playerId);
			mSocketIO.emit("login", new JSONObject().put("playerID", playerId));
		} catch (JSONException e) {
			Ln.e(e, "login(): caught exception while sending login");
		}
	}


	@Override
	public void chat(String message) throws RemoteException {
		// TODO Auto-generated method stub

	}


	@Override
	public void openMatch(String pPackageId, String pDisplayName) throws RemoteException {
		Ln.v("openMatch(): pPackageId = %s, pDisplayName = %s", pPackageId, pDisplayName);
		try {
			// PAYLOAD {gameID: "packageID", gameName: ”AppName”, maximumPlayers: “MaximumAllowedPlayersInGame” , gameType: “move/stream”}
			JSONObject payload = new JSONObject().put("gameID", pPackageId).put("gameName", pDisplayName);
			payload.put("MaximumAllowedPlayersInGame", 2).put("gameType", "move");
			mSocketIO.emit("join", payload);
		} catch (JSONException e) {
			Ln.e(e, "openMatch(): caught exception while sending join");
		}
	}


	@Override
	public void joinMatch(String pGameId, String pMatchId) throws RemoteException {
		Ln.v("joinMatch(): pGameId = %s, pMatchId = %s", pGameId, pMatchId);
		try {
			// PAYLOAD { gameID: ”packageID”, matchID: “matchID” }
			mSocketIO.emit("checkin", new JSONObject().put("gameID", pGameId).put("matchID", pMatchId));
		} catch (JSONException e) {
			Ln.e(e, "joinMatch(): caught exception while sending checkin");
		}
	}


	@Override
	public void checkin(String pGameId, String pMatchId) throws RemoteException {
		Ln.v("checkin(): pGameId = %s, pMatchId = %s", pGameId, pMatchId);
		try {
			// PAYLOAD { gameID: ”packageID”, matchID: “matchID” }
			mSocketIO.emit("checkin", new JSONObject().put("gameID", pGameId).put("matchID", pMatchId));
		} catch (JSONException e) {
			Ln.e(e, "checkin(): caught exception while sending checkin");
		}
	}


	@Override
	public void update(String pGameId, String pMatchId, String pStatus) throws RemoteException {
		Ln.v("update(): pGameId = %s", pGameId);
		try {
			// PAYLOAD { gameID: “packageID”, matchID: “matchID”, status: “open/running/close” }
			JSONObject payload = new JSONObject();
			payload.put("gameID", pGameId).put("matchID", pMatchId).put("status", pStatus);
			mSocketIO.emit("update", payload);
		} catch (JSONException e) {
			Ln.e(e, "update(): caught exception while sending update");
		}
	}


	@Override
	public void move(String pPackageId, String pMatchId, Bundle pMoveBundle) throws RemoteException {
		Ln.v("move(): pPackageId = %s, pMatchId = %s", pPackageId, pMatchId);
		try {
			// PAYLOAD { gameID: “packageID”, matchID: “matchID”, move: {... } }
			JSONObject payload = new JSONObject().put("gameID", pPackageId).put("matchId", pMatchId);
			JSONObject bundleJson = new JSONObject();
			for (String key : pMoveBundle.keySet()) {
				bundleJson.put(key, pMoveBundle.get(key));
			}
			payload.put("move", bundleJson);
			mSocketIO.emit("move", payload);
		} catch (JSONException e) {
			Ln.e(e, "move(): caught exception while sending move");
		}
	}

}

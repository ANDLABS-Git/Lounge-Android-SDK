package andlabs.lounge.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import andlabs.lounge.model.DataManager;
import andlabs.lounge.pojo.LobbyDataContainer;
import andlabs.lounge.pojo.Player;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

public class GPCController {

	public static final int LOBBYDATA = 100;
	private DataManager dataManager;
	private Messenger mMessenger;

	public GPCController(Intent intent) {
		dataManager = new DataManager();
		Log.v("GPCController", "GPCController(): intent recieved");
		mMessenger = intent.getParcelableExtra("client-messenger");
		try {
			Message message = new Message();
			message.what = 1;
			message.setData(Bundle.EMPTY);
			mMessenger.send(message);
		} catch (RemoteException e) {
			Log.e("LoungeServiceImpl",
					"caught exception while sending message", e);
		}
	}

	void processServerResponse(String verb, Object... payloadArray) {
		Log.v("GPCController", String.format("Incoming Verb: " + verb));
		JSONObject payload = new JSONObject();
		JSONArray playerArray = new JSONArray();

		try {
			payload = new JSONObject(payloadArray[0].toString());

			// ------------------------ LOGIN----------------------------
			if ("login".equals(verb)) {
				playerArray = payload.getJSONArray("playerList");
				for (int index = 0; index < playerArray.length(); index++) {
					JSONObject jsonObject = playerArray.getJSONObject(index);
					String playerId = jsonObject.getString("playerID");
					dataManager.loginPlayer(playerId, playerId, 0);
				}
			}

			// ------------------------ JOIN MATCH ----------------------------
			if ("joinMatch".equals(verb)) {

				String gameId = payload.getString("gameID");
				String matchId = payload.getString("matchID");
				String gameName = payload.getString("gameName");
				int totalSpots = payload.getInt("totalSpots");
				String status = payload.getString("status");
				playerArray = payload.getJSONArray("playerIDs");
				Log.v("LoungeLobbyController",
						"LoungeServiceCallback.onJoinMatch(): gameName = "
								+ gameName + " / " + playerArray);

				if (status.equalsIgnoreCase("created")) {
					dataManager.openMatch(matchId, playerArray.getJSONObject(0)
							.getString("playerID"), gameId);
				} else {
					dataManager.joinGame(matchId, playerArray, gameId);
				}
				updateLobbyUi();

			}

			// ------------------------ UPDATE MATCH -----------------------
			if ("joinMatch".equals(verb)) {

				String gameId = payload.getString("gameID");
				String matchId = payload.getString("matchID");
				String gameName = payload.getString("gameName");
				int totalSpots = payload.getInt("totalSpots");
				String status = payload.getString("status");
				playerArray = payload.getJSONArray("playerIDs");
				Log.v("LoungeLobbyController",
						"LoungeServiceCallback.onJoinMatch(): gameName = "
								+ gameName + " / " + playerArray);

				dataManager.updateMatchStatus(matchId, gameId, status);
				updateLobbyUi();

			}

			// ------------------------ CHECK IN ----------------------------
			if ("checkIn".equals(verb)) {

				String gameId = payload.getString("gameID");
				String matchId = payload.getString("matchID");
				String playerID = payload.getString("playerID");

				dataManager.updatePlayerStatus(playerID, gameId, matchId);
				updateLobbyUi();

			}

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void updateLobbyUi() {
		Log.d("LoungeServiceImpl", String.format("IOCallback.onConnect():"));
		try {
			Message message = new Message();
			message.what = LOBBYDATA;
			Bundle b = new Bundle();
			b.putSerializable(LOBBYDATA + "", new LobbyDataContainer(
					dataManager.getLobbydata()));
			message.setData(Bundle.EMPTY);
			mMessenger.send(message);
		} catch (RemoteException e) {
			Log.e("LoungeServiceImpl",
					"caught exception while sending message", e);
		}

	}
}

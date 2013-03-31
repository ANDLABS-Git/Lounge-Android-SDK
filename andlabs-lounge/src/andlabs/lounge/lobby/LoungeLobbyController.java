package andlabs.lounge.lobby;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import andlabs.lounge.lobby.model.ChatMessage;
import andlabs.lounge.lobby.model.LobbyListElement;
import android.content.Context;
import android.util.Log;


public class LoungeLobbyController {

	private LoungeLobbyCallback mLoungeLobbyCallback;
	private List<LobbyListElement> mLoungeLobbyList = new ArrayList<LobbyListElement>();
	private LoungeServiceController mLoungeServiceController = new LoungeServiceController();
	private LoungeServiceCallback mLoungeServiceCallback = new LoungeServiceCallback() {

		@Override
		public void theAnswerIs42() {
			Log.v("LoungeLobbyController", "LoungeServiceCallback.theAnswerIs42(): Universal Answer ;-)");
		}

		@Override
		public void onLogin(JSONObject payload) {
			Log.v("LoungeLobbyController", "LoungeServiceCallback.onLogin(): " + payload);
			try {
				JSONArray jsonArray = payload.getJSONArray("playerList");
				Log.v("LoungeLobbyController", "LoungeServiceCallback.onLogin(): " + jsonArray);
				for (int index = 0; index < jsonArray.length(); index++) {
					JSONObject jsonObject = jsonArray.getJSONObject(index);
					String playerId = jsonObject.getString("playerID");
					// TODO how to use this data
				}
			} catch (JSONException e) {
				Log.e("LoungeLobbyController", "LoungeServiceCallback.onLogin(): caught exception while processing JSON payload", e);
			}
		}

		@Override
		public void onJoinMatch(JSONObject payload) {
			Log.v("LoungeLobbyController", "LoungeServiceCallback.onJoinMatch(): " + payload);
			try {
				String gameId = payload.getString("gameID");
				String gameName = payload.getString("gameName");
				int totalSpots = payload.getInt("totalSpots");
				String status = payload.getString("status");
				JSONArray jsonArray = payload.getJSONArray("playerIDs");
				Log.v("LoungeLobbyController", "LoungeServiceCallback.onJoinMatch(): gameName = " + gameName + " / " + jsonArray);
				for (int index = 0; index < jsonArray.length(); index++) {
					JSONObject jsonObject = jsonArray.getJSONObject(index);
					String playerId = jsonObject.getString("playerID");
					// TODO how to use this data
				}
			} catch (JSONException e) {
				Log.e("LoungeLobbyController", "LoungeServiceCallback.onJoinMatch(): caught exception while processing JSON payload", e);
			}
		}

		@Override
		public void onError(String message) {
			Log.e("LoungeLobbyController", "LoungeServiceCallback.onError(): " + message);
		}

	};


	public void bindServiceTo(Context pContext) {
		Log.v("LoungeLobbyController", "bindServiceTo()");
		mLoungeServiceController.bindServiceTo(pContext);
		mLoungeServiceController.registerCallback(mLoungeServiceCallback);
	}


	public void registerCallback(LoungeLobbyCallback pLoungeLobbyCallback) {
		Log.v("LoungeLobbyController", "registerCallback()");
		mLoungeLobbyCallback = pLoungeLobbyCallback;
	}


	public void unregisterCallback(LoungeLobbyCallback pLoungeLobbyCallback) {
		Log.v("LoungeLobbyController", "unregisterCallback()");
		mLoungeLobbyCallback = null;
	}


	public void unbindServiceFrom(Context pContext) {
		Log.v("LoungeLobbyController", "unbindServiceFrom()");
		mLoungeServiceController.unregisterCallback(mLoungeServiceCallback);
		mLoungeServiceController.unbindServiceFrom(pContext);
	}

}

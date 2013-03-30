package andlabs.lounge.frontend;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import andlabs.lounge.pojo.LobbyListElement;
import android.content.Context;
import android.util.Log;


public class LoungeLobbyController {

	
	   private static LoungeLobbyController instance = null;
	   
	   private LoungeLobbyController() {
	      // Exists only to defeat instantiation.
	   }
	   public static LoungeLobbyController getInstance() {
	      if(instance == null) {
	         instance = new LoungeLobbyController();
	      }
	      return instance;
	   }
	
	
	private LoungeLobbyCallback mLoungeLobbyCallback;
	private List<LobbyListElement> mLoungeLobbyList = new ArrayList<LobbyListElement>();
	private LoungeServiceController mLoungeServiceController = new LoungeServiceController();
	private LoungeServiceCallback mLoungeServiceCallback = new LoungeServiceCallback() {
	
		

		
		
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
		public void onError(String message) {
			Log.e("LoungeLobbyController", "LoungeServiceCallback.onError(): " + message);
		}

		@Override
		public void onLobbyDataUpdated(List<LobbyListElement> data) {

			 mLoungeLobbyCallback.onLobbyDataUpdated(data);
			
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

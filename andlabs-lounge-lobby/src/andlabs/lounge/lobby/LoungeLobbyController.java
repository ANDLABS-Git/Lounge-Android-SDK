package andlabs.lounge.lobby;

import java.util.ArrayList;
import java.util.List;

import andlabs.lounge.lobby.model.LobbyListElement;
import andlabs.lounge.model.Game;
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
		public void onConnect() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDisconnect() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onOpenGamesUpdate(ArrayList<Game> pGames) {
			Log.v("LoungeLobbyController", "LoungeServiceCallback.onOpenGamesUpdate(): " + pGames);
			ArrayList<LobbyListElement> data = new ArrayList<LobbyListElement>();
			// TODO populate data from the pGames object
			mLoungeLobbyCallback.onLobbyDataUpdated(data);
		}

		@Override
		public void onRunningGamesUpdate(ArrayList<Game> pGames) {
			Log.v("LoungeLobbyController", "LoungeServiceCallback.onRunningGamesUpdate(): " + pGames);
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


	public void hostGame(String pPackageId, String pDisplayName) {
		Log.v("LoungeLobbyController", "hostGame()");
	}


	public void joinGame(String pPackageId, String pMatchId) {
		Log.v("LoungeLobbyController", "joinGame()");
	}

}

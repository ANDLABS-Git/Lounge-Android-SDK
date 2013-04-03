package andlabs.lounge;

import java.util.ArrayList;
import java.util.List;

import andlabs.lounge.model.Game;
import andlabs.lounge.model.Match;
import andlabs.lounge.model.Player;
import android.content.Context;
import android.util.Log;


public class LoungeGameController {

	private LoungeGameCallback mLoungeGameCallback;
	private LoungeServiceController mLoungeServiceController = new LoungeServiceController();
	private LoungeServiceCallback mLoungeServiceCallback = new LoungeServiceCallback() {

		@Override
		public void theAnswerIs42() {
			Log.v("LoungeGameController", "LoungeServiceCallback.theAnswerIs42(): Universal Answer ;-)");
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
			Log.v("LoungeGameController", "LoungeServiceCallback.onOpenGamesUpdate(): " + pGames);
		}

		@Override
		public void onRunningGamesUpdate(ArrayList<Game> pGames) {
			Log.v("LoungeGameController", "LoungeServiceCallback.onRunningGamesUpdate(): " + pGames);
		}

		@Override
		public void onError(String message) {
			Log.e("LoungeGameController", "LoungeServiceCallback.onError(): " + message);
		}

	};


	public void bindServiceTo(Context pContext) {
		Log.v("LoungeGameController", "bindServiceTo()");
		mLoungeServiceController.bindServiceTo(pContext);
		mLoungeServiceController.registerCallback(mLoungeServiceCallback);
	}


	public void registerCallback(LoungeGameCallback pLoungeGameCallback) {
		Log.v("LoungeGameController", "registerCallback()");
		mLoungeGameCallback = pLoungeGameCallback;
	}


	public void unregisterCallback(LoungeGameCallback pLoungeGameCallback) {
		Log.v("LoungeGameController", "unregisterCallback()");
		mLoungeGameCallback = null;
	}


	public void unbindServiceFrom(Context pContext) {
		Log.v("LoungeGameController", "unbindServiceFrom()");
		mLoungeServiceController.unregisterCallback(mLoungeServiceCallback);
		mLoungeServiceController.unbindServiceFrom(pContext);
	}


	private void checkin(String pMatchId) {
		Log.v("LoungeGameController", "checkin()");
		// TODO wire it to the corresponding LoungeServiceController method
	}


	private void checkout(String pMatchId) {
		Log.v("LoungeGameController", "checkout()");
		// TODO wire it to the corresponding LoungeServiceController method
	}

}

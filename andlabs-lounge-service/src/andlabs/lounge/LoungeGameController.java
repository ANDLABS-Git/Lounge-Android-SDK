package andlabs.lounge;

import java.util.Map;

import andlabs.lounge.model.Game;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;


public class LoungeGameController {

	private String mPackageId;
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
        public void onStart() {
            // TODO Auto-generated method stub
            
        }

		@Override
		public void onOpenGamesUpdate(Map<String, Game> pGames) {
			Log.v("LoungeGameController", "LoungeServiceCallback.onOpenGamesUpdate(): " + pGames);
		}

		@Override
		public void onRunningGamesUpdate(Map<String, Game> pGames) {
			Log.v("LoungeGameController", "LoungeServiceCallback.onRunningGamesUpdate(): " + pGames);
		}

		@Override
		public void onError(String message) {
			Log.e("LoungeGameController", "LoungeServiceCallback.onError(): " + message);
		}

	};


	public void bindServiceTo(Context pContext) {
		mPackageId = pContext.getApplicationInfo().packageName;
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


	public void checkin(String pMatchId) {
		Log.v("LoungeGameController", "checkin()");
		mLoungeServiceController.checkin(mPackageId, pMatchId);
	}


	private void checkout(String pMatchId) {
		Log.v("LoungeGameController", "checkout()");
		// TODO wire it to the corresponding LoungeServiceController method
	}


	public void sendGameMove(String pMatchId, Bundle pMoveBundle) {
		Log.v("LoungeGameController", String.format("sendGameMove(): pMoveBundle = %s", pMoveBundle));
		mLoungeServiceController.sendGameMove(mPackageId, pMatchId, pMoveBundle);
	}

}
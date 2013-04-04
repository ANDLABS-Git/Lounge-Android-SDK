package andlabs.lounge.lobby;

import java.util.ArrayList;
import java.util.Map;

import andlabs.lounge.LoungeServiceCallback;
import andlabs.lounge.LoungeServiceController;
import andlabs.lounge.lobby.util.Id;
import andlabs.lounge.model.Game;
import android.content.Context;
import android.util.Log;

public class LoungeLobbyController {

    private String mUserName;
    private LoungeLobbyCallback mLoungeLobbyCallback;
    private LoungeServiceController mLoungeServiceController = new LoungeServiceController();
    private LoungeServiceCallback mLoungeServiceCallback = new LoungeServiceCallback() {

        @Override
        public void theAnswerIs42() {
            Log.v("LoungeLobbyController", "LoungeServiceCallback.theAnswerIs42(): Universal Answer ;-)");
        }

        @Override
        public void onStart() {
            mLoungeServiceController.login(mUserName);
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
        public void onOpenGamesUpdate(Map<String, Game> pGames) {
            Log.v("LoungeLobbyController", "LoungeServiceCallback.onOpenGamesUpdate(): " + pGames);
            mLoungeLobbyCallback.onOpenGamesUpdate(new ArrayList<Game>(pGames.values()));
        }

        @Override
        public void onRunningGamesUpdate(Map<String, Game> pGames) {
            Log.v("LoungeLobbyController", "LoungeServiceCallback.onRunningGamesUpdate(): " + pGames);
            mLoungeLobbyCallback.onRunningGamesUpdate(new ArrayList<Game>(pGames.values()));
        }

        @Override
        public void onError(String message) {
            Log.e("LoungeLobbyController", "LoungeServiceCallback.onError(): " + message);
        }

    };

    public void bindServiceTo(Context pContext) {
        Log.v("LoungeLobbyController", "bindServiceTo()");
        mUserName = Id.getName(pContext);
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

    public void openMatch(String pPackageId, String pDisplayName) {
        Log.v("LoungeLobbyController", "hostGame()");
        mLoungeServiceController.openMatch(pPackageId, pDisplayName);
    }

    public void joinMatch(String pPackageId, String pMatchId) {
        Log.v("LoungeLobbyController", "joinGame()");
    }

}

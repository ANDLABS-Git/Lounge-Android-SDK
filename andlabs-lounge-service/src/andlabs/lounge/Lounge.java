package andlabs.lounge;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class Lounge {

    private static Lounge instance = null;
    private ArrayList<Multiplayable> mListeners;
    private LoungeGameController mController;
    private LoungeGameCallback mGameCallback;
    private String mCheckedInMatchId;

    private Lounge() {
        mListeners = new ArrayList<Multiplayable>();
        mController = new LoungeGameController();
        mGameCallback = new LoungeGameCallback() {

            @Override
            public void onCheckIn(String player) {
                for (Multiplayable listener : mListeners) {
                    listener.onCheckIn(player);
                }

            }

            @Override
            public void onAllPlayerCheckedIn() {
                for (Multiplayable listener : mListeners) {
                    listener.onAllPlayerCheckedIn();
                }

            }

            @Override
            public void onGameMessage(Bundle msg) {
                for (Multiplayable listener : mListeners) {
                    listener.onGameMessage(msg);
                }

            }

            @Override
            public void onCheckOut(String player) {
                for (Multiplayable listener : mListeners) {
                    listener.onCheckOut(player);
                }
            }
        };
        Log.i("Lounge","register callback "+mGameCallback);
        mController.registerCallback(mGameCallback);
    }

    public static Lounge getInstance() {
        if (instance == null) {
            instance = new Lounge();
        }
        return instance;
    }

    public void registerMultiplayableListener(Multiplayable pListener) {
        mListeners.add(pListener);
    }

    public void bind(Context pContext) {
        mController.bindServiceTo(pContext);
    }

    public void unBind(Context pContext) {
        mController.unbindServiceFrom(pContext);
    }

    public void unregisterMultiplayableListener(Multiplayable listener) {
        mListeners.remove(listener);
        //TEST
    }

    public void unregisterAllMultiplayableListener() {
        mListeners.clear();
    }

    public void checkin(String pMatchId) {
        mController.checkin(pMatchId);
        mCheckedInMatchId=pMatchId;
        
    }

    public void checkout(String pMatchId) {
        mController.checkout(pMatchId);
        mCheckedInMatchId=null;
    }

    public void closeMatch(String pMatchId) {
        mController.closeMatch(pMatchId);

    }

    public void sendGameMove(String pMatchId, Bundle pMoveBundle) {
        mController.sendGameMove(pMatchId, pMoveBundle);
    }

    public String getCheckedInMatchId() {
        return mCheckedInMatchId;
    }


}
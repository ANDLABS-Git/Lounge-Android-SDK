package andlabs.lounge;

import java.util.ArrayList;

import andlabs.lounge.util.Ln;
import android.content.Context;
import android.os.Bundle;

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
                //Switching to JsonOBject?
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
        Ln.i("Lounge","register callback "+mGameCallback);
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

    public void unbind(Context pContext) {
        mController.unbindServiceFrom(pContext);
    }

    public void unregisterMultiplayableListener(Multiplayable listener) {
        mListeners.remove(listener);
    }

    public void unregisterAllMultiplayableListener() {
        mListeners.clear();
    }

    public void checkin(String pMatchId) {
        mController.checkin(pMatchId);
        mCheckedInMatchId=pMatchId;
        
    }

    public void checkout() {
        mController.checkout(mCheckedInMatchId);
        mCheckedInMatchId=null;
    }

    public void closeMatch() {
        mController.checkout(mCheckedInMatchId);
        mController.closeMatch(mCheckedInMatchId);

    }

    public void sendGameMove(Bundle pMoveBundle) {
        //Switching to JsonOBject?
        mController.sendGameMove(mCheckedInMatchId, pMoveBundle);
    }

    public String getCheckedInMatchId() {
        return mCheckedInMatchId;
    }


}
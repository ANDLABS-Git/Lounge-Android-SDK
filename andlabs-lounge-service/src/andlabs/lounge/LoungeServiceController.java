/*
 *  Copyright (C) 2012,2013 ANDLABS. All rights reserved. 
 *  Lounge@andlabs.com
 *  lounge.andlabs.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package andlabs.lounge;

import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import roboguice.util.Ln;
import andlabs.lounge.model.Game;
import andlabs.lounge.service.LoungeService;
import andlabs.lounge.service.LoungeServiceDef;
import andlabs.lounge.util.Ln;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class LoungeServiceController {

    private Set<LoungeServiceCallback> mLoungeServiceCallbackSet = new HashSet<LoungeServiceCallback>();

    //TODO: Use weak reference to get rid of leak
    @SuppressLint("HandlerLeak")
    Messenger mMessenger = new Messenger(new Handler() {

        @Override
        public void handleMessage(Message message) {
            Ln.v("Handler.handleMessage(): message = %s", message);
            switch (message.what) {

            case 42:
                for (LoungeServiceCallback loungeServiceCallback : mLoungeServiceCallbackSet)
                    loungeServiceCallback.theAnswerIs42();
                break;

            case 1:
                Ln.v("Handler.handleMessage(): Server connected ... process login");
                for (LoungeServiceCallback loungeServiceCallback : mLoungeServiceCallbackSet) {
                    loungeServiceCallback.onStart();
                }
                break;

            case 7:
                try {
                    Ln.v("Handler.handleMessage(): Getting update for games/matches/players: %s", message.getData());
                } catch (ConcurrentModificationException e) {
                    Ln.w("LoungeServiceController", e.getMessage());
                }
                Serializable involvedGames = message.getData().getSerializable("involvedGameList");
                Serializable openGames = message.getData().getSerializable("openGameList");
                for (LoungeServiceCallback loungeServiceCallback : mLoungeServiceCallbackSet) {
                    loungeServiceCallback.onOpenGamesUpdate((ConcurrentHashMap<String, Game>) openGames);
                    loungeServiceCallback.onRunningGamesUpdate((ConcurrentHashMap<String, Game>) involvedGames);
                }
                break;

            case 18:
                try {
                    Ln.v("Handler.handleMessage(): Getting new message: %s", message.getData());
                } catch (ConcurrentModificationException e) {
                    Ln.w("LoungeServiceController", e.getMessage());
                }

                String matchId = message.getData().getString("matchID");
                Bundle data = message.getData().getBundle("data");
                for (LoungeServiceCallback loungeServiceCallback : mLoungeServiceCallbackSet) {
                    loungeServiceCallback.onGameMessage(matchId, data);
                }

            default:
                Ln.v("Handler.handleMessage(): message = %s", message);

            }
        }
    });

    private LoungeServiceDef mLoungeService;

    ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Ln.v("ServiceConnection.onServiceDisconnected():");
            mLoungeService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Ln.v("ServiceConnection.onServiceConnected():");
            mLoungeService = LoungeServiceDef.Stub.asInterface(service);
            try {
                mLoungeService.connect();
            } catch (RemoteException e) {
                Ln.e(e, "ServiceConnection.onServiceConnected(): caught exception while connecting");
            }
        }

    };

    private static LoungeServiceController mInstance;
    public static LoungeServiceController getInstance() {
        if (mInstance == null) {
            mInstance = new LoungeServiceController();
        }
        return mInstance;
    }

    private LoungeServiceController() {
        
    }

    public void bindServiceTo(Context pContext) {
        Ln.v("bindServiceTo()");
        if (mLoungeService == null) {
            Intent serviceIntent = new Intent(pContext, LoungeService.class);
            serviceIntent.putExtra("client-messenger", mMessenger);
            pContext.bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            try {
                mLoungeService.reconnect();
            } catch (RemoteException e) {
                Ln.e(e, "bindServiceTo(): caught exception while reconnecting");
            }
        }
    }

    public void registerCallback(LoungeServiceCallback pLoungeServiceCallback) {
        Ln.v("registerCallback(): pLoungeServiceCallback = %s", pLoungeServiceCallback);
        mLoungeServiceCallbackSet.add(pLoungeServiceCallback);
    }

    public void unregisterCallback(LoungeServiceCallback pLoungeServiceCallback) {
        Ln.v("unregisterCallback(): pLoungeServiceCallback = %s", pLoungeServiceCallback);
        mLoungeServiceCallbackSet.remove(pLoungeServiceCallback);
    }

    public void unbindServiceFrom(Context pContext) {
        Ln.v("unbindServiceFrom()");
        if (mLoungeServiceCallbackSet.isEmpty()) {
            pContext.unbindService(mServiceConnection);
        }
    }

    public void login(String pPlayerName) {
        Ln.v("login(): pPlayerName = %s", pPlayerName);
        try {
            mLoungeService.login(pPlayerName);
        } catch (RemoteException e) {
            Ln.e(e, "login(): caught exception while processing login");
        }
    }

    public void openMatch(String pPackageId, String pDisplayName) {
        Ln.v("openMatch(): pPackageId = %s, pDisplayName = %s", pPackageId, pDisplayName);
        try {
            mLoungeService.openMatch(pPackageId, pDisplayName);
        } catch (RemoteException e) {
            Ln.e(e, "openMatch(): caught exception while opening a match");
        }
    }

    public void joinMatch(String pGameId, String pMatchId) {
        Ln.v("joinMatch(): pGameId = %s, pMatchId = %s", pGameId, pMatchId);
        try {
            mLoungeService.joinMatch(pGameId, pMatchId);
        } catch (RemoteException e) {
            Ln.e(e, "joinMatch(): caught exception while joining a match");
        }
    }

    public void checkin(String pPackageId, String pMatchId) {
        Ln.v("checkin(): pPackageId = %s, pMatchId = %s", pPackageId, pMatchId);
        try {
            mLoungeService.checkin(pPackageId, pMatchId);
        } catch (RemoteException e) {
            Ln.e(e, "checkin(): caught exception while checkin a match");
        }
    }

    public void sendGameMove(String pPackageId, String pMatchId, Bundle pMoveBundle) {
        Ln.v("sendGameMove(): pPackageId = %s, pMatchId = %s, pMoveBundle = %s", pPackageId, pMatchId, pMoveBundle);
        try {
            mLoungeService.move(pPackageId, pMatchId, pMoveBundle);
        } catch (RemoteException e) {
            Ln.e(e, "sendGameMove(): caught exception while opening a game move");
        }

    }

    public void closeMatch(String pPackageId, String pMatchId2) {
        Ln.v("closeMatch():  pMatchId = %s",  pPackageId);
        try {
            mLoungeService.closeMatch(pPackageId, pMatchId2);
        } catch (RemoteException e) {
            Ln.e(e, "Error in closeMatch()");
        }

    }

    public void checkout(String pPackageId, String pMatchId) {
        try {
            mLoungeService.checkout(pPackageId,pMatchId);
        } catch (RemoteException e) {
            Ln.e(e, "Error in CheckOut() "+pMatchId);
        }
        
    }

}

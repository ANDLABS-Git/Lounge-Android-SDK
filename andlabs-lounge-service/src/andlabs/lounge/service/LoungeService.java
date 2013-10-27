/*
 * Copyright (C) 2012, 2013 ANDLABS GmbH. All rights reserved.
 *
 * www.lounge.andlabs.com
 * lounge@andlabs.com
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

package andlabs.lounge.service;

import java.util.Locale;

import andlabs.lounge.util.Ln;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings.Secure;

public class LoungeService extends Service {

    private Messenger mMessenger;

    private LoungeServiceImpl mLoungeService = new LoungeServiceImpl() {

        public String getPlayerId() {
            String playerId = null;
            String packageName = LoungeService.this.getPackageName();
            if ("andlabs.lounge.app".equalsIgnoreCase(packageName)) {
                AccountManager accountManager = (AccountManager) LoungeService.this.getSystemService(Activity.ACCOUNT_SERVICE);
                Account[] accounts = accountManager.getAccountsByType("andlabs.lounge");
                if (accounts.length > 0) {
                    playerId = accounts[0].name;
                    Ln.d("playerId = %s", playerId);
                } else {
                    Ln.d("here could a notification ask user to login/register");
//                    this.startActivity(new Intent("andlabs.lounge.account.AUTHENTICATE"));
                }
            } else {
                playerId = Secure.getString(LoungeService.this.getContentResolver(), Secure.ANDROID_ID);
                Ln.d("playerId = %s", playerId);
            }
            return playerId;
        };

        public String getPlayerName() {
            String playerName = null;
            String packageName = LoungeService.this.getPackageName();
            if ("andlabs.lounge.app".equalsIgnoreCase(packageName)) {
                AccountManager accountManager = (AccountManager) LoungeService.this.getSystemService(Activity.ACCOUNT_SERVICE);
                Account[] accounts = accountManager.getAccountsByType("andlabs.lounge");
                if (accounts.length > 0) {
                    playerName = accountManager.getUserData(accounts[0], "PLAYER_NAME");
                    Ln.d("playerName = %s", playerName);
                } else {
                    Ln.d("here could a notification ask user to login/register");
//                    this.startActivity(new Intent("andlabs.lounge.account.AUTHENTICATE"));
                }
            } else {
                playerName = getPlayerId().substring(0, 5).toUpperCase(Locale.UK);
                Ln.d("playerName = %s", playerName);
            }
            return playerName;
        };

        @Override
        public void login(String uuid, String playerId) {
            Ln.v("overriding %s (%s) with %s (%s)", uuid, playerId, getPlayerId(), getPlayerName());
            super.login(getPlayerId(), getPlayerName());
        };

    };

    private LoungeServiceImpl.MessageHandler mMessageHandler = new LoungeServiceImpl.MessageHandler() {
        
        @Override
        public void send(Message message) {
            try {
                if (mMessenger != null) {
                    mMessenger.send(message);
                } else {
                    Ln.w("mMessanger is not set, throwing %s", message);
                }
            } catch (RemoteException e) {
                Ln.e(e, "MessageHandler.send(): caught exception while sending message %d", message.what);
            }
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        Ln.v("intent = %s", intent);
        mMessenger = (Messenger) intent.getParcelableExtra("client-messenger");
        return mLoungeService;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Ln.v("intent = %s", intent);
        mMessenger = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        Ln.v("creating service ...");
        super.onCreate();
        mLoungeService.setMessageHandler(mMessageHandler);
        mLoungeService.connect();
    }


    @Override
    public void onStart(Intent intent, int startId) {
        Ln.v("intent = %s, startId = %d", intent, startId);
        super.onStart(intent, startId);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Ln.v("intent = %s, flags = %d, startId = %d", intent, flags, startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Ln.v("onDestroy():"); 
        if (mLoungeService != null) {
            mLoungeService.disconnect();
        }
        super.onDestroy();
    }


    private boolean mRetry = false;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context pContext, Intent pIntent) {
            final boolean noConnectivity = pIntent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            final String reason = pIntent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            final boolean isFailover = pIntent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            final NetworkInfo currentNetworkInfo = (NetworkInfo) pIntent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            final NetworkInfo otherNetworkInfo = (NetworkInfo) pIntent
                    .getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if (noConnectivity) {
                mRetry = true; // if there's no connection, try to reconnect once the connection is established again
            } else {
                tryReconnect();
            }
        }

    };

    private void tryReconnect() {
        if (mRetry) {
            mLoungeService.connect();
            mRetry = false;
        }
    }

}

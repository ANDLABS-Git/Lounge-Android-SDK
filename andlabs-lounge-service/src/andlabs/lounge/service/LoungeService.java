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
import android.accounts.OnAccountsUpdateListener;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.RemoteViews;

public class LoungeService extends Service {

    private static final String SWITCH_EVENT = "CLICK";

    private Messenger mMessenger;

    private LoungeServiceImpl mLoungeService = new LoungeServiceImpl() {

        private String playerId = "playerid";
        private String playerName = "playerName";


        public String getPlayerId() {
            String packageName = LoungeService.this.getPackageName();
            if ("andlabs.lounge.app".equalsIgnoreCase(packageName)) {
                AccountManager accountManager = (AccountManager) LoungeService.this.getSystemService(Activity.ACCOUNT_SERVICE);
                Account[] accounts = accountManager.getAccountsByType("andlabs.lounge");
                if (accounts.length > 0) {
                    playerId = accounts[0].name;
                    Ln.d("playerId = %s", playerId);
                } else {
                    Ln.d("here could a notification ask user to login/register");
                    // this.startActivity(new
                    // Intent("andlabs.lounge.account.AUTHENTICATE"));
                }
            } else {
                playerId = Secure.getString(LoungeService.this.getContentResolver(), Secure.ANDROID_ID);
                Ln.d("playerId = %s", playerId);
            }
            return playerId;
        };


        public String getPlayerName() {
            String packageName = LoungeService.this.getPackageName();
            if ("andlabs.lounge.app".equalsIgnoreCase(packageName)) {
                AccountManager accountManager = (AccountManager) LoungeService.this.getSystemService(Activity.ACCOUNT_SERVICE);
                Account[] accounts = accountManager.getAccountsByType("andlabs.lounge");
                if (accounts.length > 0) {
                    playerName = accountManager.getUserData(accounts[0], "PLAYER_NAME");
                    Ln.d("playerName = %s", playerName);
                } else {
                    Ln.d("here could a notification ask user to login/register");
                    // this.startActivity(new
                    // Intent("andlabs.lounge.account.AUTHENTICATE"));
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
                    if (message != null && message.obj != null) {
                        updateNotification(message.what + "", message.arg1 + "", message.obj.toString());
                    }
                } else {
                    Ln.w("mMessanger is not set, throwing %s", message);
                }
            } catch (RemoteException e) {
                Ln.e(e, "MessageHandler.send(): caught exception while sending message %d", message.what);
            }
        }
    };

    private OnAccountsUpdateListener mOnAccountsUpdateListener = new OnAccountsUpdateListener() {

        @Override
        public void onAccountsUpdated(Account[] accounts) {
            String packageName = LoungeService.this.getPackageName();
            if ("andlabs.lounge.app".equalsIgnoreCase(packageName)) {
                AccountManager accountManager = (AccountManager) LoungeService.this.getSystemService(Activity.ACCOUNT_SERVICE);
                accounts = accountManager.getAccountsByType("andlabs.lounge");
                if (accounts.length > 0) {
                    mLoungeService.login(null, null);
                } else {
                    Ln.d("here could a notification ask user to login/register");
                    // this.startActivity(new
                    // Intent("andlabs.lounge.account.AUTHENTICATE"));
                }
            }
        }

    };

    private SwitchButtonListener switchButtonListener;

    private Builder builder;


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
        AccountManager accountManager = (AccountManager) LoungeService.this.getSystemService(Activity.ACCOUNT_SERVICE);
        accountManager.addOnAccountsUpdatedListener(mOnAccountsUpdateListener, null, true);
        // this.setForeground(true);
        startForeground(1, getCustomNotification("init service", "-", "-"));
    }


    public Notification getCustomNotification(String n1, String n2, String n3) {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);

        // Open NotificationView Class on Notification Click
        // Send data to NotificationView Class
        // Open NotificationView.java Activity

        Intent intent = new Intent();
        // Send data to NotificationView Class
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (builder == null) {
            builder = new NotificationCompat.Builder(this)
            // Set Icon
                    .setSmallIcon(R.drawable.lounge_icon).setOnlyAlertOnce(true)
                    // Set Ticker Message
                    .setTicker("Lounge Debug")
                    // Dismiss Notification
                    .setAutoCancel(true).setContentIntent(pIntent)
                    // Set PendingIntent into Notification
                    // Set RemoteViews into Notification
                    .setContent(remoteViews);
        }

        // Locate and set the Image into customnotificationtext.xml ImageViews
        // remoteViews.setImageViewResource(R.id.imagenotileft,R.drawable.lounge_icon);
        // remoteViews.setImageViewResource(R.id.imagenotiright,R.drawable.lounge_icon);

        // Locate and set the Text into customnotificationtext.xml TextViews
        remoteViews.setTextViewText(R.id.state1, n1);
        remoteViews.setTextViewText(R.id.state2, n2);
        remoteViews.setTextViewText(R.id.state3, n3);

        Intent switchIntent = new Intent(SWITCH_EVENT);
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0, switchIntent, 0);

        remoteViews.setOnClickPendingIntent(R.id.kill_service, pendingSwitchIntent);

        switchButtonListener = new SwitchButtonListener();
        registerReceiver(switchButtonListener, new IntentFilter(SWITCH_EVENT));

        // Create Notification Manager
        // Build Notification with Notification Manager
        // notificationmanager.notify(0, builder.build());
        Log.i("debug", "notif " + builder.build());
        return builder.build();

    }


    public void updateNotification(String n1, String n2, String n3) {
        startForeground(1, getCustomNotification(n1, n2, n3));
    }


    public class SwitchButtonListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("debug", "buttttonn");
        }

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
        AccountManager accountManager = (AccountManager) LoungeService.this.getSystemService(Activity.ACCOUNT_SERVICE);
        accountManager.removeOnAccountsUpdatedListener(mOnAccountsUpdateListener);
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
                mRetry = true; // if there's no connection, try to reconnect
                               // once the connection is established again
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

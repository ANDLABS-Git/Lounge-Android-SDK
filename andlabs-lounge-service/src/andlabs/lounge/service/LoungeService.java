package andlabs.lounge.service;

import andlabs.lounge.util.Ln;
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

public class LoungeService extends Service {

    private Messenger mMessenger;
    private LoungeServiceImpl mLoungeService = new LoungeServiceImpl();
    private LoungeServiceImpl.MessageHandler mMessageHandler = new LoungeServiceImpl.MessageHandler() {
        
        @Override
        public void send(Message message) {
            try {
                mMessenger.send(message);
            } catch (RemoteException e) {
                Ln.e(e, "MessageHandler.send(): caught exception while sending message %d", message.what);
            }
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        Ln.v("onBind(): arg0 = %s", intent);
        mMessenger = intent.getParcelableExtra("client-messenger");
        mLoungeService.setMessageHandler(mMessageHandler);
        return mLoungeService;
    }


    @Override
    public void onCreate() {
        Ln.v("onCreate():");
        super.onCreate();
    }


    @Override
    public void onStart(Intent intent, int startId) {
        Ln.v("onStart():");
        super.onStart(intent, startId);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Ln.v("onStartCommand():");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Ln.v("onDestroy():");
        try {
            if (mLoungeService != null) {
                mLoungeService.disconnect();
            }
        } catch (RemoteException e) {
            Ln.e(e, "unbindServiceFrom(): caught exception while disconnecting");
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
            try {
                mLoungeService.connect();
            } catch (RemoteException e) {
                Ln.e(e, "tryReconnect(): caught exception while trying to reconnect");
            } finally {
                mRetry = false;
            }
        }
    }

}

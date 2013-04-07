package andlabs.lounge.service;

import roboguice.util.Ln;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class LoungeService extends Service {

    private LoungeServiceImpl mLoungeService;

    @Override
    public IBinder onBind(Intent intent) {
        Ln.v("onBind(): arg0 = %s", intent);
        mLoungeService = new LoungeServiceImpl(intent);
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

}

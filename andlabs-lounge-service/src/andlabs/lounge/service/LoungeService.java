package andlabs.lounge.service;

import roboguice.util.Ln;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LoungeService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        Ln.v("onBind(): arg0 = %s", intent);
        return new LoungeServiceImpl(intent);
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

}

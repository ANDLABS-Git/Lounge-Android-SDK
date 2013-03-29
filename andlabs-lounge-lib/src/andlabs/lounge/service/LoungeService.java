package andlabs.lounge.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class LoungeService extends Service {

	private Messenger mMessenger;


	@Override
	public IBinder onBind(Intent intent) {
		Log.v("LoungeService", "onBind(): arg0 = " + intent);
		mMessenger = intent.getParcelableExtra("client-messenger");
		return new LoungeServiceImpl(mMessenger);
	}


	@Override
	public void onCreate() {
		Log.v("LoungeService", "onCreate():");
		super.onCreate();
	}


	@Override
	public void onStart(Intent intent, int startId) {
		Log.v("LoungeService", "onStart():");
		super.onStart(intent, startId);
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("LoungeService", "onStartCommand():");
		return super.onStartCommand(intent, flags, startId);
	}

}

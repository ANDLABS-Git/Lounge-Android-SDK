package andlabs.lounge.service;

import java.net.MalformedURLException;

import io.socket.IOCallback;
import io.socket.SocketIO;
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
	private SocketIO mSocketIO;
	private IOCallback mSocketIOCallback;
	private IBinder mServiceBinder = new LoungeServiceDef.Stub() {

		@Override
		public void disconnect() throws RemoteException {
			Log.v("LoungeService", "Binder.disconnect():");
			mSocketIO.disconnect();
		}


		@Override
		public void chat(String message) throws RemoteException {
			// TODO Auto-generated method stub

		}
	};


	@Override
	public IBinder onBind(Intent intent) {
		Log.v("LoungeService", "onBind(): arg0 = " + intent);
		mMessenger = intent.getParcelableExtra("client-messenger");
		try {
			Message message = new Message();
			message.what = 42;
			message.setData(Bundle.EMPTY);
			mMessenger.send(message);
		} catch (RemoteException e) {
			Log.e("LoungeService", "caught exception while sending message", e);
		}
		return mServiceBinder;
	}


	@Override
	public void onCreate() {
		Log.v("LoungeService", "onCreate():");
		super.onCreate();
		createSocketIO();
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


	private void createSocketIO() {
		try {
			mSocketIO = new SocketIO("http://lounge-server.jit.su/");
			mSocketIOCallback = new SocketIOCallback(mSocketIO);
			mSocketIO.connect(mSocketIOCallback);
		} catch (MalformedURLException e) {
			Log.e("LoungeService", "caught exception while creating/connecting SocketIO", e);
		}

	}

}

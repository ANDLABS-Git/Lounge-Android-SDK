package andlabs.lounge;

import andlabs.lounge.service.LoungeService;
import andlabs.lounge.service.LoungeServiceDef;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;


public class LoungeController {

	@SuppressLint("HandlerLeak")
	Messenger mMessenger = new Messenger(new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Log.v("LoungeController", "getClientMessenger(): Handler.handleMessage(): msg = " + msg);
		}
	});

	private LoungeServiceDef mLoungeService;

	ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.v("LoungeController", "ServiceConnection.onServiceDisconnected():");
			mLoungeService = null;
		}


		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.v("LoungeController", "ServiceConnection.onServiceConnected():");
			mLoungeService = LoungeServiceDef.Stub.asInterface(service);
			try {
				mLoungeService.connect();
			} catch (RemoteException e) {
				Log.e("LoungeController", "ServiceConnection.onServiceConnected(): caught exception while connecting", e);
			}
		}

	};


	public void bindServiceTo(Context pContext) {
		Intent serviceIntent = new Intent(pContext, LoungeService.class);
		serviceIntent.putExtra("client-messenger", mMessenger);
		pContext.bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}


	public void unbindServiceFrom(Context pContext) {
		try {
			if (mLoungeService != null) {
				mLoungeService.disconnect();
			}
		} catch (RemoteException e) {
			Log.e("LoungeController", "unbindService(): caught exception while disconnecting", e);
		}
		pContext.unbindService(mServiceConnection);
	}

}

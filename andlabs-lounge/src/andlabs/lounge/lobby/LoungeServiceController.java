package andlabs.lounge.lobby;

import java.io.Serializable;
import java.util.ArrayList;

import andlabs.lounge.model.Game;
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


public class LoungeServiceController {

	private LoungeServiceCallback mLoungeServiceCallback;

	@SuppressLint("HandlerLeak")
	Messenger mMessenger = new Messenger(new Handler() {

		@Override
		public void handleMessage(Message message) {
			Log.v("LoungeServiceController", "Handler.handleMessage(): message = " + message);
			switch (message.what) {

				case 42:
					if (mLoungeServiceCallback != null) {
						mLoungeServiceCallback.theAnswerIs42();
					}
					break;

				case 1:
					Log.v("LoungeServiceController", "Handler.handleMessage(): Server connected ... process login");
					try {
						mLoungeService.login("John Doe");
					} catch (RemoteException e) {
						Log.e("LoungeServiceController", "Handler.handleMessage(): caught exception while processing login", e);
					}
					break;

				case 7:
					Log.v("LoungeServiceController", "Handler.handleMessage(): Getting update for games/matches/players: " + message.getData());
					if (mLoungeServiceCallback != null) {
						Serializable gameList = message.getData().getSerializable("gameList");
						mLoungeServiceCallback.onStateUpdate((ArrayList<Game>) gameList);
					}
					break;

				default:
					Log.v("LoungeServiceController", "Handler.handleMessage(): message = " + message);

			}
		}
	});

	private LoungeServiceDef mLoungeService;

	ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.v("LoungeServiceController", "ServiceConnection.onServiceDisconnected():");
			mLoungeService = null;
		}


		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.v("LoungeServiceController", "ServiceConnection.onServiceConnected():");
			mLoungeService = LoungeServiceDef.Stub.asInterface(service);
			try {
				mLoungeService.connect();
			} catch (RemoteException e) {
				Log.e("LoungeServiceController", "ServiceConnection.onServiceConnected(): caught exception while connecting", e);
			}
		}

	};


	public void bindServiceTo(Context pContext) {
		Log.v("LoungeServiceController", "bindServiceTo()");
		Intent serviceIntent = new Intent(pContext, LoungeService.class);
		serviceIntent.putExtra("client-messenger", mMessenger);
		pContext.bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}


	public void registerCallback(LoungeServiceCallback pLoungeServiceCallback) {
		Log.v("LoungeServiceController", "registerCallback()");
		mLoungeServiceCallback = pLoungeServiceCallback;
	}


	public void unregisterCallback(LoungeServiceCallback pLoungeServiceCallback) {
		Log.v("LoungeServiceController", "unregisterCallback()");
		mLoungeServiceCallback = null;
	}


	public void unbindServiceFrom(Context pContext) {
		Log.v("LoungeServiceController", "unbindServiceFrom()");
		try {
			if (mLoungeService != null) {
				mLoungeService.disconnect();
			}
		} catch (RemoteException e) {
			Log.e("LoungeServiceController", "unbindServiceFrom(): caught exception while disconnecting", e);
		}
		pContext.unbindService(mServiceConnection);
	}

}

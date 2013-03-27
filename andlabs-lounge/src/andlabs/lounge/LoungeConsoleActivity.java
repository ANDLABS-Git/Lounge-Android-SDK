package andlabs.lounge;

import andlabs.lounge.service.LoungeService;
import andlabs.lounge.service.LoungeServiceDef;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;

public class LoungeConsoleActivity extends Activity {

	private LoungeServiceDef mLoungeService;

	ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.v("LoungeConsoleActivity", "ServiceConnection.onServiceDisconnected():");

		}


		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.v("LoungeConsoleActivity", "ServiceConnection.onServiceConnected():");
			mLoungeService = LoungeServiceDef.Stub.asInterface(service);
		}

	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v("LoungeConsoleActivity", "onCreate():");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lounge_console);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v("LoungeConsoleActivity", "onCreateOptionsMenu():");
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lounge_console, menu);
		return true;
	}


	@Override
	protected void onResume() {
		Log.v("LoungeConsoleActivity", "onResume():");
		super.onResume();
		// bind to the Lounge Service
		Intent serviceIntent = new Intent(this, LoungeService.class);
		bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}


	@Override
	protected void onPause() {
		Log.v("LoungeConsoleActivity", "onPause():");
		try {
			mLoungeService.disconnect();
		} catch (RemoteException e) {
			Log.e("LoungeConsoleActivity", "onPause(): caught exception while disconnecting", e);
		}
		unbindService(mServiceConnection);
		super.onPause();
	}
}

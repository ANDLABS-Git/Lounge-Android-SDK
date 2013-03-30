package andlabs.lounge.frontend;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class LoungeConsoleActivity extends Activity {

	LoungeLobbyController mLoungeLobbyController = LoungeLobbyController.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v("LoungeConsoleActivity", "onCreate():");
		super.onCreate(savedInstanceState);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v("LoungeConsoleActivity", "onCreateOptionsMenu():");
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}


	@Override
	protected void onStart() {
		Log.v("LoungeConsoleActivity", "onStart():");
		super.onStart();
		// bind to the Lounge Service
		mLoungeLobbyController.bindServiceTo(this);
	}


	@Override
	protected void onResume() {
		Log.v("LoungeConsoleActivity", "onResume():");
		super.onResume();
	}


	@Override
	protected void onPause() {
		Log.v("LoungeConsoleActivity", "onPause():");
		super.onPause();
	}


	@Override
	protected void onStop() {
		Log.v("LoungeConsoleActivity", "onStop():");
		mLoungeLobbyController.unbindServiceFrom(this);
		super.onStop();
	}
}

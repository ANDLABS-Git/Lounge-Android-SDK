package andlabs.lounge;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class LoungeConsoleActivity extends Activity {

	LoungeController mLoungeController = new LoungeController();

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
	protected void onStart() {
		Log.v("LoungeConsoleActivity", "onStart():");
		super.onStart();
		// bind to the Lounge Service
		mLoungeController.bindServiceTo(this);
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
		mLoungeController.unbindServiceFrom(this);
		super.onStop();
	}
}

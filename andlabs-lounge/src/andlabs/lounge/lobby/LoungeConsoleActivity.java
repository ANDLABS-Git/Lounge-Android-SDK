package andlabs.lounge.lobby;

import java.util.List;

import andlabs.lounge.lobby.R;
import andlabs.lounge.lobby.model.ChatMessage;
import andlabs.lounge.lobby.model.LobbyListElement;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class LoungeConsoleActivity extends Activity {

	LoungeLobbyController mLoungeLobbyController = new LoungeLobbyController();

	LoungeLobbyCallback mLoungeLobbyCallback = new LoungeLobbyCallback() {
		
		@Override
		public void onLobbyDataUpdated(List<LobbyListElement> data) {
			// TODO Auto-generated method stub
			LoungeConsoleActivity.this.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
		
		@Override
		public void onChatDataUpdated(List<ChatMessage> data) {
			// TODO Auto-generated method stub
			
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
		mLoungeLobbyController.registerCallback(mLoungeLobbyCallback);
	}


	@Override
	protected void onPause() {
		Log.v("LoungeConsoleActivity", "onPause():");
		mLoungeLobbyController.unregisterCallback(mLoungeLobbyCallback);
		super.onPause();
	}


	@Override
	protected void onStop() {
		Log.v("LoungeConsoleActivity", "onStop():");
		mLoungeLobbyController.unbindServiceFrom(this);
		super.onStop();
	}
}

package andlabs.lounge.game;

import andlabs.lounge.LoungeGameCallback;
import andlabs.lounge.LoungeGameController;
import andlabs.lounge.lobby.ui.LobbyActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {

	LoungeGameController mLoungeGameController = new LoungeGameController();

	LoungeGameCallback mLoungeGameCallback = new LoungeGameCallback() {

		@Override
		public void onCheckIn(String player) {
			// TODO Auto-generated method stub

		}


		@Override
		public void onAllPlayerCheckedIn() {
			// TODO Auto-generated method stub

		}


		@Override
		public void onGameMessage(Bundle msg) {
			// TODO Auto-generated method stub

		}


		@Override
		public void onCheckOut(String player) {
			// TODO Auto-generated method stub

		}

	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startActivity(new Intent(this, LobbyActivity.class));
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mLoungeGameController.bindServiceTo(this);
	}


	@Override
	public void onResume() {
		mLoungeGameController.registerCallback(mLoungeGameCallback);
		super.onResume();
	}


	@Override
	public void onPause() {
		mLoungeGameController.unregisterCallback(mLoungeGameCallback);
		super.onPause();
	}


	@Override
	public void onStop() {
		mLoungeGameController.unbindServiceFrom(this);
		super.onStop();
	}

}

package andlabs.lounge.game;

import andlabs.lounge.LoungeGameCallback;
import andlabs.lounge.LoungeGameController;
import andlabs.lounge.lobby.LoungeConstants;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {

    private PointsView mView;
	LoungeGameController mLoungeGameController = new LoungeGameController();

	LoungeGameCallback mLoungeGameCallback = new LoungeGameCallback() {

		@Override
		public void onCheckIn(String player) {
		}


		@Override
		public void onAllPlayerCheckedIn() {
		}


		@Override
		public void onGameMessage(Bundle msg) {
		    
		    Point point = new Point();
		    point.set((int)Float.parseFloat(msg.getString("x")), (int)(Float.parseFloat(msg.getString("y"))));
		    mView.addOthersPoint(point);
		}


		@Override
		public void onCheckOut(String player) {
		}

	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mView = new PointsView(this, getIntent().getStringExtra(LoungeConstants.EXTRA_MATCH_ID), mLoungeGameController);
		
		setContentView(mView);
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
		mLoungeGameController.checkin(getIntent().getStringExtra(LoungeConstants.EXTRA_MATCH_ID));
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

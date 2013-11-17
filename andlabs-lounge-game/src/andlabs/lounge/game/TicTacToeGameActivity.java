package andlabs.lounge.game;

import andlabs.lounge.LoungeGameCallback;
import andlabs.lounge.LoungeGameController;
import andlabs.lounge.lobby.LoungeConstants;
import andlabs.lounge.util.Ln;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class TicTacToeGameActivity extends Activity {

	LoungeGameController mLoungeGameController = new LoungeGameController();

	LoungeGameCallback mLoungeGameCallback = new LoungeGameCallback() {

		@Override
		public void onCheckIn(String player) {
            Ln.d("LoungeGameCallback.onCheckIn(): player = %s", player);
		}


		@Override
		public void onAllPlayerCheckedIn() {
            Ln.d("LoungeGameCallback.onAllPlayerCheckedIn():");
		}


		@Override
		public void onGameMessage(Bundle msg) {
		    Ln.d("LoungeGameCallback.onGameMessage(): msg = %s", msg);
		}


		@Override
		public void onCheckOut(String player) {
            Ln.d("LoungeGameCallback.onCheckOut(): player = %s", player);
		}

	};


	private View mView;

    private String mMatchId;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mMatchId = getIntent().getStringExtra(LoungeConstants.EXTRA_MATCH_ID);
		mView = new View(this) {

	        public boolean dispatchTouchEvent(android.view.MotionEvent event) {
	            Ln.d("View.dispatchTouchEvent():");
	            return false;
	        };


	        protected void onDraw(android.graphics.Canvas canvas) {
	            Ln.d("View.onDraw():");
	        };
	    };
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
		super.onStart();
		mLoungeGameController.bindServiceTo(this);
	}


	@Override
	public void onResume() {
        super.onResume();
		mLoungeGameController.registerCallback(mLoungeGameCallback);
		if (mMatchId != null) {
	        mLoungeGameController.checkin(mMatchId);
		}
	}


	@Override
	public void onPause() {
        if (mMatchId != null) {
            mLoungeGameController.checkout(mMatchId);
        }
		mLoungeGameController.unregisterCallback(mLoungeGameCallback);
		super.onPause();
	}


	@Override
	public void onStop() {
		mLoungeGameController.unbindServiceFrom(this);
		super.onStop();
	}

}

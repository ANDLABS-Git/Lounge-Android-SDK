package andlabs.lounge.game;

import andlabs.lounge.LoungeGameCallback;
import andlabs.lounge.LoungeGameController;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class TicTacToeConfigurationActivity extends Activity {

    LoungeGameController mLoungeGameController = new LoungeGameController();

    LoungeGameCallback mLoungeGameCallback = new LoungeGameCallback() {

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe_configuration);
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
        Button button = (Button) findViewById(R.id.tictactoeHostButton);
        button.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mLoungeGameController.openMatch(TicTacToeGameActivity.class.getName(), "ANDLABS TicTacToe Game");
                TicTacToeConfigurationActivity.this.finish();
            }
        });
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

package andlabs.loungedebugger;

import andlabs.lounge.LoungeGameCallback;
import andlabs.lounge.LoungeGameController;
import andlabs.lounge.lobby.LoungeConstants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements LoungeGameCallback {

    private TextView history;
    private EditText key;
    private EditText value;
    private LoungeGameController lounge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        history = (TextView) findViewById(R.id.history);

        key = (EditText) findViewById(R.id.key);

        value = (EditText) findViewById(R.id.value);

        Intent i = getIntent();

        addToHistory(i.getStringExtra(LoungeConstants.EXTRA_MATCH_ID));
        addToHistory(i.getStringExtra(LoungeConstants.EXTRA_IS_HOST));
        addToHistory(i.getStringExtra(LoungeConstants.EXTRA_HOST_NAME));
        // addToHistory(i.getStringArrayExtra(LoungeConstants.EXTRA_PLAYER_NAMES));
        lounge = new LoungeGameController();

        ((Button) findViewById(R.id.checkInBtn)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("LoungeDebugger", "Checkin " + getIntent().getStringExtra(LoungeConstants.EXTRA_MATCH_ID));
                lounge.checkin(getIntent().getStringExtra(LoungeConstants.EXTRA_MATCH_ID));
            }
        });

        ((Button) findViewById(R.id.checkOutBtn)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("LoungeDebugger", "CheckOut " + getIntent().getStringExtra(LoungeConstants.EXTRA_MATCH_ID));
                lounge.checkout(getIntent().getStringExtra(LoungeConstants.EXTRA_MATCH_ID));
            }
        });

        ((Button) findViewById(R.id.closeMatchBtn)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("LoungeDebugger", "CloseMatch " + getIntent().getStringExtra(LoungeConstants.EXTRA_MATCH_ID));
                lounge.closeMatch(getIntent().getStringExtra(LoungeConstants.EXTRA_MATCH_ID));
            }
        });

        ((Button) findViewById(R.id.sendBtn)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Bundle b = new Bundle();
                Log.i("LoungeDebugger", "move :" + key.getText().toString() + " " + value.getText().toString());
                b.putString(key.getText().toString(), value.getText().toString());
                lounge.sendGameMove(getIntent().getStringExtra(LoungeConstants.EXTRA_MATCH_ID), b);
            }
        });

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        lounge.registerCallback(this);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        lounge.bindServiceTo(this);

    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        lounge.unbindServiceFrom(this);
        lounge.unregisterCallback(this);
    }

    public void addToHistory(String t) {
        history.setText(history.getText() + "\n" + t);
    }

    public void send(View v) {
        // Bundle b = new Bundle();
        // b.putString(key.getText().toString(), value.getText().toString());
        // lounge.sendGameMove(getIntent().getStringExtra(LoungeConstants.EXTRA_MATCH_ID), b);
    }

    public void checkIn(View v) {

        // lounge.checkin(getIntent().getStringExtra(LoungeConstants.EXTRA_MATCH_ID));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onCheckIn(String player) {
        addToHistory("Player checkIN: " + player);

    }

    @Override
    public void onAllPlayerCheckedIn() {
        addToHistory("all Players Checked in");

    }

    @Override
    public void onGameMessage(Bundle msg) {
        for (String key : msg.keySet()) {
            addToHistory("MOVE: " + msg.get(key));
        }

    }

    @Override
    public void onCheckOut(String player) {
        addToHistory("Player checkIN: " + player);

    }

}

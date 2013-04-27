package andlabs.loungedebugger;

import andlabs.lounge.Lounge;
import andlabs.lounge.Multiplayable;
import andlabs.lounge.lobby.LoungeConstants;
import andlabs.lounge.util.Ln;
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

public class MainActivity extends Activity implements Multiplayable {

    private TextView history;
    private EditText key;
    private EditText value;
    private Lounge lounge;

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
<<<<<<< Updated upstream
        addToHistory(i.getStringExtra(LoungeConstants.EXTRA_GUEST_NAME));
       lounge= new LoungeGameController();
       
       ((Button)findViewById(R.id.checkInBtn)).setOnClickListener(new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Log.i("LoungeDebugger" , "Checkin "+getIntent().getStringExtra(LoungeConstants.EXTRA_MATCH_ID));
            lounge.checkin(getIntent().getStringExtra(LoungeConstants.EXTRA_MATCH_ID));
        }
    });
       
       ((Button)findViewById(R.id.sendBtn)).setOnClickListener(new OnClickListener() {
           
           @Override
           public void onClick(View v) {
               // TODO Auto-generated method stub
               Bundle b = new Bundle();
               Log.i("LoungeDebugger" , "move :"+key.getText().toString()+ " "+ value.getText().toString());
               b.putString(key.getText().toString(), value.getText().toString());
               lounge.sendGameMove(getIntent().getStringExtra(LoungeConstants.EXTRA_MATCH_ID), b);
           }
       });
        
=======
        // addToHistory(i.getStringArrayExtra(LoungeConstants.EXTRA_PLAYER_NAMES));
        lounge = Lounge.getInstance();

        ((Button) findViewById(R.id.checkInBtn)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("LoungeDebugger", "Checkin " + getIntent().getStringExtra(LoungeConstants.EXTRA_MATCH_ID));
                lounge.checkin(getIntent().getStringExtra(LoungeConstants.EXTRA_MATCH_ID));
            }
        });

        ((Button) findViewById(R.id.sendBtn)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Bundle b = new Bundle();
                Log.i("LoungeDebugger", "move :" + key.getText().toString() + " " + value.getText().toString());
                b.putString(key.getText().toString(), value.getText().toString());
                lounge.sendGameMove(b);
            }
        });

>>>>>>> Stashed changes
    }

    @Override
    protected void onResume() {
        super.onResume();
        lounge.registerMultiplayableListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        lounge.bind(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        lounge.unbind(this);
        lounge.unregisterAllMultiplayableListener();
    }

    public void addToHistory(String t) {
        history.setText(history.getText() + "\n" + t);
    }

    public void send(View v) {
        Ln.d("");
        Bundle b = new Bundle();
        b.putString(key.getText().toString(), value.getText().toString());
        lounge.sendGameMove(b);
    }

    public void checkIn(View v) {
        Ln.d("");
        lounge.checkin(getIntent().getStringExtra(LoungeConstants.EXTRA_MATCH_ID));
    }

    public void close(View v) {
        Ln.d("");
        lounge.closeMatch();
    }

    public void checkOut(View v) {
        Ln.d("");
        lounge.checkout();
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
        
        Ln.d("message = " + msg);
        for (String key : msg.keySet()) {
            addToHistory("MOVE: " + msg.get(key));
        }

    }

    @Override
    public void onCheckOut(String player) {
        addToHistory("Player checkIN: " + player);

    }

}

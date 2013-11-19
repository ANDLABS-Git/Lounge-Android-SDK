package andlabs.lounge.game;

import java.util.Random;

import andlabs.lounge.LoungeGameCallback;
import andlabs.lounge.LoungeGameController;
import andlabs.lounge.lobby.LoungeConstants;
import andlabs.lounge.util.Ln;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TicTacToeGameActivity extends Activity {

    protected static final String MOVE = "MOVE";

    protected static final String SIGN = "SIGN";

    private static final String IS_STARTING = "ISSTARTING";

    private static final String IS_INIT_DATA = "ISINITDATA";

    private String opponent_sign;

    private String playerSign;

    LoungeGameController mLoungeGameController = new LoungeGameController();

    LoungeGameCallback mLoungeGameCallback = new LoungeGameCallback() {

        private boolean isOnTurn;


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
            if (msg.getBoolean(IS_INIT_DATA)) {
                if (!isHost) {
                    isOnTurn=!msg.getBoolean(IS_STARTING);
                    if(isOnTurn){
                        subHeader.setText(players[1]+ " is on turn");
                     }else{
                         subHeader.setText(players[0]+ " is on turn");
                     }
                    
                }
               
                
            } else {

                if (msg.getString(SIGN).equalsIgnoreCase(opponent_sign)) {
                    String pos = msg.getString(MOVE);
                    int xPos = Integer.parseInt(pos.split(":")[0]);
                    int yPos = Integer.parseInt(pos.split(":")[1]);

                    Button b = field[xPos][yPos];
                    b.setText(opponent_sign);
                    b.setEnabled(false);
                    subHeader.setText(myName+ " is on turn");
                    
                }else{
                    subHeader.setText(opponentName+ " is on turn");
                    
                }
            }
        }


        @Override
        public void onCheckOut(String player) {
            Ln.d("LoungeGameCallback.onCheckOut(): player = %s", player);
        }

    };



    private String mMatchId;

    private Button f11;
    private Button f12;
    private Button f13;
    private Button f21;
    private Button f22;
    private Button f23;
    private Button f31;
    private Button f32;
    private Button f33;


    private Button[][] field;

    private boolean isOnTurn;

    private boolean isHost;

    private TextView subHeader;

    private String[] players;

    private String myName;

    private String opponentName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMatchId = getIntent().getStringExtra(LoungeConstants.EXTRA_MATCH_ID);

        setContentView(R.layout.ttt);

        isHost = getIntent().getBooleanExtra(LoungeConstants.EXTRA_IS_HOST, false);
         players = getIntent().getStringArrayExtra(LoungeConstants.EXTRA_PLAYER_NAMES);
         
         

        ((TextView) findViewById(R.id.header)).setText(players[0] + " vs. " + players[1]);
        subHeader=(TextView)findViewById(R.id.subheader);

        if (isHost) {
            playerSign = "X";
            opponent_sign = "O";
            isOnTurn = new Random().nextBoolean();

            myName=players[0];
            opponentName=players[1];
            Bundle b = new Bundle();
            b.putBoolean(IS_STARTING, !isOnTurn);
            b.putBoolean(IS_INIT_DATA, true);
            mLoungeGameController.sendGameMove(mMatchId, b);
            
            if(isOnTurn){
               subHeader.setText(players[0]+ " is on turn");
            }else{
                subHeader.setText(players[1]+ " is on turn");
            }
           


        } else {
            myName=players[1];
            opponentName=players[0];
            playerSign = "O";
            opponent_sign = "X";
        }


        f11 = (Button) findViewById(R.id.f11);
        f12 = (Button) findViewById(R.id.f12);
        f13 = (Button) findViewById(R.id.f13);
        f21 = (Button) findViewById(R.id.f21);
        f22 = (Button) findViewById(R.id.f22);
        f23 = (Button) findViewById(R.id.f23);
        f31 = (Button) findViewById(R.id.f31);
        f32 = (Button) findViewById(R.id.f32);
        f33 = (Button) findViewById(R.id.f33);


        field = new Button[3][3];

        field[0][0] = f11;
        field[0][1] = f12;
        field[0][2] = f13;
        field[1][0] = f21;
        field[1][1] = f22;
        field[1][2] = f23;
        field[2][0] = f31;
        field[2][1] = f32;
        field[2][2] = f33;

        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                final Button b = field[i][j];
                final int xPos = i;
                final int yPos = j;
                b.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(isOnTurn){
                        b.setEnabled(false);
                        b.setText(playerSign);

                        Bundle b = new Bundle();
                        b.putString(MOVE, xPos + ":" + yPos);
                        b.putString(SIGN, playerSign);
                        mLoungeGameController.sendGameMove(mMatchId, b);
                        isOnTurn=!isOnTurn;
                        }else{
                            Toast.makeText(TicTacToeGameActivity.this, "Its not your turn", Toast.LENGTH_SHORT).show();
                        }
                        
                                

                    }
                });
            }
        }


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

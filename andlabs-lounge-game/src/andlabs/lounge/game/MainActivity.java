package andlabs.lounge.game;

import andlabs.lounge.lobby.ui.LoungeActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button loungeLobbyButton = (Button) findViewById(R.id.loungeLobbyButton);
        loungeLobbyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoungeActivity.class));
            }
        });
        Button pointsGameButton = (Button) findViewById(R.id.pointsGameButton);
        pointsGameButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PointsGameActivity.class));
            }
        });
        Button tictactoeGameButton = (Button) findViewById(R.id.tictactoeGameButton);
        tictactoeGameButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TicTacToeGameActivity.class));
            }
        });
    }

}

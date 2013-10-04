package andlabs.lounge.lobby.ui;

import java.util.Random;

import andlabs.lounge.lobby.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class LoginActivity extends Activity {


    public static final String PLAYER_NAME = "PLAYER_NAME";
    public static final String USER_ID = "USER_ID";
    private TextView description;
    private View usernameLayout;
    private EditText userNameField;
    private Button playNowBtn;
    private boolean inTempLoginState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        description = (TextView) findViewById(R.id.description);
        usernameLayout = findViewById(R.id.usernameLayout);
        userNameField = (EditText) findViewById(R.id.userNameField);
        playNowBtn = (Button) findViewById(R.id.playNowBtn);
    }


    public void playnow(View v) {

        if (!inTempLoginState) {
            inTempLoginState = true;
            playNowBtn.setText("Join Lobby");
            description.setVisibility(View.GONE);
            usernameLayout.setVisibility(View.VISIBLE);
        } else {
            Intent i=new Intent(this,LoungeActivity.class);
            i.putExtra(PLAYER_NAME, userNameField.getText().toString());
            i.putExtra(USER_ID, "TEMP_"+new Random().nextLong());
            
            
        }


    }


    public void getapk(View v) {

    }

}

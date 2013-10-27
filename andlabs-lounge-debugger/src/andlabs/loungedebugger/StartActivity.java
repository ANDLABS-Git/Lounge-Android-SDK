package andlabs.loungedebugger;

import andlabs.lounge.lobby.ui.LoungeActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class StartActivity extends Activity {

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        
    }

   
    
    public void openLobby(View v){
     startActivity(new Intent(this, LoungeActivity.class));   
    }
}

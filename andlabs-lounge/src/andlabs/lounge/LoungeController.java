package andlabs.lounge;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;


public class LoungeController {

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Log.v("LoungeController", "getClientMessenger(): Handler.handleMessage(): msg = " + msg);
		}
	};


	public Messenger getClientMessenger() {

		return new Messenger(handler);
	}

}

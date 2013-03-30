package andlabs.lounge.service;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;


public class LoungeServiceImpl extends LoungeServiceDef.Stub {

	private Messenger mMessenger;
	private SocketIO mSocketIO;
	private GPCController gpcController;
	private IOCallback mSocketIOCallback = new IOCallback() {

		@Override
		public void onMessage(JSONObject arg0, IOAcknowledge arg1) {
			Log.d("LoungeServiceImpl", String.format("IOCallback.onMessage(): arg0 = %s", arg0));

		}


		@Override
		public void onMessage(String arg0, IOAcknowledge arg1) {
			Log.d("LoungeServiceImpl", String.format("IOCallback.onMessage(): arg0 = %s", arg0));

		}


		@Override
		public void onError(SocketIOException arg0) {
			Log.e("LoungeServiceImpl", String.format("IOCallback.onError(): caught exception while connecting"), arg0);

		}


		@Override
		public void onDisconnect() {
			Log.d("LoungeServiceImpl", String.format("IOCallback.onDisconnect():"));

		}


		@Override
		public void onConnect() {
			Log.d("LoungeServiceImpl", String.format("IOCallback.onConnect():"));
			try {
				Message message = new Message();
				message.what = 1;
				message.setData(Bundle.EMPTY);
				mMessenger.send(message);
			} catch (RemoteException e) {
				Log.e("LoungeServiceImpl", "caught exception while sending message", e);
			}
		}


		@Override
		public void on(String verb, IOAcknowledge arg1, Object... payload) {
			Log.v("LoungeServiceImpl", String.format("IOCallback.on(): arg0 = %s, arg1 = %s, arg2 = %s", verb, arg1, Arrays.toString(payload)));
			//The interpretation of the Server Response is handled in a own class
			gpcController.processServerResponse(verb, payload);
		}


	

	};



	public LoungeServiceImpl(Intent intent) {
		super();
		gpcController=new GPCController(intent);
		
	}


	@Override
	public void connect() throws RemoteException {
		Log.v("LoungeServiceImpl", "connect():");
		try {
			mSocketIO = new SocketIO("http://lounge-server.jit.su/");
			mSocketIO.connect(mSocketIOCallback);
		} catch (MalformedURLException e) {
			Log.e("LoungeService", "caught exception while creating/connecting SocketIO", e);
		}
	}


	@Override
	public void disconnect() throws RemoteException {
		Log.v("LoungeServiceImpl", "disconnect():");
		if (mSocketIO != null) {
			mSocketIO.disconnect();
		}
	}


	@Override
	public void login(String playerId) throws RemoteException {
		Log.v("LoungeServiceImpl", "login(): playerId = " + playerId);
		try {
			mSocketIO.emit("login", new JSONObject().put("playerID", playerId));
		} catch (JSONException e) {
			Log.e("LoungeService", "caught exception while sending login", e);
		}
	}


	@Override
	public void chat(String message) throws RemoteException {
		// TODO Auto-generated method stub

	}

}

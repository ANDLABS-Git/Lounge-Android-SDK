package andlabs.lounge.service;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;


public class LoungeServiceImpl extends LoungeServiceDef.Stub {

	private Messenger mMessenger;
	private SocketIO mSocketIO;
	private IOCallback mSocketIOCallback = new IOCallback() {

		@Override
		public void onMessage(JSONObject arg0, IOAcknowledge arg1) {
			Log.d("LoungeService", String.format("IOCallback.onMessage(): arg0 = %s", arg0));

		}


		@Override
		public void onMessage(String arg0, IOAcknowledge arg1) {
			Log.d("LoungeService", String.format("IOCallback.onMessage(): arg0 = %s", arg0));

		}


		@Override
		public void onError(SocketIOException arg0) {
			Log.e("LoungeService", String.format("IOCallback.onError(): caught exception while connecting"), arg0);

		}


		@Override
		public void onDisconnect() {
			Log.d("LoungeService", String.format("IOCallback.onDisconnect():"));

		}


		@Override
		public void onConnect() {
			Log.d("LoungeService", String.format("IOCallback.onConnect():"));
			try {
				mSocketIO.emit("login", new JSONObject().put("playerID", "kodra"));
			} catch (JSONException e) {
				Log.e("LoungeService", "caught exception while sending login", e);
			}
		}


		@Override
		public void on(String arg0, IOAcknowledge arg1, Object... arg2) {
			Log.d("LoungeService", String.format("IOCallback.on(): arg0 = %s, arg1 = %s, arg2 = %s", arg0, arg1, Arrays.toString(arg2)));
			
		}

	};


	public LoungeServiceImpl(Messenger pMessenger) {
		super();
		mMessenger = pMessenger;
		try {
			Message message = new Message();
			message.what = 42;
			message.setData(Bundle.EMPTY);
			mMessenger.send(message);
		} catch (RemoteException e) {
			Log.e("LoungeServiceImpl", "caught exception while sending message", e);
		}

	}


	@Override
	public void connect() throws RemoteException {
		Log.v("LoungeService", "LoungeServiceDef.Stub.connect():");
		try {
			mSocketIO = new SocketIO("http://lounge-server.jit.su/");
			mSocketIO.connect(mSocketIOCallback);
		} catch (MalformedURLException e) {
			Log.e("LoungeService", "caught exception while creating/connecting SocketIO", e);
		}
	}


	@Override
	public void disconnect() throws RemoteException {
		Log.v("LoungeService", "LoungeServiceDef.Stub.disconnect():");
		if (mSocketIO != null) {
			mSocketIO.disconnect();
		}
	}


	@Override
	public void chat(String message) throws RemoteException {
		// TODO Auto-generated method stub

	}

}

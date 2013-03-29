package andlabs.lounge.service;

import io.socket.IOCallback;
import io.socket.SocketIO;

import java.net.MalformedURLException;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;


public class LoungeServiceImpl extends LoungeServiceDef.Stub {

	private Messenger mMessenger;
	private SocketIO mSocketIO;
	private IOCallback mSocketIOCallback;


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
			mSocketIOCallback = new SocketIOCallback(mSocketIO);
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

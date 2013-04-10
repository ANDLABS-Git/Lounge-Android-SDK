package andlabs.lounge.service;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import roboguice.util.Ln;
import andlabs.lounge.model.Game;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class LoungeServiceImpl extends LoungeServiceDef.Stub {

    private Messenger mMessenger;
    private SocketIO mSocketIO;
    private boolean mRetry = false;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        

        @Override
        public void onReceive(Context pContext, Intent pIntent) {
            final boolean noConnectivity = pIntent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            final String reason = pIntent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            final boolean isFailover = pIntent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            final NetworkInfo currentNetworkInfo = (NetworkInfo) pIntent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            final NetworkInfo otherNetworkInfo = (NetworkInfo) pIntent
                    .getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if (noConnectivity) {
                mRetry = true; // if there's no connection, try to reconnect once the connection is established again
            } else {
                tryReconnect();
            }
        }
    };

    private IOCallback mSocketIOCallback = new IOCallback() {

        @Override
        public void onMessage(JSONObject arg0, IOAcknowledge arg1) {
            Ln.d("IOCallback.onMessage(): arg0 = %s", arg0);

        }

        @Override
        public void onMessage(String arg0, IOAcknowledge arg1) {
            Ln.d("IOCallback.onMessage(): arg0 = %s", arg0);

        }

        @Override
        public void onError(SocketIOException arg0) {
            Ln.e(arg0, "IOCallback.onError(): caught exception while connecting");

            // TODO: Implement retry-counter
            try {
                disconnect();
            } catch (RemoteException e) {
                Ln.e(e, "onError(): caught exception while trying to disconnect");
            }
            mRetry = true;
        }

        @Override
        public void onDisconnect() {
            Ln.d("IOCallback.onDisconnect():");
        }

        @Override
        public void onConnect() {
            Ln.d("IOCallback.onConnect():");
            
            // register broadcast receiver for network connectivity events
            
            try {
                Message message = new Message();
                message.what = 1;
                message.setData(Bundle.EMPTY);
                mMessenger.send(message);
            } catch (RemoteException e) {
                Ln.e(e, "IOCallback.onConnect(): caught exception while sending message");
            }
        }

        @Override
        public void on(String arg0, IOAcknowledge arg1, Object... arg2) {
            Ln.v("IOCallback.on(): arg0 = %s, arg1 = %s, arg2 = %s", arg0, arg1, Arrays.toString(arg2));
            mLoungeMessageProcessor.processMessage(arg0, arg2);
        }

    };

    private void tryReconnect() {
        if (mRetry) {
            try {
                connect();
            } catch (RemoteException e) {
                Ln.e(e, "onDisconnect(): caught exception while trying to reconnect");
            } finally {
                mRetry = false;
            }
        }
    }

    private LoungeMessageProcessor mLoungeMessageProcessor = new LoungeMessageProcessor() {

        @Override
        public void triggerUpdate(ConcurrentHashMap<String, Game> pInvolvedGames, ConcurrentHashMap<String, Game> pOpenGames) {
            Ln.v("LoungeMessageProcessor.triggerUpdate(): pInvolvedGames = %s, pOpenGames = %s", pInvolvedGames, pOpenGames);
            Message message = new Message();
            message.what = 7;
            Bundle bundle = new Bundle();
            bundle.putSerializable("involvedGameList", pInvolvedGames);
            bundle.putSerializable("openGameList", pOpenGames);
            message.setData(bundle);
            try {
                mMessenger.send(message);
            } catch (RemoteException e) {
                Ln.e(e, "LoungeMessageProcessor.triggerUpdate(): caught exception while sending message");
            }
        }

        @Override
        public void onGameMove(String pMatchID, Bundle pParams) {
            Ln.v("LoungeMessageProcessor.onGameMove(): pMatchID = %s, pParams = %s", pMatchID, pParams);
            Message message = new Message();
            message.what = 18;
            Bundle bundle = new Bundle();
            bundle.putString("matchID", pMatchID);
            bundle.putBundle("data", pParams);
            message.setData(bundle);
            try {
                mMessenger.send(message);
            } catch (RemoteException e) {
                Ln.e(e, "LoungeMessageProcessor.triggerUpdate(): caught exception while sending message");
            }

        }

    };

    public LoungeServiceImpl(Intent intent) {
        super();
        Ln.v("LoungeServiceImpl():");
        mMessenger = intent.getParcelableExtra("client-messenger");
        try {
            Message message = new Message();
            message.what = 42;
            message.setData(Bundle.EMPTY);
            mMessenger.send(message);
        } catch (RemoteException e) {
            Ln.e(e, "LoungeServiceImpl(): caught exception while sending message 42");
        }
    }

    @Override
    public void connect() throws RemoteException {
        Ln.v("connect():");
        try {
            mSocketIO = new SocketIO("http://lounge-server.jit.su/");
            mSocketIO.connect(mSocketIOCallback);
        } catch (MalformedURLException e) {
            Ln.e(e, "connect(): caught exception while creating/connecting SocketIO");
        }
    }

    @Override
    public void disconnect() throws RemoteException {
        Ln.v("disconnect():");
        if (mSocketIO != null) {
            mSocketIO.disconnect();
        }
    }
    @Override
    public void login(String playerId) throws RemoteException {
        Ln.v("login(): playerId = %s", playerId);
        try {
            mLoungeMessageProcessor.setMyPlayerId(playerId);
            mSocketIO.emit("login", new JSONObject().put("playerID", playerId));
        } catch (JSONException e) {
            Ln.e(e, "login(): caught exception while sending login");
        }
    }

    @Override
    public void chat(String message) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void openMatch(String pPackageId, String pDisplayName) throws RemoteException {

        Ln.v("openMatch(): pPackageId = %s, pDisplayName = %s", pPackageId, pDisplayName);
        try {
            // PAYLOAD {gameID: "packageID", gameName: ”AppName”,
            // maximumPlayers: “MaximumAllowedPlayersInGame” , gameType:
            // “move/stream”}
            JSONObject payload = new JSONObject().put("gameID", pPackageId).put("gameName", pDisplayName);
            payload.put("maximumPlayers", 2).put("gameType", "move");
            mSocketIO.emit("join", payload);
        } catch (JSONException e) {
            Ln.e(e, "openMatch(): caught exception while sending join");
        }
    }

    @Override
    public void joinMatch(String pGameId, String pMatchId) throws RemoteException {
        Ln.v("joinMatch(): pGameId = %s, pMatchId = %s", pGameId, pMatchId);
        try {
            // PAYLOAD { gameID: ”packageID”, matchID: “matchID” }
            mSocketIO.emit("join", new JSONObject().put("gameID", pGameId).put("matchID", pMatchId));
        } catch (JSONException e) {
            Ln.e(e, "joinMatch(): caught exception while sending join");
        }
    }

    @Override
    public void checkin(String pGameId, String pMatchId) throws RemoteException {
        Ln.v("checkin(): pGameId = %s, pMatchId = %s", pGameId, pMatchId);
        try {
            // PAYLOAD { gameID: ”packageID”, matchID: “matchID” }
            mSocketIO.emit("checkin", new JSONObject().put("gameID", pGameId).put("matchID", pMatchId));
        } catch (JSONException e) {
            Ln.e(e, "checkin(): caught exception while sending checkin");
        }
    }

    @Override
    public void update(String pGameId, String pMatchId, String pStatus) throws RemoteException {
        Ln.v("update(): pGameId = %s", pGameId);
        try {
            // PAYLOAD { gameID: “packageID”, matchID: “matchID”, status:
            // “open/running/close” }
            JSONObject payload = new JSONObject();
            payload.put("gameID", pGameId).put("matchID", pMatchId).put("status", pStatus);
            mSocketIO.emit("update", payload);
        } catch (JSONException e) {
            Ln.e(e, "update(): caught exception while sending update");
        }
    }

    @Override
    public void move(String pPackageId, String pMatchId, Bundle pMoveBundle) throws RemoteException {
        sendMessage("move", pPackageId, pMatchId, pMoveBundle);
    }

    @Override
    public void stream(String pPackageId, String pMatchId, Bundle pMoveBundle) throws RemoteException {
        sendMessage("stream", pPackageId, pMatchId, pMoveBundle);
    }
    
    
    private void sendMessage(String pType, String pPackageId, String pMatchId, Bundle pMoveBundle) {
        try {
            // PAYLOAD { gameID: “packageID”, matchID: “matchID”, move: {... } }
            JSONObject payload = new JSONObject().put("gameID", pPackageId).put("matchID", pMatchId);
            JSONObject bundleJson = new JSONObject();
            for (String key : pMoveBundle.keySet()) {
                bundleJson.put(key, pMoveBundle.get(key));

            }
            payload.put("move", bundleJson.toString());
            mSocketIO.emit(pType, payload);
        } catch (JSONException e) {
            Ln.e(e, pType + "(): caught exception while sending move");
        }
    }

}

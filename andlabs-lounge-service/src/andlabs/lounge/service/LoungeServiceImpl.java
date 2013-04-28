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

import andlabs.lounge.model.Game;
import andlabs.lounge.util.Ln;
import android.os.Bundle;
import android.os.Message;

public class LoungeServiceImpl extends LoungeServiceDef.Stub {
    
    protected interface MessageHandler {

        void send(Message message);

    }

    private MessageHandler mMessageHandler;

    private SocketIO mSocketIO;


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

            // TODO: Implement error handling
            disconnect();
        }

        @Override
        public void onDisconnect() {
            Ln.d("IOCallback.onDisconnect():");
        }

        @Override
        public void onConnect() {
            Ln.d("IOCallback.onConnect():");

            Message message = Message.obtain();
            message.what = 1;
            message.setData(Bundle.EMPTY);
            mMessageHandler.send(message);
        }

        @Override
        public void on(String arg0, IOAcknowledge arg1, Object... arg2) {
            Ln.v("IOCallback.on(): arg0 = %s, arg1 = %s, arg2 = %s", arg0, arg1, Arrays.toString(arg2));
            mLoungeMessageProcessor.processMessage(arg0, arg2);
        }

    };

    private LoungeMessageProcessor mLoungeMessageProcessor = new LoungeMessageProcessor() {

        @Override
        public void triggerUpdate(ConcurrentHashMap<String, Game> pInvolvedGames, ConcurrentHashMap<String, Game> pOpenGames) {
            Ln.v("LoungeMessageProcessor.triggerUpdate(): pInvolvedGames = %s, pOpenGames = %s", pInvolvedGames, pOpenGames);
            Message message = Message.obtain();
            message.what = 7;
            Bundle bundle = new Bundle();
            bundle.putSerializable("involvedGameList", pInvolvedGames);
            bundle.putSerializable("openGameList", pOpenGames);
            message.setData(bundle);
            mMessageHandler.send(message);
        }

        @Override
        public void onGameMove(String pMatchID, Bundle pParams) {
            Ln.v("LoungeMessageProcessor.onGameMove(): pMatchID = %s, pParams = %s", pMatchID, pParams);
            Message message = Message.obtain();
            message.what = 18;
            Bundle bundle = new Bundle();
            bundle.putString("matchID", pMatchID);
            bundle.putBundle("data", pParams);
            message.setData(bundle);
            mMessageHandler.send(message);
        }

    };


    protected LoungeServiceImpl() {
        super();
    };

    public void setMessageHandler(MessageHandler pMessageHandler) {
        mMessageHandler = pMessageHandler;
        Message message = Message.obtain();
        message.what = 42;
        message.setData(Bundle.EMPTY);
        mMessageHandler.send(message);
    };

    @Override
    public void connect() {
        Ln.v("connect():");
        try {
            mSocketIO = new SocketIO("http://lounge-server.jit.su/");
            mSocketIO.connect(mSocketIOCallback);
        } catch (MalformedURLException e) {
            Ln.e(e, "connect(): caught exception while creating/connecting SocketIO");
        }
    }

    @Override
    public void reconnect() {
        Ln.v("reconnect():");
        try {
            Message message = Message.obtain();
            message.what = 1;
            message.setData(Bundle.EMPTY);
            mMessageHandler.send(message);
        } catch (NullPointerException npe) {
            Ln.e(npe, "reconnect(): caught exception while sending message");
        }
    }

    @Override
    public void disconnect() {
        Ln.v("disconnect():");
        if (mSocketIO != null) {
            mSocketIO.disconnect();
        }
    }
 
    @Override
    public void login(String playerId) {
        Ln.v("login(): playerId = %s", playerId);
        try {
            mLoungeMessageProcessor.setMyPlayerId(playerId);
            mSocketIO.emit("login", new JSONObject().put("playerID", playerId));
        } catch (JSONException e) {
            Ln.e(e, "login(): caught exception while sending login");
        }
    }

    @Override
    public void chat(String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void openMatch(String pPackageId, String pDisplayName) {
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
    public void joinMatch(String pGameId, String pMatchId) {
        Ln.v("joinMatch(): pGameId = %s, pMatchId = %s", pGameId, pMatchId);
        try {
            // PAYLOAD { gameID: ”packageID”, matchID: “matchID” }
            mSocketIO.emit("join", new JSONObject().put("gameID", pGameId).put("matchID", pMatchId));
        } catch (JSONException e) {
            Ln.e(e, "joinMatch(): caught exception while sending join");
        }
    }

    @Override
    public void checkin(String pGameId, String pMatchId) {
        Ln.v("checkin(): pGameId = %s, pMatchId = %s", pGameId, pMatchId);
        try {
            // PAYLOAD { gameID: ”packageID”, matchID: “matchID” }
            mSocketIO.emit("checkin", new JSONObject().put("gameID", pGameId).put("matchID", pMatchId));
        } catch (JSONException e) {
            Ln.e(e, "checkin(): caught exception while sending checkin");
        }
    }

    @Override
    public void update(String pGameId, String pMatchId, String pStatus) {
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
    public void move(String pPackageId, String pMatchId, Bundle pMoveBundle) {
        sendMessage("move", pPackageId, pMatchId, pMoveBundle);
    }

    @Override
    public void stream(String pPackageId, String pMatchId, Bundle pMoveBundle) {
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

    @Override
    public void closeMatch(String pPackageId, String pMatchId) {
        Ln.v("closeMatch(): pGameId = %s, pMatchId = %s", pPackageId, pMatchId);
        try {
            // PAYLOAD { gameID: ”packageID”, matchID: “matchID” }
            mSocketIO.emit("update", new JSONObject().put("gameID", pPackageId).put("matchID", pMatchId).put("status", "close"));
        } catch (JSONException e) {
            Ln.e(e, "closeMatch(): caught exception while sending checkin");
        }
        
    }


    @Override
    public void checkout(String pPackageId,String pMatchId) {
        Ln.v("checkout(): pGameId = %s, pMatchId = %s", pPackageId, pMatchId);
        try {
            // TODO: There is no checkout in Server Interface spec
            // Checkin into Empty/null/"" ?
            mSocketIO.emit("checkout", new JSONObject().put("gameID", "").put("matchID", ""));
        } catch (JSONException e) {
            Ln.e(e, "checkout(): caught exception while sending checkin");
        }
    }
    
}

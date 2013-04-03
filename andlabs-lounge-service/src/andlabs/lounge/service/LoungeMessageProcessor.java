package andlabs.lounge.service;

import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import andlabs.lounge.model.Game;
import andlabs.lounge.model.Match;
import andlabs.lounge.model.Player;
import android.util.Log;


public abstract class LoungeMessageProcessor {

	private ArrayList<Player> mPlayers = new ArrayList<Player>();

	private ArrayList<Game> mGames = new ArrayList<Game>();


	public abstract void triggerUpdate(ArrayList<Game> mGames);


	public void processMessage(String pVerb, Object[] pPayload) {
		Log.v("LoungeMessageProcessor", String.format("processMessage(): processing %s message: %s", pVerb, Arrays.toString(pPayload)));
		try {
			JSONObject payload = new JSONObject(pPayload[0].toString());
			if ("login".equals(pVerb)) {
				JSONArray jsonArray = payload.getJSONArray("playerList");
				Log.v("LoungeMessageProcessor", "processMessage(): " + jsonArray);
				for (int index = 0; index < jsonArray.length(); index++) {
					JSONObject jsonObject = jsonArray.getJSONObject(index);
					Log.v("LoungeMessageProcessor", String.format("processMessage(): processing player %s", jsonObject));
					Player player = new Player();
					player._id = jsonObject.getString("_id");
					player.socketID = jsonObject.getString("socketID");
					player.playerID = jsonObject.getString("playerID");
					mPlayers.add(player);
				}
			}
			if ("joinMatch".equals(pVerb)) {
				Game game = new Game();
				mGames.add(game);
				game.gameID = payload.getString("gameID");
				game.gameName = payload.getString("gameName");
				Match match = new Match();
				match.matchID = payload.getString("matchID");
				match.totalSpots =  payload.getInt("totalSpots");
				match.status =  payload.getString("status");
				game.matches.add(match);
				JSONArray jsonArray = payload.getJSONArray("playerIDs");
				Log.v("LoungeMessageProcessor", "processMessage(): gameName = " + game.gameName + " / " + jsonArray);
				for (int index = 0; index < jsonArray.length(); index++) {
					JSONObject jsonObject = jsonArray.getJSONObject(index);
					Player player = new Player();
					player._id = jsonObject.getString("_id");
					player.playerID = jsonObject.getString("playerID");
					match.players.add(player);
				}
				triggerUpdate(mGames);
			}
		} catch (JSONException e) {
			Log.e("LoungeMessageProcessor", "caught exception while parsing payload", e);
		}
	}

}

package andlabs.lounge.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import andlabs.lounge.model.Game;
import andlabs.lounge.model.Match;
import andlabs.lounge.model.Player;
import android.os.Bundle;
import android.util.Log;

public abstract class LoungeMessageProcessor {

	private HashMap<String, Player> mPlayers = new HashMap<String, Player>();

	private Map<String, Bundle> matchMoves;

	private ArrayList<Game> mGames = new ArrayList<Game>();

	public abstract void triggerUpdate(ArrayList<Game> mGames);

	public void processMessage(String pVerb, Object[] pPayload) {
		Log.v("LoungeMessageProcessor", String.format(
				"processMessage(): processing %s message: %s", pVerb,
				Arrays.toString(pPayload)));
		try {
			JSONObject payload = new JSONObject(pPayload[0].toString());

			if ("login".equals(pVerb)) {
				JSONArray jsonArray = payload.getJSONArray("playerList");
				Log.v("LoungeMessageProcessor", "processMessage(): "
						+ jsonArray);
				for (int index = 0; index < jsonArray.length(); index++) {
					JSONObject jsonObject = jsonArray.getJSONObject(index);
					Log.v("LoungeMessageProcessor", String.format(
							"processMessage(): processing player %s",
							jsonObject));
					Player player = new Player();
					player._id = jsonObject.getString("_id");
					player.socketID = jsonObject.getString("socketID");
					player.playerID = jsonObject.getString("playerID");
					mPlayers.put(player._id, player);
				}
			}
			if ("joinMatch".equals(pVerb)) {
				Game game = new Game();
				mGames.add(game);
				game.gameID = payload.getString("gameID");
				game.gameName = payload.getString("gameName");
				game.totalSpots = payload.getInt("totalSpots");
				game.status = payload.getString("status");
				Match match = new Match();
				match.matchID = payload.getString("matchID");
				game.matches.add(match);
				JSONArray jsonArray = payload.getJSONArray("playerIDs");
				Log.v("LoungeMessageProcessor", "processMessage(): gameName = "
						+ game.gameName + " / " + jsonArray);
				for (int index = 0; index < jsonArray.length(); index++) {
					JSONObject jsonObject = jsonArray.getJSONObject(index);
					match.players.add(mPlayers.get(jsonObject
							.getString("playerID")));
				}
				triggerUpdate(mGames);
			}

			if ("update".equals(pVerb)) {
				String gameID = payload.getString("gameID");
				String status = payload.getString("status");
				String matchID = payload.getString("matchID");
			}

			if ("checkIn".equals(pVerb)) {
				// { gameID:” packageID”, matchID: “matchID”, playerID:
				// ”playerID”}
				String gameID = payload.getString("gameID");
				String playerID = payload.getString("playerID");
				Player p = mPlayers.get(playerID);

				if ("andlabs.lounge.lobby".equalsIgnoreCase(gameID)) {
					String matchID = payload.getString("matchID");
					p.checkInMatchID = matchID;
				}

				p.checkInGame = gameID;
			}

			if ("move".equals(pVerb)) {
				// { gameID: “packageID”, matchID: “matchID”, move: {... } }
				String matchID = payload.getString("matchID");

				JSONObject json = (JSONObject) payload.getJSONObject("move");
				Bundle b = new Bundle();
				matchMoves.put(matchID, b);

				for (Iterator<String> i = json.keys(); i.hasNext();) {
					String key = i.next();
					b.putString(key, json.getString(key));
					Log.i("json", "converting - key:" + key + " / Value: "
							+ json.getString(key));
				}
				matchMoves.put(matchID, b);

			}
		} catch (JSONException e) {
			Log.e("LoungeMessageProcessor",
					"caught exception while parsing payload", e);
		}
	}

}

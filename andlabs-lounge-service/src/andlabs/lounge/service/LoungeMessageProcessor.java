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


	public void processMessage(String arg0, Object[] arg2) {
		Log.v("LoungeUtil", String.format("processMessage(): processing %s message: %s", arg0, Arrays.toString(arg2)));
		try {
			JSONObject payload = new JSONObject(arg2[0].toString());
			if ("login".equals(arg0)) {
				JSONArray jsonArray = payload.getJSONArray("playerList");
				Log.v("LoungeUtil", "processMessage(): " + jsonArray);
				for (int index = 0; index < jsonArray.length(); index++) {
					JSONObject jsonObject = jsonArray.getJSONObject(index);
					Log.v("LoungeUtil", String.format("processMessage(): processing player %s", jsonObject));
					Player player = new Player();
					player._id = jsonObject.getString("_id");
					player.socketID = jsonObject.getString("socketID");
					player.playerID = jsonObject.getString("playerID");
					mPlayers.add(player);
				}
			}
			if ("joinMatch".equals(arg0)) {
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
				Log.v("LoungeLobbyController", "processMessage(): gameName = " + game.gameName + " / " + jsonArray);
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
			Log.e("LoungeUtil", "caught exception while parsing payload", e);
		}
	}

}

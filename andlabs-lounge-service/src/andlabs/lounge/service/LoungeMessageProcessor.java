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

	private Map<String, Bundle> mMatchMoves;

	// All games currently in the lounge in which the user is involved
	private HashMap<String, Game> mInvolvedGames = new HashMap<String, Game>();
	// All games currently in the lounge in which the user is not involved
	private HashMap<String, Game> mOpenGames = new HashMap<String, Game>();
	// All matches of all games, for easier manipulation
	private HashMap<String, Match> mMatches = new HashMap<String, Match>();

	private String mPlayerID;


	public void setMyPlayerId(String pPlayerId) {
		mPlayerID = pPlayerId;
	}


	public abstract void triggerUpdate(HashMap<String, Game> pInvolvedGames, HashMap<String, Game> pOpenGames);


	public abstract void onGameMove(String pMatchID, Bundle pParams);


	public void processMessage(String pVerb, Object[] pPayload) {
		Log.v("LoungeMessageProcessor",
				String.format("processMessage(): processing %s message: %s", pVerb, Arrays.toString(pPayload)));
		try {
			JSONObject payload = new JSONObject(pPayload[0].toString());

			// Create map with all players
			if ("login".equals(pVerb)) {
				JSONArray jsonArray = payload.getJSONArray("playerList");
				Log.v("LoungeMessageProcessor", "processMessage(): " + jsonArray);
				for (int index = 0; index < jsonArray.length(); index++) {
					JSONObject jsonObject = jsonArray.getJSONObject(index);
					Log.v("LoungeMessageProcessor", String.format("processMessage(): processing player %s", jsonObject));
					addPlayer(jsonObject);
				}
			}

			// Add new player to the map
			if ("addPlayer".equals(pVerb)) {
				addPlayer(payload);
			}

			// Match was created or joined by someone.
			// If the status is "created", it is a new match, else a match is updated.
			// Needs to split into games / matches in which the user is involved and others
			if ("joinMatch".equals(pVerb)) {

				// PAYLOAD { gameID: "packageID", matchID: “matchID”, gameName:”AppName”, totalSpots: ”totalSpots”,
				//           status:”join/running”, gameType: “move/stream”, playerIDs: [player] }
				// where each player is of type {_id: “uuid”, playerID: “playerID”}

				JSONArray jsonArray = payload.getJSONArray("playerIDs");
				Log.v("LoungeMessageProcessor", "processMessage(): payload = " + jsonArray);

				ArrayList<Player> players = new ArrayList<Player>();
				boolean involvedGame = false;
				for (int index = 0; index < jsonArray.length(); index++) {
					JSONObject jsonObject = jsonArray.getJSONObject(index);
					final String playerID = jsonObject.getString("playerID");
					players.add(mPlayers.get(playerID));

					if (mPlayerID.equals(playerID)) {
						involvedGame = true;
					}
				}

				// When the game is already known, update the known data, else
				// create a new game object.
				final String gameID = payload.getString("gameID"); // TODO:
																	// is it
																	// called
																	// gameID or
																	// packageID?
																	// spec says
																	// packageID.

				Game game = null;
				if (involvedGame) {
					game = mInvolvedGames.get(gameID);
				} else {
					game = mOpenGames.get(gameID);
				}
				if (game == null) {
					game = new Game();
					game.gameID = gameID;
					game.gameName = payload.getString("gameName");
				}

				final String matchID = payload.getString("matchID");
				Match match = game.matches.get(matchID);
				if (match == null) {
					match = new Match();
					match.matchID = matchID;
					game.matches.put(matchID, match);
				}
				match.players=new ArrayList<Player>();
				JSONArray playerArray = payload.getJSONArray("playerIDs");
				for (int index = 0; index < jsonArray.length(); index++) {
					JSONObject jsonObject = jsonArray.getJSONObject(index);
					
					String player = jsonObject.getString("_id");
					match.players.add(mPlayers.get(player));
				}
				
				match.totalSpots = payload.getInt("totalSpots");
				match.status = payload.getString("status");

				final boolean closedFlag = "closed".equals(match.status);
				if (!closedFlag) {
					if (involvedGame) { // the user is involved
						mInvolvedGames.put(game.gameID, game);
					} else {
						mOpenGames.put(game.gameID, game);
					}
				} // TODO else, remove it if existing

				triggerUpdate(mInvolvedGames, mOpenGames);
			}

			// Set checkedInGame and checkedInMatch on the player's object
			if ("checkIn".equals(pVerb)) {
				// PAYLOAD { gameID:"packageID", matchID: "matchID", playerID: "playerID"}
				final String gameID = payload.getString("gameID");
				final String playerID = payload.getString("playerID");
				Player player = mPlayers.get(playerID);

				player.gameID = gameID;
				player.matchID = payload.has("matchID") ? payload.getString("matchID") : gameID;

				triggerUpdate(mInvolvedGames, mOpenGames);
			}

			// Update match and game state to updatesAvailable (or similar)
			if ("moveMatch".equals(pVerb)) {
				processGameMessage(payload, false);
			}

			if ("stream".equals(pVerb)) {
				processGameMessage(payload, true);
			}

			// Process the last move data and update match and game state to
			// updatesAvailable (or similar)
			if ("lastMove".equals(pVerb)) {
				// TODO
			}
		} catch (JSONException e) {
			Log.e("LoungeMessageProcessor", "caught exception while parsing payload", e);
		}
	}


	private void processGameMessage(JSONObject pPayload, boolean pStream) throws JSONException {
		final String matchID = pPayload.getString("matchID");

		JSONObject json = (JSONObject) pPayload.getJSONObject("move");
		Bundle b = new Bundle();

		for (Iterator<String> i = json.keys(); i.hasNext();) {
			String key = i.next();
			b.putString(key, json.getString(key));
			Log.i("json", "converting - key:" + key + " / Value: " + json.getString(key));
		}
		if (!pStream) {
			mMatchMoves.put(matchID, b);
		}

		if (!mPlayerID.equals(pPayload.getString("playerID"))) {// We react only
																// to moves not
																// send by the
																// user
			// TODO: set to false when the move was received by the game
			mMatches.get(matchID).playerOnTurn = mPlayerID;

			triggerUpdate(mInvolvedGames, mOpenGames);
		}

		// TODO: forward moves to app
	}


	private Player addPlayer(JSONObject jsonObject) throws JSONException {
		Player player = new Player();
		player._id = jsonObject.getString("_id");
		player.socketID = jsonObject.getString("socketID");
		player.playerID = jsonObject.getString("playerID");
		if (jsonObject.has("gameID")) {
			player.gameID = jsonObject.getString("gameID");
		}
		if (jsonObject.has("matchID")) {
			player.matchID = jsonObject.getString("matchID");
		}
		mPlayers.put(player._id, player);
		return player;
	}

}

/*
 * Copyright (C) 2012, 2013 ANDLABS GmbH. All rights reserved.
 *
 * www.lounge.andlabs.com
 * lounge@andlabs.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package andlabs.lounge.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import andlabs.lounge.model.Game;
import andlabs.lounge.model.Match;
import andlabs.lounge.model.Player;
import andlabs.lounge.util.Ln;
import android.os.Bundle;

public abstract class LoungeMessageProcessor {

    private HashMap<String, Player> mPlayers = new HashMap<String, Player>();

    private Map<String, Bundle> mMatchMoves = new HashMap<String, Bundle>();;

    // All games currently in the lounge in which the user is involved
    private ConcurrentHashMap<String, Game> mInvolvedGames = new ConcurrentHashMap<String, Game>();
    // All games currently in the lounge in which the user is not involved
    private ConcurrentHashMap<String, Game> mOpenGames = new ConcurrentHashMap<String, Game>();
    // All matches of all games, for easier manipulation
    private HashMap<String, Match> mMatches = new HashMap<String, Match>();

    private String mPlayerID;
    private String mPlayerName;


    // TODO refactor global: uuid -> playerId and playerId -> playerName
    public void setMyPlayer(String playerId, String playerName) {
        mPlayerID = playerId;
        mPlayerName = playerName;
    }


    public void requestUpdate() {
        triggerUpdate(mInvolvedGames, mOpenGames);
    }


    public abstract void triggerUpdate(ConcurrentHashMap<String, Game> pInvolvedGames, ConcurrentHashMap<String, Game> pOpenGames);


    public abstract void onGameMove(String pMatchID, Bundle pParams);


    public void processMessage(String pVerb, Object[] pPayload) {
        Ln.v("processMessage(): processing %s message: %s", pVerb, Arrays.toString(pPayload));
        try {
            JSONObject payload = new JSONObject(pPayload[0].toString());

            // Create map with all players
            if ("login".equals(pVerb)) {
                JSONArray jsonArray = payload.getJSONArray("playerList");
                Ln.v("processMessage(): %s", jsonArray);
                for (int index = 0; index < jsonArray.length(); index++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    Ln.v("processMessage(): processing player %s", jsonObject);
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
                // status:”join/running”, gameType: “move/stream”, playerIDs: [player] }
                // where each player is of type {_id: “uuid”, playerID: “playerID”}

                JSONArray jsonArray = payload.getJSONArray("playerIDs");
                Ln.v("processMessage(): payload = %s", jsonArray);

                ArrayList<Player> players = new ArrayList<Player>();
                boolean involvedInMatchFlag = false;
                for (int index = 0; index < jsonArray.length(); index++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    final String _id = jsonObject.getString("_id");
                    Player player = mPlayers.get(_id);
                    if (player == null) {
                        player = new Player();
                        player._id = _id;
                        mPlayers.put(_id, player);
                    }
                    String playerID = jsonObject.getString("playerID");
                    if (playerID.equals(mPlayerID)) {
                        involvedInMatchFlag = true;
                        // assuming the first player is the one who hosts the match
                        player.isLocal = index == 0;
                    }
                    player.playerName = involvedInMatchFlag ? "You" : playerID;
                    players.add(player);
                }

                // When the game is already known, update the known data, else
                // create a new game object.
                final String gameID = payload.getString("gameID");
                Game involvedGame = mInvolvedGames.get(gameID);
                Game openGame = mOpenGames.get(gameID);
                final String matchID = payload.getString("matchID");
                Match match = mMatches.get(matchID);
                if (match == null) {
                    match = new Match();
                    match.matchID = matchID;
                    mMatches.put(matchID, match);
                }
                match.players = players;
                match.totalSpots = payload.getInt("totalSpots");
                match.status = payload.getString("status");

                if ("closed".equals(match.status)) {

                    if (openGame != null) {
                        openGame.matches.remove(matchID);
                        if (openGame.matches.isEmpty()) {
                            mOpenGames.remove(gameID);
                        }
                    }
                    if (involvedGame != null) {
                        involvedGame.matches.remove(matchID);
                        if (involvedGame.matches.isEmpty()) {
                            mInvolvedGames.remove(gameID);
                        }
                    }

                } else if (involvedInMatchFlag) {

                    if (openGame != null) {
                        openGame.matches.remove(matchID);
                        if (openGame.matches.isEmpty()) {
                            mOpenGames.remove(gameID);
                        }
                    }
                    if (involvedGame == null) {
                        involvedGame = new Game();
                        involvedGame.gameID = gameID;
                        involvedGame.gameName = payload.getString("gameName");
                        mInvolvedGames.put(gameID, involvedGame);
                    }
                    involvedGame.matches.put(matchID, match);

                } else {

                    if (match.totalSpots > match.players.size()) {
                        if (openGame == null) {
                            openGame = new Game();
                            openGame.gameID = gameID;
                            openGame.gameName = payload.getString("gameName");
                            mOpenGames.put(gameID, openGame);
                        }
                        openGame.matches.put(matchID, match);
                    } else {
                        if (openGame != null && openGame.matches.isEmpty()) {
                            mOpenGames.remove(gameID);
                        }
                    }

                }

                triggerUpdate(mInvolvedGames, mOpenGames);
            }

            // Set checkedInGame and checkedInMatch on the player's object
            if ("checkIn".equals(pVerb)) {
                processCheckIn(payload);
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
            Ln.e(e, "caught exception while parsing payload");
        }
    }


    // PAYLOAD { gameID:"gameID", matchID: "matchID", playerID: "playerID" }
    private void processCheckIn(JSONObject pPayload) throws JSONException {
        final String gameID = pPayload.getString("gameID");
        final String playerID = pPayload.getString("playerID");
        Player player = mPlayers.get(playerID);

        player.gameID = gameID;
        player.matchID = pPayload.has("matchID") ? pPayload.getString("matchID") : gameID;

        triggerUpdate(mInvolvedGames, mOpenGames);
   }


    private void processGameMessage(JSONObject pPayload, boolean pStream) throws JSONException {
        final String matchID = pPayload.getString("matchID");

        JSONObject json = new JSONObject(pPayload.getString("move"));
        Bundle bundle = new Bundle();

        for (Iterator<?> i = json.keys(); i.hasNext();) {
            String key = (String) i.next();
            bundle.putString(key, json.getString(key));
            Ln.i("processGameMessage(): converting - key: %s / Value: %s", key, json.getString(key));
        }
        if (!pStream) {
            mMatchMoves.put(matchID, bundle);
        }

        if (!pPayload.has("playerID") || !mPlayerID.equals(pPayload.getString("playerID"))) {// We react only
                                                                // to moves not
                                                                // send by the
                                                                // user
            // TODO: set to false when the move was received by the game
            mMatches.get(matchID).playerOnTurn = mPlayerID;

            triggerUpdate(mInvolvedGames, mOpenGames);
        }

        onGameMove(matchID, bundle);
    }


    private Player addPlayer(JSONObject jsonObject) throws JSONException {
        boolean involvedInMatchFlag = false;
        String _id = jsonObject.getString("_id");
        Player player = mPlayers.get(_id);
        if (player == null) {
            player = new Player();
            player._id = _id;
            String playerID = jsonObject.getString("playerID");
            if (playerID.equals(mPlayerID)) {
                involvedInMatchFlag = true;
            }
            player.playerName = involvedInMatchFlag ? "You" : playerID;
            mPlayers.put(player._id, player);
        }
        if (jsonObject.has("gameID")) {
            player.gameID = jsonObject.getString("gameID");
        }
        if (jsonObject.has("matchID")) {
            player.matchID = jsonObject.getString("matchID");
        }
        return player;
    }

}

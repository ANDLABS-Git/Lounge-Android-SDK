/*
 *  Copyright (C) 2012,2013 ANDLABS. All rights reserved. 
 *  Lounge@andlabs.com
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
package andlabs.lounge.lobby.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import andlabs.lounge.model.Game;
import andlabs.lounge.model.Match;
import andlabs.lounge.model.Player;

/**
 * Class for generating static test data for the Lounge UI
 */
public class TestData {

    /**
     * Retreive a list of games the player is involved in
     * 
     * @return
     */
    public static List<Game> getJoinedGames() {

        final List<Game> list = new ArrayList<Game>();

        // Three players
        final Player playerA = new Player();
        playerA.playerID = "Tommo";

        final Player playerB = new Player();
        playerB.playerID = "Ninja";

        final Player playerC = new Player();
        playerC.playerID = "Nico";

        // Mol3cool - awesome game with a describing packagename
        final Game game1 = createGame("de.andlabs.gravitywins", "Mol3cool");
        // A match with one open spot, waiting for another player to join
        game1.matches.put("111", createMatch(2, playerA));
        // A match that's ready to be played
        game1.matches.put("222", createMatch(2, playerB, playerA));
        // A match with one open spot, waiting for another player to join
        game1.matches.put("333", createMatch(2, playerA));

        // Add the game with the three matches to the involved games list
        list.add(game1);

        // Another game
        final Game game2 = createGame("com.andlabs.gi", "Gravity Losses");
        // Add one match with one open spot
        game2.matches.put("444", createMatch(2, playerA));

        // Add the game with the one match to the involved games list
        list.add(game2);

        return list;
    }

    /**
     * Retreive a list of open games
     * 
     * @return
     */
    public static List<Game> getOpenGames() {

        final List<Game> list = new ArrayList<Game>();

        // One player, the good old ninja
        final Player playerB = new Player();
        playerB.playerID = "Ninja";

        // Create a new open game
        final Game openGame = createOpenGame("de.andlabs.gravitywins", "Mol3Cool", playerB);
        list.add(openGame);

        return list;

    }

    private static boolean sFirst = true;
    /**
     * Create a new match
     * 
     * @param pMaxPlayers
     * @param pPlayer
     *            the players. The number of players passed here must never
     *            exceed pMaxPlayers
     * @return
     */
    private static Match createMatch(final int pMaxPlayers, final Player... pPlayer) {

        final Match match = new Match();
        match.totalSpots = pMaxPlayers;

        if(sFirst) {
            match.playerOnTurn = "test";
            sFirst = false;
        }

        for (Player player : pPlayer) {
            match.players.add(player);
        }
        return match;
    }

    /**
     * create a new game
     * 
     * @param pPackageId
     * @param pTitle
     * @return
     */
    private static Game createGame(final String pPackageId, final String pTitle) {

        final Game game = new Game();

        game.gameID = pPackageId;
        game.gameName = pTitle;

        return game;
    }

    /**
     * Create a new game that is added to the openGame-List
     * 
     * @param pPackageId
     * @param pTitle
     * @param pPlayer
     * @return
     */
    private static Game createOpenGame(final String pPackageId, final String pTitle, final Player pPlayer) {

        final Game openGame = createGame(pPackageId, pTitle);

        final Match match = new Match();
        // matchID is needed to put the match into the game's match map
        match.matchID = getRandomNumberString();
        // TODO: Create a list view that's able to actually handle four spots
        match.totalSpots = 4;
        // Add only one player, meaning three spots are still open
        match.players.add(pPlayer);
        // Add the match to the open game
        
        openGame.matches.put(match.matchID, match);

        return openGame;

    }

    /**
     * Generates a pseudo-random 10-digit number string
     * 
     * @return
     */
    private static String getRandomNumberString() {
        final Random random = new Random();
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            stringBuilder.append(random.nextInt(9));
        }
        return stringBuilder.toString();
    }

}

package andlabs.lounge.lobby.mock;

import java.util.ArrayList;
import java.util.List;

import andlabs.lounge.model.Game;
import andlabs.lounge.model.Match;
import andlabs.lounge.model.Player;


public class TestData {

    public static List<Game> getJoinedGames() {

        List<Game> list = new ArrayList<Game>();

        Player playerA = new Player();
        playerA.playerID = "Tommo";

        Player playerB = new Player();
        playerB.playerID = "Ninja";

        Player playerC = new Player();
        playerC.playerID = "Nico";

        Game game1 = createGame("de.andlabs.gravitywins", "Mol3cool");
        game1.matches.put("111", createMatch(2, playerA));
        game1.matches.put("222", createMatch(2, playerB, playerA));
        game1.matches.put("333", createMatch(2, playerA));

        list.add(game1);

        Game game2 = createGame("com.andlabs.gi", "Gravity Losses");
        game2.matches.put("444", createMatch(2, playerA));

        list.add(game2);

        return list;
    }


    public static List<Game> getOpenGames() {

        List<Game> list = new ArrayList<Game>();

        Player playerB = new Player();
        playerB.playerID = "Ninja";

        Game openGame = createOpenGame("de.andlabs.gravitywins", "Mol3Cool", playerB);
        list.add(openGame);

        return list;

    }


    private static Match createMatch(int pMaxPlayers, Player... pPlayer) {

        Match match = new Match();
        match.totalSpots = pMaxPlayers;
        for (Player player : pPlayer) {
            match.players.add(player);
        }
        return match;
    }


    private static Game createGame(String pPackageId, String pTitle) {

        Game game = new Game();

        game.gameID = pPackageId;
        game.gameName = pTitle;

        return game;
    }


    private static Game createOpenGame(String pPackageId, String pTitle, Player pPlayer) {

        Game openGame = createGame(pPackageId, pTitle);

        Match match = new Match();
        match.matchID = getRandomNumberString();
        match.totalSpots = 4;

        match.players.add(pPlayer);

        openGame.matches.put(match.matchID, match);

        return openGame;

    }


    private static String getRandomNumberString() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            stringBuilder.append(i);
        }
        return stringBuilder.toString();
    }

}

package andlabs.lounge.lobby.mock;

import java.util.ArrayList;
import java.util.List;

import andlabs.lounge.lobby.model.GameMatch;
import andlabs.lounge.lobby.model.LobbyListElement;
import andlabs.lounge.lobby.model.Player;
import andlabs.lounge.lobby.model.LobbyListElement.ElementType;


public class TestData {

    public static List<LobbyListElement> getMockData() {

        List<LobbyListElement> list = new ArrayList<LobbyListElement>();
 
        Player playerA = new Player();
        playerA.setDisplayName("Tommo");

        Player playerB = new Player();
        playerB.setDisplayName("Ninja");

        Player playerC = new Player();
        playerC.setDisplayName("Nico");

        LobbyListElement game1 = createRunningGame("de.andlabs.gravitywins", "Mol3cool");
        game1.getGameMatches().add(createGameMatch(2, playerA));
        game1.getGameMatches().add(createGameMatch(2, playerB, playerA));
        game1.getGameMatches().add(createGameMatch(2, playerA));

        list.add(game1);

        LobbyListElement game2 = createRunningGame("com.andlabs.gi", "Gravity Losses");
        game2.getGameMatches().add(createGameMatch(2, playerA));

        list.add(game2);

        list.add(createSeparator());

        LobbyListElement openGame = createOpenGame("de.andlabs.gravitywins", "Mol3Cool", playerB);
		list.add(openGame);

        return list;

    }


	private static GameMatch createGameMatch(int pMaxPlayers, Player ... pPlayer) {

		GameMatch gameMatch = new GameMatch();
		gameMatch.setMaxPlayers(pMaxPlayers);
		for (Player player : pPlayer) {
			gameMatch.getPlayers().add(player);
		}
		return gameMatch;
	}


	private static LobbyListElement createRunningGame(String pPackageId, String pTitle) {

		LobbyListElement runningGame = new LobbyListElement();

		runningGame.setPgkName(pPackageId);
        runningGame.setTitle(pTitle);
        runningGame.setType(ElementType.JOINED_GAME);

        return runningGame;
	}


	private static LobbyListElement createSeparator() {

		LobbyListElement seperator = new LobbyListElement();
		seperator.setType(ElementType.SEPERATOR);

		return seperator;

	}


	private static LobbyListElement createOpenGame(String pPackageId, String pTitle, Player pPlayer) {

		LobbyListElement openGame = new LobbyListElement();

		openGame.setPgkName(pPackageId);
        openGame.setTitle(pTitle);
        openGame.setType(ElementType.OPEN_GAME);

        GameMatch match = new GameMatch();
        match.setMaxPlayers(4);

        match.getPlayers().add(pPlayer);

        openGame.getGameMatches().add(match);

        return openGame;

	}

}

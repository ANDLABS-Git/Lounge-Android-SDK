package andlabs.lounge.lobby;

import java.util.ArrayList;

import andlabs.lounge.model.Game;


public interface LoungeServiceCallback {

	public void theAnswerIs42();

	public void onConnect();

	public void onDisconnect();

	public void onOpenGamesUpdate(ArrayList<Game> pGames);

	public void onRunningGamesUpdate(ArrayList<Game> pGames);

	public void onError(String message);

}

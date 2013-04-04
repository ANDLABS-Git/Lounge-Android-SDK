package andlabs.lounge;

import java.util.Map;

import andlabs.lounge.model.Game;


public interface LoungeServiceCallback {

	public void theAnswerIs42();
	
	public void onStart();

	public void onConnect();

	public void onDisconnect();

	public void onOpenGamesUpdate(Map<String, Game> pGames);

	public void onRunningGamesUpdate(Map<String, Game> pGames);

	public void onError(String message);

}

package andlabs.lounge.lobby;

import java.util.ArrayList;
import java.util.List;

import andlabs.lounge.lobby.model.GameMatch;
import andlabs.lounge.lobby.model.LobbyListElement;
import andlabs.lounge.lobby.model.LobbyListElement.ElementType;
import andlabs.lounge.model.Game;
import andlabs.lounge.model.Match;
import andlabs.lounge.model.Player;
import android.content.Context;
import android.util.Log;


public class LoungeLobbyController {

	private LoungeLobbyCallback mLoungeLobbyCallback;
	private List<LobbyListElement> mLoungeLobbyList = new ArrayList<LobbyListElement>();
	private LoungeServiceController mLoungeServiceController = new LoungeServiceController();
	private LoungeServiceCallback mLoungeServiceCallback = new LoungeServiceCallback() {

		@Override
		public void theAnswerIs42() {
			Log.v("LoungeLobbyController", "LoungeServiceCallback.theAnswerIs42(): Universal Answer ;-)");
		}

		@Override
		public void onConnect() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDisconnect() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onOpenGamesUpdate(ArrayList<Game> pGames) {
			Log.v("LoungeLobbyController", "LoungeServiceCallback.onOpenGamesUpdate(): " + pGames);
			mOpenGames = pGames;
			createLobbyDataUpdate();
		}

		@Override
		public void onRunningGamesUpdate(ArrayList<Game> pGames) {
			Log.v("LoungeLobbyController", "LoungeServiceCallback.onRunningGamesUpdate(): " + pGames);
			mRunningGames = pGames;
			createLobbyDataUpdate();
		}

		@Override
		public void onError(String message) {
			Log.e("LoungeLobbyController", "LoungeServiceCallback.onError(): " + message);
		}

		private List<Game> mOpenGames, mRunningGames;

		private void createLobbyDataUpdate() {
			ArrayList<LobbyListElement> lobbyData = new ArrayList<LobbyListElement>();
			if (mRunningGames != null)
				for (Game game : mRunningGames) {
					LobbyListElement dataElement = new LobbyListElement();

					dataElement.setPgkName(game.gameID);
					dataElement.setTitle(game.gameName);
					dataElement.setType(ElementType.JOINED_GAME);

					for (Match match : game.matches) {
						GameMatch gameMatch = new GameMatch();
				        gameMatch.setMaxPlayers(2);

				        for (Player player : match.players) {
				        	andlabs.lounge.lobby.model.Player lobbyPlayer = new andlabs.lounge.lobby.model.Player();
				        	lobbyPlayer.setGuid(player._id);
				        	lobbyPlayer.setDisplayName(player.playerID);
							gameMatch.getPlayers().add(lobbyPlayer);
				        }

				        dataElement.getGameMatches().add(gameMatch);
					}
					lobbyData.add(dataElement);
				}
			if (mRunningGames != null && mOpenGames != null) {
				LobbyListElement seperator = new LobbyListElement();
				seperator.setType(ElementType.SEPERATOR);
				lobbyData.add(seperator);
			}
			if (mOpenGames != null)
				for (Game game : mOpenGames) {
					LobbyListElement dataElement = new LobbyListElement();

					dataElement.setPgkName(game.gameID);
					dataElement.setTitle(game.gameName);
					dataElement.setType(ElementType.OPEN_GAME);

					for (Match match : game.matches) {
						GameMatch gameMatch = new GameMatch();
				        gameMatch.setMaxPlayers(2);

				        for (Player player : match.players) {
				        	andlabs.lounge.lobby.model.Player lobbyPlayer = new andlabs.lounge.lobby.model.Player();
				        	lobbyPlayer.setGuid(player._id);
				        	lobbyPlayer.setDisplayName(player.playerID);
							gameMatch.getPlayers().add(lobbyPlayer);
				        }

				        dataElement.getGameMatches().add(gameMatch);
					}
					lobbyData.add(dataElement);
				}
			mLoungeLobbyCallback.onLobbyDataUpdated(lobbyData);	
		}

	};


	public void bindServiceTo(Context pContext) {
		Log.v("LoungeLobbyController", "bindServiceTo()");
		mLoungeServiceController.bindServiceTo(pContext);
		mLoungeServiceController.registerCallback(mLoungeServiceCallback);
	}


	public void registerCallback(LoungeLobbyCallback pLoungeLobbyCallback) {
		Log.v("LoungeLobbyController", "registerCallback()");
		mLoungeLobbyCallback = pLoungeLobbyCallback;
	}


	public void unregisterCallback(LoungeLobbyCallback pLoungeLobbyCallback) {
		Log.v("LoungeLobbyController", "unregisterCallback()");
		mLoungeLobbyCallback = null;
	}


	public void unbindServiceFrom(Context pContext) {
		Log.v("LoungeLobbyController", "unbindServiceFrom()");
		mLoungeServiceController.unregisterCallback(mLoungeServiceCallback);
		mLoungeServiceController.unbindServiceFrom(pContext);
	}


	public void openMatch(String pPackageId, String pDisplayName) {
		Log.v("LoungeLobbyController", "hostGame()");
		mLoungeServiceController.openMatch(pPackageId, pDisplayName);
	}


	public void joinMatch(String pPackageId, String pMatchId) {
		Log.v("LoungeLobbyController", "joinGame()");
	}

}

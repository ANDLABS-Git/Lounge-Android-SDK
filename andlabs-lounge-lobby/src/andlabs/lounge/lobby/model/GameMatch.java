package andlabs.lounge.lobby.model;

import java.util.ArrayList;
import java.util.List;


public class GameMatch {

	private String matchId;
	private List<Player> players;
	private int maxPlayers;
	private String playerOnTurn;
	private boolean running;
	private boolean isInvolved;


	public GameMatch() {
		this.players = new ArrayList<Player>();
	}


	public String getMatchId() {
		return matchId;
	}


	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}


	public List<Player> getPlayers() {
		return players;
	}


	public void setPlayers(List<Player> players) {
		this.players = players;
	}


	public int getMaxPlayers() {
		return maxPlayers;
	}


	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}



	public String getPlayerOnTurn() {
		return playerOnTurn;
	}


	public void setPlayerOnTurn(String playerOnTurn) {
		this.playerOnTurn = playerOnTurn;
	}



	public boolean isRunning() {
		return running;
	}


	public void setRunning(boolean running) {
		this.running = running;
	}


	public boolean isInvolved() {
		return isInvolved;
	}


	public void setInvolved(boolean isInvolved) {
		this.isInvolved = isInvolved;
	}

}

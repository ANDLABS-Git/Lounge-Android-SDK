package andlabs.lounge.model;

import java.io.Serializable;
import java.util.ArrayList;


public class Match implements Serializable {

	public String matchID;
	//	   State state;
	//     int   maxPlayers
	
	//     String PlayerOnTurn
	public ArrayList<Player> players = new ArrayList<Player>();

	@Override
	public synchronized String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{matchID: '").append(matchID).append("', players: [");
		if (players.size() > 0) for (Player player : players) {
			sb.append(player.toString()).append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append("]}");
		return sb.toString();
	}

}

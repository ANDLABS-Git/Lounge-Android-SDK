package andlabs.lounge.model;

import java.io.Serializable;
import java.util.HashMap;

public class Game implements Serializable {

	public String gameID;
	public String gameName;

	// Not in game but in match public int totalSpots;
	// Not in game but in match public String status;
	public HashMap<String, Match> matches = new HashMap<String, Match>();


	// TODO: Add game name
	@Override
	public synchronized String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{gameName: '").append(gameName).append("', matches: [");
		if (matches.size() > 0) {
			for (Match match : matches.values()) {
				sb.append(match.toString()).append(", ");
			}
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append("]}");
		return sb.toString();
	}

}

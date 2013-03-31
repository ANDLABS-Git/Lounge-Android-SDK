package andlabs.lounge.model;

import java.io.Serializable;
import java.util.ArrayList;


public class Game implements Serializable {

	public String gameID;
	public String gameName;
	public int totalSpots;
	public String status;
	public ArrayList<Match> matches = new ArrayList<Match>();

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{gameName: '").append(gameName).append("', matches: [");
		if (matches.size() > 0) for (Match match : matches) {
			sb.append(match.toString()).append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append("]}");
		return sb.toString();
	}

}

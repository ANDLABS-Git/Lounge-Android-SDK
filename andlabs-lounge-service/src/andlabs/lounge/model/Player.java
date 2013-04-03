package andlabs.lounge.model;

import java.io.Serializable;


public class Player implements Serializable {

	public String _id;
	public String socketID;
	public String playerID;

	@Override
	public synchronized String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{playerID: '").append(playerID).append("}");
		return sb.toString();
	}

}

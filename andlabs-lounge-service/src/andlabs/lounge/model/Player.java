package andlabs.lounge.model;

import java.io.Serializable;


public class Player implements Serializable {
    
    //TODO: Comment on what's the difference between the three ids?
	public String _id;
	public String socketID;
	public String playerID;
	
	//New stuff
	public String name;
	public String checkedInGame;
	public String checkedInMatch;

	//TODO: Add new stuff
	@Override
	public synchronized String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{playerID: '").append(playerID).append("}");
		return sb.toString();
	}

}

package andlabs.lounge.model;

import java.io.Serializable;

public class Player implements Serializable {

    // unique player ID
    public String _id;
    // Probably not needed for us
    public String socketID;
    // The player's name
    public String playerID;
    // The game, in which the player is currently checked in
    public String gameID;
    // The match, in which the player is currently checked in
    public String matchID;

    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{playerID: '").append(playerID).append("}");
        return sb.toString();
    }

}

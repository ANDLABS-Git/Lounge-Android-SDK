package andlabs.lounge.model;

import java.io.Serializable;

public class Player implements Serializable {

    // TODO: Comment on what's the difference between the three ids?
    public String _id;
    public String socketID;

    // New stuff
    public String name;
    // The game, in which the player is currently checked in
    public String gameID;
    // The match, in which the player is currently checked in
    public String matchID;

    // TODO: Add new stuff
    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{playerID: '").append(name).append("}");
        return sb.toString();
    }

}

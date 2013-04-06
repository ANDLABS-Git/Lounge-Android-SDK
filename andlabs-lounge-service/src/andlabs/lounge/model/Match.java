package andlabs.lounge.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Match implements Serializable {

    public String matchID;
    public ArrayList<Player> players = new ArrayList<Player>();

    // New stuff
    public String playerOnTurn;
    public int totalSpots;
    public String status;


    @Override
    public String toString() {
        return "Match [matchID=" + matchID + ", players=" + players + ", playerOnTurn=" + playerOnTurn + ", totalSpots="
                + totalSpots + ", status=" + status + "]";
    }
}

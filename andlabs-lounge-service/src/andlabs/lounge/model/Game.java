package andlabs.lounge.model;

import java.io.Serializable;
import java.util.HashMap;

public class Game implements Serializable {

    public String gameID;
    public String gameName;

    // Not in game but in match public int totalSpots;
    // Not in game but in match public String status;
    public HashMap<String, Match> matches = new HashMap<String, Match>();


    @Override
    public String toString() {
        return "Game [gameID=" + gameID + ", gameName=" + gameName + ", matches=" + matches + "]";
    }

}

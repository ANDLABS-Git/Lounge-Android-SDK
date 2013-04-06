package andlabs.lounge.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Game implements Serializable {

    public String gameID;
    public String gameName;

    // Not in game but in match public int totalSpots;
    // Not in game but in match public String status;
    public ConcurrentHashMap<String, Match> matches = new ConcurrentHashMap<String, Match>();


    @Override
    public String toString() {
        return "Game [gameID=" + gameID + ", gameName=" + gameName + ", matches=" + matches + "]";
    }

}

package andlabs.lounge.service;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

public class LoungeAPI {

	  
    /**
     * tells the other players that this player is ready to play
     * @param game  a Context that implements {@link eu.andlabs.studiolounge.Lounge$Multiplayable Multiplayable}
     * @param matchId  the Id of the Lounge match to play
     */
	    public static void checkIn(final Multiplayable game, final String matchId) {
	    	 
	     }
	        
	
	
    /**
     * tells the other players that this player paused playing
     * @param game  a Context that implements {@link eu.andlabs.studiolounge.Lounge$Multiplayable Multiplayable}
     */
    public static void checkOut(Multiplayable game) {
    	
    }

    /**
     * HOST a new 'match' of a game for others to join
     * @param game the global unique pkgId of the game
     * @param gameName 
     */
    public static void host(String pkg, String gameName) {
    }

    /**
     * JOIN a match of a game to subscribe game events
     * @param match  the global unique Id of the match
     * @param pkgName 
     */
    public static void join(String match, String pkgName) {
    }

    /**
     * CHAT in the public Lounge Lobby
     * @param msg  something to say
     */
    public static void chat(String msg) {
    }
    
    /**
     * send custom game message
     * @param msg the data to send
     */
    public static void sendGameMessage(Bundle msg) {
    }
	
}

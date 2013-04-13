package andlabs.lounge;

import android.os.Bundle;

public interface Multiplayable {

    /**
     * is called every time one of the other players is ready to play
     */
    public void onCheckIn(String player);


    /**
     * is called when all other players are ready to play
     */
    public void onAllPlayerCheckedIn();


    /**
     * is called when a custom game message is received
     */
    public void onGameMessage(Bundle msg);


    /**
     * is called when another player pauses playing
     */
    public void onCheckOut(String player);

}

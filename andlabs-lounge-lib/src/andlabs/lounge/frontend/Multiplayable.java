package andlabs.lounge.frontend;
import android.os.Bundle;

  /**
     * MULTI PLAY ABLE  ---   the ability to play multiple..
     * Implement this interface to enable multiplayer capabilities.
     */
    public interface Multiplayable {

        /**
         * is called every time one of the other players is ready to play
         * @param player
         */
        public void onCheckIn(String player);
        
        /**
         * is called when all other players are ready to play
         */
        public void onAllPlayerCheckedIn();
        
        /**
         * is called when a custom game message is received
         * @param the content of the custom game message
         */
        public void onGameMessage(Bundle msg);
        
        /**
         * is called when another player pauses playing
         * @param player
         */
        public void onCheckOut(String player);
    }
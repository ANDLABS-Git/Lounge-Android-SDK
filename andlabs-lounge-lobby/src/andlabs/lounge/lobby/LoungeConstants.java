package andlabs.lounge.lobby;

public interface LoungeConstants {

    /**
     * Indicates whether this game was hosted by this device
     */
    public static final String EXTRA_IS_HOST = "isHost";
    /**
     * The name of the hosting player
     */
    public static final String EXTRA_HOST_NAME = "hostName";
    /**
     * The ID of this match, needed for sending messages
     */
    public static final String EXTRA_MATCH_ID = "matchID";
    /**
     * All the players playing in this match
     */
    public static final String EXTRA_PLAYER_NAMES = "playerNames";
}

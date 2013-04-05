package andlabs.lounge.lobby;

import java.util.ArrayList;
import java.util.List;

import andlabs.lounge.lobby.model.ChatMessage;
import andlabs.lounge.model.Game;

public interface LoungeLobbyCallback {


    public void onChatDataUpdated(List<ChatMessage> data);


    /**
     * Sends all cached Chatmessages to the Listner. This happens either when the user logs in or the Activity need to be
     * reinstatiated after being closed.
     * 
     * @param chatLog
     */
    public void onNewChatLog(List<ChatMessage> chatLog);


    /**
     * For every new Chat Message that came in after the the User got into the LobbyActivity. Complementary Method to
     * onNewChatLog, so that method does not need to send the whole Array everytime.
     * 
     * @param chatMsg
     */
    public void onNewChatMessage(ChatMessage chatMsg);


    /**
     * This is the dataset that the LobbyList creates its running games representation from. For further Details look into
     * {@link Game}
     * 
     * @param pGames
     */
    public void onRunningGamesUpdate(ArrayList<Game> pGames);


    /**
     * This is the dataset that the LobbyList creates its open games representation from. For further Details look into
     * {@link Game}
     * 
     * @param pGames
     */
    public void onOpenGamesUpdate(ArrayList<Game> pGames);

}

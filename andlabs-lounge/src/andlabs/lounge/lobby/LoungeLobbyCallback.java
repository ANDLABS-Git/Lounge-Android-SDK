package andlabs.lounge.lobby;

import java.util.List;

import andlabs.lounge.lobby.model.ChatMessage;
import andlabs.lounge.lobby.model.LobbyListElement;

public interface LoungeLobbyCallback {

	
	/**
	 * This is the dataset that the LobbyList creates its represantation from.
	 * For further Details look into {@link LobbyListElement}
	 * @param data
	 */
	public void onLobbyDataUpdated(List<LobbyListElement> data);


	public void onChatDataUpdated(List<ChatMessage> data);
	/**
	 * Sends all cached Chatmessages to the Listner. 
	 * This happens either when the user logs in or the Activity need to be 
	 * reinstatiated after being closed.
	 * @param chatLog
	 */
	public void onNewChatLog(List<ChatMessage> chatLog);
	
	
	/**
	 * For every new Chat Message that came in after the the User 
	 * got into the LobbyActivity. Complementary Method to onNewChatLog, so that 
	 * method does not need to send the whole Array everytime.
	 * @param chatMsg
	 */
	public void onNewChatMessage(ChatMessage chatMsg);

}

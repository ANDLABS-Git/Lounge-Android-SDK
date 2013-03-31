package andlabs.lounge.lobby;

import java.util.List;

import andlabs.lounge.lobby.model.ChatMessage;
import andlabs.lounge.lobby.model.LobbyListElement;


public interface LoungeLobbyCallback {

	public void onLobbyDataUpdated(List<LobbyListElement> data);


	public void onChatDataUpdated(List<ChatMessage> data);

}

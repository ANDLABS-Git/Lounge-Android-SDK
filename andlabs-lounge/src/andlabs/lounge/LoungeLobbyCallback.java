package andlabs.lounge;

import java.util.List;

import andlabs.lounge.model.ChatMessage;
import andlabs.lounge.model.LobbyListElement;


public interface LoungeLobbyCallback {

	public void onLobbyDataUpdated(List<LobbyListElement> data);


	public void onChatDataUpdated(List<ChatMessage> data);

}

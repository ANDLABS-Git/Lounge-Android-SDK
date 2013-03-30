package andlabs.lounge.frontend;

import java.util.List;

import andlabs.lounge.pojo.ChatMessage;
import andlabs.lounge.pojo.LobbyListElement;


public interface LoungeLobbyCallback {

	public void onLobbyDataUpdated(List<LobbyListElement> data);


	public void onChatDataUpdated(List<ChatMessage> data);

}

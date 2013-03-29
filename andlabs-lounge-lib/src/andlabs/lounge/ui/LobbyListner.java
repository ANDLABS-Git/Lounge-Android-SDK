package andlabs.lounge.ui;

import java.util.List;

public interface LobbyListner {

	

	public void onLobbyDataUpdated(List<LobbyListElement> data);
	
	public void onChatDataUpdated(List<ChatMessage> data);
}

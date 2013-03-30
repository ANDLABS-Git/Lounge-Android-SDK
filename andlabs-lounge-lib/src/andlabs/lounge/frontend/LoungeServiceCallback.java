package andlabs.lounge.frontend;

import java.util.List;

import org.json.JSONObject;

import andlabs.lounge.pojo.LobbyListElement;


public interface LoungeServiceCallback {


	public void onLogin(JSONObject payload);


	public void onError(String message);
	
	public void onLobbyDataUpdated(List<LobbyListElement> data);

}

package andlabs.lounge;

import org.json.JSONObject;


public interface LoungeServiceCallback {

	public void theAnswerIs42();

	public void onLogin(JSONObject payload);

	public void onJoinMatch(JSONObject payload);

	public void onError(String message);

}

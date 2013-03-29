package andlabs.lounge.service;

interface LoungeServiceDef {

	void connect();

	void disconnect();

	void login(in String playerId);

	void chat(in String message);

}
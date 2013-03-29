package andlabs.lounge.service;

interface LoungeServiceDef {

	void connect();

	void disconnect();

	void chat(in String message);

}
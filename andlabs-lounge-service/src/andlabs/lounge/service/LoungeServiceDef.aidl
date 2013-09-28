package andlabs.lounge.service;

interface LoungeServiceDef {

	void connect();

	void reconnect();

	void disconnect();

	void login(in String playerId);

	void requestUpdate();

	void chat(in String message);

	void openMatch(in String pPackageId, in String pDisplayName);

	void joinMatch(in String pGameId, in String pMatchId);

	void checkin(in String pPackageId, in String pMatchId);

	void update(in String pPackageId, in String pMatchId, in String pStatus);

	void move(in String pPackageId, in String pMatchId, in Bundle pMoveBundle);

	void stream(in String pPackageId, in String pMatchId, in Bundle pMoveBundle);

	void closeMatch(in String pPackageId,in String pMatchId);

	void checkout(in String pPackageId, in String pMatchId);
}
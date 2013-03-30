package andlabs.lounge.model;

import java.util.Comparator;

import andlabs.lounge.pojo.LobbyListElement;

public class LobbyComperator implements Comparator<LobbyListElement> {

	@Override
	public int compare(LobbyListElement first, LobbyListElement second) {

		if (first.getType().value() < second.getType().value()) {
			return 0;
		}

		if (first.getType().value() < second.getType().value()) {
			return 1;
		} else {
			return -1;
		}

	}

}

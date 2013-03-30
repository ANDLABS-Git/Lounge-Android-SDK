package andlabs.lounge.pojo;

import java.io.Serializable;
import java.util.List;

public class LobbyDataContainer implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8722588396268666564L;
	
	
	public LobbyDataContainer(List<LobbyListElement> data){
		this.data=data;
	}
	
	private List<LobbyListElement> data;

	public List<LobbyListElement> getData() {
		return data;
	}

	public void setData(List<LobbyListElement> data) {
		this.data = data;
	}
}

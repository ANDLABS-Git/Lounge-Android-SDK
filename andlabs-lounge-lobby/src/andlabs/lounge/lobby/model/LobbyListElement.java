package andlabs.lounge.lobby.model;

import java.util.ArrayList;
import java.util.List;



/**
 * This class represents an entry in the Main Lobby List
 * Every Entry is a Game(App) 
 * A Single Game can be represented in this List up to two times. 
 * If a Game(App) has match-instances where the local user is involved then it contains all
 * those matches in a list and has the ElementType=JOINED_GAME 
 * 
 * and if the same Game(App) has also open Games then it has a second 
 * entry with a List of matches with the Type OPEN_GAMES.
 * 
 * So every App can either have 0,1,2 LobbyListElements in the List. 
 * @author Lukas Jarosch jarosch@andlabs.com
 *
 */
public class LobbyListElement {


	/**
	 * Specifies the type of the Element according to the UI Spec Document
	 * JOINED_GAME 	> Red Top section
	 * SEPERATOR 	> Divider between the upper Red section and lower green section
	 * OPEN_GAME	> Green buttom section
	 */
	private ElementType type;
	
	/**
	 *Name of the Game(App) that will be displayed in the UI
	 */
	private String Title;
	
	/**
	 * Package Name of the Game that is being used as unique identifier
	 */
	private String pgkName;
	
	/**
	 * List of Matches belonging to this game. 
	 */
	private List<GameMatch> gameMatches;
	private boolean localPlayerOnTurn;
	



	public LobbyListElement() {

		gameMatches = new ArrayList<GameMatch>();
	}


	public ElementType getType() {
		return type;
	}


	public void setType(ElementType type) {
		this.type = type;
	}


	public String getTitle() {
		return Title;
	}


	public void setTitle(String title) {
		Title = title;
	}


	public String getPgkName() {
		return pgkName;
	}


	public void setPgkName(String pgkName) {
		this.pgkName = pgkName;
	}


	public List<GameMatch> getGameMatches() {
		return gameMatches;
	}


	public void setGameMatches(List<GameMatch> gameMatches) {
		this.gameMatches = gameMatches;
	}




	public enum ElementType {

		JOINED_GAME(0),
		SEPERATOR(1),
		OPEN_GAME(2);

		private int type;


		ElementType(int type) {
			this.setType(type);
		}


		public int value() {
			return type;
		}


		public void setType(int type) {
			this.type = type;
		}

	}

}

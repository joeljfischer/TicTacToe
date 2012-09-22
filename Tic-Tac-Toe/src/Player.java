/**
 * Player.java (Tic-Tac-Toe Eclipse Project)
 * 
 * by: Joel Fischer
 */

public abstract class Player {

	private char marker;
	private int playerNum;
	
	/**
	 * Constructor
	 * @param marker The player's marker (X or O)
	 * @param playerNum The player's number (1 or 2)
	 */
	public Player(char marker, int playerNum){
		this.marker = marker;
		this.playerNum = playerNum;
	}
	
	/**
	 * Abstract function to be implemented in subclasses
	 * @return The move chosen
	 */
	public abstract int getMove();

	/**
	 * @param marker
	 * Whether this player is 'X' or 'O'
	 */
	public void setMarker(char marker) {
		this.marker = marker;
	}

	/**
	 * @return the marker
	 */
	public char getMarker() {
		return marker;
	}

	/**
	 * Whether this player is player 1 or 2
	 * @param playerNum 
	 */
	public void setPlayerNum(int playerNum) {
		this.playerNum = playerNum;
	}

	/**
	 * @return the playerNum
	 */
	public int getPlayerNum() {
		return playerNum;
	}
}

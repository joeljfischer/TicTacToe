/**
 * HumanPlayer.java (Tic-Tac-Toe Eclipse Project)
 * 
 * by: Joel Fischer
 */

/**
 * An entity HumanPlayer that is a player but chooses to play 
 * its move through showing a prompt and asking the user for a value
 * 
 */
public class HumanPlayer extends Player {
	
	/**
	 * HumanPlayer Constructor
	 * 
	 * @param mark This object's mark (X or O)
	 * @param playerNum This object's player number (1 or 2)
	 */
	public HumanPlayer(char mark, int playerNum) {
		super(mark, playerNum);
	}

	@Override
	/**
	 * Overridden function, implements asking a human user for a choice to play
	 * @Return The value chosen by the player
	 */
	public int getMove() {
		//Create a command line scanner
		int moveLoc = -1;
		
		//Retrieve the user's selection
		BoardGui.playerMark = this.getMarker();
		BoardGui.setBoardEnabled(true);
		while(moveLoc < 0 || moveLoc > 8) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ie) {}
			if(BoardGui.getGameUpdated() == true) {
				moveLoc = BoardGui.getMarkedSpace();
				BoardGui.setGameUpdated(false);
			}
		}
		BoardGui.setBoardEnabled(false);
		
		return moveLoc; //Return the value
	}
}

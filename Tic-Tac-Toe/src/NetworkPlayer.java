/**
 * NetworkPlayer.java (Tic-Tac-Toe Eclipse Project)
 * 
 * by: Joel Fischer
 */

public class NetworkPlayer extends Player{
	
	private int inInt = -1;
	private int playAgain = 1; //1=continue, 0=play again, -1=quit
	
	/**
	 * Constructor
	 * @param mark The player's mark (X or O)
	 * @param playerNum The player's number (1 or 2)
	 */
	public NetworkPlayer(char mark, int playerNum){
		super(mark, playerNum); //Call the super class constructor
	}
	
	/**
	 * Get the user's movement choice
	 */
	public int getMove() {
		int moveLoc = -1;
		//Wait for incoming game data
		while(true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ie) {
				System.err.println("Error Pausing Thread MAIN");
				System.err.println(ie);
			}
			//Receive the data
			if(this.inInt != -1) {
				moveLoc = inInt;
				break;
			}
		}
		this.inInt = -1;
		return moveLoc;
	}
	
	/**
	 * Set the Game Data
	 * @param inInt Incoming Integer Game Data
	 */
	public void setInGameData(int inInt) {
		this.inInt = inInt;
	}

	/**
	 * Set data whether or not to play again
	 * @param playAgain the boolean var playAgain
	 */
	public void setPlayAgain(int playAgain) {
		this.playAgain = playAgain;
	}

	/**
	 * Get data whether or not to play again
	 * @return the boolean var playAgain
	 */
	public int getPlayAgain() {
		return playAgain;
	}
	
}

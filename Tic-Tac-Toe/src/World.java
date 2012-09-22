/**
 * World.java (Tic-Tac-Toe Eclipse Project)
 * 
 * by: Joel Fischer
 */

import javax.swing.*;

/**
 * The class World represents the game's entity in and of itself.
 */
public class World {
	
	//The Game Board, only the world can have the master,
	//anything else gets only forced copy by value copies.
	private static char[] board;
	private static int gametype;
	
	/**
	 * World constructor
	 * Generates a new game board.
	 */
	public World(int gametype) {
		board = generateBoard();
		World.gametype = gametype;
		if (gametype == 1) {
			new BoardGui("Human vs. Human Hotseat");
		} else if (gametype == 2) {
			new BoardGui("Human vs. AI");
		} else if (gametype == 3) {
			new BoardGui("AI vs. AI Demo Mode");
		} else if (gametype == 4) {
			new BoardGui("Network Play");
		}
		
	}
	
	/**
	 * Overloaded method run. Runs the game loop based on the given players (Humans, AIs
	 * or Networked). Returns the player's decision on playing again (in most cases).
	 * 
	 * @param player1
	 * @param player2
	 * @return playContinue
	 */
	public int run(HumanPlayer player1, HumanPlayer player2){
		//Create a container for the players to make it easier/more generic to play turns
		HumanPlayer[] players = new HumanPlayer[2];
		players[0] = player1;
		players[1] = player2;
		int playContinue = -1;
		
		//Run the game loop until there is a winner or tie
		while(true){
			//Run a for each loop to play each player's turn
			for(HumanPlayer player : players){
				//Play the turn, and retrieve back a status of if we are still playing
				//or are done and what the user's choice on playing again is.
				playContinue = playHumanTurn(player);
				if(playContinue != -1){
					BoardGui.destroy();
					//Game is done, go back to Core with this status
					return playContinue;
				}
			}
		}
	}
	
	/**
	 * Overloaded method run. Runs the game loop based on the given players (Humans, AIs
	 * or Networked). Returns the player's decision on playing again (in most cases).
	 * 
	 * @param player1
	 * @param player2
	 * @return playContinue
	 */
	public int run(HumanPlayer player1, AiPlayer player2){
		int playContinue = -1;
		
		//In this case, AI goes first, so this is the easiest (though not best) way to do it
		if(player1.getMarker() == 'O'){
			//Play the AI turn, retrieve status
			playContinue = playAiTurn(player2);
			if(playContinue != -1){
				//Game is done, go back to Core with this status
				BoardGui.destroy();
				return playContinue;
			}
		}
		
		//Run the game loop until there is a winner or tie
		while(true) {
			//Play the human turn, retrieve status
			playContinue = playHumanTurn(player1);
			if(playContinue != -1){
				//Game is done, go back to Core with this status
				BoardGui.destroy();
				return playContinue;
			}
			
			//Play the AI turn, retrieve status
			playContinue = playAiTurn(player2);
			if(playContinue != -1){
				//Game is done, go back to Core with this status
				BoardGui.destroy();
				return playContinue;
			}
		}
	}
	
	/**
	 * Overloaded method run. Runs the game loop based on the given players (Humans, AIs
	 * or Networked). Returns the player's decision on playing again (in most cases).
	 * 
	 * @param player1
	 * @param player2
	 * @return playContinue
	 */
	public int run(AiPlayer player1, AiPlayer player2){
		int playContinue = -1;
		
		//Run the game loop until there is a winner or tie
		while(true){
			playContinue = playAiTurn(player1);
			if(playContinue != -1){
				BoardGui.destroy();
				//Game is done, return to Core and force going back to the menu
				return 0;
			}
			
			playContinue = playAiTurn(player2);
			if(playContinue != -1){
				BoardGui.destroy();
				//Game is done, return to Core and force going back to the menu
				return 0;
			}
		}
	}
	
	/**
	 * Overridden Method for Playing a Network Game
	 * @param player1
	 * @return
	 */
	public int run(HumanPlayer player1, NetworkPlayer player2){
		int playContinue = -1;
		
		if(player1.getMarker() == 'O') {
			playContinue = playNetworkTurn(player2);
		}
		if(playContinue != -1){
			//Game is done, go back to Core with this status
			return playContinue;
		}
		
		//Run the game loop until there is a winner or tie
		while(true) {
			//Play the human turn, retrieve status
			playContinue = playHumanTurn(player1);
			if(playContinue != -1) {
				playContinue = askAgainNet(playContinue, player2);
				BoardGui.destroy();
				return playContinue;
			}
			
			//Play the Network Player's turn, retrieve status
			playContinue = playNetworkTurn(player2);
			if(playContinue != -1){
				//Asking again and GUI destroy are built in to playNetworkTurn
				//Game is done, go back to Core with this status
				return playContinue;
			}
		}
	}
	
	/**
	 * Required for AI to know when a space is free and to
	 * alert the player in command line
	 * 
	 * @param move
	 * The Move attempting to be made
	 */
	public static boolean isFree(int move){
		char[] tempBoard = getBoard();
		//If the space is empty, return true
		if(tempBoard[move] == ' '){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Required for AI to know how to play at least semi-smartly and
	 * for the world to know when a winner has been reached
	 * 
	 * @return
	 * True if the move will cause a winner.
	 */
	public static boolean isWinner(int mark, char[] tb){
		//Return true if any case causes a winner
		return ((tb[0] == mark && tb[1] == mark && tb[2] == mark) || //Across Top
				(tb[3] == mark && tb[4] == mark && tb[5] == mark) || //Across Mid
				(tb[6] == mark && tb[7] == mark && tb[8] == mark) || //Across Bottom
				(tb[0] == mark && tb[3] == mark && tb[6] == mark) || //Down Left
				(tb[1] == mark && tb[4] == mark && tb[7] == mark) || //Down Mid
				(tb[2] == mark && tb[5] == mark && tb[8] == mark) || //Down Right
				(tb[0] == mark && tb[4] == mark && tb[8] == mark) || //Diag TL -> BR
				(tb[2] == mark && tb[4] == mark && tb[6] == mark)    //Diag TR -> BL
				);
	}

	/**
	 * Creates a copy of the board and returns it
	 * 
	 * @return boardCopy a copy of the board
	 */
	public static char[] getBoard() {
		char[] boardCopy = new char[9];
		//Force the creation of a brand new copy, not just a direct copy, or
		//we will run into trouble with pointers modifying the original object
		//(through experience)
		for(int i=0; i<9; i++){
			boardCopy[i] = board[i];
		}
		return boardCopy;
	}
	
	/**
	 * This runs a full HumanPlayer turn with the input parameter object.
	 * 
	 * @param player The player who's turn we are playing
	 * @return again If the game has ended, and if it has, the user's choice on playing again
	 */
	private static int playHumanTurn(HumanPlayer player) {
		boolean spaceFree = false;
		int again = -1;
		BoardGui.setStatus("Player: " + player.getPlayerNum() + " Please Select a Move");
		
		int move = player.getMove(); //Get the user's move
		
		//Make absolutely sure that the move is free
		spaceFree = isFree(move);
		assert spaceFree;
		//Place the user's move onto the board
		placeMove(move, player.getMarker());
		
		//Check that the network still exists, and then send the string to the other player.
		if(Network.networkExists()){
			TcpGame.setOutString(Integer.toString(move));
		}
		
		//Check for a winner
		if(isWinner(player.getMarker(), getBoard())){
			displayWinner(player.getMarker()); //Display the Winner
			again = askAgain(); //Ask if the user wishes to play again
			return again; //Return the user's choice
		//Check for a tie game
		} else if(isBoardFull()){
			displayWinner(' '); //Display that there was a tie game
			again = askAgain(); //Ask if the user wishes to play again
			return again; //Return the user's choice
		}
		return again; //Return the status to continue playing
	}
	
	/**
	 * This runs a full AiPlayer turn with the input parameter object.
	 * 
	 * @param player The AiPlayer who's turn it is
	 * @return again If the game has ended, and if it has, the user's choice on playing again
	 */
	private static int playAiTurn(AiPlayer player){
		boolean spaceFree = false;
		int again = -1;
		BoardGui.setStatus("Ai Player " + player.getPlayerNum() + " it is your turn...");
		
		//Pause for .4 seconds (Ai could blaze through)
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			System.err.println("Error pausing gameplay");
			System.err.println(e);
		}
		BoardGui.setStatus("Ai Thinking...");
		//Pause for .8 seconds
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			System.err.println("Error pausing gameplay");
			System.err.println(e);
		}
		int move = player.getMove(); //Get the Ai's move from its algorithm
		spaceFree = isFree(move); //Make absolutely sure that selection is free
		assert spaceFree;
		
		placeMove(move, player.getMarker()); //Place the move
		BoardGui.setAiOrNetworkChoice(move, player.getMarker());
		BoardGui.setStatus("Ai Chose Slot: " + (move+1));
		try {
			Thread.sleep(1100); //Pause for 1.1 seconds
		} catch (InterruptedException e) {
			System.err.println("Error pausing gameplay");
			System.err.println(e);
		}
				
		//Check for a winner
		if(isWinner(player.getMarker(), getBoard())){
			displayWinner(player.getMarker()); //Display the winner
			if(gametype != 3){
				again = askAgain();
			}
			else {
				again = 0;
			}
			BoardGui.destroy();
			return again;
		//Check for a tie
		} else if(isBoardFull()){
			displayWinner(' '); //Display the Tie Game screen
			if(gametype != 3){
				again = askAgain();
			} else {
				again = 0;
			}
			BoardGui.destroy();
			return again;
		}
		return again;
	}
	
	/**
	 * This is a full Network Player's turn with the input parameter player object
	 * 
	 * @param player The current Network Player
	 * @return Keep playing or stop
	 */
	private static int playNetworkTurn(NetworkPlayer player){
		int again = -1;
		
		BoardGui.setStatus("Waiting for Network Player...");
		
		//Get the Network Player's move
		int move = player.getMove();
		//Make absolutely certain that space is free
		boolean spaceFree = isFree(move);
		assert spaceFree;
		
		BoardGui.setStatus("Received Move...");
		//Place the move onto the board
		placeMove(move, player.getMarker());
		BoardGui.setAiOrNetworkChoice(move, player.getMarker());
		
		//Check for a winner
		if(isWinner(player.getMarker(), getBoard())){
			displayWinner(player.getMarker()); //Display the Winner
			again = askAgain(); //Ask if the user wishes to play again
			again = askAgainNet(again, player);
			BoardGui.destroy();
			return again; //Return the user's choice
		//Check for a tie game
		} else if(isBoardFull()){
			displayWinner(' '); //Display that there was a tie game
			again = askAgain(); //Ask if the user wishes to play again
			again = askAgainNet(again, player);
			BoardGui.destroy();
			return again; //Return the user's choice
		}
		return again; //Return the status to continue playing
	}
	
	/**
	 * @param board The revised board
	 */
	private static void setBoard(char[] board) {
		World.board = board; //Set the new board to the master board
	}
	
	/**
	 * Display which player won the game
	 * 
	 * @param mark
	 * The mark (X/O) of the winner
	 */
	private static void displayWinner(char mark){
		//If it's a tie, print that.
		if(mark == ' '){
			JOptionPane.showMessageDialog(BoardGui.getPopupFrame(), "Tie Game", "Game Over", 
					JOptionPane.PLAIN_MESSAGE);
		}
		else{
			JOptionPane.showMessageDialog(BoardGui.getPopupFrame(), mark + " Wins!", "Game Over", 
					JOptionPane.PLAIN_MESSAGE);
		}
	}
	
	/**
	 * Ask if the user wants to play again, return to menu, or quit.
	 * 
	 * @return
	 */
	private static int askAgain(){
		Object[] options = {"Play Again", "Main Menu", "Quit Program"};
		int playAgain = -1;
		
		//First ask if the user wants to play again
		playAgain = JOptionPane.showOptionDialog(BoardGui.getPopupFrame(), 
				"Would You Like to Play Again?", "Game Over", JOptionPane.YES_NO_CANCEL_OPTION, 
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		
		return playAgain;
	}
	
	/**
	 * Send the chosen "Play Again" data to the Network Player, compare response with
	 * the user's choice
	 * @param meAgain What the user wants to do
	 * @param otherPlayer The other player's object
	 * @return The consensus (or not) choice
	 */
	private static int askAgainNet(int meAgain, NetworkPlayer otherPlayer) {
		//Send the data to the other player through game data
		if(meAgain == 0) //Want to play again, send that status
			TcpGame.setOutString(Integer.toString(-1));
		else //Want to quit in some way
			TcpGame.setOutString(Integer.toString(-2));
		while(true){
			if(otherPlayer.getPlayAgain() == -2) { //Other player chose to quit/menu
				if (meAgain == 0) //You want to play again, boot to menu
					return 3;
				else //You don't want to play again, do what you want
					return meAgain;
			} else if(otherPlayer.getPlayAgain() == -1) { //Other player chose to play again
				if (meAgain == 0) //You want to play again, play again
					return 0;
				else //You don't want to play again, do what you want
					return meAgain;
			}
		}
	}
	
	/**
	 * Required for the world to know when there is a tie game
	 * 
	 * @return
	 * True if the board is now full (tie game)
	 */
	private static boolean isBoardFull(){
		char[] tempBoard = getBoard();
		//Check if there are any open spaces, if there are, return false
		for(int i=0; i<9; i++){
			if(tempBoard[i] == ' '){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Place a chosen move onto the game board
	 * 
	 * @param move
	 * The selected move to place onto the board
	 */
	private static void placeMove(int move, char player){
		char[] tempBoard = getBoard(); //Get the board
		tempBoard[move] = player; //Add the move onto the board
		setBoard(tempBoard); //Update the board to the master board
	}
	
	/**
	 * Create a new Tic-Tac-Toe Board
	 * 
	 * @return board
	 * The newly created game board
	 */
	private static char[] generateBoard(){
		char[] board = new char[9];
		//Generate a blank board
		for (int i=0; i<9; i++){
			board[i] = ' ';
		}
		return board;
	}
}
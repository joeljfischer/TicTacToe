/**
 * Core.java (Tic-Tac-Toe Eclipse Project)
 * 
 * by: Joel Fischer
 */
import java.util.Scanner;

/**
 * @author jfischer
 * The Core Class, initial command line splash menu.
 * Instantiate Classes.
 */
public class Core {
	
	/**
	 * Just start running the game at the main menu.
	 * 
	 * @param args
	 * Command-line arguments (unused)
	 */
	
	private static World gameWorld;
	
	public static void main(String[] args) {
		run(0); //Start the game at the main menu
	}
	
	/**
	 * Core loop that calls the World object with the correct
	 * parameters for the type of game bing played.
	 * 
	 * @param gameType
	 * If the game should be started at somewhere other
	 * than the main menu (which isgameType = 0), then pass the menu
	 * option here. Also works for resetting and restarting a game.
	 */
	public static void run(int gameType) {
		//Constants for easier reference
		final int ONE_PLAYER = 1;
		final int TWO_HOTSEAT = 2;
		final int TWO_NETWORK = 3;
		final int AI_AI = 4;
		final int QUIT = 5;
		final char X = 'X';
		final char O = 'O';
		
		//Var coming back from games on whether the user chose to play again.
		int playAgain = 0;
		
		//Non Constant Vars
		int menuChoice = 0; //Create an integer for the menu choice
		Scanner menuInput = new Scanner(System.in); //Create an input scanner for the menu
		
		//Display a Splash Screen and Menu
		if(gameType == 0){
			System.out.println("TIC-TAC-TOE");
			System.out.println("by: Joel Fischer\n\n");
			System.out.println("1. 1 Player"); //User vs. AI
			System.out.println("2. 2 Player Hotseat"); //User vs. User Same Computer
			System.out.println("3. Network Play (BUGGY)"); //User vs. User over a network
			System.out.println("4. Demo Mode (AI v. AI)"); //AI vs. AI
			System.out.println("5. Quit"); //Exit Program
			
			//Get menu choice
			while(menuChoice < 1 || menuChoice > 5){
				System.out.print("Enter Menu Choice: ");
				try{
					//Attempt to retrieve an integer
					menuChoice = menuInput.nextInt();
				} catch(Exception e) {
					System.err.println("Error: Input: \"" + menuInput.next() + "\" Was Not An Integer, try again...");
					System.err.println("\n\n\n");
					run(0);
				}
			}
		} else {
			//Auto start a menu choice (allows restarting of a gametype or direct to quit)
			menuChoice = gameType;
		}
		
		//User Chose 1 Player
		if(menuChoice == ONE_PLAYER){
			int playerX = 0; //Which player is X
			HumanPlayer player1; //One Human Player
			AiPlayer player2; //One Ai Player
			
			//Cycle until a correct choice is made
			while(true) {
				System.out.print("Will Player 1(You) or 2(AI) be X? (X goes first) Enter 1 or 2: ");
				try {
					playerX = menuInput.nextInt();
				} catch(Exception e) {
					System.err.println("Error: Input: \"" + menuInput.next() + "\" Was not an Integer...");
					System.err.println("\n");
					run(1);
				}
				//Player 1 is X, initialize the player objects
				if(playerX == 1) {
					player1 = new HumanPlayer(X, 1);
					player2 = new AiPlayer(O, 2);
					break;
				//Player 2 is X, initialize player objects
				} else if (playerX == 2){
					player1 = new HumanPlayer(O, 1);
					player2 = new AiPlayer(X, 2);
					break;
				}
			}
			
			//Initialize Game World (The Game itself's entity)
			gameWorld = new World(2);
			
			//Run the game, passing the players to the world,
			//retrieve the player's decision on whether to play again.
			playAgain = gameWorld.run(player1, player2);
			if(playAgain == 0)
				run(1); //Player chose to play again.
			else if (playAgain == 1)
				run(0); //Player chose to return to the menu
			else
				run(5); //Player chose to exit
		}
		
		//User Chose 2 Player Hotseat
		if(menuChoice == TWO_HOTSEAT) {
			HumanPlayer player1;
			HumanPlayer player2;
			//Player 1 is X, initialize player objects
			player1 = new HumanPlayer(X, 1);
			player2 = new HumanPlayer(O, 2);
			
			//Initialize Game World (The Game itself's entity)
			gameWorld = new World(1);
			
			//Run the game, passing the players to the world,
			//retrieve the player's decision on whether to play again.
			playAgain = gameWorld.run(player1, player2);
			if(playAgain == 0)
				run(2); //Player chose to play again
			else if (playAgain == 1)
				run(0); //Player chose to return to the menu
			else
				run(5); //Player chose to quit
		}
		
		//User Chose 2 Player Network
		if(menuChoice == TWO_NETWORK) {
			//Initialize Network Variables
			Network network = new Network();
			HumanPlayer HPlayer = null;
			NetworkPlayer NPlayer = null;
			String serverOrClient = "";
			Scanner input = new Scanner(System.in);
			
			//Retrieve the user input of creating a Network Server or Client
			System.out.print("Are you setting up a Server or Client? (S/C) ");
			try{
				serverOrClient = input.next().toLowerCase();
			} catch(Exception e){
				System.err.println("Error with input");
				System.err.println(e);
			}
			
			//Create the Game World (We need the GUI)
			gameWorld = new World(4);
			
			//If the user chose to create a server
			if(serverOrClient.startsWith("s")) {
				System.out.println("Setting up Server");
				//Set up the Players and Network
				HPlayer = new HumanPlayer(X, 1);
				NPlayer = new NetworkPlayer(O, 2);
				network.createNetwork(1, HPlayer, NPlayer);
			} else { //User chose to be a client
				System.out.println("Setting up Client");
				//Set up the Players and Network
				HPlayer = new HumanPlayer(O, 2);
				NPlayer = new NetworkPlayer(X, 1);
				network.createNetwork(2, HPlayer, NPlayer);
			}
			
			BoardGui.updateChat("SYSTEM: Networking is incomplete and will be buggy!");
			//Start to run the Game with the Human Player and Network Player
			playAgain = gameWorld.run(HPlayer, NPlayer);
			
			//When game is completed check the play again response and run code to match
			while(true) {
				
				//Players chose to play again
				if(playAgain == 0) {
					gameWorld = new World(4);
					BoardGui.setChatEditable(true);
					playAgain = gameWorld.run(HPlayer, NPlayer);
					
				//Player chose to return to the menu, end connections and return to menu
				} else if (playAgain == 1) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException ie) {}
					TcpChat.quitThread();
					TcpGame.quitThread();
					run(0);
					
				//Player chose to quit, end connections and exit the game
				} else if (playAgain == 2) {  //Player chose to quit
					try {
						Thread.sleep(500);
					} catch (InterruptedException ie) {}
					TcpChat.quitThread();
					TcpGame.quitThread();
					run(5);
					
				//Other player chose to quit, kick to menu
				} else if (playAgain == 3) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException ie) {}
					TcpChat.quitThread();
					TcpGame.quitThread();
					System.out.println("\nOther Player Did Not Want to Play Again");
					run(0);
				}
			}
		}
		
		//User Chose Demo Mode (AI vs. AI)
		if(menuChoice == AI_AI) {
			//Initialize AI player objects
			AiPlayer player1 = new AiPlayer(X, 1);
			AiPlayer player2 = new AiPlayer(O, 2);
			
			//Initialize Game World
			gameWorld = new World(3);
			//Run the game, passing the players to the world,
			playAgain = gameWorld.run(player1, player2);
			run(0); //Return to the main menu
		}
		
		//User Chose To Quit
		if(menuChoice == QUIT){
			System.out.println("Thanks for Playing!");
			System.out.println("Exiting in 3 Seconds...");
			try {
				//Pause for 3 seconds so the program exiting is not quite so JAR(ring)
				//JAR, get it, HA! (Java Humor)
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				System.err.println("Error Exiting, Forcing Exit: ");
				System.err.println(e);
			} finally {
				System.exit(0); //Exit with status code 0.
			}
		}
	}
	
	/**
	 * Destroy the gameworld and go to the main menu.
	 */
	public static void destroyWorld() {
		gameWorld = null;
		run(0);
	}
}

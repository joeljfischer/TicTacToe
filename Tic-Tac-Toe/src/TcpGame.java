/**
 * TcpGame.java (Tic-Tac-Toe Eclipse Project)
 * 
 * by: Joel Fischer
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *  A new runnable thread that simultaneously handles the transfer of game data
 *  with a network partner in conjunction with playing the game.
 * @author jfischer
 */
public class TcpGame extends Thread {

	private static Socket gameSocket = null;
	private static NetworkPlayer NPlayer = null;
	private static BufferedReader inData = null;
	private static PrintWriter outData = null;
	private static String outString = "";
	private static String inString = "";
	private static int inInt = -5;
	protected static boolean exitThread = false;
	
	/**
	 * Constructor sets up the thread with the specified socket
	 * 
	 * @param socket The Socket to set up the Game Server/Client on
	 * @param HPlayer The Human Player (unused currently)
	 * @param NPlayer The Network Player to communicate with
	 */
	public TcpGame(Socket socket, HumanPlayer HPlayer, NetworkPlayer NPlayer){
		super("TCP Game");
		TcpGame.gameSocket = socket;
		TcpGame.NPlayer = NPlayer;
		
		//Create I/O streams to the network partner's port
		try {
			//Input data streams
			inData = new BufferedReader(new InputStreamReader(gameSocket.getInputStream()));
			//Output data streams
			outData = new PrintWriter(gameSocket.getOutputStream(), true);
		} catch(IOException ioe) {
			System.err.println("Error Setting Up I/O Streams");
			System.err.println(ioe);
			try {
				gameSocket.close();
			} catch (IOException ioe1) {
				System.err.println("Could not Close Chat Connection while Aborting");
				System.err.println(ioe1);
			}
			quitThread();
		}
	}
	
	/**
	 * Overridden Thread function run() is the "main" function of this specific thread
	 */
	@Override
	public void run(){
		
		//Display Game Connection Status
		if(gameSocket.getRemoteSocketAddress() != null) {
			if(gameSocket.getInetAddress().isLoopbackAddress()){ //Test if the remote IP is the Local IP
				System.out.println("Game is Connected" + " Connection Port: " + 
						"LocalHost:" + gameSocket.getPort());
			}
			else {
				System.out.println("Game is Connected " + " Connection Port: " + 
						gameSocket.getInetAddress() + ":" +  gameSocket.getPort());
			}
		}
		
		//Sending/Receiving Data Game Loop
		while(!exitThread) {
			try {
				//While waiting for incoming data, if we're ready to send out data, do so
				if(!inData.ready() && !exitThread) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException ie) {
						System.err.println("Error Holding Thread");
						System.err.println(ie);
					}
					if(!outString.isEmpty()){
						outData.println(outString);
						outString = "";
					}
				}
				
				//When we are receiving incoming data and not exiting the game
				while(inData.ready() && !exitThread) {
					//Read incoming data
					inString = inData.readLine();
					//Get the integer out of the incoming stream
					inInt = Integer.parseInt(inString);
					//Data handling
					if(inInt != -1 && inInt != -2) {
						//Show in Chat the other player's move
						BoardGui.updateChat(timeStamp() + "SYSTEM: Other Player Moved Slot: " + 
								(inInt + 1));
						NPlayer.setInGameData(inInt); //Set the move onto the board
					//if -1, player wants to play again, -2, quit
					} else {
						NPlayer.setPlayAgain(inInt);
					}
					break; //Break out of reading data since we are done now.
				}
			} catch(IOException ioe) {
				if (!exitThread){
					System.err.println("Error with Game I/O Streams");
					System.err.println(ioe);
				}
			}
		}
		quitThread(); //At the end of the run cycle, properly exit the thread
	}
	
	/**
	 * Set the String data to go out
	 * @param str The String to be output
	 */
	public static void setOutString(String str){
		outString = str;
	}
	
	/**
	 * Creats a string timestamp for updating the chat with the other player's move
	 * @return The String Timestamp
	 */
	private static String timeStamp() {
		Calendar now = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(now.getTime()); //Return the timestamp string
	}
	
	/**
	 * Close all streams and sockets, notify the game thread to close, and exit this thread
	 * on next cycle.
	 */
	public static void quitThread() {
		//Close all streams, sockets and threads
		if(!exitThread){
			try{
				System.out.println("Closing Game Connection");
				inData.close();
				outData.close();
				gameSocket.close();
				exitThread = true;
			} catch (IOException ioe) {
				System.err.println("Error Attempting to End Thread");
				System.err.println(ioe); //Output the error to the command line
				try {
					gameSocket.close();
				} catch (IOException ie) {
					System.err.println("Error Closing TcpGame");
					System.err.println(ie);
				}
			}
		}
	}
}

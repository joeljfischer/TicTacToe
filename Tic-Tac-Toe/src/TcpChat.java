/**
 * TcpChat.java (Tic-Tac-Toe Eclipse Project)
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
 *  A new runnable thread that simultaneously handles chatting with a network partner
 *  in conjunction with playing the game.
 *  @author jfischer
 */
public class TcpChat extends Thread {

	private static Socket chatSocket = null;
	private static BufferedReader inData = null;
	private static PrintWriter outData = null;
	private static boolean exitThread = false;
	private static String outString = null;
	private static String inString = null;
	
	/**
	 * Constructor sets up the Thread with the specified socket
	 * 
	 * @param socket The Socket to create the thread on
	 */
	public TcpChat(Socket socket){
		super("TCP Chat");
		TcpChat.chatSocket = socket;
		
		//Create I/O streams to the network partner's port
		try {
			//Input Data Stream
			inData = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
			//Output Data Stream
			outData = new PrintWriter(chatSocket.getOutputStream(), true);
		} catch(IOException ioe) {
			System.err.println("Error Setting Up I/O Streams");
			System.err.println(ioe);
			ioe.printStackTrace();
			try {
				chatSocket.close();
			} catch (IOException ioe1) {
				System.err.println("Could not Close Chat Connection while Aborting");
				System.err.println(ioe1);
				ioe1.printStackTrace();
			}
			quitThread();
		}
	}
	
	/**
	 * Overridden Thread function run() is the "main" function of this specific thread
	 */
	@Override
	public void run(){
		
		//Allow the text field to be editable
		BoardGui.setChatEditable(true);
		
		//Display Chat Connection Status
		if(chatSocket.getRemoteSocketAddress() != null) {
			if(chatSocket.getInetAddress().isLoopbackAddress()){ //Test if the remote IP is the Local IP
				System.out.println("Chat is Connected" + " Connection Port: " + "LocalHost:"
						+ chatSocket.getPort());
			}
			else{ //If the remote IP is not on the local machine
				System.out.println("Chat is Connected " + " Connection Port: " + chatSocket.getInetAddress()
						+ ":" + chatSocket.getPort());
			}
		}
		
		//Chat instructions
		BoardGui.updateChat("SYSTEM: Welcome to Tic-Tac-Toe by Joel Fischer and Nik Heiden");
		BoardGui.updateChat("SYSTEM: Enter '/clear' to clear the chat window");
		BoardGui.updateChat("SYSTEM: Enter '/quit' to quit the game properly mid-game(Close all connections)");
		BoardGui.updateChat("SYSTEM: Type and press enter to chat. Have Fun!");
		
		//Sending/Receiving Data Chat Loop
		while(!exitThread) {
			try {
				//While waiting for incoming data, if we're reading to send out data, do so
				if(!inData.ready() && !exitThread) {
					//If the Text Field has data that is submitted
					try {
						Thread.sleep(10);
					} catch (InterruptedException ie) {
						System.err.println("Error Holding Thread");
						System.err.println(ie);
						ie.printStackTrace();
					}
					if(BoardGui.getChatUpdated()){
						//Get the data
						outString = BoardGui.getInputString();
						//Send the data to the connection partner, INCLUDING '/quit'!
						if(!outString.startsWith("/clear")) {
							outData.println(outString);
						}
						//Show this user's entry on the chat window as well
						BoardGui.updateChat(timeStamp() + " Me: " + outString);
						//Clear the Entry Field
						BoardGui.setChatTextField("");
						BoardGui.setChatUpdated(false);
						
						//Check for special commands
						if(outString.startsWith("/clear")) {
							//Clear the chat window
							BoardGui.clearChat();
						} else if(outString.startsWith("/quit")) {
							//We already sent the /quit to the other player and he will quit
							//Exit the game
							BoardGui.destroy();
							Core.destroyWorld();
							quitThread();
						}
					}
				}
				
				//When we are receiving incoming data and not exiting the game
				while(inData.ready() && !exitThread) {
					//Get the incoming data
					inString = inData.readLine();
					//Show the data in the chat area
					BoardGui.updateChat(timeStamp() + " Other: " + inString);
					//If the other user quit, we must too
					if(inString.startsWith("/quit")){
						BoardGui.updateChat("SYSTEM: Other Player Quit, exiting in 5 seconds");
						System.out.println("\nOther Player Quit, exiting in 5 seconds");
						try {
							Thread.sleep(5000);
						} catch (InterruptedException ie) {
							System.err.println("Error Pausing Game...Exiting...");
							System.err.println(ie);
							System.exit(-1);
						}
						quitThread();
						BoardGui.destroy();
						Core.destroyWorld();
					}
					break; //Break out of reading data since we are done now.
				}
			} catch(IOException ioe) { //TODO: May want to not just kick out here
				if (!exitThread){
					System.err.println("Error with Chat I/O Streams");
					System.err.println(ioe);
					ioe.printStackTrace();
					System.exit(-1);
				}
			}
		}
		quitThread(); //At the end of the run cycle, properly exit the thread
	}
	
	/**
	 * Create a timestamp to display with chat data.
	 * 
	 * @return The String of timestamp data
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
		if(!exitThread){
			try{
				System.out.println("Closing Chat Connection");
				inData.close();
				outData.close();
				chatSocket.close();
				TcpGame.quitThread();
				TcpGame.exitThread = true;
				exitThread = true;
			} catch (IOException ioe) {
				System.err.println("Error Attempting to End Thread");
				System.err.println(ioe); //Output the error to the command line
				ioe.printStackTrace();
			}
		}
	}
}

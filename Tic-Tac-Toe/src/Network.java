/**
 * Network.java (Tic-Tac-Toe Eclipse Project)
 * 
 * by: Joel Fischer
 */

import java.util.Scanner;
import java.io.IOException;
import java.net.*;

/**
 * Network Code to set up a network between 2 players
 * 
 * @author jfischer
 */
public class Network {
	
	private static int chatPortNum = 52000;
	private static int gamePortNum = 52001;
	private static int backupChatPort = 54322;
	private static int backupGamePort = 54323;
	private static HumanPlayer HPlayer = null;
	private static NetworkPlayer NPlayer = null;
	private static boolean exists = false;
	
	/**
	 * Essentially a constructor for creating the proper network type.
	 * 
	 * @param serverOrClient Create Either a Server (1) or a Client (2)
	 * @param HPlayer The Human Player in this network
	 * @param NPlayer The Network Player in this network
	 */
	public void createNetwork(int serverOrClient, HumanPlayer HPlayer, NetworkPlayer NPlayer) {
		Network.HPlayer = HPlayer;
		Network.NPlayer = NPlayer;
		if(serverOrClient == 1) {
			tcpServer();
		} else if (serverOrClient == 2) {
			tcpClient();
		}
		exists = true; //Set the exists variable when the server or client is created.
	}
	
	/**
	 * Return the boolean variable "exists", True if the network
	 * currently exists, False if the network does not exist.
	 * 
	 * @return The boolean variable "exists"
	 */
	public static boolean networkExists() {
		return exists;
	}
	
	/**
	 * Create a Tcp Server for the Chat and Gameplay Threads
	 */
	private static void tcpServer() {
		ServerSocket servChatSocket = null;
		ServerSocket servGameSocket = null;
		
		//Create a new Server Socket for Chat on the specified port
		try {
			servChatSocket = new ServerSocket(chatPortNum);
		} catch(IOException ioe) {
			System.err.println("Could not create server on port: " + chatPortNum);
			System.err.println(ioe);
			ioe.printStackTrace();
			try {
				servChatSocket = new ServerSocket(backupChatPort);
				System.out.println("Chat Socket: " + backupChatPort);
			} catch (IOException ioe1) {
				System.err.println("Could not create server on port: " + backupChatPort);
				System.err.println(ioe1);
			}
		}
		
		System.out.println("Chat Server Started on LocalHost Port Number: " + 
				servChatSocket.getLocalPort());
		
		//Create a new Server Socket for Game Data on the specified port
		try {
			servGameSocket = new ServerSocket(gamePortNum);
		} catch(IOException ioe) {
			System.err.println("Could not create server on port: " + gamePortNum);
			System.err.println(ioe);
			ioe.printStackTrace();
			try {
				servGameSocket = new ServerSocket(backupGamePort);
				System.out.println("Game Socket: " + backupGamePort);
			} catch (IOException ioe1) {
				System.err.println("Could not create server on port: " + backupGamePort);
				System.err.println(ioe1);
			}
		}
		
		System.out.println("Game Server Started on LocalHost Port Number: " + 
				servGameSocket.getLocalPort());
		
		System.out.println("Listening for Client Connection...");
		
		//Accept connections from the Network User and create Chat and Game Data Threads.
		while(true){
			try{
				//Note that this thread (The Main Thread) will block with each .accept() call
				Socket chatSocket = servChatSocket.accept();
				System.out.println("Chat Connection Established");
				Socket gameSocket = servGameSocket.accept();
				System.out.println("Game Data Connection Established");
				System.out.println("Connected with: " + chatSocket.getInetAddress());
				new TcpChat(chatSocket).start();
				new TcpGame(gameSocket, HPlayer, NPlayer).start();
				break;
			} catch(IOException ioe) {
				System.err.println("Error Accepting Connections");
				System.err.println(ioe);
				ioe.printStackTrace();
				try {
					servChatSocket.close();
					servGameSocket.close();
				} catch (IOException ioe1) {
					System.err.println("Error Aborting Connection");
					System.err.println(ioe1);
					ioe1.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Create a Tcp Client for the Chat and Game Data
	 */
	private static void tcpClient(){
		Scanner input = new Scanner(System.in);
		int ip[] = new int[] {-1, -1, -1, -1};
		String ipString = null;
		Socket chatSocket = null;
		Socket gameSocket = null;
		InetAddress servAddress = null;
		
		//Get the IP to connect to
		System.out.println("Enter the IP Number seperated by Return Presses (0 - 255)");
		System.out.println("LocalHost is 127.0.0.1");
		for(int i=0; i<4; i++){
			while(ip[i] < 0 || ip[i] > 255){
				try{
					System.out.print("Next: ");
					ip[i] = input.nextInt();
				} catch(Exception e){
					System.err.println("Error with input: \"" + input.next() + "\"");
					System.out.println("Enter a number between 0-255");
				}
				if(ip[i] < 0 || ip[i] > 255){
					System.out.println("IP number out of range");
					ip[i] = -1;
				}
			}
		}
		
		//Must be initialized (there may be a better way to do this)
		ipString = Integer.toString(ip[0]);
		for(int i=1; i<4; i++){
			ipString = ipString + "." + Integer.toString(ip[i]);
		}
		
		try {
			servAddress = InetAddress.getByName(ipString);
		} catch (UnknownHostException uhe) {
			System.err.println("Could not resolve ip: " + ipString);
			System.err.println(uhe);
		}
		
		//Connect to the Chat Server with the input IP and specified Port
		//TODO: Needs to be able to connect to backup too
		System.out.println("Attempting to connect to chat server...");
		try {
			chatSocket = new Socket(servAddress, chatPortNum);
		} catch (UnknownHostException uhe) {
			System.err.println("Error, Unknown Host on Server: " + ipString + ": " + chatPortNum);
			try {
				chatSocket = new Socket(servAddress, backupChatPort);
				System.out.println("Connected to Backup Chat Port");
			} catch (Exception e) {
				System.err.println("Unable to Connect to Server");
				System.err.println(e);
			}
		} catch (IOException ioe) {
			System.err.println("Error, Could Not Open Streams to Server: " + ipString + ": " + chatPortNum);
			try {
				chatSocket = new Socket(servAddress, backupGamePort);
				System.out.println("Connected to Backup Game Port");
			} catch (Exception e) {
				System.err.println("Unable to Connect to Server");
				System.err.println(e);
			}
		}
		System.out.println("Connected...");
		System.out.println("Starting up Communication Thread");
		
		//Start Chat Thread
		try {
			new TcpChat(chatSocket).start();
		} catch(IllegalThreadStateException itse) {
			System.err.println("Could not Start Chat Threads");
			System.err.println(itse);
			try {
				chatSocket.close();
			} catch (IOException ioe1) {
				System.err.println("Could not Close Connection");
				System.err.println(ioe1);
			}
		}
		
		//Connect to the Game Server with the input IP and specified Port
		//TODO: Needs to be able to connect to backup too
		System.out.println("Attempting to connect to Game Server...");
		try {
			gameSocket = new Socket(servAddress, gamePortNum);
		} catch (UnknownHostException uhe) {
			System.err.println("Error, Could Not Connect to Server: " + ipString);
			try {
				chatSocket = new Socket(servAddress, backupGamePort);
				System.out.println("Connected to Backup Game Port");
			} catch (Exception e) {
				System.err.println("Unable to Connect to Server");
				System.err.println(e);
			}
		} catch (IOException ioe) {
			System.err.println("Error in Input Output Connection: " + ipString);
			try {
				chatSocket = new Socket(servAddress, backupGamePort);
				System.out.println("Connected to Backup Game Port");
			} catch (Exception e) {
				System.err.println("Unable to Connect to Server");
				System.err.println(e);
			}
		}
		System.out.println("Connected...");
		System.out.println("Starting up Game Thread");
		
		//Start Game Thread
		try {
			new TcpGame(gameSocket, HPlayer, NPlayer).start();
		} catch(IllegalThreadStateException itse) {
			System.err.println("Could not Start Game Thread");
			System.err.println(itse);
			try {
				gameSocket.close();
			} catch (IOException ioe) {
				System.err.println("Could not Close Connection");
				System.err.println(ioe);
			}
		}
	}
}

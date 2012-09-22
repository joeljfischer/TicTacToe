/**
 * BoardGui.java (Tic-Tac-Toe Eclipse Project)
 * 
 * by: Joel Fischer
 */

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

/**
 * @author jfischer
 *
 */

public class BoardGui {
	
	//Game GUI Components
	private static JFrame mainWindow = null;
	private static JPanel mainPane = null;
	private static JPanel gameWindow = null;
	private static JPanel chatWindow = null;
	private static JPanel gameBoard = null;
	private static JLabel status = null;
	private static JTextArea chatText = null;
	private static JTextField textField = null;
	private static JButton[] mark = new JButton[9];
	
	//GUI Variables
	private static String inputString = null;
	private static int markedSpace = 0;
	private static boolean gameUpdated = false;
	private static boolean chatUpdated = false;
	public static char playerMark = ' ';
	public static boolean playerTurn = false;
	
	/**
	 * GUI Constructor. Builds the GUI.
	 */
	public BoardGui(String title) {
		//Create a new frame
		mainWindow = new JFrame("Tic-Tac-Toe: " + title);
		
		//Create the Buttons for the Game Board
		gameWindow = new JPanel(new BorderLayout());
		gameBoard = new JPanel(new GridLayout(3,3)); //Create a 3x3 grid
		//Create the buttons individually and create an Action Listener for clicking on them
		for(int i=0; i<9; i++){
			mark[i] = new JButton(Integer.toString(i+1));
			mark[i].addActionListener(new ButtonListener());
			gameBoard.add(mark[i]);
			mark[i].setEnabled(false); //Disable until it is your turn.
		}
		
		//Create the status label
		status = new JLabel();
		status.setText("Setting Up Game...");
		
		//Package the Board and Status into a Pane
		gameWindow.add(status, BorderLayout.NORTH);
		gameWindow.add(gameBoard, BorderLayout.CENTER);
		gameWindow.setPreferredSize(new Dimension(300,300)); //Set a start size for the board
		
		//Create the chat side
		//Create the Text Entry Field and add an Action Listener to detect when Enter is pressed
		chatWindow = new JPanel(new BorderLayout());
		textField = new JTextField(10); //Create a new Text Field for Chat
		textField.addActionListener(new EventAdapter() {
			public void actionPerformed(ActionEvent event) {
				String inText = textField.getText();
				setInputString(inText);
				chatUpdated = true;
			}
		});
		textField.setEditable(false); //Don't allow the Text Entry Field to be used by default
		chatText = new JTextArea(20, 20); //Create a new Text Area for Chat (20 long, 20 wide)
		chatText.setLineWrap(true); //Allow for word wrapping in the Chat Area
		chatText.setWrapStyleWord(true);
		chatText.setEditable(false); //Never allow the Text Area to be modified
		DefaultCaret chatCaret = (DefaultCaret)chatText.getCaret(); //Implement Chat Area autoscrolling
		chatCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scrollPane = new JScrollPane(chatText, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); //Allow scrolling
		//Pack the Text Entry Field and Chat Area into a Pane
		chatWindow.add(textField, BorderLayout.NORTH);
		chatWindow.add(scrollPane, BorderLayout.CENTER);
		chatWindow.setPreferredSize(new Dimension(350,300));
		
		//Set the two sides to the main pane
		mainPane = new JPanel(new BorderLayout());
		mainPane.add(chatWindow, BorderLayout.EAST);
		mainPane.add(gameWindow, BorderLayout.CENTER);
		
		//Finalize and display the GUI
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setContentPane(mainPane);
		mainWindow.setSize(mainWindow.getPreferredSize());
		mainWindow.pack();
		mainWindow.setVisible(true);
	}
	
	/**
	 * Add text to the chat area
	 * 
	 * @param text Text to be appended to the Chat Text Area
	 */
	public static void updateChat(final String text){
		synchronized (chatText){
			chatText.append(text + "\n");
		}
	}
	
	/**
	 * Clear all text in the Chat Text Area
	 */
	public static void clearChat(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				chatText.setText(""); //Set the entire chat area to an empty string
			}
		});
	}
	
	/**
	 * Properly destroy everything in the GUI
	 */
	public static void destroy(){
		mainWindow.setVisible(false);
		mainWindow.dispose();
	}
	
	/**
	 * Set the Text Entry Field to the input
	 * 
	 * @param input Text to be set into the entry field
	 */
	public static void setChatTextField(String input) {
		textField.setText(input);
	}
	
	/**
	 * Allow the text field to be editable. Only change it if the entry is different from
	 * the current setting.
	 * 
	 * @param editable Whether or not to allow the text entry field to be edited
	 */
	public static void setChatEditable(boolean editable) {
		if(isChatEditable() && !editable)
			textField.setEditable(editable);
		else if(!isChatEditable() && editable)
			textField.setEditable(editable);
	}
	
	/**
	 * Get data on whether the Chat Entry Field is editable
	 * @return
	 */
	public static boolean isChatEditable() {
		return textField.isEditable();
	}
	
	/**
	 * Set the Game Status Label Field
	 * 
	 * @param text Text to set to the status label
	 */
	public static void setStatus(String text) {
		status.setText(text);
	}
	
	/**
	 * Whether or not to enable to board (allow selections)
	 * 
	 * @param enabled
	 */
	public static void setBoardEnabled(boolean enabled) {
		for(int i=0; i<9; i++) {
			if(mark[i].getText() != Integer.toString(i)){
				mark[i].setEnabled(enabled);
			}
		}
	}
	
	/**
	 * Set the AI's choice into the board
	 * 
	 * @param slot The board slot to set into the board
	 */
	public static void setAiOrNetworkChoice(int slot, char playerMark) {
		mark[slot].setText(Character.toString(playerMark));
	}
	
	/**
	 * Return the Frame that allows to create a popup window
	 * 
	 * @return The Main Frame
	 */
	public static JFrame getPopupFrame() {
		return mainWindow;
	}
	
	/**
	 * 
	 * @param inputString
	 */
	public static void setInputString(String inputString){
		BoardGui.inputString = inputString;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getInputString(){
		return inputString;
	}
	
	/**
	 * @param gameUpdated Set the boolean gameUpdated
	 */
	public static void setGameUpdated(boolean gameUpdated) {
		BoardGui.gameUpdated = gameUpdated;
	}
	
	/**
	 * @return The gameUpdated variable
	 */
	public static boolean getGameUpdated() {
		return gameUpdated;
	}
	
	/**
	 * @param chatUpdated Set the boolean chatUpdated
	 */
	public static void setChatUpdated(boolean chatUpdated) {
		BoardGui.chatUpdated = chatUpdated;
	}
	
	/**
	 * @return The chatUpdated variable
	 */
	public static boolean getChatUpdated() {
		return chatUpdated;
	}

	/**
	 * @param markedSpace Set the boolean markedSpace
	 */
	public static void setMarkedSpace(int space) {
		markedSpace = space;
	}

	/**
	 * @return The markedSpace variable
	 */
	public static int getMarkedSpace() {
		return markedSpace;
	}
	
	/**
	 * @author jfischer
	 * 
	 * The Button Listener which allows Event Handling with the Game Board Buttons
	 */
	static class ButtonListener implements ActionListener {
		/**
		 * Overridden Function Action Performed (Button Pressed) contains the code
		 * that will be executed if one of the game buttons is pressed.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			//Run through each button
			for(int i=0; i<9; i++){
				//Check which button was pressed
				if(event.getSource() == mark[i]){
					//If the button has not yet been pressed (it is not marked)
					if(mark[i].getText().equals(Integer.toString(i+1))){
						mark[i].setText(Character.toString(playerMark)); //Mark the space
						setMarkedSpace(i); //Tell the game that this slot was selected
						gameUpdated = true; //Tell the game that a slot was selected
					}
				}
			}
		}
	}
	
	/**
	 * @author jfischer
	 *
	 * Essentially an abstract class that allows for it's sole function to
	 * be overridden and used by the Text Entry field.
	 */
	class EventAdapter implements ActionListener {
		/**
		 * Override this function to gain functionality
		 */
		@Override
		public void actionPerformed(ActionEvent event) {}
	}
}

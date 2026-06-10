import java.awt.event.*;
import javax.swing.SwingUtilities;

/**
 * The Controller class for the Uno game.
 * It manages the game flow, connects the data (Model) with the screen (View),
 * and handles network communication between players.
 */
public class UnoController implements ActionListener{
	//Properties
	/** Reference to the data of the state (model)*/
	private UnoModel model;
	/** Reference to the graphics UI and screen (view)*/
	private UnoView view;
	/** Reference to networking socket provider (ssm)*/
	private UnoNetwork network;
	
	// network varaible
	/** Default network connection port number*/
	private static final int intDefaultPort = 5555;
	/** Identifies if application/this player acts as host server*/
	private boolean blnIsHost = false;
	/** Identifies if a stable/consistent network establish*/
	private boolean blnConnected = false;
	/** Track if the core gameplay has started*/
	private boolean blnGameStarted = false;
	/** Track if the connecting client player if fully loaded and ready to begin*/
	private boolean blnJoinerReady = false;
	/**Unique client player index bound specifically to the local interface*/
	private int intLocalPlayerIndex = 0;
	
	//Methods
	/** Responds to system actions and custom event triggers.
		Filters incoming messages.
		The associated event triggered by user interactions or components.
	*/
	public void actionPerformed(ActionEvent evt){
		if(evt.getActionCommand() != null && evt.getActionCommand().equals("UNO_NETWORK_MESSAGE")){
			String strMessage = network.getLastMessage();
			handleNetworkMessage(strMessage);
		}
	}
	
	/**Get the index of the player associated with local player.*/
	public int getLocalPlayerIndex(){
		return intLocalPlayerIndex;
	}
	/*
	public void handleNetworkMessage(String strMessage){
		System.out.println("Network Message: " + strMessage);
		
		if(view != null){
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					view.processNetworkMessage(strMessage);
				}
			});
		}
	}
	
	*/
	
	/** Parse message received from network.
		Process and print to the view, printing the message on the screen.
		Connect message and graphics (chatbox)
	*/
	public void handleNetworkMessage(String strMessage){
		System.out.println("Network Message: " + strMessage);
		
		if(strMessage.startsWith("READY|")){
			if(blnIsHost){
				blnJoinerReady = true;
				if(blnGameStarted){
					sendCurrentSetup();
				}
			}
		}
		
		if(view != null){
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					view.processNetworkMessage(strMessage);
				}
			});
		}
	}
	
	/** Assemble the current card values, hand states, active discard, formatting them into setup messages sent directly across to connected player.*/
	private void sendCurrentSetup(){
		if(network != null){
			String[] strDiscardTop = model.getTopDiscard();
			
			if(strDiscardTop == null){
				strDiscardTop = new String[]{"", "", "", ""};
			}
			
			network.sendGameSetup(model.strHands, model.intHandSizes, model.intTurnOrder, strDiscardTop);
		}
	}
	
	/** Identify if game is successfully host with stable connection*/
	public boolean hostGame(){
		blnIsHost = true;
		blnConnected = network.startServer(intDefaultPort);
		return blnConnected;
	}
	
	/** Connects this computer to a hosted game server using the host's IP address.
	 * strIP: The network IP address of the host computer.
	 * return True if the connection was successful; false otherwise.
	 */
	public boolean joinGame(String strIP){
		blnIsHost = false;
		intLocalPlayerIndex = 1; 
		blnConnected = network.connectServer(strIP, intDefaultPort);
		return blnConnected;
	}
	
	/** Sends a chat message typed by the local player out to the network.
	 * strPlayerName: The name of the player sending the chat.
	 * strMessage: The text message content to send.
	 */
	public void sendChat(String strPlayerName, String strMessage){
		if(network != null){
			network.sendPlayerChat(strPlayerName, strMessage);
		}
	}
	/*
	public void startGame(String strPlayerName){
		model.strPlayerNames[0] = strPlayerName;
		model.startGame();
		
		if(network != null){
			network.sendJoin(strPlayerName);
			if(blnIsHost){
				String[] strDiscardTop = model.getTopDiscard();
				if(strDiscardTop == null){
					strDiscardTop = new String[]{"", "", "", ""};
				}
				network.sendGameSetup(model.strHands, model.intHandSizes, model.intTurnOrder, strDiscardTop);
			}
		}
	}
	*/
	
	/** Starts the Uno match. 
	 * If the player is a guest (Joiner), it pre-loads the card deck image data and tells the host it is ready.
	 * If the player is the Host, it shuffles and deals the deck, sets up the starting game state, and 
	 * transmits this layout data directly to the guest if they are ready.
	 * strPlayerName: The name chosen by the player on this computer.
	 * return True if the game successfully started as a host; false if you are the guest or not connected.
	 */
	public boolean startGame(String strPlayerName){
		if(!blnConnected){
			return false;
		}
		
		model.strPlayerNames[0] = strPlayerName;
		
		if(!blnIsHost){
			// Joiner: load the deck so card lookups work when SETUP arrives
			model.loadDeck();
			if(network != null){
				network.sendJoin(strPlayerName);
				network.send("READY|" + strPlayerName);
			}
			return false;
		}
		
		// Host: full game setup
		model.startGame();
		blnGameStarted = true;
		
		if(network != null){
			network.sendJoin(strPlayerName);
			if(blnJoinerReady){
				sendCurrentSetup();
			}
		}
		
		return true;
	}
		
	/** Checks if this computer application is acting as the game host server.
	 * return True if hosting; false if joining a host.
	 */
	public boolean isHost(){
		return blnIsHost;
	}
	
	//Controller
	/** Constructor. Initializes the game data (Model), screen interface (View), and 
	 * the network helper. It links them together so they can interact seamlessly.
	 */
	public UnoController(){
		model = new UnoModel();
		view = new UnoView();
		network = new UnoNetwork(this);
		
		// hand controller to view
		view.setController(this);
		view.setModel(model);
		view.setNetwork(network);
		
	}
	/** The main entry point of the application. 
	 * Starts the entire Uno program.
	 */
	public static void main(String[] args){
		new UnoController();
	}
	
}

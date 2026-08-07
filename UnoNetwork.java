
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**The UnoNetwork class handles the online multiplayer system. 
 * It connects players as hosts or guests, cleanly disconnects them, 
 * and sends/receives text data to keep everyone's game synced up for turns, setup, and chat.
 * Arthur: Chloe Lau and Sydney Khang
 * Version: 1.0
*/
public class UnoNetwork implements ActionListener{
	/** The callback listener notified when a network event fires. */
	private ActionListener gameListener = null;
	/** Stores the raw string received from the last network operation. */
	private String strLastMessage = "";
	/** The active socket controller that manages server/client networking. */
	private NetworkSocketController socketController = null;

	//Methods
	/**Automatically triggers when new network data arrives. 
	 * It grabs the incoming text and passes it along to the main game controller 
	 * so the game can respond to the player's action.
	 * evt: triggered ActionEvent originating from socket
	*/
	public void actionPerformed(ActionEvent evt){
		if(evt.getSource() == socketController){
			strLastMessage = socketController.readText();
			if(gameListener != null){
				gameListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "UNO_NETWORK_MESSAGE"));
			}
		}
	}
		
	/**
	 * Grabs the very last text message that came in over the internet.
	 * Returns the last text message received from the network.
	*/
	public String getLastMessage(){
		return strLastMessage;
	}
	
	//starting server
	/**
	 * Turns local player's computer into the game host (the server) 
	 * on a specific port number so other players can join the local player's game.
	 * Returns true if it worked, and false if something went wrong.
	*/
	public boolean startServer(int intPort){
		socketController = new NetworkSocketController(null, intPort, this);
		return socketController.startServer();
	}
	
	/**
	 * Takes the results of a player's turn
	 * and blasts that information to everyone else in the game using a specific text code.
	 * strCardPlayed: data of teh last card played
	 * strWildColor: chosen wild color (if any)
	 * intHandSize: amount of cards remain in everyone's hand
	*/
	public void sendTurnUpdate(String strCardPlayed, String strWildColor, int[] intHandSizes, int intNextTurn){
		if(socketController != null){
			String strSizes = intHandSizes[0] + "," + intHandSizes[1] + "," + intHandSizes[2];
			socketController.sendText("TURN|" + strCardPlayed + "|" + strWildColor + "|" + strSizes  + "|" + intNextTurn);
		}
	}
	
	//connecting users to server
	/**
	 * Connects a guest player (the client) to a host's game using the host's IP address and port number. 
	 * Returns true if the connection was successful, and false if it failed.
	*/
	public boolean connectServer(String strIPadress, int intPort){
		socketController = new NetworkSocketController(strIPadress, intPort,this);
		return socketController.startClient();
	}
	
	//disconnecting user from network
	/**
	 * Safely disconnects the player from the online game and cleans up the network connection
	*/
	public void userDisconnect(){
		if(socketController != null){
			socketController.disconnect();
			socketController = null;
		}
	}
	
	//Different Types of Messages sent through network: 
	//player to player chat message
	/**
	 * Takes a chat message you typed, attaches your username to it, 
	 * and sends it over the internet so all the other players can see it on their screens
	 * strPlayerName: screen display name of sending player
	 * strMessage: literal text chat message to transmit
	*/
	public void sendPlayerChat(String strPlayerName, String strMessage){
		if(socketController != null){
			socketController.sendText("[CHAT] " + strPlayerName + ": "+strMessage);
		}
	}
	
	//Game Messages:
	//joining game message
	/**
	 * Sends an alert to everyone in the game letting them know a new player has joined, passing along that player's name.
	 * strPlayerName: The username of the new player who just joined.
	*/
	public void sendJoin(String strPlayerName){
		if(socketController != null){
			socketController.sendText("[GAME MESSAGE] "+strPlayerName+ " has joined");
		}
	}
	
	//leaving game message
	/**
	 * Sends an alert to everyone in the game letting them know 
	 * a player has disconnected, passing along that player's name.
	 * strPlayerName: The username of the player who just left.
	*/
	public void sendLeft(String strPlayerName){
		if(socketController != null){
			socketController.sendText("[GAME MESSAGE] "+strPlayerName +  " has left");
		}
	}

	//selecting theme message
	/**
	 * Sends a network update letting everyone know that a player has switched their visual game theme
	 * strPlayerName: The username of the player who changed their settings.
	 * strThemeName: The name of the specific theme they chose
	*/
	public void sendTheme(String strPlayerName, String strThemeName){
		if(socketController != null){
			socketController.sendText("[GAME MESSAGE] "+strPlayerName + " SELECTED: " + strThemeName );
		}
	}
	
	//draw card message
	/**
	 * Sends a network update letting everyone know that a specific player just drew a card from the deck.
	 * strPlayerName: The username of the player who drew the card.
	*/
	public void sendCardDrawn(String strPlayerName){
		if(socketController != null){
			socketController.sendText("[GAME MESSAGE] "+strPlayerName + " has drawn a card");
		}
	}
	
	//normal card played message
	/**
	 * Sends a network update letting everyone know that a specific player just played a standard card onto the discard pile, 
	 * specifying who did it and which card they used.
	 * strPlayerName: The username of the player who played the card
	 * strCardName: The text code or ID identifying exactly which card was played
	*/
	public void sendPlayerCard(String strPlayerName, String strCardName){
		if(socketController != null){
			socketController.sendText("[GAME MESSAGE] "+strPlayerName + " played "+ strCardName);
		}
	}
	
	//Player attack/special move message
	/**
	 * Sends a network update letting everyone know that Player A just used a penalty card (like a Draw 2) against Player B.
	 * strPlayerName: The username of the aggressive player who played the penalty card.
	 * strAttackType: The type of penalty card used
	 * strTargetName: The username of the unfortunate "victim" who has to receive the penalty.
	*/
	public void sendPlayerAttack(String strPlayerName, String strAttackType, String strTargetName){
		if(socketController != null){
			socketController.sendText("[GAME MESSAGE] "+strPlayerName + " attacked "+ strTargetName + ": "+strAttackType);
		}
	}
	
	//colour change card message
	/**
	 * Sends a network update letting everyone know that a specific player used a Wild Card and changed the active game color.
	 * strPlayerName: The username of the player who played the Wild Card.
	 * strColour: The new color they selected
	*/
	public void sendColourChange(String strPlayerName, String strColour){
		if(socketController != null){
			socketController.sendText("[GAME MESSAGE] "+strPlayerName + " changed colour to: "+ strColour);
		}
	}
	
	//send game over message
	/**
	 * Sends a final network update to all players announcing that the game is over and declaring the winner.
	 * strWinner: The username of the player who won the game.
	*/
	public void sendGameOver(String strWinner){
		if (socketController != null){
			socketController.sendText("GAMEOVER: " + strWinner + " has won!!!");
		}
	}

	// send elimination message
	public void sendElimination(int intPlayerIndex, String strPlayerName){
		if(socketController != null){
			socketController.sendText("ELIM|" + intPlayerIndex + "|" + strPlayerName);
		}
	}

	// send UNO reached message
	/** Announces that a player has reached UNO (one card remaining). */
	public void sendReachedUno(String strPlayerName){
		if(socketController != null){
			socketController.sendText("[GAME MESSAGE] " + strPlayerName + " has reached UNO!");
		}
	}
	
	// send game setup (dealt hands + turn order) 
	/**
	 * Gathers all initial matchmaking data,
	 * formats it into a single text string, 
	 * and broadcasts it so all players start the match perfectly synced.
	 * strHands: The master list containing all the cards dealt to every single player.
	 * intHandSizes: A list of numbers tracking exactly how many cards each player is starting with.
	 * intTurnOrder: The randomized seating order determining who gets to play first, second, third, and so on.
	 * strDiscardTop: The name/ID of the very first card turned face-up on the table to start the game.
	 */
	public void sendGameSetup(String[][][] strHands, int[] intHandSizes, int[] intTurnOrder, String[] strDiscardTop,String[] strPlayerNames){
		if(socketController != null){
			// Each card is encoded as cardName
			String strMsg = "SETUP|";
			
			for(int p = 0; p < 3; p++){
				for(int c = 0; c < intHandSizes[p]; c++){
					strMsg += strHands[p][c][0];
					if(c < intHandSizes[p] - 1) strMsg += ",";
				}
				strMsg += "|";
			}
			
			for(int i = 0; i < 3; i++){
				strMsg += intTurnOrder[i];
				if(i < 2) strMsg += ",";
			}
			strMsg += "|";
			// card name of top of dicard
			strMsg += strDiscardTop[0];
			
			strMsg += "|";
			
			 // NEW: append the three player names
			for(int i = 0; i < 3; i++){
				strMsg += strPlayerNames[i];
				if(i < 2) strMsg += ",";
			}
				
			socketController.sendText(strMsg);
		}
	}
	
	/**
	 * A backup function that lets developers manually send any custom text message across the network, useful for unique game events or debugging.
	 * strMessage: The exact piece of text you want to transmit over the network.
	*/
	public void send(String strMessage){
		if(socketController != null){
			socketController.sendText(strMessage);
		}
	}

	//Constructor
	/**
	 * UnoNetwork constructor creates the network manager and immediately hooks it up to the gameListener
	 * so it knows exactly where to send incoming internet messages
	 * gameListener: The specific part of the main game engine assigned to listen to the network, crack open the text codes, and update the match.
	*/
	public UnoNetwork(ActionListener gameListener){
		this.gameListener = gameListener;
	}
	
	private void notifyListener(){
		if(gameListener != null){
			gameListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "UNO_NETWORK_MESSAGE"));
		}
	}
	
	private class NetworkSocketController implements Runnable{
		private final String strServerHost;
		private final int intPort;
		private final UnoNetwork owner;
		private ServerSocket serverSocket = null;
		private Socket clientSocket = null;
		private BufferedReader inBuffer = null;
		private PrintWriter outBuffer = null;
		private String strIncomingText = "";
		private volatile boolean blnRunning = false;
		private final List<ClientConnection> clients = new ArrayList<ClientConnection>();
		private Thread listenerThread = null;
		
		private NetworkSocketController(String strServerHost, int intPort, UnoNetwork owner){
			this.strServerHost = strServerHost;
			this.intPort = intPort;
			this.owner = owner;
		}
		
		public boolean startServer(){
			try{
				serverSocket = new ServerSocket(intPort, 50, InetAddress.getByName("0.0.0.0"));
				blnRunning = true;
				listenerThread = new Thread(this);
				listenerThread.start();
				return true;
			}catch(Exception e){
				return false;
			}
		}
		
		public boolean startClient(){
			try{
				clientSocket = new Socket(strServerHost, intPort);
				inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				outBuffer = new PrintWriter(clientSocket.getOutputStream(), true);
				blnRunning = true;
				listenerThread = new Thread(this);
				listenerThread.start();
				return true;
			}catch(IOException e){
				return false;
			}
		}
		
		public String readText(){
			return strIncomingText;
		}
		
		public void run(){
			if(serverSocket != null){
				acceptLoop();
			}else if(clientSocket != null){
				readLoop();
			}
		}
		
		private void acceptLoop(){
			while(blnRunning){
				try{
					Socket acceptedSocket = serverSocket.accept();
					ClientConnection connection = new ClientConnection(acceptedSocket);
					synchronized(clients){
						clients.add(connection);
					}
					Thread thread = new Thread(new ClientReader(connection));
					thread.start();
				}catch(IOException e){
					if(!blnRunning){
						break;
					}
				}
			}
		}
		
		private void readLoop(){
			while(blnRunning){
				try{
					String strLine = inBuffer.readLine();
					if(strLine != null){
						strIncomingText = strLine;
						owner.notifyListener();
					}
				}catch(IOException e){
					break;
				}
			}
			disconnect();
		}
		
		public boolean sendText(String strText){
			if(serverSocket != null){
				synchronized(clients){
					for(ClientConnection client : clients){
						if(client != null && client.writer != null){
							client.writer.println(strText);
						}
					}
				}
				return true;
			}
			if(clientSocket != null && outBuffer != null){
				outBuffer.println(strText);
				return true;
			}
			return false;
		}
		
		public void disconnect(){
			blnRunning = false;
			synchronized(clients){
				for(ClientConnection client : clients){
					try{
						if(client.socket != null) client.socket.close();
					}catch(IOException e){
					}
				}
				clients.clear();
			}
			try{
				if(serverSocket != null){
					serverSocket.close();
				}
			}catch(IOException e){
			}
			try{
				if(clientSocket != null){
					clientSocket.close();
				}
			}catch(IOException e){
			}
			serverSocket = null;
			clientSocket = null;
			inBuffer = null;
			outBuffer = null;
		}
		
		private void handleIncomingText(String strText, ClientConnection sender){
			if(strText == null || strText.trim().equals("")){
				return;
			}
			strIncomingText = strText;
			if(serverSocket != null){
				synchronized(clients){
					for(ClientConnection client : clients){
						if(client != sender && client.writer != null){
							client.writer.println(strText);
						}
					}
				}
			}
			owner.notifyListener();
		}
		
		private class ClientReader implements Runnable{
			private final ClientConnection connection;
			
			private ClientReader(ClientConnection connection){
				this.connection = connection;
			}
			
			public void run(){
				try{
					while(blnRunning){
						String strLine = connection.reader.readLine();
						if(strLine == null){
							break;
						}
						handleIncomingText(strLine, connection);
					}
				}catch(IOException e){
				}
				cleanupConnection(connection);
			}
		}
		
		private void cleanupConnection(ClientConnection connection){
			synchronized(clients){
				clients.remove(connection);
			}
			try{
				if(connection.socket != null){
					connection.socket.close();
				}
			}catch(IOException e){
			}
		}
		
		private class ClientConnection{
			private final Socket socket;
			private BufferedReader reader;
			private PrintWriter writer;
			
			private ClientConnection(Socket socket){
				this.socket = socket;
				try{
					this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					this.writer = new PrintWriter(socket.getOutputStream(), true);
				}catch(IOException e){
					this.reader = null;
					this.writer = null;
				}
			}
		}
	}
}


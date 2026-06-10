import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

import java.util.Enumeration;
import java.net.*;

/**
 * The View class for the Uno game.
 * It builds the main graphical user interface (GUI) window using Java Swing,
 * displays the player's cards, handles visual animations, and sets up the screen layout.
 * It implements listeners to catch user interactions via mouse clicks, keyboard presses, and timers.
 */
public class UnoView extends JPanel implements ActionListener, MouseListener, KeyListener{
	// Properties
	/** The width of the game window in pixels (1280). */
	final int intWidth = 1280;
	/** The height of the game window in pixels (720). */
	final int intHeight = 720;
	/** The maximum number of cards a player can hold in their hand at once (30). */
	final int intMaxCards = 30;
	// final int intMaxCards = 30;
	/** The number of cards dealt to each player at the start of a match (7). */
	final int intStartCards = 7;
	/** Grouping variable to track how many items display per page view. */
	final int intPerPage = 2;

	// JFrame 
	/** The main operating system desktop window frame containing the Uno game display canvas. */
	JFrame theFrame = new JFrame("UNO");
	
	// connect files
	/** Reference to the controller logic layer that captures and routes player decisions. */
	UnoController controller;
	/** Reference to the data core model containing deck matrices and player turn variables. */
	UnoModel model;
	/** Reference to the backend network socket */
	UnoNetwork network;
	
	// Deck & Cards Array 
	// 100 cards in a deck
	/** A master list storing details for all 100 cards included in a full standard deck. */
	String[][] strDeck = new String[100][4];
	/** Holds the current cards left in the face-down pile that players draw from. */
	String[][] strDrawPile = new String[100][4];
	/** Holds the cards that players have already played, stacked on top of each other face-up. */
	String[][] strDiscardPile = new String[100][4];
	/** Stores the actual cards currently held in the local player's visible hand. */
	String[][] strPlayHand = new String[30][4];
	
	// Card Counts in each pile
	/** Tracks exactly how many cards remain inside the original deck array. */
	int intDeckSize = 0;
	/** Tracks exactly how many cards remain available inside the draw pile. */
	int intDrawPileSize = 0;
	/** Tracks exactly how many cards inside the discard pile. */
	int intDiscardPileSize = 0;
	/** Tracks exactly how many cards the player holds. */
	int intHandSize = 0;
	
	// current page of cards shown on your turn screen
	/** Track the page of cards shown on the player's turn screen*/
	int intCardPage = 0;
	
	// last card dran from pile (display card screen)
	String[] strDrawnCard = null;
	
	// Player Data
	String[] strPlayNames = {"Player","Player 2", "Player 3"};
	int[] intTurnOrder = {0, 1, 2};
	int intCurrentTurn = 0;
	boolean blnClockwise = true;
	
	// transition
	int intFadeAlpha = 0;
	boolean blnFading = false;
	String strNextScreen = "";
	
	// Color
	Color transparentBlack = new Color(0, 0, 0, 200);
	Color transparentDark = new Color(0, 0, 0, 160);
	Color unoRed = new Color(220, 50, 50);
	Color unoBlue = new Color(30, 100, 200);
	Color unoGreen = new Color(30, 160, 80);
	Color unoYellow = new Color(230, 190, 30);
	Color goldColor = new Color(255, 200, 50);
	Color btnGold = new Color(240, 180, 20); // Button Main Color
	Color btnGoldDark = new Color(180, 130, 10); // Button Gold Color
	
	// Font
	Font titleFont = new Font("Georgia", Font.BOLD, 36);
	Font headerFont = new Font("Georgia", Font.BOLD, 22);
	Font subFont = new Font("Georgia", Font.BOLD, 18);
	Font bodyFont = new Font("Georgia", Font.PLAIN, 14);
	Font buttonFont = new Font("Georgia", Font.BOLD, 16);
	Font bigFont = new Font("Georgia", Font.BOLD, 72);
	Font cardFont = new Font("Georgia", Font.BOLD, 20);	

	// Screen Boolean Variables
	boolean blnEnterScreen = true; // Start screen
	boolean blnPlayScreen = false; // Main menu
	boolean blnThemeScreen = false; // Enter name & theme
	boolean blnWaitScreen = false; // wait screen
	boolean blnPickCard = false; // pick card screen
	boolean blnDisplayCard = false; // show card drawn screen
	boolean blnTurnScreen = false; // your turn screen
	boolean blnGameOver = false; // game over screen
	boolean blnHelp = false; // help screen
	boolean blnLeaderBoard = false; // leaderboard screen
	boolean blnChat = false; // chat screen
	boolean blnFlipFirst = false; // show first card
	boolean blnEliminated = false; // local player eliminated
	boolean blnWildPicker = false;
	boolean blnNetworkReady = false;
	
	// elimination tracking
	boolean[] blnPlayerEliminated = {false, false, false};
	int intActivePlayers = 3;
	
	// wild card chosen color
	String strWildColor = "";
	
	// Image Variables
	BufferedImage imgStart = null;
	BufferedImage imgBackground = null;
	BufferedImage imgWait = null;
	BufferedImage imgPickCard = null;
	BufferedImage imgDisplay = null;
	BufferedImage imgYourTurn = null;
	BufferedImage imgGameOver = null;
	BufferedImage imgDecision = null;
	BufferedImage imgEliminated = null;
	BufferedImage[] imgAllCards = new BufferedImage[100];
	
	// Game Data
	String strName = "Player";
	String strWinner = "";
	int intTheme = 0; // Standard = 0. Pokemon = 1. InsideOut = 2.
	
	// Player Card Count
	int intCardCount1; // player 1
	int intCardCount2; // player 2
	int intCardCount3; // player 3
	
	// JComponent (Play Screen)
	JButton playButton = createGoldButton("PLAY");
	
	// JComponent (Theme)
	JButton btnStandard = createGoldButton("STANDARD");
	JButton btnPokemon = createGoldButton("POKEMON");
	JButton btnInsideOut = createGoldButton("INSIDEOUT");
	JTextField nameField = new JTextField();
	JButton btnEnterGame = createGoldButton("START GAME");
	
	// JComponent (Game)
	JButton btnHelp = createGoldButton("HELP");
	JButton btnPickUp = createGoldButton("PICK UP A CARD");
	JButton btnLeaderBoard = createGoldButton("LEADERBOARD");
	JButton btnChat = createGoldButton("CHAT");
	
	// JComponent (Chat)
	JButton btnHost = createGoldButton("HOST GAME");
	JButton btnJoin = createGoldButton("JOIN GAME");
	JTextField ipField = new JTextField("127.0.0.1");
	JTextField chatInput = new JTextField();
	JTextArea chatArea = new JTextArea();
	JScrollPane chatScroll = new JScrollPane(chatArea);
	
	// JComponent (game play)
	JButton btnPrev = createGoldButton("<PREV");
	JButton btnNext = createGoldButton("Next>");
	JButton btnContinue = createGoldButton("CONTINUE>");
	
	// JComponent (wild card color picker)
	JButton btnWildRed = new JButton("RED");
	JButton btnWildBlue = new JButton("BLUE"); 
	JButton btnWildGreen = new JButton("GREEN"); 
	JButton btnWildYellow = new JButton("YELLOW");
	
	// JComponent (Eliminated)
	JButton btnEliminatedOK = createGoldButton("CONTINUE WATCHING");
	
	// Action Listener
	/**
	 * Handles all button clicks and UI actions.
	 * This method checks which button was pressed and performs
	 * the correct action (e.g., starting the game, drawing a card,
	 * sending chat messages, switching screens).
	 *
	 * e: The ActionEvent triggered by a user action
	 */
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == playButton){
			fadeToScreen("theme");
		}else if(e.getSource() == btnStandard){
			intTheme = 0;
		}else if(e.getSource() == btnPokemon){
			intTheme = 1;
		}else if(e.getSource() == btnInsideOut){
			intTheme = 2;
		}else if(e.getSource() == btnEnterGame){
			String strTyped = nameField.getText().trim();
			// name cannot be empty
			if(!strTyped.equals("")){
				if(!blnNetworkReady){
					repaint();
				}else{
					strName = strTyped;
					strPlayNames[0] = strName;
					
					/*
					if(controller != null && controller.isHost()){
						// Only the host shuffles, deals, and broadcasts SETUP
						controller.startGame(strName);
						startGame();
					}else{
						// Joining player: just send the join message and wait for SETUP
						if(network != null){
							network.sendJoin(strName);
						}
						chatArea.append("[GAME MESSAGE] Waiting for host to start game...\n");
					}
					
					*/
					
					if(controller != null){
						boolean blnCanStartNow = controller.startGame(strName);

						if(blnCanStartNow){
							startGame();
						}else{
							chatArea.append("[GAME MESSAGE] Waiting for host setup...\n");
							// blnChat = true;
							// setComponentVisibility();
						}
					}
				}
			}
		}else if(e.getSource() == btnHelp){
			blnHelp = !blnHelp;
			blnLeaderBoard = false;
			blnChat = false;
			setComponentVisibility();
		}else if(e.getSource() == btnChat){
			blnChat = !blnChat;
			blnHelp = false;
			blnLeaderBoard = false;
			setComponentVisibility();
			if(blnChat){
				chatInput.requestFocus();
			}else{
				this.requestFocusInWindow();
			}
		}else if(e.getSource() == btnLeaderBoard){
			blnLeaderBoard = !blnLeaderBoard;
			blnHelp = false;
			setComponentVisibility();
		}else if(e.getSource() == btnPickUp){
			drawFromPile();
		}else if(e.getSource() == btnContinue){
			if(model != null){
				int intMyIdx = (controller != null) ? controller.getLocalPlayerIndex() : 0;
				model.intHandSizes[intMyIdx] = intHandSize;
			}
			if(network != null && model != null){
				network.sendTurnUpdate("draw", "", model.intHandSizes);
			}
			
			advanceTurn();
			
			//fadeToScreen("turn");
		}else if(e.getSource() == btnPrev){
			if(intCardPage > 0){
				intCardPage--;
			}
		}else if(e.getSource() == btnWildRed){
			strWildColor = "red";
			blnWildPicker = false;
			setComponentVisibility();
			
			if(model != null){
				int intMyIdx = (controller != null) ? controller.getLocalPlayerIndex() : 0;
				model.intHandSizes[intMyIdx] = intHandSize;
			}
			
			advanceTurn();
			if(network != null){
				network.sendTurnUpdate("wild", "red", model.intHandSizes);
			}
		}else if(e.getSource() == btnWildBlue){
			strWildColor = "blue";
			blnWildPicker = false;
			setComponentVisibility();
			
			if(model != null){
				int intMyIdx = (controller != null) ? controller.getLocalPlayerIndex() : 0;
				model.intHandSizes[intMyIdx] = intHandSize;
			}
			advanceTurn();
			if(network != null){
				network.sendTurnUpdate("wild", "blue", model.intHandSizes);
			}
		}else if(e.getSource() == btnWildGreen){
			strWildColor = "green";
			blnWildPicker = false;
			setComponentVisibility();
			
			if(model != null){
				int intMyIdx = (controller != null) ? controller.getLocalPlayerIndex() : 0;
				model.intHandSizes[intMyIdx] = intHandSize;
			}
			advanceTurn();
			if(network != null){
				network.sendTurnUpdate("wild", "green", model.intHandSizes);
			}
		}else if(e.getSource() == btnWildYellow){
			strWildColor = "yellow";
			blnWildPicker = false;
			setComponentVisibility();
			
			if(model != null){
				int intMyIdx = (controller != null) ? controller.getLocalPlayerIndex() : 0;
				model.intHandSizes[intMyIdx] = intHandSize;
			}
			advanceTurn();
			if(network != null){
				network.sendTurnUpdate("wild", "yellow", model.intHandSizes);
			}
		}else if(e.getSource() == btnEliminatedOK){
			blnEliminated = false;
			blnPlayerEliminated[0] = true;
			intActivePlayers--;
			fadeToScreen("wait");
		}else if(e.getSource() == btnNext){
			// Total pages = round up (handSize / cardsPerPage)
			int intTotalPages = (intHandSize + intPerPage - 1) / intPerPage;
			if(intTotalPages == 0) intTotalPages = 1;
			if(intCardPage < intTotalPages - 1){
				intCardPage++;
			}
		}else if(e.getSource() == nameField){
			String strTyped = nameField.getText().trim();
			if(!strTyped.equals("") && blnNetworkReady){
				strName = strTyped;
				strPlayNames[0] = strName;
				/*
				if(controller != null){
					controller.startGame(strName);
				}
				startGame();
				*/
				
				if(controller != null){
					boolean blnCanStartNow = controller.startGame(strName);

					if(blnCanStartNow){
						startGame();
					}else{
						chatArea.append("[GAME MESSAGE] Waiting for host setup...\n");
						blnChat = true;
						setComponentVisibility();
					}
				}
			}
		}else if(e.getSource() == btnHost){
			chatArea.append("[GAME MESSAGE] Waiting for player to join...\n");
			btnHost.setEnabled(false);
			btnJoin.setEnabled(false);
			// blnNetworkReady = true; 
			Thread hostThread = new Thread(new Runnable(){
				public void run(){
					boolean blnWorked = (controller != null) && controller.hostGame();
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							if(blnWorked){
								blnNetworkReady = true;
								String strHostIP = getLocalIPAddress(); // ← use the reliable method
								chatArea.append("[GAME MESSAGE] Player connected! Your IP is: " + strHostIP + "\n");
								chatArea.append("[GAME MESSAGE] Tell your partner to enter that IP and click JOIN GAME (port 5555)\n");
								ipField.setText(strHostIP);
							}else{
								chatArea.append("[GAME MESSAGE] Could not host game on port 5555.\n");
								chatArea.append("[GAME MESSAGE] On Windows, allow Java through Windows Defender Firewall (Private networks),\n");
								chatArea.append("[GAME MESSAGE] or run once as Administrator. Also confirm both PCs are on the same network.\n");
								
								//chatArea.append("[GAME MESSAGE] Could not host game. Port may already be in use.\n");
								blnNetworkReady = false;
								btnHost.setEnabled(true);
								btnJoin.setEnabled(true);
							}
							repaint();
						}
					});
				}
			});
			hostThread.start();
		}else if(e.getSource() == btnJoin){
			String strIP = ipField.getText().trim();
			if(!strIP.equals("") && !strIP.equals("Enter host IP")){
				chatArea.append("[GAME MESSAGE] Connecting to " + strIP + "...\n");
				btnHost.setEnabled(false);
				btnJoin.setEnabled(false);
				Thread joinThread = new Thread(new Runnable(){
					public void run(){
						boolean blnWorked = (controller != null) && controller.joinGame(strIP);
						SwingUtilities.invokeLater(new Runnable(){
							public void run(){
								if(blnWorked){
									blnNetworkReady = true;
									chatArea.append("[GAME MESSAGE] Connected to host: " + strIP + "\n");
									chatArea.append("[GAME MESSAGE] Enter your name and press START GAME\n");
									// blnChat = true;
									// setComponentVisibility();
								}else{
									chatArea.append("[GAME MESSAGE] Could not connect. Check the IP and try again.\n");
									btnHost.setEnabled(true);
									btnJoin.setEnabled(true);
								}
								repaint();
							}
						});
					}
				});
				joinThread.start();
			}else{
				chatArea.append("[GAME MESSAGE] Enter the host IP first.\n");
			}
			
			/*
			String strIP = ipField.getText().trim();

			if(controller != null && !strIP.equals("") && !strIP.equals("Enter host IP")){
				boolean blnWorked = controller.joinGame(strIP);
				
				if(blnWorked){
					blnNetworkReady = true;
					chatArea.append("[GAME MESSAGE] Connected to host: " + strIP + "\n");
				}else{
					chatArea.append("[GAME MESSAGE] Could not connect to host. Check host IP and firewall.\n");
					blnNetworkReady = false;
				}
			}else{
				chatArea.append("[GAME MESSAGE] Enter the host IP first.\n");
				blnNetworkReady = false;
			}
			*/
		}else if(e.getSource() == chatInput){
			String strMsg = chatInput.getText().trim();
			if(!strMsg.equals("")){
				// Show the message in YOUR OWN chatArea immediately (you won't receive your own socket message back)
				String strFormatted = "[CHAT] " + strName + ": " + strMsg;
				chatArea.append(strFormatted + "\n");
				chatArea.setCaretPosition(chatArea.getDocument().getLength()); // auto-scroll to bottom
				
				// Make sure chat panel is visible
				blnChat = true;
				setComponentVisibility();
				
				// Send to all other players via network
				if(controller != null){
					controller.sendChat(strName, strMsg);
				}
				chatInput.setText("");
				repaint();
			}
			this.requestFocusInWindow();
		}
		// repaint();
		
		if(e.getSource() != chatInput){
			repaint();
		}
	}

	// Mouse Listener Method Overrides
	/**
	 * Detects when the mouse is clicked.
	 * Used for switching screens and selecting cards during gameplay.
	 *
	 * e: The MouseEvent containing click information
	 */
	public void mouseClicked(MouseEvent e){
		if(blnEnterScreen){
			fadeToScreen("play");
			// showPlayScreen();
		}else if(blnFlipFirst){
			// advance from flip first screen to first player's turn
			int intMyIndex = (controller != null) ? controller.getLocalPlayerIndex() : 0;
			if(intTurnOrder[intCurrentTurn] == intMyIndex){
				fadeToScreen("turn");
			}else{
				fadeToScreen("wait");
			}
		}else if(blnTurnScreen && !blnHelp && !blnLeaderBoard && !blnWildPicker && !blnChat){
			handleCardClick(e.getX(), e.getY());
		}
	}
	
	/**
	 * Not used, but required by MouseListener.
	 */
	public void mousePressed(MouseEvent e){}
	/**
	 * Not used, but required by MouseListener.
	 */
	public void mouseReleased(MouseEvent e){}
	/**
	 * Not used, but required by MouseListener.
	 */
	public void mouseEntered(MouseEvent e){}
	/**
	 * Not used, but required by MouseListener.
	 */
	public void mouseExited(MouseEvent e){}
	
	// Key Listener Method Overrides
	/**
	 * Detects when a key is pressed.
	 * Used for actions like opening the chat or moving between screens.
	 *
	 * e: Yhe KeyEvent containing key press information
	 */
	public void keyPressed(KeyEvent e){
		if(blnEnterScreen){
			fadeToScreen("play");
			// showPlayScreen();
		}else if(e.getKeyCode() == KeyEvent.VK_ENTER){
			// pressing Enter during gameplay
			boolean blnGameplayScreen = blnTurnScreen || blnWaitScreen || blnDisplayCard || blnPickCard;
			if(blnGameplayScreen && !chatInput.isFocusOwner()){
				blnChat = !blnChat;
				setComponentVisibility();
				if(blnChat){
					chatInput.requestFocusInWindow();
				}else{
					this.requestFocusInWindow();
				}
				repaint();
			}
		}
	}
	
	/**
	 * Not used, but required by KeyListener.
	 */
	public void keyTyped(KeyEvent e){}
	/**
	 * Not used, but required by KeyListener.
	 */
	public void keyReleased(KeyEvent e){}
	
	// load card images
	/**
	 * Loads all card images into memory before the game starts.
	 * This improves performance by preventing delays when cards are displayed.
	 *
	 * It selects images based on the current theme.
	 */
	private void preloadCardImages(){
		String strCardPath = "Image/Cards/";
		for(int i = 0; i < intDeckSize; i++){
			int intCol = intTheme + 1; // 1=standard 2=pokemon 3=insideout
			if(intCol >= strDeck[i].length) intCol = 1;
			try{
				File f = new File(strCardPath + strDeck[i][intCol]);
				if(f.exists()){
					imgAllCards[i] = ImageIO.read(f);
				}else{
					imgAllCards[i] = null;
				}
			}catch(IOException e){
				imgAllCards[i] = null;
			}
		}
		System.out.println("Card images preloaded.");
	}
	
	// draw string in center
	/**
	 * Draws a string centered horizontally at a given position.
	 *
	 * g2: The Graphics2D object used for drawing
	 * strMessage: The text to display
	 * intCenterX: The horizontal center position
	 * intY: The vertical position
	 */
	private void drawCenteredString(Graphics2D g2, String strMessage, int intCenterX, int intY){
		FontMetrics metrics = g2.getFontMetrics();
		int intX = intCenterX - metrics.stringWidth(strMessage)/2;
		g2.drawString(strMessage, intX, intY);
	}
	
	// draw gold button method
	/**
	 * Creates a styled button with a gold theme used in the game UI.
	 *
	 * strText: The text displayed on the button
	 * return A JButton with custom styling applied
	 */
	private JButton createGoldButton(String strText){
		JButton button = new JButton(strText);
		button.setFont(buttonFont);
		button.setForeground(Color.WHITE);
		button.setBackground(btnGold);
		button.setFocusPainted(false);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setOpaque(true);
		button.setContentAreaFilled(true);
		button.setBorderPainted(false);
		
		// button.addMouseListener(this);
		return button;
	}
	
	// highlight selected theme when selecting theme
	private void highlightSelectedTheme(Graphics2D g2){
		int[] intBtnY = {165, 215, 265};
		for(int i = 0; i < 3; i++){
			if(intTheme == i){
				g2.setColor(goldColor);
				g2.setStroke(new BasicStroke(3));
				g2.drawRoundRect(525, intBtnY[i] - 5, 210, 50, 14, 14);
				g2.setStroke(new BasicStroke(1));
			}
		}
	}
	
	// Back of Card (Visual) 
	private void drawCardBack(Graphics2D g2, int intX, int intY, int intW, int intH){
		g2.setColor(new Color(0, 0, 0, 100));
		g2.fillRoundRect(intX+4, intY+4, intW, intH, 14, 14);
		g2.setColor(new Color(20, 20, 80));
		g2.fillRoundRect(intX, intY, intW, intH, 14, 14);
		g2.setColor(unoRed);
		g2.fillOval(intX + intW/4, intY + intH/4, intW/2, intH/2);
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Georgia", Font.BOLD, 22));
		drawCenteredString(g2, "UNO", intX + intW/2, intY + intH/2 + 8);
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke(2));
		g2.drawRoundRect(intX, intY, intW, intH, 14, 14);
		g2.setStroke(new BasicStroke(1));
	}
	
	// draw card face method
	private void drawCardFace(Graphics2D g2, String[] strCard, int intX, int intY, int intW, int intH){
		BufferedImage img = getCardImage(strCard);
		if(img != null){
			// Clip image to rounded rectangle
			g2.setClip(new RoundRectangle2D.Double(intX, intY, intW, intH, 14, 14));
			g2.drawImage(img, intX, intY, intW, intH, null);
			g2.setClip(null);
		}else{
			// in case image not loaded
			// painted card using the card name to determine color + label
			String strCardName = strCard[0];

			// Determine background color from card name prefix
			Color cardColor = unoRed;
			if(strCardName.startsWith("blue")){
				cardColor = unoBlue;
			}else if(strCardName.startsWith("green")){
				cardColor = unoGreen;
			}else if(strCardName.startsWith("yellow")){
				cardColor = unoYellow;
			}else if(strCardName.startsWith("wild")){
				cardColor = new Color(30, 30, 30);
			}

			// Strip color prefix to get label
			String strLabel = strCardName.replace("yellow","").replace("green","").replace("blue","").replace("red","");
			if(strLabel.equals("")){
				strLabel = "W";
			}

			// Shadow
			g2.setColor(new Color(0, 0, 0, 100));
			g2.fillRoundRect(intX+4, intY+4, intW, intH, 14, 14);
			// Card body
			g2.setColor(cardColor);
			g2.fillRoundRect(intX, intY, intW, intH, 14, 14);
			// White oval
			g2.setColor(Color.WHITE);
			g2.fillOval(intX + intW/6, intY + intH/6, intW*2/3, intH*2/3);
			// Center label
			g2.setColor(cardColor);
			g2.setFont(cardFont);
			drawCenteredString(g2, strLabel, intX + intW/2, intY + intH/2 + 7);
			// Corner label
			g2.setColor(Color.WHITE);
			g2.setFont(bodyFont);
			g2.drawString(strLabel, intX + 6, intY + 18);
		}
		// Border always drawn
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke(2));
		g2.drawRoundRect(intX, intY, intW, intH, 14, 14);
		g2.setStroke(new BasicStroke(1));
	}

	// Picks the right theme column from the card array
	private BufferedImage getCardImage(String[] strCard){
		if(strCard == null){
			return null;
		}
		for(int i = 0; i < intDeckSize; i++){
			if(strDeck[i][0].equals(strCard[0])){
				return imgAllCards[i];
			}
		}
		return null;
	}
	
	private BufferedImage loadCardImage(String strFileName){
		try{
			File f = new File("Image/Cards/" + strFileName);
			if(f.exists()){
				return ImageIO.read(f);
			}
		}catch(IOException e){
			// File missing
		}
		return null;
	}

	private void flipFirstCard(){
		if(intDrawPileSize > 0){
			strDiscardPile[intDiscardPileSize] = strDrawPile[0];
			intDiscardPileSize++;
			for(int i = 0; i < intDrawPileSize - 1; i++){
				strDrawPile[i] = strDrawPile[i+1];
			}
			intDrawPileSize--;
			System.out.println("Starting card: " + strDiscardPile[0][0]);
		}
	}
	
	// Game paint element logic method (when name, theme enters -> it starts)
	/**
	 * Initializes the game after setup is complete.
	 * Deals cards, prepares the board, and switches to gameplay.
	 */
	private void startGame(){
		if(model != null){
			// Pull all game state from the model
			intDrawPileSize = model.intDrawPileSize;
			for(int i = 0; i < intDrawPileSize; i++){
				strDrawPile[i] = model.strDrawPile[i];
			}
			// Copy discard pile
			intDiscardPileSize = model.intDiscardPileSize;
			for(int i = 0; i < intDiscardPileSize; i++){
				strDiscardPile[i] = model.strDiscardPile[i];
			}
			
			// Copy local player hand using the correct player index
			int intMyIndex = (controller != null) ? controller.getLocalPlayerIndex() : 0;
			intHandSize = model.intHandSizes[intMyIndex];
			for(int i = 0; i < intHandSize; i++){
				strPlayHand[i] = model.strHands[intMyIndex][i];
			}
			// Copy turn order
			for(int i = 0; i < 3; i++){
				intTurnOrder[i] = model.intTurnOrder[i];
			}
			intCurrentTurn = model.intCurrentTurn;
			blnClockwise = model.blnClockwise;
			// Copy card counts
			// intMyIndex = (controller != null) ? controller.getLocalPlayerIndex() : 0;  // ← no 'int' here
			// intCardCount1 = model.intHandSizes[intMyIndex];
			intCardCount1 = intHandSize;

			int intOppSlot = 0;
			for(int i = 0; i < 3; i++){
				if(i != intMyIndex){
					if(intOppSlot == 0){
						intCardCount2 = model.intHandSizes[i];
					}
					else{
						intCardCount3 = model.intHandSizes[i];
					}
					intOppSlot++;
				}
			}

			intDeckSize = model.intDeckSize;
			for(int i = 0; i < intDeckSize; i++){
				strDeck[i] = model.strDeck[i];
			}
			// Reset elimination state
			for(int i = 0; i < 3; i++){
				blnPlayerEliminated[i] = model.blnEliminated[i];
			}
			intActivePlayers = model.intActivePlayers;
		} else {
			// fallback if model not connected
			shuffleDeck();
			randomizeTurnOrder();
			dealStartingHands();
			flipFirstCard();
			intCardCount2 = intStartCards;
			intCardCount3 = intStartCards;
			for(int i = 0; i < 3; i++){
				blnPlayerEliminated[i] = false;
			}
			intActivePlayers = 3;
		}
		intCardPage = 0;
		strWildColor = "";
		blnEliminated = false;
		blnWildPicker = false;
		// blnNetworkReady = false;
		preloadCardImages(); // reload images with updated deck + theme
		fadeToScreen("flipfirst");
	}
	
	private void loadDeck(){
		intDeckSize = 0;
		String strCSVPath = "cards.csv";
		try{
			BufferedReader reader = new BufferedReader(new FileReader(strCSVPath));
			reader.readLine();
			String strLine;
			
			// read through each line in csv
			while((strLine = reader.readLine()) != null){
				strLine = strLine.trim();
				if(!strLine.equals("")){
					String[] strParts = strLine.split(",");
					if(strParts.length >= 2 && intDeckSize < 100){
						strDeck[intDeckSize] = strParts;
						intDeckSize++;
					}
				}
			}
			reader.close();
			System.out.println("Deck loaded: "+intDeckSize);
		}catch(IOException e){
			System.out.println("Could not load cards.csv: "+e.getMessage());
			buildFallbackDeck();
		}
	}
	
	// if csv missing
	private void buildFallbackDeck(){
		intDeckSize = 0;
		String[] strColors = {"red", "blue", "green", "yellow"};
		String[] strNums = {"0", "1", "1", "2", "2", "3", "3", "4", "4", "5", "5", "6", "6", "7", "7", "8", "8", "9", "9"};
		String[] strSpecials = {"skip", "skip", "draw2", "draw2"};
		
		for(int intC = 0; intC < strColors.length; intC++){
			String strC = strColors[intC];
			for(int intN = 0; intN < strNums.length; intN++){
				String strCard = strC + strNums[intN];
				String[] strCardData = {strCard, "standard"+strCard+".png", "pokemon"+strCard+".png","insideout"+strCard+".png"};
				strDeck[intDeckSize] = strCardData;
				intDeckSize++;
			}
			for(int intS = 0; intS < strSpecials.length; intS++){
				String strCard = strC + strSpecials[intS];
				String[] strCardData = {strCard, "standard"+strCard+".png", "pokemon"+strCard+".png","insideout"+strCard+".png"};
				strDeck[intDeckSize] = strCardData;
				intDeckSize++;
			}
		}
		for(int i = 0; i < 4; i++){
			String[] strWildCardData = {"wild", "standardwild.png","pokemonwild.png","insideoutwild.png"};
			strDeck[intDeckSize++] = strWildCardData;
		}
		
		for(int i = 0; i < 4; i++){
			String[] strWild4CardData = {"wilddraw4","standardwilddraw4.png","pokemonwilddraw4.png","insideoutwilddraw4.png"};
			strDeck[intDeckSize++] = strWild4CardData;
		}
	} 
	
	// shuffle deck method
	private void shuffleDeck(){
		// copy deck into drawPile
		intDrawPileSize = 0;
		intDiscardPileSize = 0;
		for(int i = 0; i < intDeckSize; i++){
			strDrawPile[i] = strDeck[i];
			intDrawPileSize++;
		}
		
		// shuffle pile
		Random rand = new Random();
		for(int i = intDrawPileSize - 1; i > 0; i--){
			int intJ = rand.nextInt(i+1);
			String[] strTemp = strDrawPile[i];
			strDrawPile[i] = strDrawPile[intJ];
			strDrawPile[intJ] = strTemp;
		}
	}
	
	// randomize player's order/turn method
	private void randomizeTurnOrder(){
		intTurnOrder[0] = 0;
		intTurnOrder[1] = 1;
		intTurnOrder[2] = 2;
		Random rand = new Random();
		
		for(int i = 2; i>0; i--){
			int intJ = rand.nextInt(i + 1);
			int intTemp = intTurnOrder[i];
			intTurnOrder[i] = intTurnOrder[intJ];
			intTurnOrder[intJ] = intTemp;
		}
		intCurrentTurn = 0;
		blnClockwise = true;
		System.out.println("First player: " + strPlayNames[intTurnOrder[0]]);
	}
	
	// Deal intStartCards cards from strDrawPile 
	private void dealStartingHands(){
		intHandSize = 0;
		intCardCount1 = 0;
		// placeholder for other players
		intCardCount2 = intStartCards; 
		intCardCount3 = intStartCards;

		for(int i = 0; i < intStartCards; i++){
			if(intDrawPileSize > 0){
				// Take card from front of draw pile
				strPlayHand[intHandSize] = strDrawPile[0];
				intHandSize++;
				// Shift draw pile left by 1
				for(int j = 0; j < intDrawPileSize - 1; j++){
					strDrawPile[j] = strDrawPile[j+1];
				}
				intDrawPileSize--;
			}
		}
		intCardCount1 = intHandSize;
	}
	
	// draw from pile method
	/**
	 * Allows the player to draw a card from the draw pile.
	 * Updates the player's hand and game state.
	 */
	private void drawFromPile(){
		if(intDrawPileSize == 0){
			reshuffleDiscard();
		}
		if(intDrawPileSize > 0){
			// Take card from front of drawPile
			strDrawnCard = strDrawPile[0];
			// Shift drawPile left
			for(int i = 0; i < intDrawPileSize - 1; i++){
				strDrawPile[i] = strDrawPile[i+1];
			}
			intDrawPileSize--;

			strPlayHand[intHandSize] = strDrawnCard;
			intHandSize++;
			intCardCount1 = intHandSize;

			if(intHandSize > intMaxCards){
				blnEliminated = true;
				setComponentVisibility();
				repaint();
				return;
			}
			showDisplayCard();
		}
	}
	
	// reshuffle deck method
	private void reshuffleDiscard(){
		// Copy discard pile into draw pile
		for(int i = 0; i < intDiscardPileSize; i++){
			strDrawPile[i] = strDiscardPile[i];
		}
		intDrawPileSize = intDiscardPileSize;
		intDiscardPileSize = 0;

		// Fisher-Yates shuffle
		Random rand = new Random();
		for(int i = intDrawPileSize - 1; i > 0; i--){
			int intJ = rand.nextInt(i + 1);
			String[] strTemp = strDrawPile[i];
			strDrawPile[i] = strDrawPile[intJ];
			strDrawPile[intJ] = strTemp;
		}
		System.out.println("Draw pile reshuffled.");
	}
	
	
		// --- FIX 1 helper: check if a card name is a legal play against the view's discard pile ---
	private boolean isValidPlay(String strCardName){
		// Wild cards are always valid
		if(strCardName.startsWith("wild")){
			return true;
		}
		// If discard pile is empty anything goes
		if(intDiscardPileSize == 0){
			return true;
		}
		String strTopCard = strDiscardPile[intDiscardPileSize - 1][0];
		// If top card is a wild and a colour was chosen, must match that colour
		if(strTopCard.startsWith("wild")){
			if(!strWildColor.equals("")){
				return getCardColor(strCardName).equals(strWildColor);
			}
			return true;
		}
		// Otherwise match by colour OR by value
		return getCardColor(strCardName).equals(getCardColor(strTopCard))
			|| getCardValue(strCardName).equals(getCardValue(strTopCard));
	}
 
	// helper: extract colour prefix from card name
	private String getCardColor(String strCardName){
		if(strCardName.startsWith("red"))    return "red";
		if(strCardName.startsWith("blue"))   return "blue";
		if(strCardName.startsWith("green"))  return "green";
		if(strCardName.startsWith("yellow")) return "yellow";
		return "wild";
	}
 
	// helper: extract value suffix from card name
	private String getCardValue(String strCardName){
		String strVal = strCardName.replace("yellow","").replace("green","")
		                           .replace("blue","").replace("red","");
		return strVal.equals("") ? "wild" : strVal;
	}
 	
	// player's card
	private void playCard(int intIndex){
		if(intIndex < 0 || intIndex >= intHandSize){
			return;
		}
		String[] strCard = strPlayHand[intIndex];
		String strCardName = strCard[0];

		if(!isValidPlay(strCardName)){
			// Flash a message or just silently ignore the illegal play
			repaint();
			return;
		}
		
		
		// Move card to discard pile
		strDiscardPile[intDiscardPileSize] = strPlayHand[intIndex];
		intDiscardPileSize++;

		// Remove card from hand by shifting left
		for(int i = intIndex; i < intHandSize - 1; i++){
			strPlayHand[i] = strPlayHand[i+1];
		}
		intHandSize--;
		intCardCount1 = intHandSize;

		// Clamp page
		int intMaxPage = 0;
		// pages player has cards
		if (intHandSize > 0) {
			intMaxPage = (intHandSize + intPerPage - 1) / intPerPage - 1;
		}

		// Make sure our current page doesn't go past the last page
		if (intCardPage > intMaxPage) {
			intCardPage = intMaxPage;
		}

		if(intHandSize == 0){
			strWinner = strName;
			fadeToScreen("gameover");
			return;
		}
		
		if(strCardName.startsWith("wild")){
			strWildColor = "";
			blnWildPicker = true;
			setComponentVisibility();
			repaint();
			return;
		}
		
		// NEW — before sendTurnUpdate in playCard() and each wild button:
		if(model != null){
			int intMyIdx = (controller != null) ? controller.getLocalPlayerIndex() : 0;
			model.intHandSizes[intMyIdx] = intHandSize;
		}
				
		advanceTurn();
		if(network != null){
			network.sendTurnUpdate(strCardName, strWildColor, model.intHandSizes);
		}
	}
	
	// move to next player
	/**
	 * Moves the game to the next player's turn.
	 * Updates turn order and checks game conditions.
	 */
	private void advanceTurn(){
		int intCount = 0;
		boolean searchingForActivePlayer = true;

		while (searchingForActivePlayer){
			// Move to the next turn based on direction
			if(blnClockwise){
				intCurrentTurn = (intCurrentTurn + 1) % 3;
			} else {
				intCurrentTurn = (intCurrentTurn + 2) % 3;
			}
			intCount++;
			// stop if the player is not eliminated 
			searchingForActivePlayer = blnPlayerEliminated[intTurnOrder[intCurrentTurn]] && intCount < 3;
		}
		
		// check if only 1 active player remains
		int intActive = 0;
		int intLastActive = 0;
		for(int i = 0; i < 3; i++){
			if(!blnPlayerEliminated[i]){ 
				intActive++; 
				intLastActive = i; 
			}
		}
		
		if(intActive == 1){
			strWinner = strPlayNames[intLastActive];
			fadeToScreen("gameover");
			return;
		}
		
		int intMyIndex = (controller != null) ? controller.getLocalPlayerIndex() : 0;
		if(blnPlayerEliminated[intMyIndex]){
			fadeToScreen("wait");
		}else if(intTurnOrder[intCurrentTurn] == intMyIndex){
			fadeToScreen("turn");
		}else{
			fadeToScreen("wait");
		}
		
		/*
		if(blnClockwise){
			intCurrentTurn = (intCurrentTurn + 1) % 3;
		}else{
			intCurrentTurn = (intCurrentTurn + 2) % 3;
		}
		// if-else: play if your turn; else, wait for other player
		if(intTurnOrder[intCurrentTurn] == 0){
			fadeToScreen("turn");
		}else{
			fadeToScreen("wait");
		}
		*/
	}
	
	private String getOpponentName(){
		for(int i = 0; i < 3; i++){
			if(intTurnOrder[i] != 0){
				return strPlayNames[intTurnOrder[i]];
			}
		}
		return "Opponent";
	}
	
	// check if mouse click hit in the card page
	/**
	 * Handles when a player clicks on a card.
	 * Determines which card was selected and plays it if valid.
	 *
	 * x: The x-coordinate of the mouse click
	 * y: The y-coordinate of the mouse click
	 */
	private void handleCardClick(int intMouseX, int intMouseY){
		int intCardW  = 260;
		int intCardH  = 380;
		int intGap    = 60;
		int intTotalW = intCardW * 2 + intGap;
		int intStartX = (intWidth - intTotalW) / 2;
		int intCardY  = 140;

		for(int intSlot = 0; intSlot < intPerPage; intSlot++){
			int intCardIdx = intCardPage * intPerPage + intSlot;
			int intX = intStartX + intSlot * (intCardW + intGap);
			if(intMouseX >= intX && intMouseX <= intX + intCardW && intMouseY >= intCardY && intMouseY <= intCardY + intCardH){
				if(intCardIdx < intHandSize){
					playCard(intCardIdx);
					repaint();
					return;
				}
			}
		}
	}
	
	// transition method
	/**
	 * Starts a fade animation to switch from the current screen
	 * to another screen.
	 *
	 * strScreen: The name of the screen to switch to
	 */
	private void fadeToScreen(String strTarget){
		strNextScreen = strTarget;
		blnFading = true;
		intFadeAlpha = 0;
		
		// Create a fade timer
		Timer fadeTimer = new Timer(16, null);
		
		fadeTimer.addActionListener(new ActionListener() {
			// track direction
			boolean isFadingOut = true; 
			public void actionPerformed(ActionEvent e) {
				if (isFadingOut) {
					intFadeAlpha += 20;
					if (intFadeAlpha >= 255) {
						intFadeAlpha = 255;
						// switch screen at total back
						applyScreenSwitch(strNextScreen);
						// start to fade in
						isFadingOut = false;
					}
				} else {
					intFadeAlpha -= 20;
					if (intFadeAlpha <= 0) {
						intFadeAlpha = 0;
						blnFading = false;
						fadeTimer.stop();
					}
				}
				repaint();
			}
		});
		fadeTimer.start();
	}
	
	private void applyScreenSwitch(String strTarget){
		if(strTarget.equals("play")){
			showPlayScreen();
		}else if(strTarget.equals("theme")){
			showThemeScreen();
		}
		else if(strTarget.equals("wait")){
			showWaitScreen();
		}else if(strTarget.equals("turn")){
			showTurnScreen();
		}else if(strTarget.equals("pick")){
			showPickCard();
		}else if(strTarget.equals("display")){
			showDisplayCard();
		}else if(strTarget.equals("gameover")){
			showGameOver(strWinner);
		}else if(strTarget.equals("flipfirst")){
			showFlipFirst();
		}else if(strTarget.equals("eliminated")){
			showEliminated();
		}
	}
	
	// Paint Component Method
	/**
	 * Draws all graphics for the current screen.
	 * This method is automatically called when the UI needs to be refreshed.
	 * It displays things like cards, backgrounds, and text.
	 *
	 * g: The Graphics object used for drawing
	 */
	 public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // increase graphics smoothness
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      
		drawBackground(g2);
		
		// Individual Screen
		if(blnEnterScreen){
			drawInstruction(g2);
		}else if(blnPlayScreen){
			drawPlay(g2);
		}else if(blnThemeScreen){
			drawTheme(g2);
		}else if(blnWaitScreen){
			drawWait(g2);
		}else if(blnPickCard){
			drawPickCard(g2);
		}else if(blnDisplayCard){
			drawDisplayCard(g2);
		}else if(blnTurnScreen){
			drawYourTurn(g2);
		}else if(blnGameOver){
			drawGameOver(g2);
		}else if(blnFlipFirst){
			drawFlipFirst(g2);
		}else if(blnEliminated){
			drawEliminated(g2);
		}
		
		// Overlay Screen
		if(blnHelp){
			drawHelp(g2);
		}
		if(blnLeaderBoard){
			drawLeaderBoard(g2);
		}
		if(blnChat){
			drawChat(g2);
		}
		if(blnWildPicker){
			drawWildPicker(g2);
		}
		
		// fade overlay
		if(blnFading && intFadeAlpha > 0){
			g2.setColor(new Color(0, 0, 0, Math.min(intFadeAlpha, 255)));
			g2.fillRect(0, 0, intWidth, intHeight);
		}
	}
	
	// Draw Background Method
	private void drawBackground(Graphics2D g2){
		if(imgBackground != null){
			g2.drawImage(imgBackground, 0, 0, intWidth, intHeight, null);
		}else{
			g2.setColor(unoBlue);
			g2.fillRect(0, 0, intWidth, intHeight);
		}
	}
	
	// Draw instruction method 
	private void drawInstruction(Graphics2D g2){
		// overlay on imgBackground
		g2.setColor(transparentBlack);
		g2.fillRoundRect(200, 100, 880, 520, 10, 10);
		
		// title
		g2.setColor(Color.WHITE);
		g2.setFont(titleFont);
		drawCenteredString(g2, "INSTRUCTION", 640, 175);
		
		// instruction
		g2.setFont(headerFont);
		g2.setColor(goldColor);
		drawCenteredString(g2, "How to Play UNO", 640, 220);
		
		g2.setFont(bodyFont);
		g2.setColor(Color.WHITE);
		String[] strLines = {"• Match the top card of the discard pile by COLOR or NUMBER.", "• If you cannot play, draw a card from the deck.", "• Special cards: Skip, Draw Two, Wild, Wild Draw Four", "• First player to empty this hand wins!", "• 'UNO' when you only have 1 card left or draw 2 penalty cards.", "• You will be disqualify if you have 30+ cards", "• Click anywhere to continue to the Play"};
		
		int intY = 270;
		String strText;
		for(int intCount = 0; intCount < strLines.length; intCount++){
			strText = strLines[intCount];
			g2.drawString(strText, 250, intY);
			intY += 30;
		}
	}
	
	// Draw play button method
	private void drawPlay(Graphics2D g2){
		if(imgStart != null){
			g2.drawImage(imgStart, 0, 0, intWidth, intHeight, null);
		}else{
			// draw UNO 
			g2.setColor(unoRed);
			g2.setFont(bigFont);
			drawCenteredString(g2, "UNO", 640, 320);
			g2.setColor(goldColor);
			g2.setStroke(new BasicStroke(4));
			g2.drawRoundRect(460, 240, 360, 120, 20, 20);
			g2.setStroke(new BasicStroke(1));
		}
	}
	
	// Draw theme and name screen method
	private void drawTheme(Graphics2D g2){
		// overlay
		g2.setColor(transparentDark);
		g2.fillRoundRect(300, 100, 680, 560, 20, 20);
		
		// title
		g2.setColor(Color.WHITE);
		g2.setFont(titleFont);
		drawCenteredString(g2, "THEME", 640, 140);
		
		// High light theme button
		highlightSelectedTheme(g2);
		
		// Enter Name 
		g2.setColor(Color.WHITE);
		g2.setFont(headerFont);
		drawCenteredString(g2, "ENTER NAME:", 640, 460);
		
		// if empty on nameField
		if(nameField.getText().trim().equals("")){
			g2.setColor(unoRed);
			g2.setFont(bodyFont);
			drawCenteredString(g2, "Name required to start", 640, 430);
		}else if(!blnNetworkReady){
			g2.setColor(unoRed);
			g2.setFont(bodyFont);
			drawCenteredString(g2, "You must HOST or JOIN a game before starting", 640, 430);
		}
		
		// theme options
		g2.setFont(bodyFont);
		g2.setColor(goldColor);
		String[] strThemeNames = {"Standard", "Pokemon", "InsideOut"};
		drawCenteredString(g2, "Selected: "+ strThemeNames[intTheme], 640, 580);
	}
	
	private void drawWait(Graphics2D g2){
		if(imgWait != null){
			g2.drawImage(imgWait, 0, 0, intWidth, intHeight, null);
		}else{
			drawBackground(g2);
		}
		
		// Number of Cards (Other players' card cont)
		g2.setFont(bigFont);
		g2.setColor(Color.WHITE);
		FontMetrics fm = g2.getFontMetrics();
		
		// Opponents at top corners (back of table)
		int intC2W = fm.stringWidth(String.valueOf(intCardCount2));
		int intC3W = fm.stringWidth(String.valueOf(intCardCount3));
		g2.drawString(String.valueOf(intCardCount2), 60, 110);
		g2.drawString(String.valueOf(intCardCount3), intWidth - 60 - intC3W, 110);
		
		// local player card display
		g2.setColor(goldColor);
		int intC1W = fm.stringWidth(String.valueOf(intCardCount1));
		g2.drawString(String.valueOf(intCardCount1), 640 - intC1W/2, 690);
		g2.setFont(bodyFont);
		g2.setColor(Color.WHITE);
		drawCenteredString(g2, "Your cards", 640, 712);
		
		// Waiting for player
		g2.setColor(transparentBlack);
		g2.fillRoundRect(290, 270, 700, 180, 20, 20);
		
		g2.setFont(titleFont);
		g2.setColor(Color.WHITE);
		drawCenteredString(g2, "WAITING FOR", 640, 330);
		g2.setColor(goldColor);
		drawCenteredString(g2, strPlayNames[intTurnOrder[intCurrentTurn]].toUpperCase(), 640, 390);
	}
	
	private void drawPickCard(Graphics2D g2){
		if(imgPickCard != null){
			g2.drawImage(imgPickCard, 0, 0, intWidth, intHeight, null);
		}else{
			drawBackground(g2);
		}
		// draw card back in center as the deck
		drawCardBack(g2, 560, 190, 160, 220);
		g2.setColor(transparentBlack);
		g2.fillRoundRect(340, 445, 600, 60, 16, 16);
		g2.setFont(titleFont);
		g2.setColor(Color.WHITE);
		drawCenteredString(g2, "PICK UP A CARD", 640, 485);
		g2.setFont(bodyFont);
		g2.setColor(new Color(200, 200, 200));
		drawCenteredString(g2, "Draw pile: " + intDrawPileSize + " cards remaining", 640, 525);
	}

	private void drawDisplayCard(Graphics2D g2){
		if(imgDisplay != null){
			g2.drawImage(imgDisplay, 0, 0, intWidth, intHeight, null);
		}else{
			drawBackground(g2);
		}
		
		// Show drawn card face
		if(strDrawnCard != null){
			drawCardFace(g2, strDrawnCard, 510, 160, 260, 360);
			g2.setFont(headerFont);
			g2.setColor(Color.WHITE);
			drawCenteredString(g2, "You drew: " + strDrawnCard[0], 640, 560);
		}
		g2.setFont(bodyFont);
		g2.setColor(new Color(200, 200, 200));
		drawCenteredString(g2, "Use PREV / NEXT on the game screen to browse your hand", 640, 600);
	}
	
	private void drawYourTurn(Graphics2D g2){
		if(imgYourTurn != null){
			g2.drawImage(imgYourTurn, 0, 0, intWidth, intHeight, null);
		}else{
			drawBackground(g2);
		}
		
		// Your turn label
		g2.setFont(titleFont);
		g2.setColor(Color.WHITE);
		drawCenteredString(g2, "YOUR TURN", 640, 60);
		
		// player hand
		int intTotalPages = 1;
		if(intHandSize > 0){
			intTotalPages = (intHandSize + intPerPage - 1) / intPerPage;
		}
		
		// hand size 
		g2.setFont(bodyFont);
		g2.setColor(goldColor);
		drawCenteredString(g2,"Hand: " + intHandSize + " / " + intMaxCards +"   |   Page " + (intCardPage + 1) + " of " + intTotalPages, 640, 82);

		// Elimination warning
		if(intHandSize >= intMaxCards - 3){
			g2.setColor(unoRed);
			g2.setFont(subFont);
			drawCenteredString(g2,"WARNING: " + (intMaxCards - intHandSize) + " cards until elimination!",640, 110);
		}

		// Card slot dimensions & positions
		int intCardW = 260;
		int intCardH = 380;
		int intGap = 60;
		int intTotalW = intCardW * 2 + intGap;
		int intStartX = (intWidth - intTotalW) / 2;
		int intCardY = 140;
		int intFirst = intCardPage * intPerPage;

		for(int intSlot = 0; intSlot < intPerPage; intSlot++){
			int intCardIdx = intFirst + intSlot;
			int intX = intStartX + intSlot * (intCardW + intGap);

			if(intCardIdx < intHandSize){
				drawCardFace(g2, strPlayHand[intCardIdx], intX, intCardY, intCardW, intCardH);
				// "Click to play"
				g2.setFont(bodyFont);
				g2.setColor(new Color(255, 255, 255, 160));
				drawCenteredString(g2, "Click to play", intX + intCardW/2, intCardY + intCardH + 22);
			}else{
				// Empty slot — dashed outline
				g2.setColor(new Color(255, 255, 255, 40));
				g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
				                             0, new float[]{10, 8}, 0));
				g2.drawRoundRect(intX, intCardY, intCardW, intCardH, 14, 14);
				g2.setStroke(new BasicStroke(1));
			}
		}

		// Page indicator dots below cards
		int intDotY = intCardY + intCardH + 48;
		for(int i = 0; i < intTotalPages; i++){
			g2.setColor(i == intCardPage ? goldColor : new Color(255, 255, 255, 80));
			g2.fillOval(640 - (intTotalPages * 18)/2 + i*18, intDotY, 10, 10);
		}

		// Draw/Discard pile info (bottom right)
		g2.setFont(bodyFont);
		g2.setColor(new Color(200, 200, 200));
		g2.drawString("Draw pile: "    + intDrawPileSize,    1050, 670);
		g2.drawString("Discard pile: " + intDiscardPileSize, 1050, 690);
		
		if(intDiscardPileSize > 0){
			g2.setFont(bodyFont);
			g2.setColor(Color.WHITE);
			g2.drawString("Top card:", 1050, 630);
			drawCardFace(g2, strDiscardPile[intDiscardPileSize - 1], 1050, 530, 80, 110);
		}
	}
	
	private void drawGameOver(Graphics2D g2){
		if(imgGameOver != null){
			g2.drawImage(imgGameOver, 0, 0, intWidth, intHeight, null);
		}else{
			drawBackground(g2);
		}
		
		// winner panel
		g2.setColor(transparentBlack);
		g2.fillRoundRect(290, 200, 700, 280, 20, 20);
		g2.setFont(titleFont);
		g2.setColor(goldColor);
		drawCenteredString(g2, strWinner.toUpperCase()+" WINS", 640, 300);
		g2.setFont(headerFont);
		g2.setColor(Color.WHITE);
		drawCenteredString(g2, "GAME OVER...", 640, 370);
		
		// Decorations/Animation Row
	}
	
	// show the first flipped card
	private void drawFlipFirst(Graphics2D g2){
		drawBackground(g2);
		g2.setColor(transparentBlack);
		g2.fillRoundRect(200, 100, 880, 560, 20, 20);
		g2.setFont(titleFont);
		g2.setColor(Color.WHITE);
		drawCenteredString(g2, "STARTING CARD", 640, 140);
		g2.setFont(bodyFont);
		g2.setColor(new Color(200, 200, 200));
		drawCenteredString(g2, "This is the first card on the discard pile.", 640, 180);
		if(intDiscardPileSize > 0){
			drawCardFace(g2, strDiscardPile[0], 510, 210, 260, 360);
			g2.setFont(headerFont);
			g2.setColor(goldColor);
			drawCenteredString(g2, strDiscardPile[0][0].toUpperCase(), 640, 600);
		}
		g2.setFont(bodyFont);
		g2.setColor(new Color(200, 200, 200));
		drawCenteredString(g2, "Click anywhere to begin", 640, 650);
	}
	
	// Show eliminated screen when local player exceeds 30 cards
	private void drawEliminated(Graphics2D g2){
		if(imgEliminated != null){
			g2.drawImage(imgEliminated, 0, 0, intWidth, intHeight, null);
		}else{
			drawBackground(g2);
		}
		g2.setColor(new Color(150, 0, 0, 210));
		g2.fillRoundRect(240, 160, 800, 400, 20, 20);
		g2.setColor(unoRed);
		g2.setStroke(new BasicStroke(4));
		g2.drawRoundRect(240, 160, 800, 400, 20, 20);
		g2.setStroke(new BasicStroke(1));
		g2.setFont(titleFont);
		g2.setColor(Color.WHITE);
		drawCenteredString(g2, "YOU ARE ELIMINATED!", 640, 250);
		g2.setFont(headerFont);
		g2.setColor(new Color(255, 180, 180));
		drawCenteredString(g2, "You exceeded " + intMaxCards + " cards in your hand.", 640, 310);
		g2.setFont(bodyFont);
		g2.setColor(Color.WHITE);
		drawCenteredString(g2, "The remaining players will continue without you.", 640, 360);
		drawCenteredString(g2, "Click CONTINUE WATCHING to spectate.", 640, 390);
	}
	
	// Wild color picker overlay
	private void drawWildPicker(Graphics2D g2){
		g2.setColor(new Color(0, 0, 0, 190));
		g2.fillRect(0, 0, intWidth, intHeight);
		g2.setColor(new Color(20, 20, 60, 240));
		g2.fillRoundRect(340, 200, 600, 320, 20, 20);
		g2.setColor(goldColor);
		g2.setStroke(new BasicStroke(2));
		g2.drawRoundRect(340, 200, 600, 320, 20, 20);
		g2.setStroke(new BasicStroke(1));
		g2.setFont(titleFont);
		g2.setColor(Color.WHITE);
		drawCenteredString(g2, "CHOOSE A COLOR", 640, 260);
		g2.setFont(bodyFont);
		g2.setColor(new Color(200, 200, 200));
		drawCenteredString(g2, "Pick the color for the next player to match", 640, 295);
	}
	
	private void drawHelp(Graphics2D g2){
		// Overlay background
		g2.setColor(new Color(0, 0, 0, 180));
		g2.fillRect(0, 0, intWidth, intHeight);
		
		g2.setColor(new Color(20, 20, 60, 230));
		g2.fillRoundRect(150, 80, 980, 560, 20, 20);
		
		g2.setColor(goldColor);
		g2.setStroke(new BasicStroke(2));
		g2.drawRoundRect(150, 80, 980, 560, 20, 20);
		g2.setStroke(new BasicStroke(1));
		
		// Title
		g2.setFont(titleFont);
		g2.setColor(Color.WHITE);
		drawCenteredString(g2, "HELP", 640, 140);
		
		// Content
		g2.setFont(headerFont);
		g2.setColor(goldColor);
		g2.drawString("Button Reference", 200, 195);
		g2.setFont(bodyFont);
		g2.setColor(Color.WHITE);
		String[][] strHelpRows = {{"HELP", "Opens this help screen explaining game controls."}, {"PICK UP A CARD", "Draw a card from the deck when you have no valid play."}, {"LEADERBOARD", "Shows current card counts for all players."}, {"CHAT", "Type a message to send to all players in the game."}, {"Card (click)", "Click a card in your hand to play it on your turn."}};
		
		// draw help lines
		int intHeightY = 240;
		int intCount;
		int intRowCount = strHelpRows.length;
		for(intCount = 0; intCount < intRowCount; intCount++){
			String[] strRow = strHelpRows[intCount];
			g2.setColor(goldColor);
			g2.setFont(subFont);
			g2.drawString(strRow[0], 200, intHeightY);
			g2.setColor(Color.WHITE);
			g2.setFont(bodyFont);
			g2.drawString(strRow[1], 430, intHeightY);
			intHeightY += 45;
		}
		
		// color guide
		g2.setFont(headerFont);
		g2.setColor(goldColor);
		g2.drawString("Card Colors", 200, intHeightY + 10);
		intHeightY += 40;
		
		Color[] cardColors = {unoRed, unoBlue, unoGreen, unoYellow};
		String[] strColorNames = {"Red", "Blue", "Green", "Yellow"};
		for(intCount = 0; intCount < 4; intCount++){
			g2.setColor(cardColors[intCount]);
			g2.fillRoundRect(200 + intCount * 180, intHeightY, 120, 40, 10, 10);
			g2.setColor(Color.WHITE);
			g2.setFont(bodyFont);
			drawCenteredString(g2, strColorNames[intCount], 260+intCount*180, intHeightY + 26);
		}
		
		// Close message
		g2.setFont(bodyFont); 
		g2.setColor(new Color(180, 180, 180));
		drawCenteredString(g2, "Press HELP again to close", 640, 620);
	}
	
	private void drawLeaderBoard(Graphics2D g2){
		// Background
		g2.setColor(new Color(0, 0, 0, 160));
		g2.fillRect(0, 0, intWidth,intHeight);
		g2.setColor(new Color(20, 20, 60, 230));
		g2.fillRoundRect(400, 150, 480, 380, 20, 20);
		g2.setColor(goldColor);
		g2.setStroke(new BasicStroke(2));
		g2.drawRoundRect(400, 150, 480, 380, 20, 20);
		g2.setStroke(new BasicStroke(1));
		
		// Header
		g2.setFont(headerFont);
		g2.setColor(Color.WHITE);
		drawCenteredString(g2, "LEADERBOARD", 640, 210);
		
		String[] strPlayers = {"Player 1", "Player 2", "Player 3"};
		int[] intCounts = {intCardCount1, intCardCount2, intCardCount3};
		g2.setFont(subFont);
		int intLengthY = 270;
		
		for(int intCount = 0; intCount < 3; intCount++){
			// Red if near elimination limit
			if(intCounts[intCount] >= intMaxCards - 3){
				g2.setColor(unoRed);
			}else if(intCount == 0){
				g2.setColor(goldColor);
			}else{
				g2.setColor(Color.WHITE);
			}
			g2.drawString(strPlayNames[intCount], 450, intLengthY);
			g2.drawString(intCounts[intCount] + " / " + intMaxCards, 730, intLengthY);
			intLengthY += 60;
		}
		
		// close text
		g2.setFont(bodyFont);
		g2.setColor(new Color(180, 180, 180));
		drawCenteredString(g2, "Press LEADERBOARD to close", 640, 500);
	}
	
	private void drawChat(Graphics2D g2){
		// chat panel on the top-left
		g2.setColor(new Color(0, 0, 0, 200));
		g2.fillRoundRect(10, 10, 300, 340, 12, 12);
		g2.setColor(goldColor);
		g2.setStroke(new BasicStroke(1.5f));
		g2.drawRoundRect(10, 10, 300, 340, 12, 12);
		g2.setStroke(new BasicStroke(1));
		g2.setFont(headerFont);
		g2.setColor(Color.WHITE);
		g2.drawString("CHAT", 130, 42);
	}
	
	// Display of JButton
	/**
	 * Updates which UI components are visible based on the current screen.
	 * Helps control what the player sees at different stages of the game.
	 */
	private void setComponentVisibility(){
		boolean blnGameplayScreen = blnTurnScreen || blnWaitScreen || blnDisplayCard || blnPickCard;
		playButton.setVisible(blnPlayScreen);
		
		// Theme Screen
		btnStandard.setVisible(blnThemeScreen);
		btnPokemon.setVisible(blnThemeScreen);
		btnInsideOut.setVisible(blnThemeScreen);
		nameField.setVisible(blnThemeScreen);
		btnEnterGame.setVisible(blnThemeScreen);
		//host/join buttons
		btnHost.setVisible(blnThemeScreen);
		btnJoin.setVisible(blnThemeScreen);
		ipField.setVisible(blnThemeScreen);
		
		// Game screen
		boolean blnShowGameBtns = blnTurnScreen && !blnHelp && !blnLeaderBoard;
		btnHelp.setVisible(blnTurnScreen);
		btnPickUp.setVisible(blnShowGameBtns);
		btnLeaderBoard.setVisible(blnTurnScreen);
		btnPrev.setVisible(blnShowGameBtns);
		btnNext.setVisible(blnShowGameBtns);
		btnContinue.setVisible(blnDisplayCard);
		btnChat.setVisible(blnGameplayScreen);
		
		// Wild color picker buttons
		btnWildRed.setVisible(blnWildPicker);
		btnWildBlue.setVisible(blnWildPicker);
		btnWildGreen.setVisible(blnWildPicker);
		btnWildYellow.setVisible(blnWildPicker);
		
		// Eliminated screen button
		btnEliminatedOK.setVisible(blnEliminated);
		
		// chat area screen overlay
		chatInput.setVisible(blnChat && blnGameplayScreen);
		chatScroll.setVisible(blnChat && blnGameplayScreen);
	}
	
	// Reset Screens
	private void resetScreens(){
		blnEnterScreen = false;
		blnPlayScreen = false;
		blnThemeScreen = false;
		blnWaitScreen = false;
		blnPickCard = false;
		blnDisplayCard = false;
		blnTurnScreen = false;
		blnGameOver = false;
		blnHelp = false;
		blnLeaderBoard = false;
		blnFlipFirst   = false;
		blnEliminated  = false;
	}
	
	// Translate Screen
	/**
	 * Shows the main gameplay screen.
	 * Players can see their cards and take turns here.
	 */
	public void showPlayScreen(){
		resetScreens();
		blnPlayScreen = true;
		setComponentVisibility();
	}
	
	/**
	 * Shows the theme selection screen.
	 * Allows the player to choose a visual theme for the game.
	 */
	public void showThemeScreen(){
		resetScreens();
		blnThemeScreen = true;
		setComponentVisibility();
	}
	
	/**
	 * Shows the waiting screen.
	 * This is used while waiting for other players in multiplayer.
	 */
	public void showWaitScreen(){
		resetScreens();
		blnWaitScreen = true;
		setComponentVisibility();
	}
	
	/**
	 * Shows the screen where a player must pick a card.
	 * This can happen when no valid moves are available.
	 */
	public void showPickCard(){
		resetScreens();
		blnPickCard = true;
		setComponentVisibility();
	}
	
	/**
	 * Shows the display card screen.
	 * This screen shows the top card currently in play.
	 */
	public void showDisplayCard(){
		resetScreens();
		blnDisplayCard = true;
		setComponentVisibility();
	}
	
	/**
	 * Shows the turn screen.
	 * Displays whose turn it is before gameplay continues.
	 */
	public void showTurnScreen(){
		resetScreens();
		blnTurnScreen = true;
		setComponentVisibility();
	}
	
	/**
	 * Shows the game over screen.
	 * Displays the winner of the game.
	 *
	 * strWinner: The name of the winning player
	 */
	public void showGameOver(String strWinner){
		this.strWinner = strWinner;
		resetScreens();
		blnGameOver = true;
		setComponentVisibility();
	}
	
	/**
	 * Shows the screen where the first card is flipped.
	 * This usually happens at the start of the game.
	 */
	public void showFlipFirst(){
		resetScreens();
		blnFlipFirst = true;
		setComponentVisibility();
	}
	
	/**
	 * Shows the eliminated screen.
	 * This screen appears when a player is out of the game.
	 */
	public void showEliminated(){
		// overlay on top of current screen
		blnEliminated = true;
		setComponentVisibility();
	}
	
	/**
	 * Connects this view to the controller.
	 * The controller handles game logic and user actions.
	 *
	 * controller: The UnoController used to manage game logic
	 */
	public void setController(UnoController controller){
		this.controller = controller;
	}
	
	/**
	 * Connects this view to the model.
	 * The model stores all game data such as players, cards, and scores.
	 *
	 * model: The UnoModel containing the game data
	 */
	public void setModel(UnoModel model){
		this.model = model;
	}
	
	/**
	 * Connects this view to the network system.
	 * Used for sending and receiving multiplayer data.
	 *
	 * network: The UnoNetwork used for communication
	 */
	public void setNetwork(UnoNetwork network){
		this.network = network;
	}
	
	/**
	 * Handles messages received from the network (multiplayer).
	 * It reads the message and updates the game state or UI accordingly.
	 *
	 * strMessage: The message received from another player or server
	 */
	public void processNetworkMessage(String strMessage){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				if(strMessage.startsWith("[CHAT]")){
					chatArea.append(strMessage + "\n");
					chatArea.setCaretPosition(chatArea.getDocument().getLength()); 
					boolean blnGameplayScreen = blnTurnScreen || blnWaitScreen || blnDisplayCard || blnPickCard;
					if(blnGameplayScreen){
						blnChat = true;
					}
					setComponentVisibility();
					repaint();
				}else if(strMessage.startsWith("[GAME MESSAGE]")){
					chatArea.append(strMessage + "\n");
					chatArea.setCaretPosition(chatArea.getDocument().getLength());
					boolean blnGameplayScreen = blnTurnScreen || blnWaitScreen || blnDisplayCard || blnPickCard;
					if(blnGameplayScreen){
						blnChat = true;
					}
					setComponentVisibility();
					repaint();
				}else if(strMessage.startsWith("GAMEOVER")){
					showGameOver(strMessage);
					
				}else if(strMessage.startsWith("TURN|")){
					if(blnWaitScreen  || blnDisplayCard){
						String[] strTurnParts = strMessage.split("\\|");
						String strCardPlayed = strTurnParts.length >= 2 ? strTurnParts[1] : "";
						// Capture chosen wild colour if present
						if(strTurnParts.length >= 3 && !strTurnParts[2].equals("")){
							strWildColor = strTurnParts[2];
						}
						
						/*
						
						if(model != null){
							int intMyIndex = (controller != null) ? controller.getLocalPlayerIndex() : 0;
							intCardCount1 = model.intHandSizes[intMyIndex];
							int intOppSlot = 0;
							for(int i = 0; i < 3; i++){
								if(i != intMyIndex){
									if (intOppSlot == 0) intCardCount2 = model.intHandSizes[i];
									else                 intCardCount3 = model.intHandSizes[i];
									intOppSlot++;
								}
							}
						}
						*/
						
						if (model != null && !strCardPlayed.equals("") && !strCardPlayed.equals("draw")) {
							for (int d = 0; d < model.intDeckSize; d++) {
								if (model.strDeck[d][0].equals(strCardPlayed)) {
									model.strDiscardPile[model.intDiscardPileSize] = model.strDeck[d];
									model.intDiscardPileSize++;
									break;
								}
							}
						}
						
						
						if (model != null && strTurnParts.length >= 4) {
							String[] strSizes = strTurnParts[3].split(",");
							if (strSizes.length == 3) {
								for (int i = 0; i < 3; i++) {
									model.intHandSizes[i] = Integer.parseInt(strSizes[i]);
								}
							}
							int intMyIndex = (controller != null) ? controller.getLocalPlayerIndex() : 0;
							intCardCount1 = model.intHandSizes[intMyIndex];
							int intOppSlot = 0;
							for (int i = 0; i < 3; i++) {
								if (i != intMyIndex) {
									if (intOppSlot == 0) intCardCount2 = model.intHandSizes[i];
									else                 intCardCount3 = model.intHandSizes[i];
									intOppSlot++;
								}
							}
							// Also sync the view's local discard pile
							intDiscardPileSize = model.intDiscardPileSize;
							for (int i = 0; i < intDiscardPileSize; i++) {
								strDiscardPile[i] = model.strDiscardPile[i];
							}
						}
										
						String strValue = strCardPlayed.replace("yellow","").replace("green","")
														.replace("blue","").replace("red","");
						boolean blnIsSkip = strValue.equals("skip")
										 || strValue.equals("draw2")
										 || strValue.equals("wilddraw4");
						advanceTurn();
						if (blnIsSkip) {
							advanceTurn();
						}
					}
				}else if(strMessage.startsWith("SETUP|")){
					// Parse the host's dealt hands
					String[] strParts = strMessage.split("\\|");			
					if(strParts.length >= 6){
						// Load each player's hand into the model
						for(int p = 0; p < 3; p++){
							model.intHandSizes[p] = 0;
							if(!strParts[p + 1].equals("")){
								String[] strCards = strParts[p + 1].split(",");
								for(int c = 0; c < strCards.length; c++){
									// Find this card in the model's deck by name
									for(int d = 0; d < model.intDeckSize; d++){
										if(model.strDeck[d][0].equals(strCards[c])){
											model.strHands[p][model.intHandSizes[p]] = model.strDeck[d];
											model.intHandSizes[p]++;
											break;
										}
									}
								}
							}
						}
						
						// Load turn order
						String[] strOrder = strParts[4].split(",");
						for(int i = 0; i < 3 && i < strOrder.length; i++){
							model.intTurnOrder[i] = Integer.parseInt(strOrder[i]);
						}
						model.intCurrentTurn = 0;
						
						// Set top discard
						String strTopName = strParts[5];
						for(int d = 0; d < model.intDeckSize; d++){
							if(model.strDeck[d][0].equals(strTopName)){
								model.strDiscardPile[0] = model.strDeck[d];
								model.intDiscardPileSize = 1;
								break;
							}
						}
						
						
						if(strParts.length >= 7){
							String[] strNames = strParts[6].split(",");
							for(int i = 0; i < 3 && i < strNames.length; i++){
								strPlayNames[i] = strNames[i];
							}
						}
						
						// sync view from model
						startGame();
					}
				}
			}
		});
	}

	//added
	/**
	 * Gets the local IP address of the current device.
	 * Used when hosting a multiplayer game.
	 *
	 * return The local IP address as a string
	 */
	private String getLocalIPAddress(){
		try{
			java.util.Enumeration<java.net.NetworkInterface> interfaces = 
				java.net.NetworkInterface.getNetworkInterfaces();
			while(interfaces.hasMoreElements()){
				java.net.NetworkInterface iface = interfaces.nextElement();
				if(iface.isLoopback() || !iface.isUp()) continue;
				
				java.util.Enumeration<java.net.InetAddress> addresses = iface.getInetAddresses();
				while(addresses.hasMoreElements()){
					java.net.InetAddress addr = addresses.nextElement();
					if(!(addr instanceof java.net.Inet4Address)) continue;
						String strIP = addr.getHostAddress();

						// ADDED: accept site-local OR any common private-range prefix
						if(addr.isSiteLocalAddress()
								|| strIP.startsWith("192.168.")
								|| strIP.startsWith("10.")
								|| strIP.startsWith("172.")){
							System.out.println("Using IP: " + strIP
								+ " from interface: " + iface.getDisplayName());
							return strIP;
						}
				}
			}
		}catch(Exception e){
			System.out.println("Could not get local IP: " + e.getMessage());
		}
		
		// ADDED: hostname-based fallback before giving up
		try{
			String strFallback = java.net.InetAddress.getLocalHost().getHostAddress();
			if(!strFallback.equals("127.0.0.1")){
				System.out.println("Fallback IP via hostname: " + strFallback);
				return strFallback;
			}
		}catch(Exception e){
			System.out.println("Hostname fallback failed: " + e.getMessage());
		}
		
		
		return "127.0.0.1";
	}
	/*
	private String getLocalIPAddress(){
		try{
			java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
			while(interfaces.hasMoreElements()){
				java.net.NetworkInterface iface = interfaces.nextElement();
				// skip loopback and inactive interfaces
				if(iface.isLoopback() || !iface.isUp()){
					continue;
				}
				java.util.Enumeration<java.net.InetAddress> addresses = iface.getInetAddresses();
				while(addresses.hasMoreElements()){
					java.net.InetAddress addr = addresses.nextElement();
					// only want IPv4 site-local addresses (192.168.x.x, 10.x.x.x, 172.16.x.x)
					if(addr instanceof java.net.Inet4Address && addr.isSiteLocalAddress()){
						return addr.getHostAddress();
					}
				}
			}
		}catch(Exception e){
			System.out.println("Could not get local IP: " + e.getMessage());
		}
		// fallback
		return "127.0.0.1";
	}
	*/
	/*
	private String getLocalIPAddress(){
		try{
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			
			while(interfaces.hasMoreElements()){
				NetworkInterface networkInterface = interfaces.nextElement();
				
				if(networkInterface.isLoopback() || !networkInterface.isUp()){
					continue;
				}
				
				Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
				
				while(addresses.hasMoreElements()){
					InetAddress address = addresses.nextElement();
					
					if(address instanceof Inet4Address){
						String strIP = address.getHostAddress();
						
						if(strIP.startsWith("192.168.") || strIP.startsWith("10.") || strIP.startsWith("172.")){
							return strIP;
						}
					}
				}
			}
		}catch(Exception e){
			System.out.println("Could not find local IP: " + e.getMessage());
		}
		
		return "unknown";
	}
	*/

	/**
	 * Default public constructor initializing of UnoView
	*/
	// Constructor
	public UnoView(){
		// Panel Setup
		this.setLayout(null);
		this.setPreferredSize(new Dimension(intWidth, intHeight));
		this.setBackground(new Color(10, 20, 60));
		this.addMouseListener(this);
		this.addKeyListener(this);
		this.setFocusable(true);
		
		// Play button setup
		playButton.setBounds(560, 560, 160, 55);
		playButton.setVisible(false);
		playButton.addActionListener(this);
		this.add(playButton);
		
		// Theme screen setup
		btnStandard.setBounds(530, 165, 200, 40);
		btnStandard.setVisible(false);
		btnStandard.addActionListener(this);
		this.add(btnStandard);
		
		btnPokemon.setBounds(530, 215, 200, 40);
		btnPokemon.setVisible(false);
		btnPokemon.addActionListener(this);
		this.add(btnPokemon);

		btnInsideOut.setBounds(530, 265, 200, 40);
		btnInsideOut.setVisible(false);
		btnInsideOut.addActionListener(this);
		this.add(btnInsideOut);

		nameField.setBounds(430, 490, 420, 45);
		nameField.setVisible(false);
		this.add(nameField);
		
		// start game button
		btnEnterGame.setBounds(530, 550, 200, 45);
		btnEnterGame.setVisible(false);
		btnEnterGame.addActionListener(this);
		this.add(btnEnterGame);
		
		// Help button
		btnHelp.setBounds(1040, 230, 210, 45);
		btnHelp.setVisible(false);
		btnHelp.addActionListener(this);
		this.add(btnHelp);

		//Host game button
		btnHost.setBounds(340, 610, 180, 40);
		btnHost.setVisible(false);
		btnHost.addActionListener(this);
		this.add(btnHost);
		
		//Join game Button
		btnJoin.setBounds(760, 610, 180, 40);
		btnJoin.setVisible(false);
		btnJoin.addActionListener(this);
		this.add(btnJoin);
		
		//Ip address 
		ipField.setBounds(535, 610, 210, 40);
		ipField.setVisible(false);
		this.add(ipField);
		

		// Pick up a card button
		btnPickUp.setBounds(1040, 290, 210, 45);
		btnPickUp.setVisible(false);
		btnPickUp.addActionListener(this);
		this.add(btnPickUp);

		// Leaderboard button
		btnLeaderBoard.setBounds(1040, 350, 210, 45);
		btnLeaderBoard.setVisible(false);
		btnLeaderBoard.addActionListener(this);
		this.add(btnLeaderBoard);
		
		// Chat button
		btnChat.setBounds(1040, 410, 210, 45);
        btnChat.setVisible(false);
        btnChat.addActionListener(this);
        this.add(btnChat);
        
		// previous/next buttons
		btnPrev.setBounds(320, 575, 130, 42);
		btnPrev.setVisible(false);
		btnPrev.addActionListener(this);
		this.add(btnPrev);

		btnNext.setBounds(830, 575, 130, 42);
		btnNext.setVisible(false);
		btnNext.addActionListener(this);
		this.add(btnNext);
		
		// cont button
		btnContinue.setBounds(490, 630, 300, 50);
		btnContinue.setVisible(false);
		btnContinue.addActionListener(this);
		this.add(btnContinue);
		
		// Wild color picker buttons
		btnWildRed.setBackground(new Color(220, 50, 50));
		btnWildRed.setForeground(Color.WHITE);
		btnWildRed.setFont(buttonFont);
		btnWildRed.setFocusPainted(false);
		btnWildRed.setBounds(370, 320, 120, 120);
		btnWildRed.setVisible(false);
		btnWildRed.addActionListener(this);
		this.add(btnWildRed);
		
		btnWildBlue.setBackground(new Color(30, 100, 200));
		btnWildBlue.setForeground(Color.WHITE);
		btnWildBlue.setFont(buttonFont);
		btnWildBlue.setFocusPainted(false);
		btnWildBlue.setBounds(510, 320, 120, 120);
		btnWildBlue.setVisible(false);
		btnWildBlue.addActionListener(this);
		this.add(btnWildBlue);
		
		btnWildGreen.setBackground(new Color(30, 160, 80));
		btnWildGreen.setForeground(Color.WHITE);
		btnWildGreen.setFont(buttonFont);
		btnWildGreen.setFocusPainted(false);
		btnWildGreen.setBounds(650, 320, 120, 120);
		btnWildGreen.setVisible(false);
		btnWildGreen.addActionListener(this);
		this.add(btnWildGreen);
		
		btnWildYellow.setBackground(new Color(230, 190, 30));
		btnWildYellow.setForeground(Color.WHITE);
		btnWildYellow.setFont(buttonFont);
		btnWildYellow.setFocusPainted(false);
		btnWildYellow.setBounds(790, 320, 120, 120);
		btnWildYellow.setVisible(false);
		btnWildYellow.addActionListener(this);
		this.add(btnWildYellow);
		
		// Eliminated OK button
		btnEliminatedOK.setBounds(440, 440, 400, 55);
		btnEliminatedOK.setVisible(false);
		btnEliminatedOK.addActionListener(this);
		this.add(btnEliminatedOK);
		
		// chat components
		chatArea.setFont(bodyFont);
		chatArea.setForeground(Color.WHITE);
		chatArea.setOpaque(false);
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);
		chatArea.setWrapStyleWord(true);

		chatScroll.setBounds(15, 50, 290, 250);
		chatScroll.setOpaque(false);
		chatScroll.getViewport().setOpaque(false);
		chatScroll.setBorder(BorderFactory.createEmptyBorder());
		chatScroll.setVisible(false);
		this.add(chatScroll);
		
		chatInput.setBounds(15, 312, 290, 35);
		chatInput.setFont(bodyFont);
		chatInput.setForeground(Color.WHITE);
		chatInput.setBackground(new Color(30, 30, 60));
		chatInput.setBorder(BorderFactory.createLineBorder(goldColor, 1));
		chatInput.setCaretColor(Color.WHITE);
		chatInput.setVisible(false);
		chatInput.addActionListener(this);
		this.add(chatInput);
		
		// Frame Setup
		theFrame.setContentPane(this);
		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		theFrame.pack();
		theFrame.setResizable(false);
		theFrame.setVisible(true);
		
		// Timer (60 fps)
		Timer timer = new Timer(16, this);
		timer.start();
		
		// Image
		String strPath = "Image/Background/";
		try{
			imgStart = ImageIO.read(new File(strPath + "start_bg.png"));
			imgBackground = ImageIO.read(new File(strPath + "general_bg.png"));
			imgWait = ImageIO.read(new File(strPath + "wait_bg.png"));
			// imgPickCard = ImageIO.read(new File(strPath + "pick_card_bg.png"));
			// imgDisplay = ImageIO.read(new File(strPath + "display_bg.png"));
			// imgYourTurn = ImageIO.read(new File(strPath + "your_turn_bg.png"));
		 	// imgGameOver = ImageIO.read(new File(strPath + "general_bg.png"));
			// imgEliminated = ImageIO.read(new File(strPath + "general_bg.png"));
		}catch(IOException e){
			System.out.println("Error: Could not load image");
			e.printStackTrace();
		}
		loadDeck();
		preloadCardImages();
	}
	
	// Main Method
	/*
	public static void main(String[] args){
		new UnoView();
	}
	*/
}

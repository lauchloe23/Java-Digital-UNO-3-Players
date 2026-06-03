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

public class UnoView extends JPanel implements ActionListener, MouseListener, KeyListener{
	// Properties
	final int intWidth = 1280;
	final int intHeight = 720;
	final int intMaxCards = 30;
	final int intStartCards = 7;
	final int intPerPage = 2;

	// JFrame 
	JFrame theFrame = new JFrame("UNO");
	
	// Deck & Cards Array 
	// 100 cards in a deck
	String[][] strDeck = new String[100][4];
	String[][] strDrawPile = new String[100][4];
	String[][] strDiscardPile = new String[100][4];
	String[][] strPlayHand = new String[30][4];
	
	// Card Counts in each pile
	int intDeckSize = 0;
	int intDrawPileSize = 0;
	int intDiscardPileSize = 0;
	int intHandSize = 0;
	
	// current page of cards shown on your turn screen
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
	
	// Image Variables
	BufferedImage imgStart = null;
	BufferedImage imgBackground = null;
	BufferedImage imgWait = null;
	BufferedImage imgPickCard = null;
	BufferedImage imgDisplay = null;
	BufferedImage imgYourTurn = null;
	BufferedImage imgGameOver = null;
	BufferedImage imgDecision = null;
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
	
	// JComponent (Chat)
	JTextField chatInput = new JTextField();
	JTextArea chatArea = new JTextArea();
	
	// JComponent (game play)
	JButton btnPrev = createGoldButton("<PREV");
	JButton btnNext = createGoldButton("Next>");
	JButton btnContinue = createGoldButton("CONTINUE>");
	
	// Action Listener
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
				strName = strTyped;
				strPlayNames[0] = strName;
				startGame();
			}
		}else if(e.getSource() == btnHelp){
			blnHelp = !blnHelp;
			blnLeaderBoard = false;
			setComponentVisibility();
		}else if(e.getSource() == btnLeaderBoard){
			blnLeaderBoard = !blnLeaderBoard;
			blnHelp = false;
			setComponentVisibility();
		}else if(e.getSource() == btnPickUp){
			drawFromPile();
		}else if(e.getSource() == btnContinue){
			fadeToScreen("turn");
		}else if(e.getSource() == btnPrev){
			if(intCardPage > 0){
				intCardPage--;
			}
		}else if(e.getSource() == btnNext){
			// Total pages = round up (handSize / cardsPerPage)
			int intTotalPages = (intHandSize + intPerPage - 1) / intPerPage;
			if(intTotalPages == 0) intTotalPages = 1;
			if(intCardPage < intTotalPages - 1){
				intCardPage++;
			}
		}else if(e.getSource() == nameField){
			String strTyped = nameField.getText().trim();
			if(!strTyped.equals("")){
				strName = strTyped;
				strPlayNames[0] = strName;
				startGame();
			}
		}else if(e.getSource() == chatInput){
			String strMsg = chatInput.getText().trim();
			if(!strMsg.equals("")){
					chatArea.append(strName + ": " + strMsg + "\n");
					chatInput.setText("");
					// need to send via socket to other players
				}
		}
		repaint();
	}
	
	// Mouse Listener Method Overrides
	public void mouseClicked(MouseEvent e){
		if(blnEnterScreen){
			fadeToScreen("play");
			// showPlayScreen();
		}else if(blnTurnScreen && !blnHelp && !blnLeaderBoard){
			handleCardClick(e.getX(), e.getY());
		}
	}
	
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	
	// Key Listener Method Overrides
	public void keyPressed(KeyEvent e){
		if(blnEnterScreen){
			fadeToScreen("play");
			// showPlayScreen();
		}
	}
	public void keyTyped(KeyEvent e){}
	public void keyReleased(KeyEvent e){}
	
	// load card images
	private void preloadCardImages(){
		String strCardPath = "../Image/Cards/";
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
	private void drawCenteredString(Graphics2D g2, String strMessage, int intCenterX, int intY){
		FontMetrics metrics = g2.getFontMetrics();
		int intX = intCenterX - metrics.stringWidth(strMessage)/2;
		g2.drawString(strMessage, intX, intY);
	}
	
	// draw gold button method
	private JButton createGoldButton(String strText){
		JButton button = new JButton(strText);
		button.setFont(buttonFont);
		button.setForeground(Color.WHITE);
		button.setBackground(btnGold);
		button.setFocusPainted(false);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setOpaque(true);
		button.setContentAreaFilled(true);
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
			File f = new File("../Image/Cards/" + strFileName);
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
	private void startGame(){
		// loadDeck();
		shuffleDeck();
		randomizeTurnOrder();
		dealStartingHands();
		flipFirstCard();
		intCardPage = 0;

		// Local player is index 0 in strPlayNames
		// If intTurnOrder[0] == 0, local player goes first
		if(intTurnOrder[intCurrentTurn] == 0){
			fadeToScreen("turn");
		}else{
			fadeToScreen("wait");
		}
	}
	
	private void loadDeck(){
		intDeckSize = 0;
		String strCSVPath = "../Graphics/cards.csv";
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
				strWinner = getOpponentName();
				fadeToScreen("gameover");
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
	
	// player's card
	private void playCard(int intIndex){
		if(intIndex < 0 || intIndex >= intHandSize){
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
		advanceTurn();
	}
	
	// move to next player
	private void advanceTurn(){
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
		}
	}
	
	// Paint Component Method
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
		g2.fillRoundRect(300, 80, 680, 560, 20, 20);
		
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
			drawCenteredString(g2, "Name required to start", 640, 630);
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
	private void setComponentVisibility(){
		// Play screen
		playButton.setVisible(blnPlayScreen);
		
		// Theme Screen
		btnStandard.setVisible(blnThemeScreen);
		btnPokemon.setVisible(blnThemeScreen);
		btnInsideOut.setVisible(blnThemeScreen);
		nameField.setVisible(blnThemeScreen);
		btnEnterGame.setVisible(blnThemeScreen);
		
		// Game screen
		boolean blnShowGameBtns = blnTurnScreen && !blnHelp && !blnLeaderBoard;
		btnHelp.setVisible(blnTurnScreen);
		btnPickUp.setVisible(blnShowGameBtns);
		btnLeaderBoard.setVisible(blnTurnScreen);
		btnPrev.setVisible(blnShowGameBtns);
		btnNext.setVisible(blnShowGameBtns);
		btnContinue.setVisible(blnDisplayCard);
		
		// chat area screen overlay
		chatInput.setVisible(blnChat && blnTurnScreen);
		chatArea.setVisible(blnChat && blnTurnScreen);
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
	}
	
	// Translate Screen
	public void showPlayScreen(){
		resetScreens();
		blnPlayScreen = true;
		setComponentVisibility();
	}
	
	public void showThemeScreen(){
		resetScreens();
		blnThemeScreen = true;
		setComponentVisibility();
	}
	
	public void showWaitScreen(){
		resetScreens();
		blnWaitScreen = true;
		setComponentVisibility();
	}
	
	public void showPickCard(){
		resetScreens();
		blnPickCard = true;
		setComponentVisibility();
	}
	
	public void showDisplayCard(){
		resetScreens();
		blnDisplayCard = true;
		setComponentVisibility();
	}
	
	public void showTurnScreen(){
		resetScreens();
		blnTurnScreen = true;
		setComponentVisibility();
	}
	
	public void showGameOver(String strWinner){
		this.strWinner = strWinner;
		resetScreens();
		blnGameOver = true;
		setComponentVisibility();
	}
	
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
		btnHelp.setBounds(30, 230, 210, 45);
		btnHelp.setVisible(false);
		btnHelp.addActionListener(this);
		this.add(btnHelp);

		// Pick up a card button
		btnPickUp.setBounds(30, 290, 210, 45);
		btnPickUp.setVisible(false);
		btnPickUp.addActionListener(this);
		this.add(btnPickUp);

		// Leaderboard button
		btnLeaderBoard.setBounds(30, 350, 210, 45);
		btnLeaderBoard.setVisible(false);
		btnLeaderBoard.addActionListener(this);
		this.add(btnLeaderBoard);
		
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
		
		// chat components
		chatArea.setBounds(15, 50, 290, 250);
		chatArea.setFont(bodyFont);
		chatArea.setForeground(Color.WHITE);
		chatArea.setOpaque(false);
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);
		chatArea.setWrapStyleWord(true);
		chatArea.setVisible(false);
		this.add(chatArea);
		
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
		String strPath = "../Image/Background/";
		try{
			imgStart = ImageIO.read(new File(strPath + "start_bg.png"));
			imgBackground = ImageIO.read(new File(strPath + "general_bg.png"));
			imgWait = ImageIO.read(new File(strPath + "wait_bg.png"));
			
			// imgPickCard = ImageIO.read(new File(strPath + "pick_card_bg.png"));
			// imgDisplay = ImageIO.read(new File(strPath + "display_bg.png"));
			// imgYourTurn = ImageIO.read(new File(strPath + "your_turn_bg.png"));
			// imgGameOver = ImageIO.read(new File(strPath + "game_over_bg.png"));
			loadDeck();
			preloadCardImages();
		}catch(IOException e){
			System.out.println("Error: Could not load image");
			e.printStackTrace();
		}
	
	}
	
	// Main Method
	public static void main(String[] args){
		new UnoView();
	}
	
}

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class UnoView extends JPanel implements ActionListener{
	// Properties
	final int intWidth = 1280;
	final int intHeight = 720;
	
	// JFrame 
	JFrame theFrame = new JFrame("UNO");
	
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
	BufferedImage imtWait = null;
	BufferedImage imgPickCard = null;
	BufferedImage imgDisplay = null;
	BufferedImage imgYourTurn = null;
	BufferedImage imgGameOver = null;
	BufferedImage imgDecision = null;
	
	// Game Data
	String strName = "Player";
	String strWinner = "";
	int intTheme = 0; // Standard = 0. Pokemon = 1. InsideOut = 2.
	
	// Player Card Count
	int intCardCount1; // player 1
	int intCardCount2; // player 2
	int intCardCount3; // player 3
	
	// Draw Cards (color, value)
	// int[][] strCard = {}
	
	// int intDrawnCard[] = 
	// int intDecisionCard[] =
	
	// JComponent (Play Screen)
	JButton playButton = createGoldButton("PLAY");
	
	// JComponent (Theme)
	JButton btnStandard = createGoldButton("STANDARD");
	JButton btnPokemon = createGoldButton("POKEMON");
	JButton btnInsideOut = createGoldButton("INSIDEOUT");
	JTextField nameField = new JTextField();
	
	// JComponent (Game)
	JButton btnHelp = createGoldButton("HELP");
	JButton btnPickUp = createGoldButton("PICK UP A CARD");
	JButton btnLeaderBoard = createGoldButton("LEADERBOARD");
	
	// JComponent (Chat)
	JTextField chatInput = new JTextField();
	JTextArea chatArea = new JTextArea();
	
	
	// Action Listener
	public void actionPerformed(ActionEvent e){
		repaint();
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
		// button.createLineBorder(btnGoldDark, 2);
		button.setFocusPainted(false);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // have to look if code works
		button.setOpaque(true);
		button.setContentAreaFilled(true);
		
		// have to add mouse listener to button
		// button.addMouseListener(
		
		return button;
	}
	
	// highlight selected theme when selecting theme
	private void highlightSelectedTheme(Graphics2D g2){
		int[] btnY = {165, 215, 265};
		for(int i = 0; i < 3; i++){
			if(intTheme == i){
				g2.setColor(goldColor);
				g2.setStroke(new BasicStroke(3));
				g2.drawRoundRect(430-5, btnY[i] - 5, 220, 50, 14, 14);
				g2.setStroke(new BasicStroke(1));
			}
		}
	}
	
	// Back of Card (Visual) 
	private void drawCardBack(Graphics2D g2, int intX, int intY, int intCardWidth, int intCardHeight){
		g2.setColor(new Color(0, 0, 0, 100));
		g2.fillRoundRect(intX+4, intY+4, intCardWidth, intCardHeight, 14, 14);
		g2.setColor(new Color(20, 20, 80));
		g2.fillRoundRect(intX, intY, intCardWidth, intCardHeight, 14, 14);
		g2.setColor(unoRed);
		g2.fillOval(intX+intCardWidth/4, intY+intCardHeight/4, intCardWidth/2, intCardHeight/2);
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Georgia", Font.BOLD, 22));
		drawCenteredString(g2, "UNO", intX+intCardWidth/2, intY+intCardHeight/2+8);
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke(2));
		g2.drawRoundRect(intX, intY, intCardWidth, intCardHeight, 14, 14);
		g2.setStroke(new BasicStroke(1));
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
			drawWait(g2);
		}else if(blnWaitScreen){
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
		drawCenteredString(g2, "INSTRUCTION", 640, 220);
		
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
	
	// Draw play method
	private void drawPlay(Graphics2D g2){
		// imgStart as background
		// PlayButton JComponent (have to positive in constructor)
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
		
		
		// Waiting for player
		g2.setColor(transparentBlack);
		g2.fillRoundRect(290, 270, 700, 180, 20, 20);
		
		g2.setFont(titleFont);
		g2.setColor(Color.WHITE);
		drawCenteredString(g2, "WAITING FOR", 640, 330);
		g2.setColor(goldColor);
		drawCenteredString(g2, strName.toUpperCase(), 640, 390);
	}
	private void drawPickCard(Graphics2D g2){
		if(imgPickCard != null){
			g2.drawImage(imgPickCard, 0, 0, intWidth, intHeight, null);
		}else{
			drawBackground(g2);
		}
		
		
	}
	private void drawDisplayCard(Graphics2D g2){
		if(imgDisplay != null){
			g2.drawImage(imgDisplay, 0, 0, intWidth, intHeight, null);
		}else{
			drawBackground(g2);
		}
		
		// Show drawn card face
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
		int intCol = 5;
		
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
		int intHeightY;
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
	
	private void drawLeaderBoard(Graphics2D g2){}
	private void drawChat(Graphics2D g2){}
	
	/*
	// Draw Help
	private void drawHelp(Graphics2D g2){
		g2.setColor(transparentBlack);
		g2.fillRoundRect(200, 100, 880, 520, 10, 10);
		
		g2.setColor(Color.WHITE);
		g2.setFont(titleFont);
		g2.drawString("Help", 600, 150);
	}
	*/
	
	// Display of JButton
	private void setComponentVisibility(){
		// Play screen
		playButton.setVisible(blnPlayScreen);
		
		// Theme Screen
		btnStandard.setVisible(blnThemeScreen);
		btnPokemon.setVisible(blnThemeScreen);
		btnInsideOut.setVisible(blnThemeScreen);
		nameField.setVisible(blnThemeScreen);
		
		// Game screen
		btnHelp.setVisible(blnTurnScreen);
		btnPickUp.setVisible(blnTurnScreen);
		btnLeaderBoard.setVisible(blnThemeScreen);
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
	
	public void showDisplayCard(){}
	
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
		
		// Frame Setup
		theFrame.setContentPane(this);
		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		theFrame.pack();
		theFrame.setResizable(false);
		theFrame.setVisible(true);
		
		// Timer (60 fps)
		Timer timer = new Timer(16, this);
		timer.start();
		
		// load images
		/*
		try{
			imgStart = ImageIO.read(new File("start.png"));
			imgBackground = ImageIO.read(new File("background.png"));
		}catch(IOException e){
			System.out.println("Unable to load image");
		}
		*/
	}
	
	// Main Method
	public static void main(String[] args){
		new UnoView();
	}
	
}

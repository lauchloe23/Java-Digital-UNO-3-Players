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
	
	private void drawWait(Graphics2D g2){}
	private void drawPickCard(Graphics2D g2){}
	private void drawDisplayCard(Graphics2D g2){}
	private void drawYourTurn(Graphics2D g2){}
	private void drawGameOver(Graphics2D g2){}
	private void drawHelp(Graphics2D g2){}
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

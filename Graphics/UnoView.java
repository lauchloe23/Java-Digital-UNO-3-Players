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
	Color btnGold = new Color(240, 180, 20);
	Color btnGoldDark = new Color(180, 130, 10);
	
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
	
	// Image
	BufferedImage imgStart = null;
	BufferedImage imgBackground = null;
	
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
	
	// JComponent (Play Screen);
	
	
	// Action Listener
	public void actionPerformed(ActionEvent e){
		repaint();
	}
	
	// Paint Component Method
	 public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      
		drawInstruction(g2);
        /*
        if(blnHelp){
			drawHelp(g2);
		}
		*/
	}
	
	// Draw Help
	private void drawHelp(Graphics2D g2){
		g2.setColor(transparentBlack);
		g2.fillRoundRect(200, 100, 880, 520, 10, 10);
		
		g2.setColor(Color.WHITE);
		g2.setFont(titleFont);
		g2.drawString("Help", 600, 150);
	}
	
	private void drawInstruction(Graphics2D g2){
		g2.setColor(transparentBlack);
		g2.fillRoundRect(200, 100, 880, 520, 10, 10);
		
		g2.setColor(Color.WHITE);
		g2.setFont(titleFont);
		g2.drawString("Instruction", 550, 150);
	}
	
	private void drawYourTurn(Graphics2D g2){}
	
	private void drawWait(Graphics2D g2){
		/*g2.drawImage(
		 * 
		 * imgWait
		 * imgWin
		 * imgPickCard
		*/
	}
	
	private void drawTheme(Graphics2D g2){}
	private void drawChat(Graphics2D g2){}
	
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

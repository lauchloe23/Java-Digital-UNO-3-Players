import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class UnoView extends JPanel implements ActionListener{
	// Properties
	int intWidth = 1280;
	int intHeight = 720;
	
	// JFrame 
	JFrame theFrame = new JFrame("UNO");
	
	// Color
	Color transparentBlack = new Color(0, 0, 0, 200);
	
	// Font
	Font titleFont = new Font("Georgia", Font.BOLD, 36);
	Font headerFont = new Font("Georgia", Font.BOLD, 22);
	Font bodyFont = new Font("Georgia", Font.PLAIN, 14);	

	
	// Screen
	boolean blnHelp = false;
	boolean blnThemeScreen = false;
	boolean blnEnterScreen = true;
	boolean blnTurnScreen = false;
	
	// Image
	BufferedImage imgStart = null;
	BufferedImage imgBackground = null;
	
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

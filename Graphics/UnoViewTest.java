import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class UnoViewTest extends JPanel implements ActionListener{
	// Properties
	final int intWidth = 1280;
	final int intHeight = 720;
	
	// JFrame 
	JFrame theFrame = new JFrame("UNO");
	BufferedImage imgBackground = null;
	
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
	
	// Action Listener
	public void actionPerformed(ActionEvent e){
		repaint();
	}
	
	 public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // increase graphics smoothness
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		g2.drawImage(imgBackground, 0, 0, intWidth, intHeight, null);
      }
	
	
	// Constructor
	public UnoViewTest(){
		// Panel Setup
		this.setLayout(null);
		this.setPreferredSize(new Dimension(intWidth, intHeight));
		this.setBackground(new Color(10, 20, 60));
		
		// Frame Setup
		theFrame.setContentPane(this);
		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		theFrame.pack();
		theFrame.setResizable(false);
		theFrame.setVisible(true);
		
		// Timer (60 fps)
		Timer timer = new Timer(16, this);
		timer.start();
		
		String strPath = "../Image/Background/";
		try{
			imgBackground = ImageIO.read(new File(strPath+"general_bg.png"));
		}catch(IOException e){
			System.out.println("Error: Could not load image");
			e.printStackTrace();
		}
	
	}
	
	// Main Method
	public static void main(String[] args){
		new UnoViewTest();
	}
	
}

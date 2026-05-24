import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class UnoView extends JPanel implements ActionListener{
	// Properties
	// private UnoModel model; (have to connect with the model)
	private JTextArea chatArea;
	private JTextField chatInput;
	
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
	}
	
	private void drawHelp(Graphics2D g2){
	
	}
	
	
	// Constructor
	public UnoView(){
		// this.model = model;
		this.setPreferredSize(new Dimension(1280, 720));
		
		// Timer (60 fps)
		Timer timer = new Timer(16, this);
		timer.start();
	}
	
}

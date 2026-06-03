import java.awt.event.*;

public class UnoController implements ActionListener{
	private UnoModel model;
	private UnoView view;
	private UnoNetwork network;
	
	// network varaible
	private static final int intDefaultPort = 5555;
	private boolean blnIsHost = false;
	private boolean blnConnected = false;
	
	public UnoController(UnoModel model, UnoView view, UnoNetwork network){
		this.model = model;
		this.view = view;
		this.network = network;
		
		// hand controller to view
		view.controller = this;
	}
	
}

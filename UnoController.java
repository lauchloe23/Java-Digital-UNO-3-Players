import java.awt.event.*;

public class UnoController implements ActionListener{
	//Properties
	private UnoModel model;
	private UnoView view;
	private UnoNetwork network;
	
	// network varaible
	private static final int intDefaultPort = 5555;
	private boolean blnIsHost = false;
	private boolean blnConnected = false;
	
	//Methods
	public void actionPerformed(ActionEvent evt){
		if(evt.getActionCommand() != null && evt.getActionCommand().equals("UNO_NETWORK_MESSAGE")){
			String strMessage = network.getLastMessage();
			handleNetworkMessage(strMessage);
		}
	}
	
	public void handleNetworkMessage(String strMessage){
		System.out.println("Network Message: " + strMessage);
		
		if(view != null){
			view.processNetworkMessage(strMessage);
		}
	}
	
	public boolean hostGame(){
		blnIsHost = true;
		Thread t = new Thread(){
			public void run(){
				blnConnected = network.startServer(intDefaultPort);
				System.out.println("Host result: " + blnConnected);
			}
		};
		t.start();
		return true; 
	}
	
	public boolean joinGame(String strIP){
		blnIsHost = false;
		Thread t = new Thread(){
			public void run(){
				blnConnected = network.connectServer(strIP, intDefaultPort);
				System.out.println("Join result: " + blnConnected);
			}
		};
		t.start();
		return true;
	}
	
	public void sendChat(String strPlayerName, String strMessage){
		if(network != null){
			network.sendPlayerChat(strPlayerName, strMessage);
		}
	}
	
	public void startGame(String strPlayerName){
		model.strPlayerNames[0] = strPlayerName;
		model.startGame();
		
		if(network != null){
			network.sendJoin(strPlayerName);
			if(blnIsHost){
				network.sendSeed(model.lngSeed);
			}
		}
	}
	
	//Controller
	public UnoController(){
		model = new UnoModel();
		view = new UnoView();
		network = new UnoNetwork(this);
		
		// hand controller to view
		view.setController(this);
		view.setModel(model);
		view.setNetwork(network);
		
	}
	
	public static void main(String[] args){
		new UnoController();
	}
	
}

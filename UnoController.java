import java.awt.event.*;
import javax.swing.SwingUtilities;

public class UnoController implements ActionListener{
	//Properties
	private UnoModel model;
	private UnoView view;
	private UnoNetwork network;
	
	// network varaible
	private static final int intDefaultPort = 8080;
	private boolean blnIsHost = false;
	private boolean blnConnected = false;
	private boolean blnGameStarted = false;
	
	//Methods
	public void actionPerformed(ActionEvent evt){
		if(evt.getActionCommand() != null && evt.getActionCommand().equals("UNO_NETWORK_MESSAGE")){
			String strMessage = network.getLastMessage();
			handleNetworkMessage(strMessage);
		}
	}
	/*
	public void handleNetworkMessage(String strMessage){
		System.out.println("Network Message: " + strMessage);
		
		if(view != null){
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					view.processNetworkMessage(strMessage);
				}
			});
		}
	}
	
	*/
	
	public void handleNetworkMessage(String strMessage){
		System.out.println("Network Message: " + strMessage);
		
		if(strMessage.startsWith("READY|")){
			if(blnIsHost && blnGameStarted){
				sendCurrentSetup();
			}
		}
		
		if(view != null){
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					view.processNetworkMessage(strMessage);
				}
			});
		}
	}
	
	private void sendCurrentSetup(){
		if(network != null){
			String[] strDiscardTop = model.getTopDiscard();
			
			if(strDiscardTop == null){
				strDiscardTop = new String[]{"", "", "", ""};
			}
			
			network.sendGameSetup(model.strHands, model.intHandSizes, model.intTurnOrder, strDiscardTop);
		}
	}
	
	public boolean hostGame(){
		blnIsHost = true;
		blnConnected = network.startServer(intDefaultPort);
		return blnConnected;
	}
	
	public boolean joinGame(String strIP){
		blnIsHost = false;
		blnConnected = network.connectServer(strIP, intDefaultPort);
		return blnConnected;
	}
	
	public void sendChat(String strPlayerName, String strMessage){
		if(network != null){
			network.sendPlayerChat(strPlayerName, strMessage);
		}
	}
	/*
	public void startGame(String strPlayerName){
		model.strPlayerNames[0] = strPlayerName;
		model.startGame();
		
		if(network != null){
			network.sendJoin(strPlayerName);
			if(blnIsHost){
				String[] strDiscardTop = model.getTopDiscard();
				if(strDiscardTop == null){
					strDiscardTop = new String[]{"", "", "", ""};
				}
				network.sendGameSetup(model.strHands, model.intHandSizes, model.intTurnOrder, strDiscardTop);
			}
		}
	}
	*/
	public boolean startGame(String strPlayerName){
		if(!blnConnected){
			return false;
		}
		
		model.strPlayerNames[0] = strPlayerName;
		
		if(network != null){
			network.sendJoin(strPlayerName);
		}
		
		if(!blnIsHost){
			if(network != null){
				network.send("READY|" + strPlayerName);
			}
			return false;
		}
		
		model.startGame();
		blnGameStarted = true;
		
		if(network != null){
			sendCurrentSetup();
		}
		
		return true;
	}
		
	
	public boolean isHost(){
		return blnIsHost;
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

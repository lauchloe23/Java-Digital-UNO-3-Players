
import java.awt.event.*;

public class UnoNetwork implements ActionListener{
	//Properties
	private SuperSocketMaster ssm;
	private ActionListener gameListener = null;
	private String strLastMessage = "";

	//Methods
	public void actionPerformed(ActionEvent evt){
		if(evt.getSource() == ssm){
			strLastMessage = ssm.readText();

			if(gameListener != null){
				gameListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "UNO_NETWORK_MESSAGE"));
			}
		}
	}
		
	public String getLastMessage(){
		return strLastMessage;
	}
	
	//starting server
	public boolean startServer(int intPort){
		ssm = new SuperSocketMaster(intPort, this);
		return ssm.connect();
	}
	
	public void sendTurnUpdate(String strCardPlayed, String strWildColor, int[] intHandSizes){
		if(ssm != null){
			String strSizes = intHandSizes[0] + "," + intHandSizes[1] + "," + intHandSizes[2];
			ssm.sendText("TURN|" + strCardPlayed + "|" + strWildColor + "|" + strSizes);
		}
	}
	
	//connecting users to server
	public boolean connectServer(String strIPadress, int intPort){
		ssm = new SuperSocketMaster(strIPadress, intPort,this);
		return ssm.connect();
	}
	
	//disconnecting user from network
	public void userDisconnect(){
		if(ssm!= null){
			ssm.disconnect();
		}
	}
	//Different Types of Messages sent through network: 
	//player to player chat message
	public void sendPlayerChat(String strPlayerName, String strMessage){
		if(ssm != null){
			ssm.sendText("[CHAT] " + strPlayerName + ": "+strMessage);
		}
	}
	
	//Game Messages:
	//joining game message
	public void sendJoin(String strPlayerName){
		if(ssm != null){
			ssm.sendText("[GAME MESSAGE] "+strPlayerName+ " has joined");
		}
	}
	
	//leaving game message
	public void sendLeft(String strPlayerName){
		if(ssm != null){
			ssm.sendText("[GAME MESSAGE] "+strPlayerName +  " has left");
		}
	}

	//selecting theme message
	public void sendTheme(String strPlayerName, String strThemeName){
		if(ssm != null){
			ssm.sendText("[GAME MESSAGE] "+strPlayerName + " SELECTED: " + strThemeName );
		}
	}
	
	//draw card message
	public void sendCardDrawn(String strPlayerName){
		if(ssm != null){
			ssm.sendText("[GAME MESSAGE] "+strPlayerName + " has drawn a card");
		}
	}
	
	//normal card played message
	public void sendPlayerCard(String strPlayerName, String strCardName){
		if(ssm != null){
			ssm.sendText("[GAME MESSAGE] "+strPlayerName + " played "+ strCardName);
		}
	}
	
	//Player attack/special move message
	public void sendPlayerAttack(String strPlayerName, String strAttackType, String strTargetName){
		if(ssm != null){
			ssm.sendText("[GAME MESSAGE] "+strPlayerName + " attacked "+ strTargetName + ": "+strAttackType);
		}
	}
	//colour change card message
	public void sendColourChange(String strPlayerName, String strColour){
		if(ssm != null){
			ssm.sendText("[GAME MESSAGE] "+strPlayerName + " changed colour to: "+ strColour);
		}
	}
	
	//send game over message
	public void sendGameOver(String strWinner){
		if (ssm != null){
			ssm.sendText("GAMEOVER: " + strWinner + " has won!!!");
		}
	}
	
	// send game setup (dealt hands + turn order) 
	public void sendGameSetup(String[][][] strHands, int[] intHandSizes, int[] intTurnOrder, String[] strDiscardTop){
		if(ssm != null){
			// Each card is encoded as cardName
			String strMsg = "SETUP|";
			
			for(int p = 0; p < 3; p++){
				for(int c = 0; c < intHandSizes[p]; c++){
					strMsg += strHands[p][c][0];
					if(c < intHandSizes[p] - 1) strMsg += ",";
				}
				strMsg += "|";
			}
			
			for(int i = 0; i < 3; i++){
				strMsg += intTurnOrder[i];
				if(i < 2) strMsg += ",";
			}
			strMsg += "|";
			// card name of top of dicard
			strMsg += strDiscardTop[0];
			
			ssm.sendText(strMsg);
		}
	}
	
	public void send(String strMessage){
		if(ssm != null){
			ssm.sendText(strMessage);
		}
	}

	//Constructor
	public UnoNetwork(ActionListener gameListener){
		this.gameListener = gameListener;
	}

}

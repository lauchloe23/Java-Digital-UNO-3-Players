package Network;

import java.awt.event.*;

public class UnoNetwork implements ActionListener{
	//Properties
	SuperSocketMaster ssm;
	private ActionListener gameListener = null;
	private String strLastMessage = "";

	//Methods
	public void actionPerformed(ActionEvent evt){
		if(evt.getSource() == ssm){
			strLastMessage = ssm.readText();

			if(gameListener != null){
				gameListener.actionPerformed(
					new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "UNO_NETWORK_MESSAGE")
				);
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
			ssm.sendText("[GAME MESSAGE] "+strPlayerName + " has played "+ strCardName);
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
			ssm.sendText("[GAME MESSAGE] "+strPlayerName + " has changed the colour to: "+ strColour);
		}
	}
	
	//send game over message
	public void sendGameOver(String strWinner){
		if (ssm != null){
			ssm.sendText("GAMEOVER: " + strWinner + " has won!!!");
		}
	}

	//Constructor
	public UnoNetwork(ActionListener gameListener){
		this.gameListener = gameListener;
	}

	//Main Method
	public static void main(String[] args){
 
    
    
	}


}

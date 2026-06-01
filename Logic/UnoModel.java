import java.io.*;
import java.util.Random;

public class UnoModel{
	// Properties
	final int intMaxCards = 30;
	final int intStartCards = 7;
	final int intPlayers = 3;
	
	String [][] strDeck = new String[100][4];
	String [][] strDrawPile = new String[100][4];
	String [][] strDicardPile = new String[100][4];
	
	String [][][]strHands = new String [3][30][4];
	
	int intDeckSize = 0;
	int intDrawPileSize = 0;
	int intDiscardPileSize = 0;
	
	int[] intHandSizes = new int[3];
	
	int intCurrentPlayer = 0;
	boolean blnClockwise = true;
	
	String strWinner = "";
	
	// Methods
	public void startGame(){
		loadDeck();
		shuffleDeck();
		dealStartingHand();
		DiscardPile();
		intCurrentPlayer = 0;
		strWinner = "";
	}
	//loading card decks
	private void loadDeck(){
		intDeckSize = 0;
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader("cards.csv"));
			String strLine;
			
			//reading & adding cards from csv file
			while((strLine = reader.readLine()) != null){
				if(!strLine.equals("")){
					String[] strCardParts = strLine.split(",");
					
					if(strCardParts.length >= 4 && intDeckSize < 100){
						strDeck[intDeckSize] = strCardParts;
						intDeckSize++;
					}
				}
			}
			
			reader.close();
			
		}catch(Exception e){
			System.out.println("Could not load cards");
		}
		
	}
	
	//shuffling decks
	private void shuffleDeck(){
		intDrawPileSize = 0;
		intDiscardPileSize = 0;
		
		for (int intCount = 0; intCount < intDeckSize; intCount++){
			strDrawPile[intCount] = strDeck[intCount];
			intDrawPileSize++;
		}
		
		Random rand = new Random();
		
		for(int intCount = intDrawPileSize -1; intCount > 0; intCount--){
			
			//generating random card position
			int intNum = rand.nextInt(intCount + 1);
			
			String[] strTemp = strDrawPile[intCount];
			strDrawPile[intCount] = strDrawPile[intNum];
			strDrawPile[intNum] = strTemp;
		}
	}
	
	//dealing each player starting hands
	private void dealStartingHand(){
		//reseting all hands to zero
		for(int intCount = 0; intCount < intPlayers; intCount++){
			intHandSizes[intCount] = 0;
		}
		
		//dealing starting number of cards to each player
		for(int intCard = 0; intCard < intStartCards; intCard++){
			for(int intPlayer = 0; intPlayer < intPlayers; intPlayer++){
				drawCard(intPlayer);
			}
		}
	}
	
	//drawing cards
	public void drawCard(int intPlayer){
		if(intDrawPileSize <= 0){
			return;
		}
		
		strHands[intPlayer][intHandSizes[intPlayer]] = strDrawPile[0];
		intHandSizes[intPlayer]++;
		
		//moving remaining deck up/forward
		for(int intCount = 0; intCount < intDrawPileSize - 1; intCount++){
			strDrawPile[intCount] = strDrawPile[intCount + 1];
			
		}
		
		intDrawPileSize--;
	}
	
	public void DiscardPile(){
		if(intDrawPileSize > 0){
			strDicardPile[0] = strDrawPile[0];
			intDiscardPileSize = 1;
			
			for(int intCount = 0; intCount < intDrawPileSize - 1; intCount++){
				strDrawPile[intCount] = strDrawPile[intCount + 1];
			}
			
			intDrawPileSize--;
		}	
		
		
		
	}
	
	
	// Constructor
	public UnoModel(){
		
	}
}

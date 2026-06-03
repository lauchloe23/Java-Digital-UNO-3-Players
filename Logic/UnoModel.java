import java.io.*;
import java.util.Random;

public class UnoModel{
	// Properties
	final int intMaxCards = 30;
	final int intStartCards = 7;
	final int intPlayers = 3;
	
	String [][] strDeck = new String[100][4];
	String [][] strDrawPile = new String[100][4];
	String [][] strDiscardPile = new String[100][4];
	
	int intDeckSize = 0;
	int intDrawPileSize = 0;
	int intDiscardPileSize = 0;
	
	// Player Hands (index 1: player. Index 2: card. Index 3: card data/value)
	String[][][] strHands = new String [3][30][4];
	int[] intHandSizes = new int[3];
	
	// track player's turn
	int[] intTurnOrder = {0, 1, 2};
	int intCurrentTurn = 0;
	boolean blnClockwise = true;
	
	// game setup variables
	boolean blnGameOver = false;
	String strWinner = "";
	
	// player names (index 0 = local)
	String[] strPlayerNames = {"Player 1", "Player 2", "Player 3"};
	
	// Methods
	public void startGame(){
		loadDeck();
		shuffleDeck();
		randomizeTurnOrder();
		dealStartingHands();
		flipFirstCard();
		strWinner = "";
		blnGameOver = false;
	}
	
	//loading card decks
	private void loadDeck(){
		intDeckSize = 0;
		// try & catch reading csv file
		try{
			BufferedReader reader = new BufferedReader(new FileReader("cards.csv"));
			String strLine;
			//reading & adding cards from csv file
			while((strLine = reader.readLine()) != null){
				strLine = strLine.trim();
				if(!strLine.equals("")){
					String[] strParts = strLine.split(",");
					
					if(strParts.length >= 4 && intDeckSize < 100){
						strDeck[intDeckSize] = strParts;
						intDeckSize++;
					}
				}
			}	
			reader.close();
			System.out.println("Deck loaded: "+ intDeckSize+" card");
		}catch(Exception e){
			System.out.println("Could not load cards");
		}
	}
	
	// backup/fallback deck if csv missing
	private void buildFallbackDeck(){
		intDeckSize = 0;
		String[] strColors = {"red", "blue", "green", "yellow"};
		String[] strNums = {"0","1","1","2","2","3","3","4","4","5","5","6","6","7","7","8","8","9","9"};
		String[] strSpecials = {"skip", "skip", "draw2", "draw2"};
		
		for(int intC = 0; intC<strColors.length; intC++){
			String strC = strColors[intC];
			for(int intN = 0; intN < strNums.length; intN++){
				String strCard = strC + strNums[intN];
				// image csv array
				String[] strRegImages = {strCard, "standard"+strCard+".png", "pokemon"+strCard+".png", "insideout"+strCard+".png"};
				strDeck[intDeckSize++] = strRegImages;
			}
			for (int intS = 0; intS < strSpecials.length; intS++) {
				String strCard = strC + strSpecials[intS];
				String[] strSpecImages = {strCard, "standard"+strCard+".png", "pokemon"+strCard+".png", "insideout"+strCard+".png"};
				strDeck[intDeckSize++] = strSpecImages;
			}
		}
		for(int i = 0; i < 4; i++){
			String[] strWildImages = {"wild", "standardwild.png","pokemonwild.png","insideoutwild.png"};
			strDeck[intDeckSize++] = strWildImages;
		}
		for(int i = 0; i < 4; i++){
			String[] strDraw4Images = {"wilddraw4","standardwilddraw4.png","pokemonwilddraw4.png","insideoutwilddraw4.png"};
			strDeck[intDeckSize++] = strDraw4Images;
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
	
	// randomize turn order
	private void randomizeTurnOrder(){
		intTurnOrder[0] = 0;
		intTurnOrder[1] = 1;
		intTurnOrder[2] = 2;
		Random rand = new Random();
		for(int i = 2; i > 0; i--){
			int intJ = rand.nextInt(i + 1);
			int intTemp = intTurnOrder[i];
			intTurnOrder[i] = intTurnOrder[intJ];
			intTurnOrder[intJ] = intTemp;
		}
		intCurrentTurn = 0;
		blnClockwise = true;
		System.out.println("First player: " + strPlayerNames[intTurnOrder[0]]);
	}
	
	//dealing each player starting hands
	private void dealStartingHands(){
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
	
	// flip first card by taking the top card of draw pil and place on discard pile
	private void flipFirstCard(){
		if(intDrawPileSize > 0){
			strDiscardPile[0] = strDrawPile[0];
			intDiscardPileSize = 1;
			for(int i = 0; i < intDrawPileSize - 1; i++){
				strDrawPile[i] = strDrawPile[i+1];
			}
			intDrawPileSize--;
			System.out.println("Starting discard: " + strDiscardPile[0][0]);
		}
	}
	
	//drawing cards
	// take top card from draw pil & reshuffle discard into draw pil if low/run out of draw pile
	public void drawCard(int intPlayer){
		if(intDrawPileSize <= 0){
			reshuffleDiscard();
		}
		if(intDrawPileSize <= 0){
			System.out.println("No cards left to draw!");
			return;
		}
		// add top of draw to player's hand
		strHands[intPlayer][intHandSizes[intPlayer]] = strDrawPile[0];
		intHandSizes[intPlayer]++;
		
		//moving remaining deck up/forward
		for(int intCount = 0; intCount < intDrawPileSize - 1; intCount++){
			strDrawPile[intCount] = strDrawPile[intCount + 1];
		}
		
		intDrawPileSize--;
		
		// check if player exceed 30 cards
		if(intHandSizes[intPlayer] > intMaxCards){
			eliminatePlayer(intPlayer);
		}
	}
	
	// reshuffle the discard into the draw pile
	private void reshuffleDiscard(){
		if(intDiscardPileSize <= 1){
			System.out.println("Not enough cards to reshuffle!");
			return; // would not reshuffle if not enough cards
		}
		
		// keep top discard card
		String[] strTopCard = strDiscardPile[intDiscardPileSize - 1];
		
		// move cards into draw pile
		intDrawPileSize = 0;
		for(int i = 0; i < intDiscardPileSize - 1; i++){
			strDrawPile[intDrawPileSize] = strDiscardPile[i];
			intDrawPileSize++;
		}
		
		// reset discard pile
		strDiscardPile[0] = strTopCard;
		intDiscardPileSize = 1;
		
		// shuffle draw pile
		Random rand = new Random();
		for(int i = intDrawPileSize - 1; i > 0; i--){
			int intJ = rand.nextInt(i+1);
			String[] strTemp = strDrawPile[i];
			strDrawPile[i] = strDrawPile[intJ];
			strDrawPile[intJ] = strTemp;
		}
		System.out.println("Discard reshuffled. Draw pile: " + intDrawPileSize);		
	}
	
	// eliminate players when exceed 30 cards in hand
	// unsure if going to change this game play rule
	private void eliminatePlayer(int intPlayer){
		System.out.println(strPlayerNames[intPlayer] + " is eliminated (over " + intMaxCards + " cards)!");
		int intMinCards = intHandSizes[0];
		int intWinPlayer = 0;
		for(int i = 1; i < intPlayers; i++){
			if(i != intPlayer && intHandSizes[i] < intMinCards){
				intMinCards = intHandSizes[i];
				intWinPlayer = i;
			}
		}
		strWinner = strPlayerNames[intWinPlayer];
		blnGameOver = true;
	}
	
	public void DiscardPile(){
		if(intDrawPileSize > 0){
			strDiscardPile[0] = strDrawPile[0];
			intDiscardPileSize = 1;
			
			for(int intCount = 0; intCount < intDrawPileSize - 1; intCount++){
				strDrawPile[intCount] = strDrawPile[intCount + 1];
			}
			
			intDrawPileSize--;
		}	
	}
	
	// access method to be used in view file
	public int getCurrentPlayer(){
		return intTurnOrder[intCurrentTurn];
	}
	
	// Constructor
	public UnoModel(){	
	}
}

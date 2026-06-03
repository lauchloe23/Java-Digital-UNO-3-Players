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
	
	// Player Hands
	// index 1: player. Index 2: card. Index 3: card data/value)
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
	
	// play card (removes a card from player's hand to place on discard)
	public boolean playCard(int intPlayer, int intCardIndex){
		// index check
		if(intCardIndex < 0 || intCardIndex >= intHandSizes[intPlayer]){
			return false;
		}
		String[] strCard = strHands[intPlayer][intCardIndex];
		String strCardName = strCard[0];
		
		// validate card play to be place on discard pile
		if(!isValidPlay(strCardName)){
			System.out.println("Invalid play: "+strCardName);
			return false;
		}
		
		// move card to top
		strDiscardPile[intDiscardPileSize] = strCard;
		intDiscardPileSize++;
		
		// remove card from hand
		for(int i = intCardIndex; i < intHandSizes[intPlayer] - 1; i++){
			strHands[intPlayer][i] = strHands[intPlayer][i+1];
		}
		intHandSizes[intPlayer]--;
		
		// special cards effect
		applyCardEffect(strCardName);
		
		// check
		if(intHandSizes[intPlayer] == 0){
			strWinner = strPlayerNames[intPlayer];
			blnGameOver = true;
			System.out.println("Winner: "+strWinner);
			return true;
		}
		
		// next player's turn
		advanceTurn();
		return true;
	}	
	
	// if the play is valid
	public boolean isValidPlay(String strCardName){
		// wild cards
		if(strCardName.startsWith("wild")){
			return true;
		}
		if(intDiscardPileSize == 0){
			return true;
		}
		String strTopCard = strDiscardPile[intDiscardPileSize - 1][0];
		
		// get color 
		String strPlayColor = getColor(strCardName);
		String strTopColor = getColor(strTopCard);
		
		// get value
		String strPlayValue = getValue(strCardName);
		String strTopValue= getValue(strTopCard);
		
		// return based on color & value
		return strPlayColor.equals(strTopColor) || strPlayValue.equals(strTopValue);
	}
	
	// get color of card
	public String getColor(String strCardName){
		if(strCardName.startsWith("red")){
			return "red";
		}else if(strCardName.startsWith("blue")){
			return "blue";
		}else if(strCardName.startsWith("green")){
			return "green";
		}else if(strCardName.startsWith("yellow")){
			return "yellow";
		}else{
			return "wild";
		}
	}
	
	// get values of cards 
	public String getValue(String strCardName){
		String strVal = strCardName.replace("yellow","").replace("green","").replace("blue","").replace("red","");
		if(strVal.equals("")){
			strVal = "wild";
		}
		return strVal;
	}
	
	// apply card effect (handle special cards)
	private void applyCardEffect(String strCardName){
		String strValue = getValue(strCardName);
		
		if(strValue.equals("skip")){
			// skip
			advanceTurn();
			System.out.println("Player " + intTurnOrder[intCurrentTurn] + " is skipped!");
		}else if(strValue.equals("draw2")){
			// draw 2 cards + lose their turn
			advanceTurn();
			int intNextPlayer = intTurnOrder[intCurrentTurn];
			drawCard(intNextPlayer);
			drawCard(intNextPlayer);
			System.out.println("Player " + intNextPlayer + " draws 2 cards and is skipped!");
			
			// skip their turn
			advanceTurn();
		}else if(strValue.equals("wilddraw4")){
			// draw 4 cards + loses their turn
			advanceTurn();
			int intNextPlayer = intTurnOrder[intCurrentTurn];
			drawCard(intNextPlayer);
			drawCard(intNextPlayer);
			drawCard(intNextPlayer);
			drawCard(intNextPlayer);
			System.out.println("Player " + intNextPlayer + " draws 4 cards and is skipped!");
			advanceTurn();
		}
	}
	
	// advance turn (next player)
	public void advanceTurn(){
		if(blnClockwise){
			intCurrentTurn = (intCurrentTurn + 1)% intPlayers;
		}else{
			intCurrentTurn = (intCurrentTurn + intPlayers - 1)% intPlayers;
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
	
	/*
	private String getCardColor(String strCardName){
		if(strCardName.startsWith("red")){
			return "red";
		}else if(strCardName.startsWith("blue")){
			return "blue";
		}else if(strCardName.startsWith("green")){
			return "green";
		}else if(strCardName.startsWith("yellow")){
			return "yellow";
		}else if(strCardName.startsWith("wild")){
			return "wild";
		}
		return "";
	}

	private String getCardValue(String strCardName){
		if(strCardName.startsWith("red")){
			return strCardName.substring(3);
		}else if(strCardName.startsWith("blue")){
			return strCardName.substring(4);
		}else if(strCardName.startsWith("green")){
			return strCardName.substring(5);
		}else if(strCardName.startsWith("yellow")){
			return strCardName.substring(6);
		}else if(strCardName.startsWith("wild")){
			return strCardName;
		}
		return strCardName;
	}
	
	public boolean playableCard (int intPlayer, int intCardIndex){
		if(intPlayer < 0 || intPlayer >=  intPlayers){
			return false;
		}
		
		if(intCardIndex < 0 || intCardIndex >= intHandSizes[intPlayer]){
			return false;
		}
		if(intDiscardPileSize <= 0){
			return true;
		}
		
		String strSelectedCard = strHands[intPlayer][intCardIndex][0];
		String strTopCard = strDiscardPile[intDiscardPileSize - 1][0];
		
		if(strSelectedCard.startsWith("wild")){
			return true;
		}
		
		String strSelectedColor = getCardColor(strSelectedCard);
		String strTopColor = getCardColor(strTopCard);
		String strSelectedValue = getCardValue(strSelectedCard);
		String strTopValue = getCardValue(strTopCard);
		
		// card is valid if color or value matches
		if(strSelectedColor.equals(strTopColor)){
			return true;
		}
		if(strSelectedValue.equals(strTopValue)){
			return true;
		}
		
		return false;
		}
	*/
	
	// access method to be used in view file
	// return player index
	public int getCurrentPlayer(){
		return intTurnOrder[intCurrentTurn];
	}
	
	// return card count
	public int getHandSize(int intPlayer){
		return intHandSizes[intPlayer];
	}
	
	// return player's hand
	public String[][] getHand(int intPlayer){
		return strHands[intPlayer];
	}	
	
	// return top card of discard pile
	public String[] getTopDiscard(){
		if(intDiscardPileSize > 0){
			return strDiscardPile[intDiscardPileSize - 1];
		}
		return null;
	}
	
	// return card counts in draw pile
	public int getDrawPileSize(){
		return intDrawPileSize;
	}
	
	// return true if game over
	public boolean isGameOver(){
		return blnGameOver;
	}
	
	// return winner
	public String getWinner(){
		return strWinner;
	}
	
	// Constructor
	public UnoModel(){	
		startGame();
	}
}

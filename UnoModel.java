
import java.io.*;
import java.util.Random;

/**The UnoModel class represents the core game logic and state management for a 
 * 3-player Uno game. It handles deck initialization, card shuffling, player turns, 
 * hand management, game rules, and win/elimination conditions.
 * The class supports loading custom card sets from a CSV file with a 
 * fallback mechanism to create UNO card deck if the file cannot be read.</p>
 * Arthur: Chloe Lau and Sydney Khang
 * Version: 1.0 
 */

public class UnoModel{
	// Properties
	/** Maximum number of cards a player can hold before being eliminated. */
	final int intMaxCards = 30;
	/** The number of cards dealt to each player at the beginning of the game. */
	final int intStartCards = 7;
	/** Total number of players in the game. */
	final int intPlayers = 3;
	
	/** Master deck array storing up to 100 cards, each with 4 string data columns (Name, Standard Image, Pokemon Image, Inside Out Image). */
	String [][] strDeck = new String[100][4];
	/** The pile from which players draw cards during the game. */
	String [][] strDrawPile = new String[100][4];
	/** The pile where played cards are placed. */
	String [][] strDiscardPile = new String[100][4];
	
	/** Current number of cards remaining in the master deck. */
	int intDeckSize = 0;
	/** Current number of cards remaining in the draw pile. */
	int intDrawPileSize = 0;
	/** Current number of cards in the discard pile. */
	int intDiscardPileSize = 0;
	
	// Player Hands
	// index 1: player. Index 2: card. Index 3: card data/value
	/** 3D array repsenting the player hands.
	 * Index 1: Player ID
	 * Index 2: Card Position
	 * Index 3: Card Data (Value, Themes)
	*/
	String[][][] strHands = new String [3][30][4];
	
	/** Array storing the current hand size for each of the 3 players. */
	int[] intHandSizes = new int[3];
	
	// track player's turn
	/** Array maintaining the seat/turn order sequence of the players. */
	int[] intTurnOrder = {0, 1, 2};
	/** Index pointing to the player whose turn it currently is within the intTurnOrder variable. */
	int intCurrentTurn = 0;
	/** Direction of gameplay; true for clockwise, false for counterclockwise */
	boolean blnClockwise = true;
	
	// track which players are eliminated
	/** Array tracking the elimination status of each player. */
	boolean[] blnEliminated = {false, false, false};
	/** The number of active players who have not been eliminated. */
	int intActivePlayers = 3;
	
	// wild card chosen color
	/** The color declared by a player after playing a Wild card. Empty string if no active wild restriction exists. */
	String strWildColor = "";

	// last action info for broadcasting
	/** Last played card name (for network/view sync) */
	public String lastPlayedCard = "";
	/** Last attack type (e.g., "draw2", "skip", "draw4") or empty if none */
	public String lastAttackType = "";
	/** Last attack target player index or -1 if none */
	public int lastAttackTarget = -1;
	
	// game setup variables
	/** Tracking whether the current game play has ended. */
	boolean blnGameOver = false;
	/** The name of the player who won the game. */
	String strWinner = "";
	
	// player names (index 0 = local)
	/** Players enter names. 
	 *  Index 0 corresponds to the local client. */
	String[] strPlayerNames = {"Player 1", "Player 2", "Player 3"};
	
	// Methods
	/**
	 * Initializes and resets all state variables required to start a fresh game.
     * It loads/shuffles the deck, randomizes player order, deals hands, and flips the initial card.
    */
	public void startGame(){
		strWildColor = ""; 
		strWinner = "";
		blnGameOver = false;
		intActivePlayers = intPlayers;
		for(int i = 0; i < intPlayers; i++){
			blnEliminated[i] = false;
		}
		loadDeck();
		shuffleDeck();
		randomizeTurnOrder();
		dealStartingHands();
		flipFirstCard();
	}
	
	//loading card decks
	/**Attempts to read and load the card data from an  "cards.csv" file.
     * If fails to read/load, it will automatically fallback to the fallback deck.
    */
	public void loadDeck(){
		intDeckSize = 0;
		// try & catch reading csv file
		try{
			BufferedReader reader = new BufferedReader(new FileReader("cards.csv"));
			reader.readLine(); 
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
			buildFallbackDeck();
		}
	}
	
	// backup/fallback deck if csv missing
	/**constructs a standard 100-card Uno deck configuration to serve 
     * as a backup if "cards.csv" external file cannot be loaded.
	*/
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
	/**
     * Generate the draw the draw pile using the master deck array and shuffles it through randomizing.
     * Resets the discard pile size to 0.
     */
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
	/**Randomizes the indices inside intTurnOrder array to change game order.
	 * Defaults direction to clockwise.
	*/
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
	/**Resets individual hand counters to zero and transfers the
	 * initial amount of cards into each active player's hand array structure.
	*/
	private void dealStartingHands(){
		//reseting all hands to zero
		for(int intCount = 0; intCount < intPlayers; intCount++){
			intHandSizes[intCount] = 0;
		}
		
		//dealing starting number of cards to each player
		for(int intPlayer = 0; intPlayer < intPlayers; intPlayer++){
			for(int intCard = 0; intCard < intStartCards; intCard++){
				drawCard(intPlayer);
			}
		}
	}
	
	// flip first card by taking the top card of draw pil and place on discard pile
	/**Transfers index zero of the draw pile to establish the base of the discard pile.
	 * Remaining cards in teh draw pile are shifted forward by one position.
	*/
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
	// take top card from draw pil & reshuffle discard into draw pile if low/run out of draw pile
	/**Draws a single card for a specified player. Automatically triggers a discard pile 
     * reshuffle if the draw pile runs empty. If the player exceeds the max card ceiling 
     * after drawing, they are automatically eliminated.
     * intPlayer: The index value identifying the target player (0 to 2).
	*/
	public void drawCard(int intPlayer){
		if(intPlayer < 0 || intPlayer >= intPlayers){
			return;
		}
		
		if(blnEliminated[intPlayer]){
			return;
		}
		
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
	/**Validates and executes a player's card play from their hand array. Handles 
     * array, checks for win conditions, and carry out game rules via helper methods.
     * intPlayer: the index value identifying the active player.
     * intCardIndex: array position index of targeted card within the player's hand
     * return: true if card was valid and successfully played, false otherwise
	*/
	public boolean playCard(int intPlayer, int intCardIndex){
		
		if(intPlayer < 0 || intPlayer >= intPlayers){
			return false;
		}
		
		if(blnEliminated[intPlayer]){
			return false;
		}
		
		// index check
		if(intCardIndex < 0 || intCardIndex >= intHandSizes[intPlayer]){
			return false;
		}
		String[] strCard = strHands[intPlayer][intCardIndex];
		String strCardName = strCard[0];
		// record last played card
		lastPlayedCard = strCardName;
		lastAttackType = "";
		lastAttackTarget = -1;
		
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
		
		// check
		if(intHandSizes[intPlayer] == 0){
			strWinner = strPlayerNames[intPlayer];
			blnGameOver = true;
			System.out.println("Winner: "+strWinner);
			return true;
		}
		
		if(strCardName.startsWith("wild")){
			strWildColor = "";
			// wait for color choice before advancing turn
			return true;
		}

		// special cards effect
		applyCardEffect(strCardName);
		// next player's turn (applyCardEffect will handle advancing)
		return true;
	}	
	
	// if the play is valid
	/**Compares the evaluated parameters of a candidate card against the current top card of 
     * the discard pile to enforce matching color, identity value, or wild override permission rules.
     * strCardName: literal string code signature identifier of the card being played.
     * return: true if the placement follows the game rule; false otherwise.
	*/
	public boolean isValidPlay(String strCardName){
		// wild cards
		if(strCardName.startsWith("wild")){
			return true;
		}
		if(intDiscardPileSize == 0){
			return true;
		}
		String strTopCard = strDiscardPile[intDiscardPileSize - 1][0];
		
		// check against chosen wild color
		if(strTopCard.startsWith("wild")){
			if(!strWildColor.equals("")){
				return getColor(strCardName).equals(strWildColor);
			}
			return true;
		}
		
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
	/**Determines the color property of a given card using prefix string to determine.
	 * strCardName: name code of card
	 * return: A lowercase color identity string ("red", "blue", "green", "yellow", or "wild").
	*/
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
	/**Extracts the specific action value or numeral string of a card 
     * by stripping away color prefix substrings.
     * strCardname: code identifier of the card.
     * return: specific action descriptor or raw numerical representation string.
	*/
	public String getValue(String strCardName){
		String strVal = strCardName.replace("yellow","").replace("green","").replace("blue","").replace("red","");
		if(strVal.equals("")){
			strVal = "wild";
		}
		return strVal;
	}
	
	// apply card effect (handle special cards)
	/**Enforces logic rules linked with specific special cards
     * and auto-increments turn allocations to target skipped individuals appropriately.
     * strCardname: The descriptive string identification token of the card played.
	*/
	private void applyCardEffect(String strCardName){
		String strValue = getValue(strCardName);
		
		if(strValue.equals("skip")){
			//moves to the skipped player
			advanceTurn(); 
			System.out.println(strPlayerNames[intTurnOrder[intCurrentTurn]] + " is skipped!");
			advanceTurn();
			
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
			
		}else{
			advanceTurn();
		}
	}
	
	// advance turn (next player)
	/**Determine the next active turn value, incrementing or decrementing indexing pointers 
     * while checking for and skipping past players who have been eliminated.
	*/
	public void advanceTurn() {
		int intCount = 0;
		boolean foundValidPlayer = false;

		while (intCount < intPlayers && !foundValidPlayer) {
			// move to next player
			if (blnClockwise) {
				intCurrentTurn = (intCurrentTurn + 1) % intPlayers;
			} else {
				intCurrentTurn = (intCurrentTurn + intPlayers - 1) % intPlayers;
			}
			intCount++;

			// stop if the player is NOT eliminated
			foundValidPlayer = !blnEliminated[intTurnOrder[intCurrentTurn]];
		}
	}
	
	/**Assigns the user's declared Wild color parameter selection before passing execution to the next player.
	 * strColor: string description color selected by the player.
	*/
	public void advanceTurnAfterWild(String strColor){
		strWildColor = strColor;
		advanceTurn();
	}
	
	// reshuffle the discard into the draw pile
	/**Gather all items presently underlying the discard stack (leave behind the surface card)
	 * Turn them into draw pile and shuffle.
	*/
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
	/**Flag alters a targeted player position array marker to true to flag elimination. 
     * Reduces active population count, checking if a single lone survivor can be crowned winner.
     * intPlayer: The array assignment index of the player to eliminate.
	*/
	private void eliminatePlayer(int intPlayer){
		if(blnEliminated[intPlayer]){
			return; 
		}
		blnEliminated[intPlayer] = true;
		intActivePlayers--;
		System.out.println(strPlayerNames[intPlayer] + " is eliminated!");
		
		// If only one player is left, they win
		if(intActivePlayers == 1){
			for(int i = 0; i < intPlayers; i++){
				if(!blnEliminated[i]){
					strWinner   = strPlayerNames[i];
					blnGameOver = true;
					return;
				}
			}
		}
	}
	
	// access method to be used in view file
	
	//set wild card colour
	/**
     * Input the restriction property filter value for active wild states.
     * strColor: The name text string defining the color choice constraint.
     */
	public void setWildColor(String strColor){
		strWildColor = strColor;
	}
	
	//get wild card colour
	/**
     * Discovering the active restricted color parameter set by wild cards.
     * return: The color string name value currently active.
     */
	public String getWildColor(){
		return strWildColor;
	}
	
	/**
     * Extracts the text identifying the string code representing the face card item layout 
     * on top of the current discard stack.
     * return: String code configuration value or empty text if empty.
     */
	public String getTopCardName(){
		if(intDiscardPileSize > 0){
			return strDiscardPile[intDiscardPileSize - 1][0];
		}
		return "";
	}
	
	/**
     * A status update tracking string convey state parameters.
     * return: A formatted text stream containing state indicators.
     */
	public String getGameStateMessage(){
		return "STATE|" +
			getCurrentPlayer() + "|" +
			getTopCardName() + "|" +
			intHandSizes[0] + "|" +
			intHandSizes[1] + "|" +
			intHandSizes[2] + "|" +
			blnGameOver + "|" +
			strWinner;
	}
	
	// return player index
	/**
     * Obtaining the index value of the active turn holder.
     * return: Player reference array offset index.
     */
	public int getCurrentPlayer(){
		return intTurnOrder[intCurrentTurn];
	}
	
	/**Collects data defining total card inventory load volumes with local player.
     * intPlayer: Index position value associated with seat array space.
     * return: Integer depth total specifying cards remaining.
	*/
	// return card count
	public int getHandSize(int intPlayer){
		return intHandSizes[intPlayer];
	}
	
	/**Grab reference copy of the player's hand into array/
	 * intPlayer: target player value data space index position.
	 * return: 2D array tracking cards.
	*/
	// return player's hand
	public String[][] getHand(int intPlayer){
		return strHands[intPlayer];
	}	
	
	// return top card of discard pile
	/**
     * Safely checks the size of the most recently logged batch of discarded inventory/deck.
     * return: String array containing image path names and elements, or null if empty.
     */
	public String[] getTopDiscard(){
		if(intDiscardPileSize > 0){
			return strDiscardPile[intDiscardPileSize - 1];
		}
		return null;
	}

	// return card counts in draw pile
	/**
	 * checks how many more items player is allowed to pull (draw) from the draw pile before hitting the maximum limit.
	 * Returns the number of additional items that can still be safely withdrawn before reaching the maximum limit
	*/
	public int getDrawPileSize(){
		return intDrawPileSize;
	}

	// return true if game over
	/**Checks the current game state to see if a player has won. 
	 * Return: true if the game is over, and false if the game should keep running.
	*/
	public boolean isGameOver(){
		return blnGameOver;
	}
	
	// return winner
	/**Gets the display name of the winning player.
	 * return: name of the winner.
	*/
	public String getWinner(){
		return strWinner;
	}
	
	// Constructor
	/**Default public constructor initializing of UnoModel
	*/
	public UnoModel(){	
		//startGame();
	}
}

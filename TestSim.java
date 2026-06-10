public class TestSim {
    public static void main(String[] args) {
        // Test 1: Skip
        {
            UnoModel model = new UnoModel();
            model.startGame();
            model.strPlayerNames[0] = "A";
            model.strPlayerNames[1] = "B";
            model.strPlayerNames[2] = "C";
            model.strHands[0][0] = new String[]{"redskip","s","s","s"};
            model.strHands[0][1] = new String[]{"red5","s","s","s"};
            model.strHands[1][0] = new String[]{"blue5","s","s","s"};
            model.strHands[2][0] = new String[]{"green2","s","s","s"};
            model.intHandSizes[0]=2; model.intHandSizes[1]=1; model.intHandSizes[2]=1;
            model.intTurnOrder[0]=0; model.intTurnOrder[1]=1; model.intTurnOrder[2]=2;
            model.intCurrentTurn = 0;
            model.strDiscardPile[0] = new String[]{"red7","s","s","s"};
            model.intDiscardPileSize = 1;
            System.out.println("Start turn: " + model.getCurrentPlayer());
            System.out.println("Player 0 plays redskip");
            model.playCard(0, 0);
            System.out.println("After skip, current player index: " + model.getCurrentPlayer());
        }

        // Test 2: Draw2
        {
            UnoModel model = new UnoModel();
            model.startGame();
            model.strPlayerNames[0] = "A";
            model.strPlayerNames[1] = "B";
            model.strPlayerNames[2] = "C";
            model.strHands[0][0] = new String[]{"reddraw2","s","s","s"};
            model.strHands[0][1] = new String[]{"red5","s","s","s"};
            model.strHands[1][0] = new String[]{"blue5","s","s","s"};
            model.strHands[2][0] = new String[]{"green2","s","s","s"};
            model.intHandSizes[0]=2; model.intHandSizes[1]=1; model.intHandSizes[2]=1;
            model.intTurnOrder[0]=0; model.intTurnOrder[1]=1; model.intTurnOrder[2]=2;
            model.intCurrentTurn=0;
            model.strDiscardPile[0] = new String[]{"red7","s","s","s"};
            model.intDiscardPileSize = 1;
            System.out.println("\nStart draw2 test. Current: " + model.getCurrentPlayer());
            model.playCard(0,0);
            System.out.println("After draw2, player hand sizes: " + model.intHandSizes[0] + "," + model.intHandSizes[1] + "," + model.intHandSizes[2]);
            System.out.println("Current player now: " + model.getCurrentPlayer());
        }

        // Test 3: Wild
        {
            UnoModel model = new UnoModel();
            model.startGame();
            model.strPlayerNames[0] = "A";
            model.strPlayerNames[1] = "B";
            model.strPlayerNames[2] = "C";
            model.strHands[0][0] = new String[]{"wild","s","s","s"};
            model.strHands[0][1] = new String[]{"red5","s","s","s"};
            model.strHands[1][0] = new String[]{"blue5","s","s","s"};
            model.strHands[2][0] = new String[]{"green2","s","s","s"};
            model.intHandSizes[0]=2; model.intHandSizes[1]=1; model.intHandSizes[2]=1;
            model.intTurnOrder[0]=0; model.intTurnOrder[1]=1; model.intTurnOrder[2]=2;
            model.intCurrentTurn=0;
            model.strDiscardPile[0] = new String[]{"red7","s","s","s"};
            model.intDiscardPileSize = 1;
            System.out.println("\nWild test: player 0 plays wild");
            model.playCard(0,0);
            System.out.println("Wild color before choice: '" + model.getWildColor() + "'");
            model.advanceTurnAfterWild("blue");
            System.out.println("Wild color after choice: '" + model.getWildColor() + "', current player: " + model.getCurrentPlayer());
        }

        // Test 4: UNO/win
        {
            UnoModel model = new UnoModel();
            model.startGame();
            model.strPlayerNames[0] = "A";
            model.strPlayerNames[1] = "B";
            model.strPlayerNames[2] = "C";
            model.strHands[1][0] = new String[]{"red3","s","s","s"};
            // Ensure top discard allows playing red3
            model.strDiscardPile[0] = new String[]{"red5","s","s","s"};
            model.intDiscardPileSize = 1;
            model.intHandSizes[0]=2; model.intHandSizes[1]=1; model.intHandSizes[2]=2;
            model.intTurnOrder[0]=0; model.intTurnOrder[1]=1; model.intTurnOrder[2]=2;
            model.intCurrentTurn=1;
            System.out.println("\nUNO test: player 1 has " + model.intHandSizes[1] + " card(s)");
            model.playCard(1,0);
            System.out.println("After playing, player1 hand size: " + model.intHandSizes[1] + ", gameOver=" + model.isGameOver() + ", winner=" + model.getWinner());
        }
    }
}
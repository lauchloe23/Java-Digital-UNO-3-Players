public class UnoModelEliminationTest {
    public static void main(String[] args) {
        UnoModel model = new UnoModel();
        model.startGame();
        model.intHandSizes[0] = 31;

        boolean eliminated = model.checkElimination(0);
        if (!eliminated || !model.blnEliminated[0]) {
            throw new IllegalStateException("Expected player 0 to be eliminated when their hand exceeds 30 cards");
        }

        System.out.println("Elimination test passed");
    }
}

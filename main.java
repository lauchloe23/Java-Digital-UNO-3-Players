import Graphics.UnoView;
import Logic.UnoModel;
import Network.UnoNetwork;

public class main{
	public static void main(String[] args){
		UnoModel model = new UnoModel();
		UnoView view = new UnoView();
		UnoNetwork network = new UnoNetwork(view);
		
		view.setModel(model);
		view.setNetwork(network);
	}
}

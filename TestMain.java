import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.awt.event.ActionListener;

public class TestMain{
	public static void main(String[] args){
		try{
			URL[] folders = {
				new File("Graphics").toURI().toURL(),
				new File("Logic").toURI().toURL(),
				new File("Network").toURI().toURL()
			};

			URLClassLoader loader = new URLClassLoader(folders);

			Class<?> viewClass = Class.forName("UnoView", true, loader);
			Class<?> modelClass = Class.forName("UnoModel", true, loader);
			Class<?> networkClass = Class.forName("UnoNetwork", true, loader);

			Object view = viewClass.getDeclaredConstructor().newInstance();
			Object model = modelClass.getDeclaredConstructor().newInstance();
			Object network = networkClass.getDeclaredConstructor(ActionListener.class).newInstance((ActionListener)view);

			viewClass.getMethod("setModel", Object.class).invoke(view, model);
			viewClass.getMethod("setNetwork", Object.class).invoke(view, network);

		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

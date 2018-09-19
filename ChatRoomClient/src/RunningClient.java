import javax.swing.*;

//run after running server
public class RunningClient {

	public static void main(String[] args) {

		Client runclient = new Client("127.0.0.1");
		runclient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		runclient.startRunning();

	}
}

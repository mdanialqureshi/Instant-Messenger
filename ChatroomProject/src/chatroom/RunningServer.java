package chatroom;

//run this before running client 
import javax.swing.*;

public class RunningServer {

	public static void main(String[] args) {
		Server runServer = new Server();// creating this will call constructor and initialize gui
		runServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		runServer.startRunning();
	}

}

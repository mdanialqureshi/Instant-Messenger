import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.net.*;
import java.awt.event.*;

public class Client extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField userInput;
	private JTextArea chatWindow;// can type freely not just one line like JTextField
	private ObjectOutputStream output;
	private ObjectInputStream input;
	// private String message = "";
	private String serverIP;
	private Socket connection;

	// constructor
	public Client(String host) {
		super("User Window");
		serverIP = host;
		userInput = new JTextField();
		userInput.setEditable(false);
		userInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				sendMessage(event.getActionCommand());
				userInput.setText("");
			}
		});

		add(userInput, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300, 300);
		setVisible(true);
	}

	// connect to server
	public void startRunning() {
		try {
			connectToServer();
			setUpStreams();
			whileChatting();
		} catch (EOFException eofException) {
			showMessage("\n Client terminated the connection");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			closeAll();
		}
	}

	private void connectToServer() throws IOException {
		showMessage("Attempting connection please wait...\n");
		connection = new Socket(InetAddress.getByName(serverIP), 1234);
		showMessage("Connected to: " + connection.getInetAddress().getHostName());
	}

	public void setUpStreams() throws IOException {// set up streams to send and receive messages
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Dude your streams are now good to go! \n");
	}

	// while chatting with server
	private void whileChatting() throws IOException {
		String message = "";// this message string will be getting updated
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			} catch (ClassNotFoundException classNotfoundException) {
				showMessage("\n error. I dont know what you're saying. ");
			}
		} while (!message.equals("SERVER - END"));
	}

	// close streams and sockets
	private void closeAll() {
		showMessage("\n Closing down .. ");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	// send messages to server
	private void sendMessage(String message) {
		try {
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("\nCLIENT - " + message);
		} catch (IOException ioException) {
			chatWindow.append("\n something went wrong as you sent message.");
		}
	}

	// update chat window
	private void showMessage(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				chatWindow.append(message);
			}
		});
	}

	// gives user permission to type into textfield
	private void ableToType(final boolean t) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				userInput.setEditable(t);
			}
		});
	}

}

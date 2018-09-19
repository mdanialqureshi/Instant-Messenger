package chatroom;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.net.*;
import java.awt.event.*;

public class Server extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField userInput;
	private JTextArea chatWindow;// can type freely not just one line like JTextField
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server; // server that receives client info
	private Socket connection; // connections(between to computers) are named sockets

	public Server() {
		super("DQ's Messenger");
		userInput = new JTextField();
		userInput.setEditable(false);// can't send a message when no ones connected to you
		userInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				sendMessage(event.getActionCommand());// when we hit enter itll send message
				userInput.setText("");// seting text blank after message is sent
			}
		});
		add(userInput, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		;
		setSize(300, 300);
		setVisible(true);
		setLocationRelativeTo(null);

	}

	public void startRunning() { // set up and run server
		try {
			server = new ServerSocket(1234, 100);// (port number , and how many people can wait in server)
			while (true) {// keep looping aka keep running
				try {
					// connect and have conversation
					waitForConnection();// method
					setupStreams();// method
					whileChatting();// method
				} catch (EOFException eofException) { // end of stream aka connection
					showMessage("\n Server ended the connection!");
				} finally {
					closeAll();
				}
			}

		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

	}

	private void waitForConnection() throws IOException {
		showMessage("Waiting for someone to connect...\n");
		connection = server.accept(); // connection will be made if someone connects aka client is run
		showMessage("Now Connected to " + connection.getInetAddress().getHostName());// displays persons ip address as a
																						// string

	}

	// get stream to send and receive data
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());// creating path that allows us to connect to
																		// another comp(send out)
		output.flush();// incase there is stuff left in stream it pushes it to user
		input = new ObjectInputStream(connection.getInputStream());// path ways to receive messages from others
		showMessage("\nyour streams are now setup! \n");
	}

	private void whileChatting() throws IOException {// what is happening during chat conversation
		String message = ("You are now connected");// this message string will be getting updated
		sendMessage(message);
		ableToType(true);// allows user to start typing again

		do {
			// Have convo
			try {
				message = (String) input.readObject();// when user sends something it reads it and makes sure its a
														// string a stores it in message
				showMessage("\n" + message);// displays message that was just received
			} catch (ClassNotFoundException classNotFoundException) {
				showMessage("\n Can not understand! ");
			}

		} while (!message.equals("CLIENT - END"));// ends convo if they type end
	}

	// close streams and sockets after done chatting
	public void closeAll() {
		showMessage("\n Closing connections...\n");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

	}

	private void sendMessage(String message) {// send messages
		try {
			output.writeObject("SERVER - " + message);
			output.flush();
			showMessage("\nSERVER - " + message);
		} catch (IOException ioException) {
			chatWindow.append("\n ERROR): Message failed to send.");
		}
	}

	private void showMessage(final String text) {// shows both ppls messages in JTextArea
		SwingUtilities.invokeLater(new Runnable() {// thread that updates GUI aka the text inside the jtextarea window
			public void run() {
				chatWindow.append(text); // updates gui chat window by adding the text passed into the method from
											// textbar
			}
		});

	}

	private void ableToType(final boolean t) { // allows user to type in the box jtextfeild
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				userInput.setEditable(t);// updated by whilechatting and close all
			}
		});
	}

}

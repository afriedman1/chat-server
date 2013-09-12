package pkg;

import java.io.*;

public class UserThread implements Runnable {
	
	private static int nextUser = 1;
	
	private String name;
	private String message;
	private BufferedReader in;
	private ChatResponder responder;

	public UserThread(BufferedReader input, ChatResponder r) {
		responder = r;
		name = "guest_"+UserThread.nextUser;
		nextUser++;
		in = input;
	}
	public void run() {
		while (true) {
			try {
				String input = this.in.readLine();
				message = "<"+name+"> "+input;
				responder.addMessageToQueue(message);
			} catch (IOException e) {
				System.out.println("IOException: " + e);
			}
		}
	}
	public void setName(String newName) {
		name = newName;
	}
	public String getName() {
		return name;
	}
	public String getMessage() {
		return message;
	}
}

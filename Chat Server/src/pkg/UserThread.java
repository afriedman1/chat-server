package pkg;

import java.io.*;

public class UserThread implements Runnable {
	
	private static int nextUser = 1;
	
	private String name;
	private String message;
	private BufferedReader in;
	private PrintWriter out;
	private ChatResponder responder;

	public UserThread(BufferedReader input, PrintWriter output, ChatResponder r) {
		responder = r;
		name = "guest_"+UserThread.nextUser;
		nextUser++;
		in = input;
		out = output;
	}
	public void run() {
		while (true) {
			try {
				boolean sendMessage = true;
				String input = this.in.readLine();
				if (input == null) {
					responder.removeUser(this);
					responder.addMessageToQueue(name+" disconnected...");
					return;
				}
				if (input.split(" ")[0].equals("/n")) {
					String newName = input.substring(input.indexOf(" ")+1);
					if (responder.validateName(newName)) {
						String oldName = this.name;
						this.name = newName;
						message = oldName+" changed his/her name to "+newName+".";
					} else {
						synchronized(responder) {
							out.println("That name is already taken.");
							sendMessage = false;
						}
					}
				} else {
					message = "<"+name+"> "+input;
				}
				if (sendMessage) {
					responder.addMessageToQueue(message);
				}
			} catch (IOException e) {
				System.out.println("IOException: " + e);
			}
		}
	}
	public String getName() {
		return name;
	}
	public String getMessage() {
		return message;
	}
}

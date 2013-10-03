package pkg;

import java.net.*;
import java.io.*;

public class UserConnector {
	private ChatResponder responder;
	private ServerSocket serverSocket;
	
	public static void main(String [] args) {
		String a[] = { "4000" };
		UserConnector connector = new UserConnector(a);
		connector.openConnectingPort();
	}
	
	public UserConnector(String[] args) {
		if (args.length < 1) {
            System.out.println("Usage: ChatServer <port>");
            System.exit(1);
        }
        
        /* Create the server socket */
        try {
            serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        } catch (IOException e) {
            System.out.println("IOException: " + e);
            System.exit(1);
        }
        responder = new ChatResponder();
        new Thread(responder).start();
        /* In the main thread, continuously listen for new clients and spin off threads for them. */
	}
	public void openConnectingPort() {
		 while (true) {
	            try {
	                /* Get a new client */
	                Socket clientSocket = serverSocket.accept();
	                
	                /* Create a thread for it and start! */
	                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
	                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	                UserThread clientThread = new UserThread(clientSocket,in, out, responder);
	                new Thread(clientThread).start();
	                responder.addUser(clientThread, out);
	                
	            } catch (IOException e) {
	                System.out.println("Accept failed: " + e);
	            }
	        }
	}
}


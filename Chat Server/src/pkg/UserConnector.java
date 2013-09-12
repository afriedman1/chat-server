package pkg;

import java.net.*;
import java.io.*;
import java.util.ArrayDeque;
import java.util.LinkedList;

public class UserConnector {
	
	public static void main(String [] args) {
		String a[] = { "4000" };
		UserConnector connector = new UserConnector(a);
	}
	
	public UserConnector(String[] args) {
		if (args.length < 1) {
            System.out.println("Usage: ChatServer <port>");
            System.exit(1);
        }
        ServerSocket serverSocket = null;
        
        /* Create the server socket */
        try {
            serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        } catch (IOException e) {
            System.out.println("IOException: " + e);
            System.exit(1);
        }
        ChatResponder responder = new ChatResponder();
        new Thread(responder).start();
        /* In the main thread, continuously listen for new clients and spin off threads for them. */
        while (true) {
            try {
                /* Get a new client */
                Socket clientSocket = serverSocket.accept();
                
                /* Create a thread for it and start! */
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                UserThread clientThread = new UserThread(in, responder);
                new Thread(clientThread).start();
                responder.addUser(clientThread, out);
                
            } catch (IOException e) {
                System.out.println("Accept failed: " + e);
            }
        }
	}
}


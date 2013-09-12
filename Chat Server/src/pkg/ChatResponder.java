package pkg;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class ChatResponder implements Runnable {
	
	private LinkedList<PrintWriter> outs;
	private LinkedList<String> messages;
	private LinkedList<UserThread> users;
	private Semaphore blocker;
	
	public ChatResponder() {
		outs = new LinkedList<PrintWriter>();
		messages = new LinkedList<String>();;
		users = new LinkedList<UserThread>();;
		blocker = new Semaphore(0);
	}

	public void addOutput(PrintWriter out) {
		outs.add(out);
	}
	
	private void outputMessage(String message) {
		System.out.println("printing...");
		for (PrintWriter o: outs) {
			o.write(message);
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				System.out.println("Waiting...");
				blocker.acquire();
				System.out.println("Done waiting...");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String msg;
			synchronized(this) {
				msg = messages.remove();
			}
			outputMessage(msg);
		}
	}
	public void addMessageToQueue(String str) {
		synchronized(this) {
			System.out.println("Adding message...");
			messages.add(str);
		}
		System.out.println("Notifying of addition...");
		blocker.release();
	}
	public void addUser(UserThread user, PrintWriter out) {
		users.add(user);
		outs.add(out);
	}
}

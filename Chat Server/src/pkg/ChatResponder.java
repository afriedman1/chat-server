package pkg;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.Semaphore;

public class ChatResponder implements Runnable {
	
	private ArrayList<PrintWriter> outs;
	private LinkedList<String> messages;
	private ArrayList<UserThread> users;
	private Semaphore blocker;
	
	public ChatResponder() {
		outs = new ArrayList<PrintWriter>();
		messages = new LinkedList<String>();;
		users = new ArrayList<UserThread>();;
		blocker = new Semaphore(0);
	}
	
	private void outputMessage(String message) {
		for (PrintWriter o: outs) {
			o.println(message);
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				blocker.acquire();
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
			messages.add(str);
			System.out.println(messages);
		}
		blocker.release();
	}
	public void addUser(UserThread user, PrintWriter out) {
		synchronized(blocker) {
			users.add(user);
			outs.add(out);
		}
	}
	public void removeUser(UserThread user) {
		int index = users.indexOf(user);
		synchronized(blocker) {
			users.remove(index);
			outs.remove(index);
		}
	}
	public boolean validateName(String name) {
		for (UserThread user : users) {
			if (user.getName().equals(name)) {
				return false;
			}
		}
		return true;
	}
	public UserThread getUserByName(String name) {
		for (UserThread u : users) {
			if (name.equals(u.getName())) {
				return u;
			}
		}
		return null;
	}
}

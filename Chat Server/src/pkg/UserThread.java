package pkg;

import java.io.*;
import java.net.Socket;

public class UserThread implements Runnable {
	
	private static int nextUser = 1;
	
	private String name;
	private String message;
	private BufferedReader in;
	private PrintWriter out;
	private ChatResponder responder;
	private int health;
	private Socket socket;
	public Object healthLock;
	private int def;
	private int kills;
	private int level;
	private int killsToNextLevel;
	private int attackPower;

	public UserThread(Socket s, BufferedReader input, PrintWriter output, ChatResponder r) {
		responder = r;
		name = "guest_"+UserThread.nextUser;
		nextUser++;
		in = input;
		out = output;
		health = 100;
		def = 1;
		socket = s;
		kills = 0;
		level = 1;
		killsToNextLevel += nextLevelKills(level);
		attackPower = -5;
	}
	public void run() {
		while (true) {
			try {
				boolean sendMessage = true;
				String input = this.in.readLine();
				if (input == null) {
					responder.removeUser(this);
					responder.addMessageToQueue(name+" died peacefully.");
					return;
				}
				if (input.split(" ")[0].equals("/n")) {
					sendMessage = changeName(input.substring(input.indexOf(" ")+1));
				}
				else if (input.split(" ")[0].equals("/a")) {
					sendMessage = attackUserByName (input.substring(input.indexOf(" ")+1));
				} else {
					message = "<"+name+"|"+level+"|"+kills+"> "+input;
				}
				if (sendMessage) {
					responder.addMessageToQueue(message);
				}
			} catch (IOException e) {
				responder.removeUser(this);
				responder.addMessageToQueue(name+" died a glorious death.");
				return;
			}
		}
	}
	public String getName() {
		return name;
	}
	public String getMessage() {
		return message;
	}
	public boolean changeName(String newName) {
		if (responder.validateName(newName)) {
			String oldName = this.name;
			this.name = newName;
			message = oldName+" changed his/her name to "+newName+".";
			return true;
		} else {
			synchronized(responder) {
				out.println("That name is already taken.");
				return false;
			}
		}
	}
	public boolean attackUserByName(String n) {
		UserThread target = responder.getUserByName(n);
		if (target != null) {
			if (((int)(Math.random()*11)) > target.def) {
				target.changeHealth(attackPower);
				message = name+" attacked "+target.name+"!";
				if (target.isDead()) {
					kills++;
					if (kills >= killsToNextLevel) {
						levelUp();
						out.println("You have reached level "+level+".");
					}
				}
			} else {
				message = name+" attacked "+target.name+", but the attack was blocked...";
			}
			return true;
		}
		out.println(n+" is nowhere to be found...");
		return false;
	}
	public synchronized void changeHealth(int h) {
		health += h;
		if (health > 100) health = 100;
		else if (health <= 0) die();
	}
	public boolean isDead() {
		return health <= 0;
	}
	private void die() {
		out.println("You died.");
		try {
			socket.close();
		} catch(IOException e) {
			
		}
	}
	private void levelUp() {
		level++;
		attackPower = (int)(attackPower*1.3);
		if (level%5==0) def++;
	}
	private static int nextLevelKills(int l) {
		return (int)Math.pow(l,2);
	}
}

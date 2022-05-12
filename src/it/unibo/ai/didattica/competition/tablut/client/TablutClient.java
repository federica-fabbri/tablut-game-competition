package it.unibo.ai.didattica.competition.tablut.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;

import com.google.gson.Gson;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.util.StreamUtils;
import it.unibo.ai.didattica.competition.tablut.server.Server;

/**
 * Classe astratta di un client per il gioco Tablut
 * 
 * @author Andrea Piretti
 *
 */
public abstract class TablutClient implements Runnable {

	private State.Turn player;
	private String name;
	private Socket playerSocket;
	private DataInputStream in;
	private DataOutputStream out;
	private Gson gson;
	private State currentState;
	private int timeout;
	private String serverIp;

	public State.Turn getPlayer() {
		return player;
	}

	public void setPlayer(State.Turn player) {
		this.player = player;
	}

	public State getCurrentState() {
		return currentState;
	}

	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

	public TablutClient(String player, String name, int timeout, String ipAddress)
			throws UnknownHostException, IOException {
		int port = 0;
		serverIp = ipAddress;
		this.timeout = timeout;
		this.gson = new Gson();
		if (player.toLowerCase().equals("white")) {
			this.player = State.Turn.WHITE;
			port = Server.whitePort;
		} else if (player.toLowerCase().equals("black")) {
			this.player = State.Turn.BLACK;
			port = Server.blackPort;
		} else {
			throw new InvalidParameterException("Player role must be BLACK or WHITE");
		}
		playerSocket = new Socket(serverIp, port);
		out = new DataOutputStream(playerSocket.getOutputStream());
		in = new DataInputStream(playerSocket.getInputStream());
		this.name = name;
	}

	public TablutClient(String player, String name, int timeout) throws UnknownHostException, IOException {
		this(player, name, timeout, "localhost");
	}


	public TablutClient(String player, String name) throws UnknownHostException, IOException {
		this(player, name, 60, "localhost");
	}

	public TablutClient(String player, String name, String ipAddress) throws UnknownHostException, IOException {
		this(player, name, 60, ipAddress);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void write(Action action) throws IOException, ClassNotFoundException {
		StreamUtils.writeString(out, this.gson.toJson(action));
	}

	public void declareName() throws IOException, ClassNotFoundException {
		StreamUtils.writeString(out, this.gson.toJson(this.name));
	}

	public void read() throws ClassNotFoundException, IOException {
		this.currentState = this.gson.fromJson(StreamUtils.readString(in), StateTablut.class);
	}
}

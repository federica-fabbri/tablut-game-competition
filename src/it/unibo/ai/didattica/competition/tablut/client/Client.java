package it.unibo.ai.didattica.competition.tablut.client;

import java.io.IOException;
import java.net.UnknownHostException;

import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.domain.*;
import HIMYT.*; 

public class Client extends TablutClient {
	
	private static int DEPTH = 4; 
	
	public Client(String player, String name) throws UnknownHostException, IOException {
		super(player, name);
	}
	public Client(String player, String name, int timeout, String ipAddress) throws UnknownHostException, IOException {
		super(player, name, timeout, ipAddress);
	}

	@Override
	public void run() 
	{
		try {
			this.declareName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		State state;		
		Game game = null;
		state = new StateTablut();
		state.setTurn(State.Turn.WHITE);
		game = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
		AlphaBetaSearch alphabeta = new AlphaBetaSearch(game, super.getTimeout()-3, DEPTH);
		
		System.out.println("You are player " + this.getPlayer().toString() + "!");
		
		while(true) {
			try {
				this.read();
			} catch (ClassNotFoundException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(1);
			}
			System.out.println("Current state:");
			
			state = this.getCurrentState();
			state.updatePos();

			System.out.println(state.toString());			

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			
			if (this.getPlayer().equals(Turn.WHITE)) {
				if (this.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE)) {
					Action action = alphabeta.makeDecision(state);
					System.out.println("Mossa scelta: " + action.toString());
					
					try {
						this.write(action);
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				else if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
					System.out.println("Waiting for your opponent move... ");
				}
				else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
					System.out.println("YOU WIN!");
					System.exit(0);
				}
				else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
					System.out.println("YOU LOSE!");
					System.exit(0);
				}
				else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}
				
			}
			else{
				if (this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK)) {
					Action action = alphabeta.makeDecision(state);
					System.out.println("Mossa scelta: " + action.toString());

					try {
						this.write(action);
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				else if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
					System.out.println("Waiting for your opponent move... ");
				} 		
				else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
					System.out.println("YOU LOSE!");
					System.exit(0);
				} 
				else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
					System.out.println("YOU WIN!");
					System.exit(0);
				} 
				else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}		
			}			
		}
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		String color = "";
		String name = "HowIMetYourTablut";
		String ipAddress = "localhost";
		int timeout = 60;
		
		if (args.length < 1) {
			System.out.println("You must specify which player you are (WHITE or BLACK)");
			System.exit(-1);
		} else {
			System.out.println(args[0]);
			color = (args[0]);
		}
		if (args.length == 2) {
			System.out.println(args[1]);
			timeout = Integer.parseInt(args[1]);
		}
		if (args.length == 3) {
			timeout = Integer.parseInt(args[1]);
			ipAddress = args[2];
		}
		System.out.println("Selected client: " + args[0]);

		Client client = new Client(color, name, timeout, ipAddress);
		client.run();
	}
}

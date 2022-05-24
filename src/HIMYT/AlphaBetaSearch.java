package HIMYT;

import java.util.ArrayList;
import java.util.List;
import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class AlphaBetaSearch {

    public static Game game;
    protected int depthMax;
    private Timer timer;
    public State.Turn turn;

    public AlphaBetaSearch(Game game, int time, int d) {
        AlphaBetaSearch.game = game;
        this.timer = new Timer(time);
        this.depthMax = d;
    }

    public Action makeDecision(State state) {
    	int i = 0;
    	this.turn = state.getTurn();
    	boolean contains;
    	State clone;
        List<Action> temp = state.getAllLegalActions(game);
        List<Action> actions = new ArrayList<Action>();

        if(state.getTurn() == State.Turn.WHITE) {
        	actions = state.getLegalMovesForPosition(game, state.getKingPosition());
        	actions.addAll(temp);
        }
        else
        	actions = temp;
        	
        double[] valA = new double [actions.size()];
        timer.start();
        int depthVal = 0;
        
        do {
        	depthVal++;
	        i = 0;
	        for (Action action : actions) {
	            clone = state.clone();
	            valA[i] = minValue(game.processMove(clone, action), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depthVal);
	            if (timer.timeOut()) {
	                break; 
	            }
	            i++;
	        }
	        contains = false;
	        for (int j=0; j<valA.length && !contains; j++ ) {
	        	if ( valA[j] == Double.POSITIVE_INFINITY) {
	        		contains = true;
	        	}
	        }
        } while(!timer.timeOut() && !contains && depthVal < depthMax);
        
        int maxVal = 0;
        double max = valA[0];
        for (int k=0; k < valA.length; k++) {
        	if (valA[k] > max) {
    			max = valA[k];
    			maxVal = k;
    		}
        }
        	
        return actions.get(maxVal);
    }

    public double maxValue(State state, double alpha, double beta, int depth) {

    	if ((this.turn == Turn.WHITEWIN || this.turn == Turn.BLACKWIN) || depth >= this.depthMax || timer.timeOut() ) {
            return eval(state);
        } else {
            double value = Double.NEGATIVE_INFINITY;
            ArrayList<State> successors = state.getSuccessors(game);
            
            for (State s : successors) 
            {
                value = Math.max(value, minValue(s, alpha, beta, depth + 1));
                if (value >= beta)
                    return value;
                alpha = Math.max(alpha, value);
            }
            return value;
        }
    }

    public double minValue(State state, double alpha, double beta, int depth) {
    	if ((this.turn == Turn.WHITEWIN || this.turn == Turn.BLACKWIN) || depth >= this.depthMax || timer.timeOut() ) {
            return eval(state);
        } else {
        	
            double value = Double.POSITIVE_INFINITY;
            ArrayList<State> successors = state.getSuccessors(game);
            
            for (State sS : successors) 
            {
                value = Math.min(value, maxValue(sS, alpha, beta, depth + 1));
                if (value <= alpha)
                    return value;
                beta = Math.min(beta, value);
            }
            return value;
        }
    }

    protected double eval(State state) {
    	
    	double value = Heuristic.evaluation(state, turn);
    	return value;
    }  
}

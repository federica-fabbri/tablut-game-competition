package HIMYT; 

import java.util.List;


import it.unibo.ai.didattica.competition.tablut.domain.*;

public class Heuristic {
	
	public static double evaluation(State state, State.Turn player) {
	
		double myPieces, advPieces;
		
		if (player == State.Turn.WHITE) {
			myPieces = (double) state.getCounterNumWhite();
			advPieces = (double) state.getCounterNumBlack();
		} else {
			advPieces = (double) state.getCounterNumWhite();
			myPieces = (double) state.getCounterNumBlack();
		}
		
		double res = 0.0;
    	if (player == State.Turn.WHITE) {
    		if (state.getTurn() == State.Turn.WHITEWIN)
    			return Double.POSITIVE_INFINITY;
    		if (state.getTurn() == State.Turn.BLACKWIN)
    			return Double.NEGATIVE_INFINITY;
    		res = myPieces - advPieces - advNearKing(state) + kingBestPos(state);
    	} 
    	else {
    		if (state.getTurn() == State.Turn.BLACKWIN)
				return Double.POSITIVE_INFINITY;
    		if (state.getTurn() == State.Turn.WHITEWIN)
    			return Double.NEGATIVE_INFINITY;

    		res = myPieces - advPieces + advNearKing(state);
    	}

    	return res;
    }
	
	public static double advNearKing(State state) {
		double numPieces = 0.0;
		
		List<Position> aroundKing = state.getPiecesAround(state.getKingPosition());

		for (Position p : aroundKing) {
			if (state.getPawn(p.x, p.y) == State.Pawn.BLACK) {
				numPieces += 0.25;
			}
		}
		
		return numPieces;
	}
	
	public static double bestPos(State state) {
		double value = 0.0;
		
		if (state.getPawn(1, 1) == State.Pawn.BLACK) {
			value += 0.25;
		}
		if (state.getPawn(1, 7) == State.Pawn.BLACK) {
			value += 0.25;
		}
		if (state.getPawn(7, 1) == State.Pawn.BLACK) {
			value += 0.25;
		}
		if (state.getPawn(7, 7) == State.Pawn.BLACK) {
			value += 0.1;
		}
		if (state.getPawn(8, 6) == State.Pawn.BLACK) {
			value += 0.1;
		}
		if (state.getPawn(6, 8) == State.Pawn.BLACK) {
			value += 0.1;
		}
		if (state.getPawn(0, 2) == State.Pawn.BLACK) {
			value += 0.1;
		}
		if (state.getPawn(2, 0) == State.Pawn.BLACK) {
			value += 0.1;
		}
		if (state.getPawn(0, 6) == State.Pawn.BLACK) {
			value += 0.1;
		}
		if (state.getPawn(6, 0) == State.Pawn.BLACK) {
			value += 0.1;
		}
		if (state.getPawn(8, 2) == State.Pawn.BLACK) {
			value += 0.1;
		}
		if (state.getPawn(2, 8) == State.Pawn.BLACK) {
			value += 0.1;
		}
		
		return value;
	}
	
	public static double kingBestPos(State state) {
		double value = 0.0;
		Position kingPos = state.getKingPosition();
		int i = 0;
		int length = state.getBoard().length;
		boolean taken = false;
		boolean attacked = false;
		
		if(kingPos.x == 2) {
			for(i = 0; i < length; i++) 
				if ((state.getPawn(kingPos.x, i) != State.Pawn.EMPTY) && (state.getPawn(kingPos.x, i) != State.Pawn.KING))
					taken = true;
			if(!taken) {
				if ((state.getPawn(kingPos.x-1, kingPos.y) != State.Pawn.BLACK) && 
						(state.getPawn(kingPos.x+1, kingPos.y) != State.Pawn.BLACK) && (kingPos.y != 4)) 
					return 500;
				else {
					for(i = kingPos.y; i < length; i++) {
						if (state.getPawn(kingPos.x+1, i) == State.Pawn.WHITE)
							break;
						if (state.getPawn(kingPos.x+1, i) == State.Pawn.BLACK)
							attacked = true;
					}
					for(i = kingPos.y; i >= 0; i--) {
						if (state.getPawn(kingPos.x+1, i) == State.Pawn.WHITE)
							break;
						if (state.getPawn(kingPos.x+1, i) == State.Pawn.BLACK)
							attacked = true;
					}
					for (i = kingPos.x+1; i < length; i++) {
						if (state.getPawn(i, kingPos.y) == State.Pawn.WHITE)
							break;
						if (state.getPawn(i, kingPos.y) == State.Pawn.BLACK)
							attacked = true;
					}
					if(!attacked)
						return 500;
				}
			}		
		}
		
		taken = false;
		attacked = false;		
		if(kingPos.x == 6) {
			for(i = 0; i < length; i++) 
				if ((state.getPawn(kingPos.x, i) != State.Pawn.EMPTY) && (state.getPawn(kingPos.x, i) != State.Pawn.KING))
					taken = true;
			if(!taken) {
				if ((state.getPawn(kingPos.x-1, kingPos.y) != State.Pawn.BLACK) && 
						(state.getPawn(kingPos.x+1, kingPos.y) != State.Pawn.BLACK) && (kingPos.y != 4)) 
					return 500;
				else {
					for(i = kingPos.y; i < length; i++) {
						if (state.getPawn(kingPos.x-1, i) == State.Pawn.WHITE)
							break;
						if (state.getPawn(kingPos.x-1, i) == State.Pawn.BLACK)
							attacked = true;
					}
					for(i = kingPos.y; i >= 0; i--) {
						if (state.getPawn(kingPos.x-1, i) == State.Pawn.WHITE)
							break;
						if (state.getPawn(kingPos.x-1, i) == State.Pawn.BLACK)
							attacked = true;
					}
					for (i = kingPos.x-1; i >= 0; i--) {
						if (state.getPawn(i, kingPos.y) == State.Pawn.WHITE)
							break;
						if (state.getPawn(i, kingPos.y) == State.Pawn.BLACK)
							attacked = true;
					}
					if(!attacked)
						return 500;
				}
			}		
		}
		
		taken = false;
		attacked = false;
		if(kingPos.y == 2) {
			for(i = 0; i < length; i++) 
				if ((state.getPawn(i, kingPos.y) != State.Pawn.EMPTY) && (state.getPawn(i, kingPos.y) != State.Pawn.KING))
					taken = true;
			if(!taken) {
				if ((state.getPawn(kingPos.x, kingPos.y-1) != State.Pawn.BLACK) && 
						(state.getPawn(kingPos.x, kingPos.y+1) != State.Pawn.BLACK) && (kingPos.x != 4)) 
					return 500;
				else {
					for(i = kingPos.x; i < length; i++) {
						if (state.getPawn(i, kingPos.y+1) == State.Pawn.WHITE)
							break;
						if (state.getPawn(i, kingPos.y+1) == State.Pawn.BLACK)
							attacked = true;
					}
					for(i = kingPos.x; i >= 0; i--) {
						if (state.getPawn(i, kingPos.y+1) == State.Pawn.WHITE)
							break;
						if (state.getPawn(i, kingPos.y+1) == State.Pawn.BLACK)
							attacked = true;
					}
					for (i = kingPos.y+1; i < length; i++) {
						if (state.getPawn(kingPos.x, i) == State.Pawn.WHITE)
							break;
						if (state.getPawn(kingPos.x, i) == State.Pawn.BLACK)
							attacked = true;
					}
					if(!attacked)
						return 500;
				}
			}		
		}
		
		taken = false;
		attacked = false;
		if(kingPos.y == 6) {
			for(i = 0; i < length; i++) 
				if ((state.getPawn(i, kingPos.y) != State.Pawn.EMPTY) && (state.getPawn(i, kingPos.y) != State.Pawn.KING))
					taken = true;
			if(!taken) {
				if ((state.getPawn(kingPos.x, kingPos.y-1) != State.Pawn.BLACK) && 
						(state.getPawn(kingPos.x, kingPos.y+1) != State.Pawn.BLACK) && (kingPos.x != 4)) 
					return 500;
				else {
					for(i = kingPos.x; i < length; i++) {
						if (state.getPawn(i, kingPos.y-1) == State.Pawn.WHITE)
							break;
						if (state.getPawn(i, kingPos.y-1) == State.Pawn.BLACK)
							attacked = true;
					}
					for(i = kingPos.x; i >= 0; i--) {
						if (state.getPawn(i, kingPos.y-1) == State.Pawn.WHITE)
							break;
						if (state.getPawn(i, kingPos.y-1) == State.Pawn.BLACK)
							attacked = true;
					}
					for (i = kingPos.y-1; i >= 0; i--) {
						if (state.getPawn(kingPos.x, i) == State.Pawn.WHITE)
							break;
						if (state.getPawn(kingPos.x, i) == State.Pawn.BLACK)
							attacked = true;
					}
					if(!attacked)
						return 500;
				}
			}		
		}
		
		return value;
	}
	
}

package it.unibo.ai.didattica.competition.tablut.domain;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Abstract class for a State of a game We have a representation of the board
 * and the turn
 * 
 * @author Andrea Piretti
 *
 */
public abstract class State {

	/**
	 * Turn represent the player that has to move or the end of the game(A win
	 * by a player or a draw)
	 * 
	 * @author A.Piretti
	 */
	public enum Turn {
		WHITE("W"), BLACK("B"), WHITEWIN("WW"), BLACKWIN("BW"), DRAW("D");
		private final String turn;

		private Turn(String s) {
			turn = s;
		}

		public boolean equalsTurn(String otherName) {
			return (otherName == null) ? false : turn.equals(otherName);
		}

		public String toString() {
			return turn;
		}
	}

	/**
	 * 
	 * Pawn represents the content of a box in the board
	 * 
	 * @author A.Piretti
	 *
	 */
	public enum Pawn {
		EMPTY("O"), WHITE("W"), BLACK("B"), THRONE("T"), KING("K");
		private final String pawn;

		private Pawn(String s) {
			pawn = s;
		}

		public boolean equalsPawn(String otherPawn) {
			return (otherPawn == null) ? false : pawn.equals(otherPawn);
		}

		public String toString() {
			return pawn;
		}

	}

	protected Pawn board[][];
	protected Turn turn;
	public ArrayList<Position> whitePos;
	public ArrayList<Position> blackPos;
	public ArrayList<Position> winningPosWhite;
	public Position kingPos;
	public int startingNWhite;
	public int startingNBlack;
	public int counterNumWhite;
	public int counterNumBlack;
	public int turnNumber;
	

	public State() {
		super();
	}

	public Pawn[][] getBoard() {
		return board;
	}

	public String boardString() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < this.board.length; i++) {
			for (int j = 0; j < this.board.length; j++) {
				result.append(this.board[i][j].toString());
				if (j == 8) {
					result.append("\n");
				}
			}
		}
		return result.toString();
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();

		// board
		result.append("");
		result.append(this.boardString());

		result.append("-");
		result.append("\n");

		// TURNO
		result.append(this.turn.toString());

		return result.toString();
	}

	public String toLinearString() {
		StringBuffer result = new StringBuffer();

		result.append("");
		result.append(this.boardString().replace("\n", ""));
		result.append(this.turn.toString());

		return result.toString();
	}

	public Pawn getPawn(int row, int column) {
		return this.board[row][column];
	}
	
	public Position getKingPosition() {
		return kingPos;
	}
	
	public int getTurnNumber() {
		return turnNumber;
	}
	
	public ArrayList<Position> getPiecesAround(Position pos) {
		ArrayList<Position> around = new ArrayList<Position>();
		if(pos.x != 8) {
			around.add(new Position(pos.x+1, pos.y));
		}
		if(pos.x != 0) {
			around.add(new Position(pos.x-1, pos.y));
		}
		if(pos.y != 8) {
			around.add(new Position(pos.x, pos.y+1));
		}
		if(pos.y != 0) {
			around.add(new Position(pos.x, pos.y-1));
		}
		
		return around;
	}

	public void removePawn(int row, int column) {
		this.board[row][column] = Pawn.EMPTY;
	}

	public void setBoard(Pawn[][] board) {
		this.board = board;
	}

	public Turn getTurn() {
		return turn;
	}

	public void setTurn(Turn turn) {
		this.turn = turn;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (this.board == null) {
			if (other.board != null)
				return false;
		} else {
			if (other.board == null)
				return false;
			if (this.board.length != other.board.length)
				return false;
			if (this.board[0].length != other.board[0].length)
				return false;
			for (int i = 0; i < other.board.length; i++)
				for (int j = 0; j < other.board[i].length; j++)
					if (!this.board[i][j].equals(other.board[i][j]))
						return false;
		}
		if (this.turn != other.turn)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.board == null) ? 0 : this.board.hashCode());
		result = prime * result + ((this.turn == null) ? 0 : this.turn.hashCode());
		return result;
	}

	public String getBox(int row, int column) {
		String ret;
		char col = (char) (column + 97);
		ret = col + "" + (row + 1);
		return ret;
	}
	
	public State clone() {
		Class<? extends State> stateclass = this.getClass();
		Constructor<? extends State> cons = null;
		State result = null;
		
		try {
			cons = stateclass.getConstructor(stateclass);
			result = cons.newInstance(new Object[0]);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		Pawn oldboard[][] = this.getBoard();
		Pawn newboard[][] = result.getBoard();

		for (int i = 0; i < this.board.length; i++) {
			for (int j = 0; j < this.board[i].length; j++) {
				newboard[i][j] = oldboard[i][j];
			}
		}
		
		result.setBoard(newboard);
		result.setTurn(this.turn);
		
		return result;
	}

	public ArrayList<Position> getWhitePos() {
		return whitePos;
	}

	public void setWhitePos(ArrayList<Position> whitePos) {
		this.whitePos = whitePos;
	}

	public ArrayList<Position> getBlackPos() {
		return blackPos;
	}

	public void setBlackPos(ArrayList<Position> blackPos) {
		this.blackPos = blackPos;
	}

	public ArrayList<Position> getWhiteWinPos() {
		return winningPosWhite;
	}

	public void setWhiteWinPos(ArrayList<Position> whiteWinPos) {
		this.winningPosWhite = whiteWinPos;
	}

	public Position getKingPos() {
		return kingPos;
	}

	public void setKingPos(Position kingPos) {
		this.kingPos = kingPos;
	}

	public int getStartingNWhite() {
		return startingNWhite;
	}

	public void setStartingNWhite(int startingNWhite) {
		this.startingNWhite = startingNWhite;
	}

	public int getStartingNBlack() {
		return startingNBlack;
	}

	public void setStartingNBlack(int startingNBlack) {
		this.startingNBlack = startingNBlack;
	}

	public int getCounterNumWhite() {
		return counterNumWhite;
	}

	public void setCounterNumWhite(int counterNWhite) {
		this.counterNumWhite = counterNWhite;
	}

	public int getCounterNumBlack() {
		return counterNumBlack;
	}

	public void setCounterNumBlack(int counterNBlack) {
		this.counterNumBlack = counterNBlack;
	}

	public void setTurnNumber(int turnNumber) {
		this.turnNumber = turnNumber;
	}

	public int getNumberOf(Pawn color) {
		int count = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == color)
					count++;
			}
		}
		return count;
	}

	public void updatePos() {
		int i = 0;
		int j = 0;
		boolean king = false;
		this.turnNumber++;
		ArrayList<Position> tempWhitePos = new ArrayList<Position>();
		ArrayList<Position> tempBlackPos = new ArrayList<Position>();
		
		for (i = 0; i < board.length; i++) {
			for (j = 0; j < board.length; j++) {
				if (this.board[i][j] == Pawn.WHITE) 
					tempWhitePos.add(new Position(i,j));
				if (this.board[i][j] == Pawn.BLACK) 
					tempBlackPos.add(new Position(i,j));
				if (this.board[i][j] == Pawn.KING) {
					this.kingPos = new Position(i,j);
					king = true;
				}
			}
		}
		this.counterNumWhite = tempWhitePos.size();
		this.counterNumBlack = tempBlackPos.size();
		if (king)
			this.counterNumWhite++;
		this.whitePos = tempWhitePos;
		this.blackPos = tempBlackPos;
		
	}

    public ArrayList<Action> getAllLegalActions(Game game) {

        ArrayList<Action> allActions = new ArrayList<>();
        ArrayList<Position> posSet = new ArrayList<>();
        
        if(this.turn.equals(StateTablut.Turn.WHITE)) {
        	posSet = whitePos;
        } else {
        	posSet = blackPos;
        }
        
        for (Position pos : posSet) {
            allActions.addAll(getLegalMovesForPosition(game, pos));
        }
        return allActions;
    }
    
    public ArrayList<Action> getLegalMovesForPosition(Game game, Position pos) {
    	ArrayList<Action> legalMoves = new ArrayList<>();
    	
    	legalMoves.addAll(getLegalMovesInDirection(game, pos, -1, 0));
    	legalMoves.addAll(getLegalMovesInDirection(game, pos, 1, 0));
    	legalMoves.addAll(getLegalMovesInDirection(game, pos, 0, -1));
    	legalMoves.addAll(getLegalMovesInDirection(game, pos, 0, 1));
    	
    	return legalMoves;
    }
    
    public ArrayList<Action> getLegalMovesInDirection(Game game, Position pos, int x, int y)
    {
    	boolean correct = false;
    	ArrayList<Action> legalMoves = new ArrayList<>();
    	assert (!(x != 0 && y != 0));
        int startPos = (x != 0) ? pos.x : pos.y;
        int incr = (x != 0) ? x : y;
        int endIdx = (incr == 1) ? board.length - 1 : 0;

        for (int i = startPos + incr; incr * i <= endIdx; i += incr) {
            correct = false;
        	Action temp_action = null;
        	
			try {
				temp_action = (x != 0) ? new Action(getBox(pos.x, pos.y), getBox(i, pos.y), turn) : new Action(getBox(pos.x, pos.y), getBox(pos.x, i), turn);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
        	try {
				game.checkMove(this, temp_action);
				correct = true;
			} catch (Exception e) {

			}
        	if(correct)
        		legalMoves.add(temp_action);        	
        }

    	return legalMoves;
    }	
    
    public ArrayList<State> getSuccessors(Game game)  {
    	ArrayList<State> successors = new ArrayList<State>();
    	List<Action> temp = getAllLegalActions(game);
        List<Action> allActions = new ArrayList<Action>();
		
        Collections.shuffle(temp);
        
        if(getTurn() == State.Turn.WHITE) {
        	allActions = getLegalMovesForPosition(game, getKingPosition());
        	allActions.addAll(temp);
        }
        else
        	allActions = temp;
        
    	for (Action action: allActions) {
    		State clonedState = (State) clone();
    		clonedState = game.processMove(clonedState, action); 
    		successors.add(clonedState);
    	}
    	return successors;    	
    }
    
}

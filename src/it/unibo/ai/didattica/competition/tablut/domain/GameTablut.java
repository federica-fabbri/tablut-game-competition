package it.unibo.ai.didattica.competition.tablut.domain;

import java.io.File;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import it.unibo.ai.didattica.competition.tablut.exceptions.*;

/**
 * 
 * This class represents the pure game, with all the rules;
 * it check the move, the state of the match and is a pawn is eliminated or not
 * @author A.Piretti
 *
 */
public class GameTablut implements Game {
	
	private int movesDraw;
	private int movesWithutCapturing;
	private String gameLogName;
	private File gameLog;
	private FileHandler fh;
	private Logger loggGame;
	
	public GameTablut() {
		this(0);
	}
	
	public GameTablut(int moves) {
		super();
		this.movesDraw = moves;
		this.movesWithutCapturing=0;
		this.gameLogName = (new Date().getTime())+"_gameLog.txt";
		this.setGameLog(new File(this.gameLogName));
		fh = null;
		try
		{
			fh = new FileHandler(gameLogName, true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		this.loggGame = Logger.getLogger("GameLog");
		loggGame.addHandler(this.fh);
		this.fh.setFormatter(new SimpleFormatter());
		loggGame.setLevel(Level.FINE);
		loggGame.fine("Inizio partita");
	}

	public void checkMove(State state, Action a) throws BoardException, ActionException, StopException, PawnException, DiagonalException, ClimbingException, ThroneException, OccupitedException
	{
	
		if(a.getTo().length()!=2 || a.getFrom().length()!=2)
		{
			this.loggGame.warning("Formato mossa errato");
			throw new ActionException(a);
		}
		int columnFrom = a.getColumnFrom();
		int columnTo = a.getColumnTo();
		int rowFrom = a.getRowFrom();
		int rowTo = a.getRowTo();
		
		
		if(columnFrom>state.getBoard().length-1 || rowFrom>state.getBoard().length-1 || rowTo>state.getBoard().length-1 || columnTo>state.getBoard().length-1 || columnFrom<0 || rowFrom<0 || rowTo<0 || columnTo<0)
		{
			this.loggGame.warning("Mossa fuori tabellone");
			throw new BoardException(a);			
		}
		
		
		if(state.getPawn(rowTo, columnTo).equalsPawn(State.Pawn.THRONE.toString()))
		{
			this.loggGame.warning("Mossa sul trono");
			throw new ThroneException(a);
		}
		
		
		if(!state.getPawn(rowTo, columnTo).equalsPawn(State.Pawn.EMPTY.toString()))
		{
			this.loggGame.warning("Mossa sopra una casella occupata");
			throw new OccupitedException(a);
		}
		
		
		if(rowFrom==rowTo && columnFrom==columnTo)
		{
			this.loggGame.warning("Nessuna mossa");
			throw new StopException(a);
		}
		
		if(state.getTurn().equalsTurn(State.Turn.WHITE.toString()))
		{
			if(!state.getPawn(rowFrom, columnFrom).equalsPawn("W") && !state.getPawn(rowFrom, columnFrom).equalsPawn("K"))
			{
				this.loggGame.warning("Giocatore "+a.getTurn()+" cerca di muovere una pedina avversaria");
				throw new PawnException(a);
			}
		}
		if(state.getTurn().equalsTurn(State.Turn.BLACK.toString()))
		{
			if(!state.getPawn(rowFrom, columnFrom).equalsPawn("B"))
			{
				this.loggGame.warning("Giocatore "+a.getTurn()+" cerca di muovere una pedina avversaria");
				throw new PawnException(a);
			}
		}
		
		
		if(rowFrom != rowTo && columnFrom != columnTo)
		{
			this.loggGame.warning("Mossa in diagonale");
			throw new DiagonalException(a);
		}
		
		
		if(rowFrom==rowTo)
		{
			if(columnFrom>columnTo)
			{
				for(int i=columnTo; i<columnFrom; i++)
				{
					if(!state.getPawn(rowFrom, i).equalsPawn(State.Pawn.EMPTY.toString()))
					{
						this.loggGame.warning("Mossa che scavalca una pedina");
						throw new ClimbingException(a);
					}
				}
			}
			else
			{
				for(int i=columnFrom+1; i<=columnTo; i++)
				{
					if(!state.getPawn(rowFrom, i).equalsPawn(State.Pawn.EMPTY.toString()))
					{
						this.loggGame.warning("Mossa che scavalca una pedina");
						throw new ClimbingException(a);
					}
				}
			}
		}
		else
		{
			if(rowFrom>rowTo)
			{
				for(int i=rowTo; i<rowFrom; i++)
				{
					if(!state.getPawn(i, columnFrom).equalsPawn(State.Pawn.EMPTY.toString()) && !state.getPawn(i, columnFrom).equalsPawn(State.Pawn.THRONE.toString()))
					{
						this.loggGame.warning("Mossa che scavalca una pedina");
						throw new ClimbingException(a);
					}
				}
			}
			else
			{
				for(int i=rowFrom+1; i<=rowTo; i++)
				{
					if(!state.getPawn(i, columnFrom).equalsPawn(State.Pawn.EMPTY.toString()) && !state.getPawn(i, columnFrom).equalsPawn(State.Pawn.THRONE.toString()))
					{
						this.loggGame.warning("Mossa che scavalca una pedina");
						throw new ClimbingException(a);
					}
				}
			}
		}
	}
	
	@Override
	public State processMove(State state, Action a)
	{
		
		state = this.movePawn(state, a);
		
		
		if(state.getTurn().equalsTurn("W"))
		{
			state = this.checkCaptureBlack(state, a);
		}
		if(state.getTurn().equalsTurn("B"))
		{
			state = this.checkCaptureWhite(state, a);
		}
		
		this.loggGame.fine("Stato: "+state.toString());
		
		return state;
	}

	
	
	private State movePawn(State state, Action a) {
		State.Pawn pawn = state.getPawn(a.getRowFrom(), a.getColumnFrom());
		State.Pawn[][] newBoard = state.getBoard();
		this.loggGame.fine("Movimento pedina");
		
		if(newBoard.length==9)
		{
			if(a.getColumnFrom()==4 && a.getRowFrom()==4)
			{
				newBoard[a.getRowFrom()][a.getColumnFrom()]= State.Pawn.THRONE;
			}
			else
			{
				newBoard[a.getRowFrom()][a.getColumnFrom()]= State.Pawn.EMPTY;
			}
		}
		if(newBoard.length==7)
		{
			if(a.getColumnFrom()==3 && a.getRowFrom()==3)
			{
				newBoard[a.getRowFrom()][a.getColumnFrom()]= State.Pawn.THRONE;
			}
			else
			{
				newBoard[a.getRowFrom()][a.getColumnFrom()]= State.Pawn.EMPTY;
			}
		}
		
	
		newBoard[a.getRowTo()][a.getColumnTo()]=pawn;
		state.setBoard(newBoard);
		if(state.getTurn().equalsTurn(State.Turn.WHITE.toString()))
		{
			state.setTurn(State.Turn.BLACK);
		}
		else
		{
			state.setTurn(State.Turn.WHITE);
		}
		
		
		return state;
	}
	
	private State checkCaptureWhite(State state, Action a)
	{
	
		if(a.getColumnTo()<state.getBoard().length-2 && state.getPawn(a.getRowTo(), a.getColumnTo()+1).equalsPawn("B") && (state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("W")||state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("T")||state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("K")))
		{
			state.removePawn(a.getRowTo(), a.getColumnTo()+1);
			this.movesWithutCapturing=-1;
			this.loggGame.fine("Pedina nera rimossa in: "+state.getBox(a.getRowTo(), a.getColumnTo()+1));
		}
		
		if(a.getColumnTo()>1 && state.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("B") && (state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("W")||state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("T")||state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("K")))
		{
			state.removePawn(a.getRowTo(), a.getColumnTo()-1);
			this.movesWithutCapturing=-1;
			this.loggGame.fine("Pedina nera rimossa in: "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
		}
		
		if(a.getRowTo()>1 && state.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("B") && (state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("W")||state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("T")||state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("K")))
		{
			state.removePawn(a.getRowTo()-1, a.getColumnTo());
			this.movesWithutCapturing=-1;
			this.loggGame.fine("Pedina nera rimossa in: "+state.getBox(a.getRowTo()-1, a.getColumnTo()));
		}
		
		if(a.getRowTo()<state.getBoard().length-2 && state.getPawn(a.getRowTo()+1, a.getColumnTo()).equalsPawn("B") && (state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("W")||state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("T")||state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("K")))
		{
			state.removePawn(a.getRowTo()+1, a.getColumnTo());
			this.movesWithutCapturing=-1;
			this.loggGame.fine("Pedina nera rimossa in: "+state.getBox(a.getRowTo()+1, a.getColumnTo()));
		}
		
		if(a.getRowTo()==0 || a.getRowTo()==state.getBoard().length-1 || a.getColumnTo()==0 || a.getColumnTo()==state.getBoard().length-1)
		{
			if(state.getPawn(a.getRowTo(), a.getColumnTo()).equalsPawn("K"))
			{
				state.setTurn(State.Turn.WHITEWIN);
				this.loggGame.fine("Bianco vince con re in "+a.getTo());
			}
		}
		
	
		if(this.movesWithutCapturing>=this.movesDraw && (state.getTurn().equalsTurn("B")||state.getTurn().equalsTurn("W")))
		{
			state.setTurn(State.Turn.DRAW);
			this.loggGame.fine("Stabilito un pareggio per troppe mosse senza mangiare");
		}
		this.movesWithutCapturing++;
		return state;
	}
	
	
	private State checkCaptureBlack(State state, Action a)
	{
		
		if(a.getColumnTo()<state.getBoard().length-2 && (state.getPawn(a.getRowTo(), a.getColumnTo()+1).equalsPawn("W")||state.getPawn(a.getRowTo(), a.getColumnTo()+1).equalsPawn("K")) && (state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("B")||state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("T")))
		{
			
			if(state.getPawn(a.getRowTo(), a.getColumnTo()+1).equalsPawn("K") && state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("T"))
			{
			
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("B") && state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()+1));
				}
			}
			
			if(state.getPawn(a.getRowTo(), a.getColumnTo()+1).equalsPawn("K") && state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("B"))
			{
				
				if(!state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("T") && !state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("T"))
				{
					if(!(a.getRowTo()*2 + 1==9 && state.getBoard().length==9) && !(a.getRowTo()*2 + 1==7 && state.getBoard().length==7))
					{
						state.setTurn(State.Turn.BLACKWIN);
						this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()+1));
					}	
				}						
			
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("B") && state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("T"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()+1));
				}
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("T") && state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()+1));
				}
			}			
			
			if(state.getPawn(a.getRowTo(), a.getColumnTo()+1).equalsPawn("W"))
			{
				state.removePawn(a.getRowTo(), a.getColumnTo()+1);
				this.movesWithutCapturing=-1;
				this.loggGame.fine("Pedina bianca rimossa in: "+state.getBox(a.getRowTo(), a.getColumnTo()+1));
			}
			
		}
		
		if(a.getColumnTo()>1 && (state.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("W")||state.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("K")) && (state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("B")||state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("T")))
		{
			
			if(state.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("K") && state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("T"))
			{
			
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("B") && state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
				}
			}
			
			if(state.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("K") && state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("B"))
			{
				
				if(!state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("T") && !state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("T"))
				{
					if(!(a.getRowTo()*2 + 1==9 && state.getBoard().length==9) && !(a.getRowTo()*2 + 1==7 && state.getBoard().length==7))
					{
						state.setTurn(State.Turn.BLACKWIN);
						this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
					}
				}
				
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("B") && state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("T"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
				}
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("T") && state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
				}
			}
		
			if(state.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("W"))
			{
				state.removePawn(a.getRowTo(), a.getColumnTo()-1);
				this.movesWithutCapturing=-1;
				this.loggGame.fine("Pedina bianca rimossa in: "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
			}
		}
	
		if(a.getRowTo()>1 && (state.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("W")||state.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("K")) && (state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("B")||state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("T")))
		{
			
			if(state.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("K") && state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("T"))
			{
				
				if(state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("B") && state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo()-1, a.getColumnTo()));
				}
			}			
			
			if(state.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("K") && state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("B"))
			{
				
				if(state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("B") && state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("T"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
				}
				if(state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("T") && state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
				}
			
				if(!state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("T") && !state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("T"))
				{
					if(!(a.getRowTo()*2 + 1==9 && state.getBoard().length==9) && !(a.getRowTo()*2 + 1==7 && state.getBoard().length==7))
					{
						state.setTurn(State.Turn.BLACKWIN);
						this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
					}
				}
			}			
			
			if(state.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("W"))
			{
				state.removePawn(a.getRowTo()-1, a.getColumnTo());
				this.movesWithutCapturing=-1;
				this.loggGame.fine("Pedina bianca rimossa in: "+state.getBox(a.getRowTo()-1, a.getColumnTo()));
			}
		}
		
		if(a.getRowTo()<state.getBoard().length-2 && (state.getPawn(a.getRowTo()+1, a.getColumnTo()).equalsPawn("W")||state.getPawn(a.getRowTo()+1, a.getColumnTo()).equalsPawn("K")) && (state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("B")||state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("T")))
		{
			
			if(state.getPawn(a.getRowTo()+1, a.getColumnTo()).equalsPawn("K") && state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("T"))
			{
				
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("B") && state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo()+1, a.getColumnTo()));
				}
			}			
			
			if(state.getPawn(a.getRowTo()+1, a.getColumnTo()).equalsPawn("K") && state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("B"))
			{
				
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("B") && state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("T"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo()+1, a.getColumnTo()));
				}
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("T") && state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo()+1, a.getColumnTo()));
				}
				
				if(!state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("T") && !state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("T"))
				{
					if(!(a.getRowTo()*2 + 1==9 && state.getBoard().length==9) && !(a.getRowTo()*2 + 1==7 && state.getBoard().length==7))
					{
						state.setTurn(State.Turn.BLACKWIN);
						this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo()+1, a.getColumnTo()));
					}
				}
			}		
			
			if(state.getPawn(a.getRowTo()+1, a.getColumnTo()).equalsPawn("W"))
			{
				state.removePawn(a.getRowTo()+1, a.getColumnTo());
				this.movesWithutCapturing=-1;
				this.loggGame.fine("Pedina bianca rimossa in: "+state.getBox(a.getRowTo()+1, a.getColumnTo()));
			}			
		}
		
		if(state.getPawn(4, 4).equalsPawn(State.Pawn.KING.toString()) && state.getBoard().length==9)
		{
			if(state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 3).equalsPawn("B") && state.getPawn(5, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);
				this.loggGame.fine("Nero vince con re catturato sul trono");
			}
		}
		if(state.getPawn(3, 3).equalsPawn(State.Pawn.KING.toString()) && state.getBoard().length==7)
		{
			if(state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 3).equalsPawn("B") && state.getPawn(2, 3).equalsPawn("B") && state.getPawn(3, 2).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);
				this.loggGame.fine("Nero vince con re catturato sul trono");
			}
		}
		
		if(state.getBoard().length==9)
		{
			if(a.getColumnTo()==4 && a.getRowTo()==2)
			{
				if(state.getPawn(3, 4).equalsPawn("W") && state.getPawn(4, 4).equalsPawn("K") && state.getPawn(4, 3).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B") && state.getPawn(5, 4).equalsPawn("B"))
				{
					state.removePawn(3, 4);
					this.movesWithutCapturing=-1;
					this.loggGame.fine("Pedina bianca rimossa in: "+state.getBox(3, 4));
				}
			}
			if(a.getColumnTo()==4 && a.getRowTo()==6)
			{
				if(state.getPawn(5, 4).equalsPawn("W") && state.getPawn(4, 4).equalsPawn("K") && state.getPawn(4, 3).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B") && state.getPawn(3, 4).equalsPawn("B"))
				{
					state.removePawn(5, 4);
					this.movesWithutCapturing=-1;
					this.loggGame.fine("Pedina bianca rimossa in: "+state.getBox(5, 4));
				}
			}
			if(a.getColumnTo()==2 && a.getRowTo()==4)
			{
				if(state.getPawn(4, 3).equalsPawn("W") && state.getPawn(4, 4).equalsPawn("K") && state.getPawn(3, 4).equalsPawn("B") && state.getPawn(5, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B"))
				{
					state.removePawn(4, 3);
					this.movesWithutCapturing=-1;
					this.loggGame.fine("Pedina bianca rimossa in: "+state.getBox(4, 3));
				}
			}
			if(a.getColumnTo()==6 && a.getRowTo()==4)
			{
				if(state.getPawn(4, 5).equalsPawn("W") && state.getPawn(4, 4).equalsPawn("K") && state.getPawn(4, 3).equalsPawn("B") && state.getPawn(5, 4).equalsPawn("B") && state.getPawn(3, 4).equalsPawn("B"))
				{
					state.removePawn(4, 5);
					this.movesWithutCapturing=-1;
					this.loggGame.fine("Pedina bianca rimossa in: "+state.getBox(4, 5));
				}
			}
		}
		
		
		if(this.movesWithutCapturing>=this.movesDraw && (state.getTurn().equalsTurn("B")||state.getTurn().equalsTurn("W")))
		{
			state.setTurn(State.Turn.DRAW);
			this.loggGame.fine("Stabilito un pareggio per troppe mosse senza mangiare");
		}
		this.movesWithutCapturing++;
		return state;
	}
	
	
	
	public File getGameLog() {
		return gameLog;
	}

	public void setGameLog(File gameLog) {
		this.gameLog = gameLog;
	}

	
	
}

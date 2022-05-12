package it.unibo.ai.didattica.competition.tablut.domain;

public class Position {

	public int x;
	public int y;
	
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "Coord [x=" + x + ", y=" + y + "]";
	}

	public int distance(Position c) {
        return Math.abs(this.x - c.x) + Math.abs(this.y - c.y);
    }
}

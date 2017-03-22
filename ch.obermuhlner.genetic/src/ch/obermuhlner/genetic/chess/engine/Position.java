package ch.obermuhlner.genetic.chess.engine;

public class Position {
	public Piece piece;
	public int x;
	public int y;
	public Side side;

	public Position(Piece piece, Side side, int x, int y) {
		this.piece = piece;
		this.x = x;
		this.y = y;
		this.side = side;
	}
	
	public char getCharacter() {
		return piece.getCharacter(side);
	}
	
	public String getPositionString() {
		return Board.toPositionString(x, y);
	}

	@Override
	public String toString() {
		return String.valueOf(getCharacter()) + getPositionString();
	}
}
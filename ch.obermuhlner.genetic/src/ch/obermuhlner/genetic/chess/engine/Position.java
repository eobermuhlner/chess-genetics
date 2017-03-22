package ch.obermuhlner.genetic.chess.engine;

public class Position {
	private final Piece piece;
	private final int x;
	private final int y;
	private final Side side;

	public Position(Piece piece, Side side, int x, int y) {
		this.piece = piece;
		this.x = x;
		this.y = y;
		this.side = side;
	}
	
	public Piece getPiece() {
		return piece;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Side getSide() {
		return side;
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
package ch.obermuhlner.genetic.chess.engine;

public enum Piece {
	
	Pawn('p', 1, 4),
	Knight('n', 3, 8),
	Bishop('b', 3, 13),
	Rook('r', 5, 14),
	Queen('q', 9, 27),
	King('k', 4, 9);
	
	private char character;
	private double value;
	private int maxMoves;

	Piece(char character, double value, int maxMoves) {
		this.character = character;
		this.value = value;
		this.maxMoves = maxMoves;
	}
	
	public char getCharacter(Side side) {
		return side == Side.White ? getWhiteCharacter() : getBlackCharacter();
	}
	
	public char getBlackCharacter() {
		return character;
	}

	public char getWhiteCharacter() {
		return Character.toUpperCase(character);
	}

	public double getValue() {
		return value;
	}

	public int getMaxMoves() {
		return maxMoves;
	}
}

package ch.obermuhlner.genetic.chess.engine;

public enum Piece {
	
	Pawn('p', 1, 4, 2),
	Knight('n', 3, 8, 8),
	Bishop('b', 3, 13, 4),
	Rook('r', 5, 14, 4),
	Queen('q', 9, 27, 8),
	King('k', 4, 8, 8);
	
	private char character;
	private double value;
	private int maxMoves;
	private int maxAttacks;

	Piece(char character, double value, int maxMoves, int maxAttacks) {
		this.character = character;
		this.value = value;
		this.maxMoves = maxMoves;
		this.maxAttacks = maxAttacks;
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
	
	public int getMaxAttacks() {
		return maxAttacks;
	}
}

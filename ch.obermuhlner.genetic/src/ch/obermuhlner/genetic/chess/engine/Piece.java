package ch.obermuhlner.genetic.chess.engine;

public enum Piece {
	
	Pawn('p', 1, 4, 2) {
		public double getValue(Side side, int x, int y) {
			double value = super.getValue(side, x, y);
			value *= 0.9 + getPawnLine(side, y) * 0.2;
			value *= 0.98 + PAWN_VALUE_X[x];
			return value;
		}		
	},
	Knight('n', 3, 8, 8) {
		public double getValue(Side side, int x, int y) {
			double value = super.getValue(side, x, y);
			value *= 0.98 + KNIGHT_VALUE_XY[x];
			value *= 0.98 + KNIGHT_VALUE_XY[x];
			return value;
		}		
	},
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
	
	public double getValue(Side side, int x, int y) {
		return getValue();
	}

	public int getMaxMoves() {
		return maxMoves;
	}
	
	public int getMaxAttacks() {
		return maxAttacks;
	}

	private static int getPawnLine(Side side, int y) {
		switch(side) {
		case White:
			return y;
		case Black:
			return 7 - y;
		}
		throw new IllegalArgumentException("Unknown side: " + side);
	}
	
	private static final double PAWN_VALUE_X[] = { 0.0, 0.01, 0.02, 0.05, 0.05, 0.02, 0.01, 0.0 };
	private static final double KNIGHT_VALUE_XY[] = { 0.0, 0.01, 0.02, 0.05, 0.05, 0.02, 0.01, 0.0 };
}

package ch.obermuhlner.genetic.chess.engine;

public enum Piece {
	
	Pawn('p', 1),
	Knight('n', 3),
	Bishop('b', 3),
	Rook('r', 5),
	Queen('q', 9),
	King('k', 4);
	
	private char character;
	private double value;

	Piece(char character, double value) {
		this.character = character;
		this.value = value;
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
}

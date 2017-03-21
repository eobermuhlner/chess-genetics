package ch.obermuhlner.genetic.chess.engine;

public enum Side {
	White,
	Black;
	
	public Side otherSide() {
		return otherSide(this);
	}

	private static Side otherSide(Side side) {
		if (side == White) {
			return Black;
		} else {
			return White;
		}
	}
}
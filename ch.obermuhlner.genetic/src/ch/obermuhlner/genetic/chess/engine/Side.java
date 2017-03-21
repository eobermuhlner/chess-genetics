package ch.obermuhlner.genetic.chess.engine;

public enum Side {
	White,
	Black;

	public static Side toOtherSide(Side side) {
		if (side == White) {
			return Black;
		} else {
			return White;
		}
	}
}
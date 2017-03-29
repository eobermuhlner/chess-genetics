package ch.obermuhlner.genetic.chess.engine;

public interface LookupTable {

	default String bestMove(Board board) {
		return null;
	}

}

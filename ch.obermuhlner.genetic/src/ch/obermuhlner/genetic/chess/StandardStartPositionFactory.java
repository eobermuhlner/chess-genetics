package ch.obermuhlner.genetic.chess;

import ch.obermuhlner.genetic.GenomeFactory;

public class StandardStartPositionFactory implements GenomeFactory<StartPosition> {

	@Override
	public StartPosition createGenom() {
		StartPosition startPosition = new StartPosition();
		for (int index = 0; index < Board.BLACK_INITIAL_POSITION.length; index++) {
			char figure = Board.BLACK_INITIAL_POSITION[index];
			startPosition.setField(index, figure);
		}
		return startPosition;
	}
}

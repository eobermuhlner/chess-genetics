package ch.obermuhlner.genetic.chess;

import java.util.Random;

import ch.obermuhlner.genetic.GenomeFactory;

public class RandomStartPositionFactory implements GenomeFactory<StartPosition> {

	private final Random random = new Random();
	
	@Override
	public StartPosition createGenom() {
		StartPosition startPosition = new StartPosition();

		for (int index = 0; index < Board.BLACK_INITIAL_POSITION.length; index++) {
			char figure = Board.BLACK_INITIAL_POSITION[index];

			int x;
			int y;
			do {
				x = startPosition.randomX(random);
				y = startPosition.randomY(random, figure);
			} while (startPosition.getField(x, y) != Board.EMPTY);
			startPosition.setField(x, y, figure);
		}
		
		return startPosition;
	}

}

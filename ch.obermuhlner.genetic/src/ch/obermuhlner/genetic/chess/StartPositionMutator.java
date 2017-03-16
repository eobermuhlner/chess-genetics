package ch.obermuhlner.genetic.chess;

import java.util.Random;

import ch.obermuhlner.genetic.GenomeMutator;

public class StartPositionMutator implements GenomeMutator<StartPosition> {

	private final Random random = new Random();

	@Override
	public StartPosition createMutated(StartPosition genome) {
		StartPosition offspring = genome.copy();

		mutate(offspring);

		return offspring;
	}

	private void mutate(StartPosition genome) {
		int xStart;
		int yStart;
		do {
			xStart = genome.randomX(random);
			yStart = genome.randomY(random);
		} while (genome.getField(xStart, yStart) == Board.EMPTY);
		
		char figure = genome.getField(xStart, yStart);

		int xTarget;
		int yTarget;
		do {
			xTarget = genome.randomX(random);
			yTarget = genome.randomY(random, figure);
		} while (genome.getField(xTarget, yTarget) != Board.EMPTY);
		
		genome.setField(xTarget, yTarget, figure);
		genome.setField(xStart, yStart, Board.EMPTY);
	}

}

package ch.obermuhlner.genetic.chess;

import ch.obermuhlner.genetic.GenomeEvaluator;

public class BottomLeftEvaluator implements GenomeEvaluator<StartPosition> {

	@Override
	public double evaluate(StartPosition first, StartPosition second) {

		double firstValue = calculateValue(first);
		double secondValue = calculateValue(second);
		
		return firstValue - secondValue;
	}
	
	private double calculateValue(StartPosition startPosition) {
		double value = 0;
		
		for (int y = 0; y < startPosition.getHeight(); y++) {
			for (int x = 0; x < startPosition.getWidth(); x++) {
				char figure = startPosition.getField(x, y);
				value += Board.value(figure) * x * y;
			}
		}
		
		return value;
	}
}

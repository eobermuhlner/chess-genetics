package ch.obermuhlner.genetic.chess;

import ch.obermuhlner.genetic.Genetic;

public class StartPositionAnalyzer {


	public static void main(String[] args) {
		Genetic<StartPosition> genetic = new Genetic<StartPosition>(new StartPositionFactory(), new StartPositionEvaluator(), new StartPositionMutator());
		
		genetic.run();
	}
}

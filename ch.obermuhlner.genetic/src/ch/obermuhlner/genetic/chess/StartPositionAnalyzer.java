package ch.obermuhlner.genetic.chess;

import ch.obermuhlner.genetic.Genetic;
import ch.obermuhlner.genetic.GenomeFactory;
import ch.obermuhlner.genetic.GenomeMutator;

public class StartPositionAnalyzer {


	public static void main(String[] args) {
		GenomeFactory<StartPosition> factory = new StartPositionFactory();
		//GenomeEvaluator<StartPosition> evaluator = new BottomLeftEvaluator();
		StockfishEvaluator evaluator = new StockfishEvaluator();
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

		evaluator.start();
		
		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		genetic.run();
		
		evaluator.stop();
	}
}

package ch.obermuhlner.genetic.chess;

import ch.obermuhlner.genetic.Genetic;
import ch.obermuhlner.genetic.GenomeEvaluator;
import ch.obermuhlner.genetic.GenomeFactory;
import ch.obermuhlner.genetic.GenomeMutator;

public class StartPositionAnalyzer {


	public static void main(String[] args) {
		GenomeFactory<StartPosition> factory = new StartPositionFactory();
		//GenomeEvaluator<StartPosition> evaluator = new BottomLeftEvaluator();
		GenomeEvaluator<StartPosition> evaluator = new StockfishEvaluator();
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();
		
		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		
		genetic.run();
	}
}

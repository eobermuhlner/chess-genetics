package ch.obermuhlner.genetic.chess;

import ch.obermuhlner.genetic.Genetic;
import ch.obermuhlner.genetic.GenomeEvaluator;
import ch.obermuhlner.genetic.GenomeFactory;
import ch.obermuhlner.genetic.GenomeMutator;

public class StartPositionAnalyzer {


	public static void main(String[] args) {
		GenomeFactory<StartPosition> factory = new StartPositionFactory();
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

//		GenomeEvaluator<StartPosition> evaluator = new BottomLeftEvaluator();
//		GenomeEvaluator<StartPosition> evaluator = new StockfishSimpleEvaluator();
		GenomeEvaluator<StartPosition> evaluator = new StockfishPlayEvaluator();
//		GenomeEvaluator<StartPosition> evaluator = new CompositeEvaluator<>(Arrays.asList(new StockfishSimpleEvaluator(), new StockfishPlayEvaluator()));

		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		genetic.run();
	}
}

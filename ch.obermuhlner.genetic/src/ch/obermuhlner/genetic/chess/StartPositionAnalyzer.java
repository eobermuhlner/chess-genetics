package ch.obermuhlner.genetic.chess;

import ch.obermuhlner.genetic.Genetic;
import ch.obermuhlner.genetic.GenomeEvaluator;
import ch.obermuhlner.genetic.GenomeFactory;
import ch.obermuhlner.genetic.GenomeMutator;
import ch.obermuhlner.genetic.util.AverageGenomeEvaluator;

public class StartPositionAnalyzer {


	public static void main(String[] args) {
//		GenomeFactory<StartPosition> factory = new StandardStartPositionFactory();
		GenomeFactory<StartPosition> factory = new RandomStartPositionFactory();
		
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

//		GenomeEvaluator<StartPosition> evaluator = new BottomLeftEvaluator();
//		GenomeEvaluator<StartPosition> evaluator = new StockfishSimpleEvaluator();
//		GenomeEvaluator<StartPosition> evaluator = new StockfishPlayEvaluator();
		GenomeEvaluator<StartPosition> evaluator = new AverageGenomeEvaluator<>(new StockfishPlayEvaluator(), 10);
//		GenomeEvaluator<StartPosition> evaluator = new CompositeGenomeEvaluator<>(Arrays.asList(new StockfishSimpleEvaluator(), new StockfishPlayEvaluator()));

		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		genetic.setPopulationCount(10);
		genetic.setEvaluationCount(3);
		genetic.run();
	}
}

package ch.obermuhlner.genetic.chess;

import ch.obermuhlner.genetic.Genetic;
import ch.obermuhlner.genetic.GenomeEvaluator;
import ch.obermuhlner.genetic.GenomeFactory;
import ch.obermuhlner.genetic.GenomeMutator;
import ch.obermuhlner.genetic.util.AverageGenomeEvaluator;

public class StartPositionAnalyzer {


	public static void main(String[] args) {
		//analyzeBottomRight();
		analyzeStockfishEval();
		//analyzeStockfishPlayFastest();
		//analyzeStockfishPlayFast();
		//analyzeStockfishPlaySlow();
		//analyzeStockfishPlaySlowest();
		//analyzeStockfishPlayUltraSlow();
		//analyzeStockfishPlayUltraSlowest();
	}

	public static void analyzeBottomRight() {
		GenomeFactory<StartPosition> factory = new RandomStartPositionFactory();
		
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

		GenomeEvaluator<StartPosition> evaluator = new BottomRightEvaluator();

		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		genetic.setPopulationCount(1000);
		genetic.setEvaluationCount(10);
		genetic.run();
	}

	public static void analyzeStockfishEval() {
		GenomeFactory<StartPosition> factory = new RandomStartPositionFactory();
		
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

		GenomeEvaluator<StartPosition> evaluator = new StockfishSimpleEvaluator();

		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		genetic.setPopulationCount(100);
		genetic.setEvaluationCount(10);
		genetic.run();
	}

	public static void analyzeStockfishPlayFastest() {
		GenomeFactory<StartPosition> factory = new StandardStartPositionFactory();
//		GenomeFactory<StartPosition> factory = new RandomStartPositionFactory();
		
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

		GenomeEvaluator<StartPosition> evaluator = new StockfishPlayEvaluator(5, 1);

		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		genetic.setPopulationCount(10);
		genetic.setEvaluationCount(1);
		genetic.run();
	}

	public static void analyzeStockfishPlayFast() {
//		GenomeFactory<StartPosition> factory = new StandardStartPositionFactory();
		GenomeFactory<StartPosition> factory = new RandomStartPositionFactory();
		
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

		GenomeEvaluator<StartPosition> evaluator = new StockfishPlayEvaluator(10, 10);

		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		genetic.setPopulationCount(10);
		genetic.setEvaluationCount(3);
		genetic.run();
	}

	public static void analyzeStockfishPlaySlow() {
//		GenomeFactory<StartPosition> factory = new StandardStartPositionFactory();
		GenomeFactory<StartPosition> factory = new RandomStartPositionFactory();
		
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

		GenomeEvaluator<StartPosition> evaluator = new AverageGenomeEvaluator<>(new StockfishPlayEvaluator(20, 10), 3);

		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		genetic.setPopulationCount(10);
		genetic.setEvaluationCount(3);
		genetic.run();
	}

	public static void analyzeStockfishPlaySlowest() {
//		GenomeFactory<StartPosition> factory = new StandardStartPositionFactory();
		GenomeFactory<StartPosition> factory = new RandomStartPositionFactory();
		
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

		GenomeEvaluator<StartPosition> evaluator = new AverageGenomeEvaluator<>(new StockfishPlayEvaluator(1000, 10), 10);

		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		genetic.setPopulationCount(10);
		genetic.setEvaluationCount(3);
		genetic.run();
	}

	public static void analyzeStockfishPlayUltraSlow() {
//		GenomeFactory<StartPosition> factory = new StandardStartPositionFactory();
		GenomeFactory<StartPosition> factory = new RandomStartPositionFactory();
		
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

		GenomeEvaluator<StartPosition> evaluator = new AverageGenomeEvaluator<>(new StockfishPlayEvaluator(1000, 10), 10);

		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		genetic.setPopulationCount(100);
		genetic.setEvaluationCount(10);
		genetic.run();
	}

	public static void analyzeStockfishPlayUltraSlowest() {
//		GenomeFactory<StartPosition> factory = new StandardStartPositionFactory();
		GenomeFactory<StartPosition> factory = new RandomStartPositionFactory();
		
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

		GenomeEvaluator<StartPosition> evaluator = new AverageGenomeEvaluator<>(new StockfishPlayEvaluator(1000, 20), 10);

		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		genetic.setPopulationCount(100);
		genetic.setEvaluationCount(10);
		genetic.run();
	}
}

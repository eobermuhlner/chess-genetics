package ch.obermuhlner.genetic.chess;

import ch.obermuhlner.genetic.Genetic;
import ch.obermuhlner.genetic.GenomeEvaluator;
import ch.obermuhlner.genetic.GenomeFactory;
import ch.obermuhlner.genetic.GenomeMutator;
import ch.obermuhlner.genetic.util.AverageGenomeEvaluator;

// http://webchess.freehostia.com/diag/chessdiag.php?fen=rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR%20w&size=medium&coord=no&cap=no&stm=no&fb=no&theme=smart&format=auto&color1=f5d08c&color2=bf704b&color3=000000
public class StartPositionAnalyzer {

	
	private static final StartPosition KNOWN_START_POSITIONS[] = {
			new StartPosition("6k1/rr1q2p1/2bnnpbp/2ppppp1"),
			
	};

	public static void main(String[] args) {
		//analyzeFrontLeft();
		//analyzeStockfishEvalFast();
		//analyzeStockfishEvalSlow();
		//analyzeStockfishPlayFastest();
		//analyzeStockfishPlayFast();
		//analyzeStockfishPlaySlow();
		//analyzeStockfishPlaySlowest();
		//analyzeStockfishPlayUltraSlow();
		//analyzeStockfishPlayUltraSlowest();
		
		//analyzeEvalMonteCarloChessEngine();
		analyzePlayMonteCarloChessEngine();
	}

	public static void analyzeFrontLeft() {
		GenomeFactory<StartPosition> factory = new RandomStartPositionFactory();
		
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

		GenomeEvaluator<StartPosition> evaluator = new FrontLeftEvaluator();

		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		injectStartPositions(genetic, KNOWN_START_POSITIONS);

		genetic.setPopulationCount(1000);
		genetic.setEvaluationCount(10);
		genetic.run();
	}

	public static void analyzeStockfishEvalFast() {
		GenomeFactory<StartPosition> factory = new RandomStartPositionFactory();
		
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

		GenomeEvaluator<StartPosition> evaluator = new StockfishPlayEvaluator(0, 0);

		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		injectStartPositions(genetic, KNOWN_START_POSITIONS);

		genetic.setPopulationCount(100);
		genetic.setEvaluationCount(10);
		genetic.run();
	}

	public static void analyzeStockfishEvalSlow() {
		GenomeFactory<StartPosition> factory = new RandomStartPositionFactory();
		
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

		GenomeEvaluator<StartPosition> evaluator = new StockfishPlayEvaluator(0, 0);

		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		injectStartPositions(genetic, KNOWN_START_POSITIONS);
		
		genetic.setPopulationCount(1000);
		genetic.setEvaluationCount(100);
		genetic.run();
	}

	/**
	 * About 1 to 10 seconds per step.
	 */
	public static void analyzeStockfishPlayFastest() {
		GenomeFactory<StartPosition> factory = new StandardStartPositionFactory();
//		GenomeFactory<StartPosition> factory = new RandomStartPositionFactory();
		
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

		GenomeEvaluator<StartPosition> evaluator = new StockfishPlayEvaluator(5, 1);

		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		injectStartPositions(genetic, KNOWN_START_POSITIONS);

		genetic.setPopulationCount(10);
		genetic.setEvaluationCount(1);
		genetic.run();
	}

	public static void analyzeStockfishPlayFast() {
		GenomeFactory<StartPosition> factory = new StandardStartPositionFactory();
//		GenomeFactory<StartPosition> factory = new RandomStartPositionFactory();
		
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

		GenomeEvaluator<StartPosition> evaluator = new StockfishPlayEvaluator(10, 10);

		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		injectStartPositions(genetic, KNOWN_START_POSITIONS);

		genetic.setPopulationCount(10);
		genetic.setEvaluationCount(3);
		genetic.run();
	}

	public static void analyzeStockfishPlaySlow() {
		GenomeFactory<StartPosition> factory = new StandardStartPositionFactory();
//		GenomeFactory<StartPosition> factory = new RandomStartPositionFactory();
		
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

		GenomeEvaluator<StartPosition> evaluator = new AverageGenomeEvaluator<>(new StockfishPlayEvaluator(20, 10), 3);

		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		injectStartPositions(genetic, KNOWN_START_POSITIONS);

		genetic.setPopulationCount(10);
		genetic.setEvaluationCount(3);
		genetic.run();
	}

	/**
	 * About  seconds per step.
	 */
	public static void analyzeStockfishPlaySlowest() {
//		GenomeFactory<StartPosition> factory = new StandardStartPositionFactory();
		GenomeFactory<StartPosition> factory = new RandomStartPositionFactory();
		
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

		GenomeEvaluator<StartPosition> evaluator = new AverageGenomeEvaluator<>(new StockfishPlayEvaluator(1000, 10), 10);

		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		injectStartPositions(genetic, KNOWN_START_POSITIONS);

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
		injectStartPositions(genetic, KNOWN_START_POSITIONS);

		genetic.setPopulationCount(100);
		genetic.setEvaluationCount(10);
		genetic.run();
	}

	public static void analyzeStockfishPlayUltraSlowest() {
		GenomeFactory<StartPosition> factory = new StandardStartPositionFactory();
//		GenomeFactory<StartPosition> factory = new RandomStartPositionFactory();
		
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

		GenomeEvaluator<StartPosition> evaluator = new AverageGenomeEvaluator<>(new StockfishPlayEvaluator(1000, 20), 10);

		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		injectStartPositions(genetic, KNOWN_START_POSITIONS);

		genetic.setPopulationCount(100);
		genetic.setEvaluationCount(10);
		genetic.run();
	}

	public static void analyzeEvalMonteCarloChessEngine() {
		GenomeFactory<StartPosition> factory = new RandomStartPositionFactory();
		
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

		GenomeEvaluator<StartPosition> evaluator = new MonteCarloChessEngineEvaluator(1, 0, false);

		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		//injectStartPositions(genetic, KNOWN_START_POSITIONS);

		genetic.setPopulationCount(100);
		genetic.setEvaluationCount(10);
		genetic.run();
	}

	public static void analyzePlayMonteCarloChessEngine() {
		GenomeFactory<StartPosition> factory = new RandomStartPositionFactory();
		
		GenomeMutator<StartPosition> mutator = new StartPositionMutator();

		GenomeEvaluator<StartPosition> evaluator = new MonteCarloChessEngineEvaluator(10, 50, true);

		Genetic<StartPosition> genetic = new Genetic<StartPosition>(factory, evaluator, mutator);
		//injectStartPositions(genetic, KNOWN_START_POSITIONS);

		genetic.setPopulationCount(10);
		genetic.setEvaluationCount(1);
		genetic.run();
	}

	private static void injectStartPositions(Genetic<StartPosition> genetic, StartPosition[] startPositions) {
		for (StartPosition startPosition : startPositions) {
			genetic.addGenome(startPosition);
		}
	}
}

package ch.obermuhlner.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Genetic<T> {

	private final GenomeFactory<T> factory;
	private final GenomeEvaluator<T> evaluator;
	private final GenomeMutator<T> mutator;

	private final Random random = new Random();
	
	private final List<T> population = new ArrayList<>();
	
	private double killFactor = 0.2;
	private int populationCount = 10000;
	private int stepCount = 10000;
	private int evaluationCount = 10;
	private int printCount = 3;
	
	public Genetic(GenomeFactory<T> factory, GenomeEvaluator<T> evaluator, GenomeMutator<T> mutator) {
		this.factory = factory;
		this.evaluator = evaluator;
		this.mutator = mutator;
	}
	
	public void run() {
		if (population.isEmpty()) {
			for (int i = 0; i < populationCount; i++) {
				population.add(factory.createGenom());
			}
		}
		
		for (int i = 0; i < stepCount; i++) {
			runStep();
		}

		System.out.println();
		System.out.println("==========================================================");
		for (int i = 0; i < populationCount; i++) {
			System.out.println(population.get(i));
		}
	}

	private void runStep() {
		double[] accumulatedEvaluation = evaluatePopulation();

		sortPopulation(accumulatedEvaluation);
		
		cullPopulation();
		
		System.out.println();
		for (int i = 0; i < printCount; i++) {
			System.out.println(population.get(i));
		}

	}

	private double[] evaluatePopulation() {
		double[] accumulatedEvaluation = new double[populationCount];
		
		for (int genomIndex1 = 0; genomIndex1 < accumulatedEvaluation.length; genomIndex1++) {
			for (int i = 0; i < evaluationCount; i++) {
				int genomIndex2 = genomIndex1;
				while (genomIndex2 == genomIndex1) {
					genomIndex2 = random.nextInt(populationCount);
				}
				
				T genom1 = population.get(genomIndex1);
				T genom2 = population.get(genomIndex2);
				
				double evaluation = evaluator.evaluate(genom1, genom2);
				accumulatedEvaluation[genomIndex1] += evaluation;
				accumulatedEvaluation[genomIndex2] -= evaluation;
			}
		}
		return accumulatedEvaluation;
	}

	private void sortPopulation(double[] accumulatedEvaluation) {
		List<EvaluatedGenome<T>> evaluatedGenomes = new ArrayList<>();
		
		for (int i = 0; i < accumulatedEvaluation.length; i++) {
			evaluatedGenomes.add(new EvaluatedGenome(population.get(i), accumulatedEvaluation[i]));
		}
		
		evaluatedGenomes.sort(null);
		
		population.clear();
		for (EvaluatedGenome<T> evaluatedGenome : evaluatedGenomes) {
			population.add(evaluatedGenome.genome);
		}
	}
	
	private static class EvaluatedGenome<T> implements Comparable<EvaluatedGenome<T>> {
		public final T genome;
		public final double value;
		
		public EvaluatedGenome(T genome, double value) {
			this.genome = genome;
			this.value = value;
		}

		@Override
		public int compareTo(EvaluatedGenome<T> other) {
			return Double.compare(value, other.value);
		}
	}

	private void cullPopulation() {
		int killCount = (int) (populationCount * killFactor);
		
		for (int i = 0; i < killCount; i++) {
			population.remove(population.size() - 1);
		}
		
		for (int i = 0; i < killCount; i++) {
			T genome = population.get(i);
			T offspring = mutator.createMutated(genome);
			population.add(offspring);
		}		
	}

}

package ch.obermuhlner.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Genetic<T> {

	private final GenomeFactory<T> factory;
	private final GenomeEvaluator<T> evaluator;
	private final GenomeMutator<T> mutator;

	private final Random random = new Random();
	
	private final List<EvaluatedGenome<T>> population = new ArrayList<>();
	
	private double growBestFactor = 0.1;
	private double growRandomFactor = 0.3;
	private int populationCount = 1000;
	private int stepCount = 100000;
	private int evaluationCount = 100;
	private int printCount = 3;
	
	public Genetic(GenomeFactory<T> factory, GenomeEvaluator<T> evaluator, GenomeMutator<T> mutator) {
		this.factory = factory;
		this.evaluator = evaluator;
		this.mutator = mutator;
	}
	
	public void run() {
		if (population.isEmpty()) {
			for (int i = 0; i < populationCount; i++) {
				addGenome(factory.createGenom());
			}
		}
		
		for (int i = 0; i < stepCount; i++) {
			runStep();
		}

		printPopulation(population.size());
	}

	private void runStep() {
		evaluatePopulation();

		sortPopulation();
		printPopulation(printCount);
		
		cullPopulation();
	}

	private void printPopulation(int count) {
		System.out.println();
		for (int i = 0; i < Math.min(count, population.size()); i++) {
			System.out.println(population.get(i));
		}
	}
	
	private void addGenome(T genome) {
		population.add(new EvaluatedGenome<T>(genome));
	}
	
	private void evaluatePopulation() {
		for (int genomIndex1 = 0; genomIndex1 < population.size(); genomIndex1++) {
			for (int i = 0; i < evaluationCount; i++) {
				int genomIndex2 = genomIndex1;
				while (genomIndex2 == genomIndex1) {
					genomIndex2 = random.nextInt(populationCount);
				}
				
				EvaluatedGenome<T> genom1 = population.get(genomIndex1);
				EvaluatedGenome<T> genom2 = population.get(genomIndex2);
				
				double evaluation = evaluator.evaluate(genom1.genome, genom2.genome);
				
				genom1.value += evaluation;
				genom1.count++;
				
				genom2.value -= evaluation;
				genom2.count++;
			}
		}
	}

	private void sortPopulation() {
		population.sort(null);
	}
	
	private static class EvaluatedGenome<T> implements Comparable<EvaluatedGenome<T>> {
		public final T genome;
		public double value;
		public int count;
		
		public EvaluatedGenome(T genome) {
			this.genome = genome;
		}
		
		@Override
		public int compareTo(EvaluatedGenome<T> other) {
			return -Double.compare(getAverageValue(), other.getAverageValue());
		}
		
		public double getAverageValue() {
			if (count == 0) {
				return 0.0;
			}
			return value / count;
		}
		
		@Override
		public String toString() {
			return genome + " " + getAverageValue();
		}
	}

	private void cullPopulation() {
		int growBestCount = (int) (populationCount * growBestFactor);
		int growRandomCount = (int) (populationCount * growRandomFactor);
		int killCount = growBestCount + growRandomCount;
		
		for (int i = 0; i < killCount; i++) {
			population.remove(population.size() - 1);
		}
		
		for (int i = 0; i < growRandomCount; i++) {
			T genome = population.get(random.nextInt(population.size())).genome;
			population.add(new EvaluatedGenome<T>(mutator.createMutated(genome)));
		}		

		for (int i = 0; i < growBestCount; i++) {
			T genome = population.get(i).genome;
			population.add(new EvaluatedGenome<T>(mutator.createMutated(genome)));
		}		
	}

}

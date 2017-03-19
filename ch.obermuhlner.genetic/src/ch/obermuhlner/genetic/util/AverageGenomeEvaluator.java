package ch.obermuhlner.genetic.util;

import ch.obermuhlner.genetic.GenomeEvaluator;

public class AverageGenomeEvaluator<T> implements GenomeEvaluator<T> {

	private GenomeEvaluator<T> decorated;
	private int count;

	public AverageGenomeEvaluator(GenomeEvaluator<T> decorated, int count) {
		this.decorated = decorated;
		this.count = count;
	}
	
	@Override
	public double evaluate(T first, T second) {
		double total = 0;
		
		for (int i = 0; i < count; i++) {
			total += decorated.evaluate(first, second);
		}
		
		return total / count;
	}
}

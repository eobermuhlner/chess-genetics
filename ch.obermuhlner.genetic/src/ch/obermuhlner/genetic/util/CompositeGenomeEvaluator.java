package ch.obermuhlner.genetic.util;

import java.util.List;

import ch.obermuhlner.genetic.GenomeEvaluator;

public class CompositeGenomeEvaluator<T> implements GenomeEvaluator<T> {

	private List<GenomeEvaluator<T>> evaluators;

	public CompositeGenomeEvaluator(List<GenomeEvaluator<T>> evaluators) {
		this.evaluators = evaluators;
	}
	
	@Override
	public double evaluate(T first, T second) {
		double totalValue = 0;
		
		for (GenomeEvaluator<T> genomeEvaluator : evaluators) {
			double value = genomeEvaluator.evaluate(first, second);
			totalValue += value;
		}
		
		return totalValue / evaluators.size();
	}

}

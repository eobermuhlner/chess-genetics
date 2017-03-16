package ch.obermuhlner.genetic;

public interface GenomeEvaluator<T> {
	double evaluate(T first, T second);
}

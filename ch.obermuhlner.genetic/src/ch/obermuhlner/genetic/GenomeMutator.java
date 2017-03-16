package ch.obermuhlner.genetic;

public interface GenomeMutator<T> {
	T createMutated(T genome);
}

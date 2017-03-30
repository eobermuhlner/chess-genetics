package ch.obermuhlner.genetic.chess.engine;

import ch.obermuhlner.genetic.chess.engine.MonteCarloChessEngine.EntityWithValue;

public class EntityValueTuple<E> implements EntityWithValue<E> {
	private final E entity;
	final double value;
	
	public EntityValueTuple(E entity, double value) {
		this.entity = entity;
		this.value = value;
	}
	
	@Override
	public E getEntity() {
		return entity;
	}
	
	@Override
	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.format("%s(%6.4f)", entity, value);
	}
}
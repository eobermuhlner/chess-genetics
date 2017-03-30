package ch.obermuhlner.genetic.chess.engine;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import ch.obermuhlner.genetic.chess.engine.MonteCarloChessEngine.EntityWithValue;

public class RandomUtil {

	public static <E> E pickRandom(Random random, Collection<? extends EntityWithValue<E>> allEntitiesWithValue) {
		if (allEntitiesWithValue.isEmpty()) {
			return null;
		}
		
		double total = 0;
		double min = 0;
		for (EntityWithValue<E> entityWithValue : allEntitiesWithValue) {
			double value = entityWithValue.getValue();
			total += value;
			min = Math.min(min, value);
		}

		double offset = -min;
		total += offset * allEntitiesWithValue.size();

		double r = random.nextDouble() * total;
		
		total = 0;
		for (EntityWithValue<E> entityWithValue : allEntitiesWithValue) {
			total += entityWithValue.getValue() + offset;
			if (r <= total) {
				return entityWithValue.getEntity();
			}
		}

		// should not happen, but just to be save in case of rounding errors
		return allEntitiesWithValue.iterator().next().getEntity();
	}


}

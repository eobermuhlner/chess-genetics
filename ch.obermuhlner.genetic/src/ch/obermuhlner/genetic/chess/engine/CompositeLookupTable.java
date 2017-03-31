package ch.obermuhlner.genetic.chess.engine;

import java.util.Arrays;
import java.util.List;

public class CompositeLookupTable implements LookupTable {

	private final List<LookupTable> lookupTables;

	public CompositeLookupTable(LookupTable... lookupTables) {
		this(Arrays.asList(lookupTables));
	}

	public CompositeLookupTable(List<LookupTable> lookupTables) {
		this.lookupTables = lookupTables;
	}

	@Override
	public String bestMove(Board board) {
		for (LookupTable lookupTable : lookupTables) {
			String bestMove = lookupTable.bestMove(board);
			if (bestMove != null) {
				return bestMove;
			}
		}
		
		return null;
	}
}

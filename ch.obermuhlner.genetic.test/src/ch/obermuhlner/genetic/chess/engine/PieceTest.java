package ch.obermuhlner.genetic.chess.engine;

import org.junit.Test;

public class PieceTest {

	@Test
	public void testKnightValue() {
		System.out.println(Piece.Knight.getValue(Side.White, 0, 1));
		System.out.println(Piece.Knight.getValue(Side.White, 1, 3));
	}
}

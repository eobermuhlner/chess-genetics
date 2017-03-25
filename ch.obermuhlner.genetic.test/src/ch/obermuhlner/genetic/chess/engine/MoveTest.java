package ch.obermuhlner.genetic.chess.engine;

import org.junit.Test;

public class MoveTest {

	@Test
	public void testValue() {
		Move move = new Move(new Position(Piece.Knight, Side.White, 1, 3), 0, 1, null);
		System.out.println(move);
	}
}

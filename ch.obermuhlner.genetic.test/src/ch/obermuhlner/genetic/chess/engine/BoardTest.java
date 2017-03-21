package ch.obermuhlner.genetic.chess.engine;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import ch.obermuhlner.genetic.chess.engine.Board.Move;

public class BoardTest {

	@Test
	public void testGetFenString() {
		Board board = new Board();
		assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", board.toFenString());
	}

	@Test
	public void testSetFenString() {
		Board board = new Board();
		board.setFenString("6k1/rr1q2p1/2bnnpbp/2ppppp1/8/8/PPPPPPPP/RNBQKBNR");
		assertEquals("6k1/rr1q2p1/2bnnpbp/2ppppp1/8/8/PPPPPPPP/RNBQKBNR", board.toFenString());
	}
	
	@Test
	public void testPawnMoves1() {
		assertMoves(newBoard("Pe2").getAllMoves(), "e3", "e4");
	}

	@Test
	public void testPawnMoves2() {
		assertMoves(newBoard("Pe3").getAllMoves(), "e4");
	}

	@Test
	public void testPawnMoves3() {
		assertMoves(newBoard("Pe3", "pd4", "pf4").getAllMoves(), "e4", "d4", "f4");
	}

	private Board newBoard(String... positions) {
		Board board = new Board();
		board.clear();
		
		for (String position : positions) {
			board.addPosition(position);
		}
		
		return board;
	}
	
	private void assertMoves(List<Move> actualMoves, String... expectedTargetPositions) {
		List<String> actualTargetPositions = actualMoves.stream().map(move -> move.getTargetPositionString()).collect(Collectors.toList());
		List<String> remainingTargetPositions = new ArrayList<>(actualTargetPositions);
		
		for(String expectedTargetPosition : expectedTargetPositions) {
			boolean found = remainingTargetPositions.remove(expectedTargetPosition);
			assertTrue("expected " + expectedTargetPosition + " not found in " + actualTargetPositions, found);
		}
		assertTrue("unexpected positions: " + remainingTargetPositions, remainingTargetPositions.isEmpty());
	}
}
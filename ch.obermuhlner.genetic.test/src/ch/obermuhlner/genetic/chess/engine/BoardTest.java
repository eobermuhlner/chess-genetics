package ch.obermuhlner.genetic.chess.engine;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class BoardTest {

	@Test
	public void testGetFenString() {
		Board board = new Board();
		assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w", board.toFenString());
	}

	@Test
	public void testSetFenString() {
		Board board = new Board();

		board.setFenString("6k1/rr1q2p1/2bnnpbp/2ppppp1/8/8/PPPPPPPP/RNBQKBNR");
		assertEquals("6k1/rr1q2p1/2bnnpbp/2ppppp1/8/8/PPPPPPPP/RNBQKBNR w", board.toFenString());
		
		board.setFenString("6k1/rr1q2p1/2bnnpbp/2ppppp1/8/8/PPPPPPPP/RNBQKBNR w");
		assertEquals("6k1/rr1q2p1/2bnnpbp/2ppppp1/8/8/PPPPPPPP/RNBQKBNR w", board.toFenString());

		board.setFenString("6k1/rr1q2p1/2bnnpbp/2ppppp1/8/8/PPPPPPPP/RNBQKBNR b");
		assertEquals("6k1/rr1q2p1/2bnnpbp/2ppppp1/8/8/PPPPPPPP/RNBQKBNR b", board.toFenString());
	}
	
	@Test
	public void testWhitePawnMoves1() {
		assertMoves(whiteToMove("Pe2"), "e3", "e4"); // double move
		assertMoves(whiteToMove("Pe3"), "e4"); // single move
		assertMoves(whiteToMove("Pe3", "pd4", "pf4"), "e4", "d4", "f4"); // kill left and right
		assertMoves(whiteToMove("Pa3", "pb4"), "a4", "b4"); // left side of board, kill right
		assertMoves(whiteToMove("Ph3", "pg4"), "h4", "g4"); // right side of board, kill left
		assertMoves(whiteToMove("Pe3", "pe4")); // blocked pawn
	}

	@Test
	public void testWhitePawnMoves2() {
		assertMoves(whiteToMove("Pe7"), "e8", "e8", "e8", "e8"); // four conversions: NBRQ
		assertMoves(whiteToMove("Pe7", "pd8"), "e8", "e8", "e8", "e8", "d8", "d8", "d8", "d8"); // four conversions: NBRQ
	}
	
	@Test
	public void testBlackPawnMoves1() {
		assertMoves(blackToMove("pe7"), "e6", "e5"); // double move
		assertMoves(blackToMove("pe6"), "e5"); // single move
		assertMoves(blackToMove("pe6", "Pd5", "Pf5"), "e5", "d5", "f5"); // kill left and right
		assertMoves(blackToMove("pa6", "Pb5"), "a5", "b5"); // left side of board, kill right
		assertMoves(blackToMove("ph6", "Pg5"), "h5", "g5"); // right side of board, kill left
		assertMoves(blackToMove("pe6", "Pe5")); // blocked pawn
	}

	@Test
	public void testBlackPawnMoves2() {
		assertMoves(blackToMove("pe2"), "e1", "e1", "e1", "e1"); // four conversions: NBRQ
		assertMoves(blackToMove("pe2", "Pd1"), "e1", "e1", "e1", "e1", "d1", "d1", "d1", "d1"); // four conversions: NBRQ
	}

	@Test
	public void testKnightMoves() {
		assertMoves(whiteToMove("Ne4"), "d6", "f6", "g5", "g3", "f2", "d2", "c3", "c5");
		assertMoves(whiteToMove("Ne4", "Pd6"), "f6", "g5", "g3", "f2", "d2", "c3", "c5", // pawn blocks one move
				"d7"); // pawn can also move
		assertMoves(whiteToMove("Ne4", "pd6"), "d6", "f6", "g5", "g3", "f2", "d2", "c3", "c5"); // enemy pawn just taken
	}

	@Test
	public void testBishopMoves() {
		assertMoves(whiteToMove("Be4"),
				"d3", "c2", "b1",
				"f3", "g2", "h1",
				"d5", "c6", "b7", "a8",
				"f5", "g6", "h7");
		assertMoves(whiteToMove("Be4", "Pc6"),
				"d3", "c2", "b1",
				"f3", "g2", "h1",
				"d5", // this ray blocked by own pawn
				"f5", "g6", "h7"
				, "c7"); // pawn can also move
		assertMoves(whiteToMove("Be4", "pc6"),
				"d3", "c2", "b1",
				"f3", "g2", "h1",
				"d5", "c6", // this ray blocked by enemy pawn
				"f5", "g6", "h7");
	}

	@Test
	public void testRookMoves() {
		assertMoves(whiteToMove("Re4"),
				"f4", "g4", "h4", 
				"e5", "e6", "e7", "e8", 
				"e3", "e2", "e1", 
				"d4", "c4", "b4", "a4");
		assertMoves(whiteToMove("Re4", "Pc4"),
				"f4", "g4", "h4", 
				"e5", "e6", "e7", "e8", 
				"e3", "e2", "e1",
				"d4", // this ray blocked by own pawn
				"c5"); // pawn can also move
		assertMoves(whiteToMove("Re4", "pd4"),
				"f4", "g4", "h4", 
				"e5", "e6", "e7", "e8", 
				"e3", "e2", "e1",
				"d4"); // this ray blocked by enemy pawn
	}
	
	@Test
	public void testQueenMoves() {
		assertMoves(whiteToMove("Qe4"),
				"d3", "c2", "b1",
				"f3", "g2", "h1",
				"d5", "c6", "b7", "a8",
				"f5", "g6", "h7",
				"f4", "g4", "h4", 
				"e5", "e6", "e7", "e8", 
				"e3", "e2", "e1", 
				"d4", "c4", "b4", "a4");
		assertMoves(whiteToMove("Qe4", "Pc6"),
				"d3", "c2", "b1",
				"f3", "g2", "h1",
				"d5", // this ray blocked by own pawn
				"f5", "g6", "h7"
				, "c7", // pawn can also move
				"f4", "g4", "h4", 
				"e5", "e6", "e7", "e8", 
				"e3", "e2", "e1", 
				"d4", "c4", "b4", "a4");
		assertMoves(whiteToMove("Qe4", "Pc4"),
				"d3", "c2", "b1",
				"f3", "g2", "h1",
				"d5", "c6", "b7", "a8",
				"f5", "g6", "h7",
				"f4", "g4", "h4", 
				"e5", "e6", "e7", "e8", 
				"e3", "e2", "e1",
				"d4", // this ray blocked by own pawn
				"c5"); // pawn can also move
		assertMoves(whiteToMove("Qe4", "pc6"),
				"d3", "c2", "b1",
				"f3", "g2", "h1",
				"d5", "c6", // this ray blocked by enemy pawn
				"f5", "g6", "h7",
				"f4", "g4", "h4", 
				"e5", "e6", "e7", "e8", 
				"e3", "e2", "e1", 
				"d4", "c4", "b4", "a4");
		assertMoves(whiteToMove("Qe4", "pd4"),
				"d3", "c2", "b1",
				"f3", "g2", "h1",
				"d5", "c6", "b7", "a8",
				"f5", "g6", "h7",
				"f4", "g4", "h4", 
				"e5", "e6", "e7", "e8", 
				"e3", "e2", "e1",
				"d4"); // this ray blocked by enemy pawn
	}
	
	@Test
	public void testKingMoves() {
		assertMoves(whiteToMove("Ke4"),
				"d5", "e5", "f5", "d4", "f4", "d3", "e3", "f3");
	}
	
	@Test
	public void testKingMovesInCheck() {
		assertMoves(whiteToMove("Ke4", "re8"),
				"d5", "f5", "d4", "f4", "d3", "f3");
	}
	
	@Test
	public void testKingNotInCheck() {
		Board board = newBoard(Side.White, "Ka1", "rb8");
		assertEquals(false, board.isCheck());
		assertEquals(false, board.isMate());
		assertEquals(false, board.isPatt());
		assertMoves(board.getAllMoves(), "a2");
	}	
	
	@Test
	public void testKingInCheck() {
		Board board = newBoard(Side.White, "Ka1", "ra8");
		assertEquals(true, board.isCheck());
		assertEquals(false, board.isMate());
		assertEquals(false, board.isPatt());
		assertMoves(board.getAllMoves(), "b1", "b2");
	}	
	
	@Test
	public void testKingInCheckPawn() {
		Board board = newBoard(Side.White, "Ka1", "pb2");
		assertEquals(true, board.isCheck());
		assertEquals(false, board.isMate());
		assertEquals(false, board.isPatt());
		assertMoves(board.getAllMoves(), "a2", "b1", "b2");
	}	

	@Test
	public void testKingCannotMoveIntoPawnThreat() {
		Board board = newBoard(Side.White, "Ka1", "pa2");
		assertEquals(false, board.isCheck());
		assertEquals(false, board.isMate());
		assertEquals(false, board.isPatt());
		assertMoves(board.getAllMoves(), "a2", "b2");
	}	

	@Test
	public void testKingMate() {
		Board board = newBoard(Side.White, "Ka1", "ra8", "rb8");
		assertEquals(true, board.isCheck());
		assertEquals(true, board.isMate());
		assertEquals(false, board.isPatt());
		assertMoves(board.getAllMoves()); // no moves
	}	
	
	@Test
	public void testKingPatt() {
		Board board = newBoard(Side.White, "Ka1", "rh2", "rb8");
		assertEquals(false, board.isCheck());
		assertEquals(false, board.isMate());
		assertEquals(true, board.isPatt());
		assertMoves(board.getAllMoves()); // no moves
	}	

	@Test
	public void testKingsThreateningEachOther() {
		Board board = newBoard(Side.White, "Ka1", "kc1");
		assertEquals(false, board.isCheck());
		assertEquals(false, board.isMate());
		assertEquals(false, board.isPatt());
		assertMoves(board.getAllMoves(), "a2");
	}	

	private List<Move> whiteToMove(String... positions) {
		Board board = newBoard(Side.White, positions);
		return board.getAllMoves();
	}

	private List<Move> blackToMove(String... positions) {
		Board board = newBoard(Side.Black, positions);
		return board.getAllMoves();
	}

	private Board newBoard(Side sideToMove, String... positions) {
		Board board = new Board();
		board.clear();
		
		for (String position : positions) {
			board.addPosition(position);
		}
		
		board.setSideToMove(sideToMove);
		
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

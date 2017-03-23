package ch.obermuhlner.genetic.chess.engine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import ch.obermuhlner.genetic.chess.engine.MonteCarloChessEngine.MoveValue;

public class ChessEngineDiagram {

	private static final String[] PIECE_NAMES = {
			"black_pawn", "black_knight", "black_bishop", "black_rook", "black_queen", "black_king",
			"white_pawn", "white_knight", "white_bishop", "white_rook", "white_queen", "white_king"
			};
	
	private static final int FIELD_PIXELS = 55;

	private static final int IMAGE_OFFSET = (FIELD_PIXELS - 45) / 2;
	
	private static final MonteCarloChessEngine chessEngine = new MonteCarloChessEngine();

	private static ImageObserver imageObserver = new ImageObserver() {
		@Override
		public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
			return false;
		}
	};

	public static void main(String[] args) {
		String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
		long thinkMilliseconds = 1000;
		String diagramFileName = null;
		
		if (args.length >= 1) {
			fen = args[0];
		}
		if (args.length >= 2) {
			thinkMilliseconds = Long.parseLong(args[1]);
		}
		if (args.length >= 3) {
			diagramFileName = args[2];
		}
		
		Board board = new Board();
		board.setFenString(fen);
		
		List<MoveValue> allMoves = chessEngine.getAllMoves(board, thinkMilliseconds);
		
		for (MoveValue moveValue : allMoves) {
			System.out.printf("%8.5f %s\n", moveValue.value, moveValue.move.toNotationString());
		}

		if (diagramFileName == null) {
			diagramFileName = toDiagramFileName(fen);
		}
		
		createDiagram(diagramFileName, board, allMoves);
	}

	private static String toDiagramFileName(String fen) {
		String convertedFen = fen.replace("/", "_").replace(" ", "_");
		return "diagram_" + convertedFen + ".png";
	}

	private static void createDiagram(String diagramFileName, Board board, List<MoveValue> allMoves) {
		Map<String, Image> pieceImages = new HashMap<>();
		try {
			for(String pieceName : PIECE_NAMES) {
				File pathToFile = Paths.get("resources", pieceName + ".png").toFile();
				Image image = ImageIO.read(pathToFile);
				pieceImages.put(pieceName, image);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Color lightBackgroundColor = new Color(181, 136, 99);
		Color darkBackgroundColor = new Color(240, 217, 181);
		
		BufferedImage image = new BufferedImage(FIELD_PIXELS * 8, FIELD_PIXELS * 8, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				int pixelX = x * FIELD_PIXELS;
				int pixelY = y * FIELD_PIXELS;
				
				boolean whiteBackground = ((pixelX + pixelY) % 2) == 0;
				Color color = whiteBackground ? lightBackgroundColor : darkBackgroundColor;
				graphics.setColor(color);
				graphics.fillRect(pixelX, pixelY, FIELD_PIXELS, FIELD_PIXELS);
				
				Position position = board.getPosition(x, y);
				if (position != null) {
					String pieceName = toPieceName(position);
					Image pieceImage = pieceImages.get(pieceName);
					
					graphics.drawImage(pieceImage, pixelX + IMAGE_OFFSET, pixelY + IMAGE_OFFSET, imageObserver);
				}
			}
		}
		
		for (MoveValue moveValue : allMoves) {
			int sourceX = moveValue.move.getSource().getX();
			int sourceY = moveValue.move.getSource().getY();
			
			int targetX = moveValue.move.getTargetX();
			int targetY = moveValue.move.getTargetY();

			int thickness = valueToThickness(moveValue.value);
			Color color = valueToColor(moveValue.value);
		
			graphics.setColor(color);

			int[] xPoints = {
					toCenterPixels(sourceX) - thickness,
					toCenterPixels(sourceX) + thickness,
					toCenterPixels(targetX)
			};
			int[] yPoints = {
					toCenterPixels(sourceY),
					toCenterPixels(sourceY),
					toCenterPixels(targetY)
			};
			int nPoints = 3;
			graphics.fillPolygon(xPoints, yPoints, nPoints);
		}
		
		try {
			File diagramFile = new File(diagramFileName);
			ImageIO.write(image, "png", diagramFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static int valueToThickness(double value) {
		double thickness = Math.min(5, Math.abs(value) * 40);
		return (int) (thickness + 0.5);
	}

	private static Color valueToColor(double value) {
		return value >= 0 ? Color.GREEN : Color.RED;
	}

	private static int toCenterPixels(int fieldIndex) {
		return fieldIndex * FIELD_PIXELS + FIELD_PIXELS / 2;
	}

	private static String toPieceName(Position position) {
		return position.getSide().toString().toLowerCase() + "_" + position.getPiece().toString().toLowerCase();
	}
}

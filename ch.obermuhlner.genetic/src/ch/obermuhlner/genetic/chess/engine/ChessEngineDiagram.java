package ch.obermuhlner.genetic.chess.engine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import ch.obermuhlner.genetic.chess.engine.MonteCarloChessEngine.EntityWithValue;

public class ChessEngineDiagram {

	private static final int DEFAULT_THINK_MILLISECONDS = 10000;
	private static final int DEFAULT_MOVE_COUNT = 50;

	private static final String[] PIECE_NAMES = {
			"black_pawn", "black_knight", "black_bishop", "black_rook", "black_queen", "black_king",
			"white_pawn", "white_knight", "white_bishop", "white_rook", "white_queen", "white_king"
			};
	
	private static final int FIELD_PIXELS = 55;
	private static final int IMAGE_OFFSET = (FIELD_PIXELS - 45) / 2;
	private static final int CIRCLE_RADIUS_PIXELS = 3;
	private static final int VALUE_OFFSET_PIXELS = 4;
	
	private static final int THICKNESS_FACTOR = 40;

	private static final MonteCarloChessEngine chessEngine = new MonteCarloChessEngine();

	private static final Color LIGHT_BACKGROUND_COLOR = new Color(181, 136, 99);
	private static final Color DARK_BACKGROUND_COLOR = new Color(240, 217, 181);
	private static final Color COLOR_RED = new Color(255, 0, 0, 150);
	private static final Color COLOR_GREEN = new Color(0, 255, 0, 150);

	private static ImageObserver imageObserver = new ImageObserver() {
		@Override
		public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
			return false;
		}
	};

	public static void main(String[] args) {
		String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
		long thinkMilliseconds = DEFAULT_THINK_MILLISECONDS;
		int moveCount = DEFAULT_MOVE_COUNT;
		String diagramFileName = null;
		
		if (args.length > 0) {
			fen = args[0];
		}
		if (args.length > 1) {
			thinkMilliseconds = Long.parseLong(args[1]);
		}
		if (args.length > 2) {
			moveCount = Integer.parseInt(args[2]);
		}
		if (args.length > 3) {
			diagramFileName = args[3];
		}
		
		Board board = new Board();
		board.setFenString(fen);
		
		List<EntityWithValue<Position>> allPositions = chessEngine.getAllPositions(board);
		for (EntityWithValue<Position> positionValue : allPositions) {
			System.out.printf("%3s %8.5f (%8.5f)\n", positionValue.getEntity().toString(), positionValue.getValue(), positionValue.getEntity().getPiece().getValue());
		}
		System.out.println();
		
		List<EntityWithValue<Move>> allMoves = chessEngine.getAllMoves(board, thinkMilliseconds, moveCount);
		for (EntityWithValue<Move> moveValue : allMoves) {
			System.out.printf("%15s %8.5f\n", moveValue.getEntity().toNotationString(), moveValue.getValue());
		}

		createDiagram(diagramFileName, board, allPositions, allMoves);
	}

	public static String toDiagramFileName(Board board) {
		String fen = board.toFenString();
		String convertedFen = fen.replace("/", "_").replace(" ", "_");
		return "diagram_" + board.getMoveNumber() + "_" + convertedFen + ".png";
	}

	public static void createDiagram(String diagramFileName, Board board, List<? extends EntityWithValue<Position>> allPositions, List<? extends EntityWithValue<Move>> allMoves) {
		if (diagramFileName == null) {
			diagramFileName = toDiagramFileName(board);
		}

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
				
		BufferedImage image = new BufferedImage(FIELD_PIXELS * 8, FIELD_PIXELS * 8, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				int pixelX = toFieldPixelX(x);
				int pixelY = toFieldPixelY(y);
				
				boolean whiteBackground = ((pixelX + pixelY) % 2) != 0;
				Color color = whiteBackground ? LIGHT_BACKGROUND_COLOR : DARK_BACKGROUND_COLOR;
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

		if (allPositions != null) {
			for (EntityWithValue<Position> positionValue : allPositions) {
				Position position = positionValue.getEntity();
				
				int pixelX = toFieldPixelX(position.getX());
				int pixelY = toFieldPixelY(position.getY());
				
				graphics.setColor(position.getSide() == Side.White ? Color.YELLOW : Color.BLACK);
				int positionValuePixels = toInt(positionValue.getValue() * 5);
				int pieceValuePixels = toInt(position.getPiece().getValue() * 5);
				graphics.setStroke(new BasicStroke(2));
				graphics.drawLine(pixelX, pixelY + FIELD_PIXELS - VALUE_OFFSET_PIXELS, pixelX + positionValuePixels, pixelY + FIELD_PIXELS - VALUE_OFFSET_PIXELS);
				graphics.setColor(position.getSide() == Side.White ? Color.DARK_GRAY : Color.YELLOW);
				graphics.setStroke(new BasicStroke(1));
				graphics.drawLine(pixelX + pieceValuePixels, pixelY + FIELD_PIXELS - VALUE_OFFSET_PIXELS + 1, pixelX + pieceValuePixels, pixelY + FIELD_PIXELS - VALUE_OFFSET_PIXELS - 1);
			}
		}

		if (allMoves != null) {
			for (EntityWithValue<Move> moveValue : allMoves) {
				int sourceX = moveValue.getEntity().getSource().getX();
				int sourceY = moveValue.getEntity().getSource().getY();
				
				int targetX = moveValue.getEntity().getTargetX();
				int targetY = moveValue.getEntity().getTargetY();
				
				double value = moveValue.getValue();
				
				int thickness = valueToThickness(value);
				Color color = valueToColor(value);
				
				graphics.setColor(color);
				
				int sourceFieldCenterPixelX = toFieldCenterPixelX(sourceX);
				int sourceFieldCenterPixelY = toFieldCenterPixelY(sourceY);
				int targetFieldCenterPixelX = toFieldCenterPixelX(targetX);
				int targetFieldCenterPixelY = toFieldCenterPixelY(targetY);
				double arrowAngle = cartesianToAngle(targetFieldCenterPixelX-sourceFieldCenterPixelX, targetFieldCenterPixelY-sourceFieldCenterPixelY);
				int arrowBaseLeftX = toInt(polarToX(arrowAngle-Math.PI/2, thickness));
				int arrowBaseLeftY = toInt(polarToY(arrowAngle-Math.PI/2, thickness));
				int arrowBaseRightX = toInt(polarToX(arrowAngle+Math.PI/2, thickness));
				int arrowBaseRightY = toInt(polarToY(arrowAngle+Math.PI/2, thickness));
				int[] xPoints = {
						sourceFieldCenterPixelX + arrowBaseLeftX,
						sourceFieldCenterPixelX + arrowBaseRightX,
						targetFieldCenterPixelX
				};
				int[] yPoints = {
						sourceFieldCenterPixelY + arrowBaseLeftY,
						sourceFieldCenterPixelY + arrowBaseRightY,
						targetFieldCenterPixelY
				};
				graphics.fillPolygon(xPoints, yPoints, xPoints.length);
				
				//graphics.drawLine(toFieldCenterPixelX(sourceX), sourceFieldCenterPixelY, toFieldCenterPixelX(targetX), toFieldCenterPixelY(targetY));
				graphics.fillOval(toFieldCenterPixelX(targetX) - CIRCLE_RADIUS_PIXELS, toFieldCenterPixelY(targetY) - CIRCLE_RADIUS_PIXELS, 2*CIRCLE_RADIUS_PIXELS, 2*CIRCLE_RADIUS_PIXELS);
			}
		}
		try {
			File diagramFile = new File(diagramFileName);
			ImageIO.write(image, "png", diagramFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static int toInt(double value) {
		return (int) (value + 0.5);
	}

	private static int valueToThickness(double value) {
		double thickness = Math.min(FIELD_PIXELS / 2, Math.abs(value) * THICKNESS_FACTOR);
		return Math.max(1, toInt(thickness));
	}

	private static Color valueToColor(double value) {
		return value >= 0 ? COLOR_GREEN : COLOR_RED;
	}

	private static int toFieldPixelX(int x) {
		return x * FIELD_PIXELS;
	}

	private static int toFieldPixelY(int y) {
		return (7 - y) * FIELD_PIXELS;
	}

	private static int toFieldCenterPixelX(int x) {
		return x * FIELD_PIXELS + FIELD_PIXELS / 2;
	}

	private static int toFieldCenterPixelY(int y) {
		return (7 - y) * FIELD_PIXELS + FIELD_PIXELS / 2;
	}

	private static String toPieceName(Position position) {
		return position.getSide().toString().toLowerCase() + "_" + position.getPiece().toString().toLowerCase();
	}
	
	private static double cartesianToAngle(double x, double y) {
		return Math.atan2(y, x);
	}
	
	@SuppressWarnings("unused")
	private static double cartesianToRadius(double x, double y) {
		return Math.sqrt(x*x + y*y);
	}
	
	private static double polarToX(double angle, double radius) {
        return radius * Math.cos(angle);
	}

	private static double polarToY(double angle, double radius) {
		return radius * Math.sin(angle);
	}
}

package cn.keke.qqtetris;

import static cn.keke.qqtetris.QQTetris.BoardCoordX;
import static cn.keke.qqtetris.QQTetris.BoardCoordY;
import static cn.keke.qqtetris.QQTetris.BoardHeight;
import static cn.keke.qqtetris.QQTetris.BoardWidth;
import static cn.keke.qqtetris.QQTetris.DEBUG;
import static cn.keke.qqtetris.QQTetris.Future1Height;
import static cn.keke.qqtetris.QQTetris.Future1Width;
import static cn.keke.qqtetris.QQTetris.Future1X;
import static cn.keke.qqtetris.QQTetris.Future1Y;
import static cn.keke.qqtetris.QQTetris.Future2Height;
import static cn.keke.qqtetris.QQTetris.Future2Width;
import static cn.keke.qqtetris.QQTetris.Future2X;
import static cn.keke.qqtetris.QQTetris.Future2Y;
import static cn.keke.qqtetris.QQTetris.MyCoordX;
import static cn.keke.qqtetris.QQTetris.MyCoordY;
import static cn.keke.qqtetris.QQTetris.MyHeight;
import static cn.keke.qqtetris.QQTetris.MyWidth;
import static cn.keke.qqtetris.QQTetris.PieceSize;
import static cn.keke.qqtetris.QQTetris.PiecesHeight;
import static cn.keke.qqtetris.QQTetris.PiecesWidth;
import static cn.keke.qqtetris.QQTetris.QQCoord;
import static cn.keke.qqtetris.QQTetris.QQHeight;
import static cn.keke.qqtetris.QQTetris.QQWidth;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.peer.RobotPeer;
import java.util.ArrayList;

import cn.keke.qqtetris.exceptions.BoardNotReadyException;
import cn.keke.qqtetris.exceptions.MissingTetrisWindowException;
import cn.keke.qqtetris.exceptions.UnknownBlockTypeException;
import cn.keke.qqtetris.exceptions.UnknownBoardStateException;

import sun.awt.ComponentFactory;

public class QQRobot {
    private static final StopWatch     STOPPER            = new StopWatch("robot");
    private static final boolean       IGNORE_INVALID_BOARD_COLOR;
    private static final boolean       IGNORE_INVALID_FUTURE_1;
    private static final boolean       IGNORE_INVALID_FUTURE_2;
    public static final int            ROBOT_DELAY_MILLIS = 15;
    private static final int           DEVICES;
    private static final Rectangle[]   RECTANGLES;
    private static final RobotPeer[]   ROBOTS;
    public static RobotPeer            CURRENT_ROBOT;
    private static Robot               robot;
    private static Rectangle           RECT_SCREEN;
    private static Rectangle           RECT_MY            = new Rectangle(QQTetris.MyCoordX, QQTetris.MyCoordY, QQTetris.MyWidth, QQTetris.MyHeight);
    private static Rectangle           RECT_FUTURE1       = new Rectangle(QQTetris.Future1X, QQTetris.Future1Y, QQTetris.Future1Width, QQTetris.Future1Height);
    private static Rectangle           RECT_FUTURE2       = new Rectangle(QQTetris.Future2X, QQTetris.Future2Y, QQTetris.Future2Width, QQTetris.Future2Height);
    private static Rectangle           RECT_BOARD         = new Rectangle(QQTetris.BoardCoordX, QQTetris.BoardCoordY, QQTetris.BoardWidth, QQTetris.BoardHeight);
    private static boolean[]           BOARD_DATA         = new boolean[PiecesHeight * PiecesWidth];
    public static final KeyboardThread keyboardThread     = new KeyboardThread();
    static {
        try {
            robot = new Robot();
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        RECT_SCREEN = new Rectangle(screenDim.width, screenDim.height);
        GraphicsDevice[] devices = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getScreenDevices();
        DEVICES = devices.length;
        ROBOTS = new RobotPeer[DEVICES];
        RECTANGLES = new Rectangle[DEVICES];
        for (int i = 0; i < DEVICES; i++) {
            try {
                GraphicsDevice graphicsDevice = devices[i];
                ROBOTS[i] = ((ComponentFactory) Toolkit.getDefaultToolkit())
                        .createRobot(new Robot(graphicsDevice), graphicsDevice);
                RECTANGLES[i] = graphicsDevice.getDefaultConfiguration()
                        .getBounds();
                // System.out.println(i + ") bounds: " + RECTANGLES[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (DEBUG) {
            IGNORE_INVALID_BOARD_COLOR = false;
            IGNORE_INVALID_FUTURE_1 = false;
            IGNORE_INVALID_FUTURE_2 = false;
        } else {
            IGNORE_INVALID_BOARD_COLOR = true;
            IGNORE_INVALID_FUTURE_1 = true;
            IGNORE_INVALID_FUTURE_2 = true;
        }

        keyboardThread.start();
    }

    public static void click(int x, int y) throws InterruptedException {
        if (!QQTetris.ANALYZE) {
            Point oldLocation = MouseInfo.getPointerInfo().getLocation();
            CURRENT_ROBOT.mouseMove(RECT_SCREEN.x + x, RECT_SCREEN.y + y);
            CURRENT_ROBOT.mousePress(InputEvent.BUTTON1_MASK);
            Thread.sleep(ROBOT_DELAY_MILLIS);
            CURRENT_ROBOT.mouseRelease(InputEvent.BUTTON1_MASK);
            Thread.sleep(ROBOT_DELAY_MILLIS);
            CURRENT_ROBOT.mouseMove(oldLocation.x, oldLocation.y);
        }
    }

    private final static boolean findSimilar(final int[] a, final int r, final int g, final int b) {
        for (int i : a) {
            if (Math.abs(((i >> 16) & 0x000000FF) - r) < 16 &&
                    Math.abs(((i >> 8) & 0x000000FF) - g) < 16 &&
                    Math.abs(((i) & 0x000000FF) - b) < 18) {
                return true;
            }
        }
        return false;
    }

    public final static boolean isSimilar(final int a, final int key) {
        if (Math.abs(((a >> 16) & 0x000000FF) - ((key >> 16) & 0x000000FF)) <= 8 &&
               Math.abs(((a >> 8) & 0x000000FF) - ((key >> 8) & 0x000000FF)) <= 8 &&
               Math.abs(((a) & 0x000000FF) - ((key) & 0x000000FF)) <= 10) {
            return true;
        }
        return false;
    }

    public static BufferedImage getBoardImage(BufferedImage myImage) {
        return myImage.getSubimage(BoardCoordX, BoardCoordY, BoardWidth, BoardHeight);
    }

    public static BufferedImage getFuture1Image(BufferedImage myImage) {
        return myImage.getSubimage(Future1X, Future1Y, Future1Width, Future1Height);
    }

    public static BufferedImage getFuture2Image(BufferedImage myImage) {
        return myImage.getSubimage(Future2X, Future2Y, Future2Width, Future2Height);
    }

    public static BufferedImage getMyFrame(BufferedImage qqImage) {
        return qqImage.getSubimage(MyCoordX, MyCoordY, MyWidth, MyHeight);
    }

    public static BufferedImage getQQFrame() {
        return getScreen(QQCoord.x, QQCoord.y, QQWidth, QQHeight);
    }

    public static BufferedImage getQQFrame(BufferedImage screenImg) {
        return screenImg.getSubimage(QQCoord.x, QQCoord.y, QQWidth, QQHeight);
    }

    public static BufferedImage getScreen() {
        return getScreen(0, 0, RECT_SCREEN.width, RECT_SCREEN.height);
    }

    public static void saveScreen(String name) {
        QQDebug.save(getScreen(0, 0, RECT_SCREEN.width, RECT_SCREEN.height), name);
    }

    public static BufferedImage getScreen(int x, int y, int w, int h) {
        return robot.createScreenCapture(new Rectangle(x, y, w, h));
    }

    public static void findQQTetris() {
        boolean foundQQ = false;
        for (int i = 0; i < DEVICES; i++) {
            RobotPeer rp = ROBOTS[i];
            if (rp != null) {
                Rectangle bounds = RECTANGLES[i];
                int[] s = rp.getRGBPixels(bounds);
                if (findQQTetris(s, bounds)) {
                    foundQQ = true;
                    CURRENT_ROBOT = rp;
                    break;
                }
            }
        }
        if (!foundQQ) {
            throw new MissingTetrisWindowException("QQTetris is not running!");
        }
    }

    private static boolean findQQTetris(int[] s, Rectangle bounds) {
        for (int y = QQHeight / 2; y < bounds.height; y += QQHeight - 10) {
            for (int x = 0; x < bounds.width - QQWidth; x++) {
            	// System.out.println("x,y: "+x+","+y+", expected,real: 0xFF578143,"+Integer.toHexString(getValue(s, bounds, x, y)));
                if (isSimilar(0xFF578143, getValue(s, bounds, x, y)) && isSimilar(0xFF578143, getValue(s, bounds, x, y + 1))
                    && isSimilar(0xFF578143, getValue(s, bounds, x, y - 1)) && isSimilar(0xFF578143, getValue(s, bounds, x, y + 2))
                    && isSimilar(0xFF578143, getValue(s, bounds, x, y - 2))) {
                    boolean foundCorner = false;
                    for (int v = y - 3; v >= 0; v--) {
                        if (isSimilar(0xFF83A373, getValue(s, bounds, x, v))) {
                            y = v;
                            foundCorner = true;
                            break;
                        } else if (!isSimilar(0xFF578143, getValue(s, bounds, x, v))) {
                            break;
                        }
                    }
                    if (foundCorner) {
                        if (x + QQWidth > bounds.width || y + QQHeight > bounds.height) {
                            throw new MissingTetrisWindowException("QQTetris cannot fully viewed!");
                        }
                        RECT_SCREEN.setBounds(bounds);
                        RECT_MY.setLocation(QQTetris.MyCoordX, QQTetris.MyCoordY);
                        RECT_MY.translate(x, y);
                        QQCoord.x=x;
                        QQCoord.y=y;
                        // System.out.println("找到QQTetris视窗: " + x + " / " + y);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void findQQTetris(BufferedImage image) {
        int[] s = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        boolean found = findQQTetris(s, new Rectangle(image.getWidth(), image.getHeight()));
        if (!found) {
            throw new MissingTetrisWindowException("QQTetris is not running!");
        }
    }

    private static final int getSubValue(final int[] array, final Rectangle rect, final int i, final int j) {
        return array[rect.x + i + (rect.y + j) * RECT_MY.width];
    }

    private static final int getValue(final int[] s, final Rectangle bounds, final int x, final int y) {
        return s[x + y * bounds.width];
    }

    public static void init() {
        // STOPPER.start();
        findQQTetris();
        // STOPPER.printTime("init");
    }

    private static final float[] HSV = new float[3];

    private static boolean isBlockColor(int c) {
        int r = (c >> 16) & 0x000000FF;
        int g = (c >> 8) & 0x000000FF;
        int b = (c) & 0x000000FF;

        Color.RGBtoHSB(r, g, b, HSV);
        float hue = HSV[0];
        if (HSV[1] < 0.06f) {
            if (r < 10 && g < 10) {
                // auto generated pieces
                return true;
            } else {
                return false;
            }
        } else if (hue < 0.035f && hue > 0.025f) {
            // points animation
            // System.out.println("points");
            return false;
        } else if (Math.abs(hue - 0.24f) < .0000001 ) {
            throw new BoardNotReadyException("Found explosion artifacts!");
        }
        if (findSimilar(BlockType.BLOCK_BOTTOM_COLORS, r, g, b)) {
            return true;
        }
        if (!IGNORE_INVALID_BOARD_COLOR) {
            System.out.print("Invalid board color found: ");
            QQDebug.debugColor(c);
            throw new UnknownBoardStateException("Color cannot be interpreted: " + Integer.toHexString(c));
        }
        return false;
    }

    public static QQStats makeStats(boolean autoBlue) {
        if (QQTetris.ANALYZE) {
            STOPPER.start();
        }
        int[] my = CURRENT_ROBOT.getRGBPixels(RECT_MY);
        QQStats stats = makeStats(my);
        if (stats.lowest > 4 && autoBlue && stats.isValid()) {
            doAutoBlue(my);
        }
        if (QQTetris.ANALYZE) {
            STOPPER.printTime("stats");
        }
        return stats;
    }

    private static void doAutoBlue(int[] my) {
        int c;
        boolean[] blues = new boolean[QQTetris.PiecesWidth];
        int bluesCounter = 0;
        int r, b;
        for (int x = QQTetris.BoardCoordX + QQTetris.PieceSize / 2, i = 0; x < QQTetris.MyWidth; x += QQTetris.PieceSize, i++) {
            c = getValue(my, RECT_MY, x, QQTetris.MyHeight - 1);
            r = (c >> 16) & 0x000000FF;
            b = (c) & 0x000000FF;
            if (b > 150 && r < 180) {
                blues[i] = true;
                // System.out.println("blue");
                bluesCounter++;
            } else if (b == 53) {
                // empty
                break;
            }
            // fill: 0xFF000000
            // -1: 0xFF2B7BB0
            // +2: 0xFFDB5700
            // arrow up: 0xFF2B7BB0
            // arrow down: 0xFFDB008B
        }
        if (bluesCounter > 0) {
            ArrayList<MoveType> moves = new ArrayList<MoveType>(QQTetris.PiecesWidth);
            for (boolean blue : blues) {
                if (blue) {
                    moves.add(MoveType.PERSON_ME);
                    if (--bluesCounter == 0) {
                        break;
                    }
                } else {
                    moves.add(MoveType.SKIP_ITEM);
                }
            }
            try {
                keyboardThread.putMoves(moves.toArray(new MoveType[moves.size()]));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
   /**
    * <pre>
    * 1. make full screen shot
    * 2. find left border color of frame
    * 3. check if frame is correct
    * 4. calculate board, next blocks positions
    * 5. extract current block from board
    * 6. calculate stats of current board
    * </pre>
    */
    private static QQStats makeStats(int[] my) {
        if (isSimilar(getValue(my, RECT_MY, 1, 1), 0xFF353535)) {
            boolean[] data = readBoardData(my);

            BlockType ft1 = readFutureBlock(my);
            BlockType ft2 = readFuture2Block(my);
            Tetromino t = BoardUtils.getAndCleanNextType(data);

            if (QQTetris.DEBUG) {
                QQDebug.debugScreen(data, t, ft1, ft2);
            }

            if (t == null) {
                if (QQTetris.ANALYZE) {
                    System.out.print(".");
                }
                return new QQStats(data, null, QQTetris.EMPTY_BLOCKTYPE_ARRAY);
            } else if (ft1 == null) {
                return new QQStats(data, t, new BlockType[] { t.block });
            } else if (ft2 == null) {
                return new QQStats(data, t, new BlockType[] { t.block, ft1 });
            } else {
                return new QQStats(data, t, new BlockType[] { t.block, ft1, ft2 });
            }
        } else {
            throw new MissingTetrisWindowException("Tetris frame disappeared!");
        }
    }

    public static QQStats makeStats(BufferedImage img) {
        return makeStats(img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth()));
    }

    public static void press(MoveType move, int sleep) throws InterruptedException {
        CURRENT_ROBOT.keyPress(move.KEY);
        Thread.sleep(sleep);
        CURRENT_ROBOT.keyRelease(move.KEY);
        Thread.sleep(1);
        if (QQTetris.ANALYZE) {
            System.out.println("pressed: " + move);
        }
    }

    public static void press(MoveType move) throws InterruptedException {
    	press(move, ROBOT_DELAY_MILLIS);
    }
    
    public static final void putMoves(MoveType[] move) throws InterruptedException {
        keyboardThread.putMoves(move);
    }

    public static boolean[] readBoardData(BufferedImage img) {
        return readBoardData(img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth()));
    }

    public static boolean[] readBoardData(int[] myData) {
        boolean[] boardData = BOARD_DATA;
        int x = 0;
        int y = 0;
        int c;
        int i;
        while (x < PiecesWidth && y < PiecesHeight) {
            c = getSubValue(myData, RECT_BOARD, x * PieceSize + 7, y * PieceSize + 15);
            i = BoardUtils.getBoardPos(x, y);
            if (isBlockColor(c)) {
                boardData[i] = true;
            } else {
                boardData[i] = false;
            }
            x++;
            if (x >= PiecesWidth) {
                x = 0;
                y++;
            }
        }
        return boardData;
    }

    public static BlockType readFuture2Block(int[] myData) {
        boolean found;
        for (BlockType b : BlockType.values()) {
            found = true;
            for (Point p : b.idCoords) {
                if (!isSimilar(getSubValue(myData, RECT_FUTURE2, p.x, p.y), 0xFF515151)) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return b;
            }
        }

        if (!IGNORE_INVALID_FUTURE_2) {
            throw new UnknownBlockTypeException("Future2: Unknown block!");
        } else {
            if (DEBUG) {
                System.err.println("Future2: Unknown block!");
            }
            return null;
        }
    }

    public static BlockType readFuture2Block(BufferedImage img) {
        return readFuture2Block(img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth()));
    }

    public static BlockType readFutureBlock(int[] myData) {
        int c = getSubValue(myData, RECT_FUTURE1, BlockType.L.innerCoord.x, BlockType.L.innerCoord.y);
        if (isSimilar(c, BlockType.L.innerColor)) {
            return BlockType.L;
        } else if (isSimilar(c, BlockType.J.innerColor)) {
            return BlockType.J;
        } else if (isSimilar(c, BlockType.S.innerColor)) {
            return BlockType.S;
        } else if (isSimilar(c, BlockType.T.innerColor)) {
            return BlockType.T;
        } else if (isSimilar(c, BlockType.Z.innerColor)) {
            return BlockType.Z;
        }
        int c2 = getSubValue(myData, RECT_FUTURE1, BlockType.I.innerCoord.x, BlockType.I.innerCoord.y);
        if (isSimilar(c2, BlockType.I.innerColor)) {
            return BlockType.I;
        } else if (isSimilar(c2, BlockType.O.innerColor)) {
            return BlockType.O;
        }
        if (!IGNORE_INVALID_FUTURE_1) {
            throw new UnknownBlockTypeException("Future1: Unknown block! (42,35)=" + Integer.toHexString(c) + ", (35,26)="
                                                + Integer.toHexString(c2));
        } else {
            if (DEBUG) {
                System.err.println("Future1: Unknown block! (42,35)=" + Integer.toHexString(c) + ", (35,26)="
                                   + Integer.toHexString(c2));
            }
            return null;
        }
    }

    public final static BlockType readFutureBlock(BufferedImage img) {
        return readFutureBlock(img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth()));
    }

    public final static int findVertical(final BlockType t, final BlockRotation r, final int xTry, final int yTry) {
        Point bottomPoint = new Point(RECT_MY.x, RECT_MY.y);
        bottomPoint.translate(RECT_BOARD.x, RECT_BOARD.y);
        final int xCoord = (xTry + r.freeLeft) * PieceSize + 7;
        int y = yTry;
        final int bottomColor = t.bottomColor;
        final int l = yTry + QQTetris.BlockDrawSize;
        
        boolean found = false;
        while (y++ < l) {
            if (!isSimilar(bottomColor, CURRENT_ROBOT.getRGBPixel(xCoord, (y + r.freeTop + r.height) * PieceSize + 15))) {
            	found = true;
                break;
            }
        }
        if (found) {
        	return y - 1;
        } else {
        	return -1; 
        }
    }
}

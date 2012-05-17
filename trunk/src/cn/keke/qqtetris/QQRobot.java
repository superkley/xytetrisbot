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
import static cn.keke.qqtetris.QQTetris.MyAreaHeight;
import static cn.keke.qqtetris.QQTetris.MyAreaWidth;
import static cn.keke.qqtetris.QQTetris.MyCoordX;
import static cn.keke.qqtetris.QQTetris.MyCoordY;
import static cn.keke.qqtetris.QQTetris.PieceSize;
import static cn.keke.qqtetris.QQTetris.PiecesHeight;
import static cn.keke.qqtetris.QQTetris.PiecesWidth;
import static cn.keke.qqtetris.QQTetris.QQCoord;
import static cn.keke.qqtetris.QQTetris.QQHeight;
import static cn.keke.qqtetris.QQTetris.QQWidth;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.peer.RobotPeer;
import java.lang.reflect.Method;

import sun.awt.ComponentFactory;
import cn.keke.qqtetris.exceptions.BoardNotReadyException;
import cn.keke.qqtetris.exceptions.MissingTetrisWindowException;
import cn.keke.qqtetris.exceptions.UnknownBlockTypeException;
import cn.keke.qqtetris.exceptions.UnknownBoardStateException;

public class QQRobot {
    private static final StopWatch STOPPER = new StopWatch("robot");
    private static final boolean IGNORE_INVALID_BOARD_COLOR;
    private static final boolean IGNORE_INVALID_FUTURE_1;
    private static final boolean IGNORE_INVALID_FUTURE_2;
    public static final int ROBOT_DELAY_MILLIS = 10;
    private static RobotPeer ROBOT;
    private static Method ROBOT_getRGBPixels;
    public final static Rectangle RECT_SCREEN;
    public final static Rectangle RECT_MY = new Rectangle(QQTetris.MyCoordX, QQTetris.MyCoordY, QQTetris.MyAreaWidth,
            QQTetris.MyAreaHeight);
    public static Rectangle RECT_FUTURE1 = new Rectangle(QQTetris.Future1X, QQTetris.Future1Y, QQTetris.Future1Width,
            QQTetris.Future1Height);
    public static Rectangle RECT_FUTURE2 = new Rectangle(QQTetris.Future2X, QQTetris.Future2Y, QQTetris.Future2Width,
            QQTetris.Future2Height);
    public static Rectangle RECT_BOARD = new Rectangle(QQTetris.BoardCoordX, QQTetris.BoardCoordY, QQTetris.BoardWidth,
            QQTetris.BoardHeight);
    static {
        try {
            ROBOT = ((ComponentFactory) Toolkit.getDefaultToolkit()).createRobot(null, null);
            final Class[] params = new Class[] { int.class, int.class, int.class, int.class, int[].class };
            ROBOT_getRGBPixels = ROBOT.getClass().getDeclaredMethod("getRGBPixels", params);
            ROBOT_getRGBPixels.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InstantiationError(e.toString());
        }

        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        RECT_SCREEN = new Rectangle(screenDim.width, screenDim.height);

        if (DEBUG) {
            IGNORE_INVALID_BOARD_COLOR = false;
            IGNORE_INVALID_FUTURE_1 = false;
            IGNORE_INVALID_FUTURE_2 = false;
        } else {
            IGNORE_INVALID_BOARD_COLOR = true;
            IGNORE_INVALID_FUTURE_1 = true;
            IGNORE_INVALID_FUTURE_2 = true;
        }
    }

    public static void click(int x, int y) throws InterruptedException {
        Point oldLocation = MouseInfo.getPointerInfo().getLocation();
        ROBOT.mouseMove(RECT_SCREEN.x + x, RECT_SCREEN.y + y);
        ROBOT.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(ROBOT_DELAY_MILLIS);
        ROBOT.mouseRelease(InputEvent.BUTTON1_MASK);
        Thread.sleep(ROBOT_DELAY_MILLIS);
        ROBOT.mouseMove(oldLocation.x, oldLocation.y);
        if (QQTetris.DEBUG) {
            System.out.println("鼠：" + x + ", " + y);
        }
    }

    private final static boolean findSimilar(final int[] a, final int r, final int g, final int b) {
        for (int i : a) {
            if (Math.abs(((i >> 16) & 0x000000FF) - r) < 16 && Math.abs(((i >> 8) & 0x000000FF) - g) < 16
                    && Math.abs(((i) & 0x000000FF) - b) < 18) {
                return true;
            }
        }
        return false;
    }

    public final static boolean isSimilar(final int a, final int key) {
        if (Math.abs(((a >> 16) & 0x000000FF) - ((key >> 16) & 0x000000FF)) <= 8
                && Math.abs(((a >> 8) & 0x000000FF) - ((key >> 8) & 0x000000FF)) <= 8
                && Math.abs(((a) & 0x000000FF) - ((key) & 0x000000FF)) <= 10) {
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
        return qqImage.getSubimage(MyCoordX, MyCoordY, MyAreaWidth, MyAreaHeight);
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
        try {
            return new Robot().createScreenCapture(new Rectangle(x, y, w, h));
        } catch (AWTException e) {
            return null;
        }
    }

    private static int[] BUFFER_findQQTetris = new int[RECT_SCREEN.width * RECT_SCREEN.height];

    private static void findQQTetris() {
        try {
            ROBOT_getRGBPixels.invoke(ROBOT, RECT_SCREEN.x, RECT_SCREEN.y, RECT_SCREEN.width, RECT_SCREEN.height,
                    BUFFER_findQQTetris);
            if (!findQQTetris(BUFFER_findQQTetris, RECT_SCREEN)) {
                throw new MissingTetrisWindowException("QQTetris is not running!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean findQQTetris(int[] s, Rectangle bounds) {
        for (int y = QQHeight / 2; y < bounds.height; y += QQHeight - 10) {
            for (int x = 0; x < bounds.width - QQWidth; x++) {
                // System.out.println("x,y: "+x+","+y+", expected,real: 0xFF578143,"+Integer.toHexString(getValue(s,
                // bounds, x, y)));
                if (isSimilar(0xFF578143, getValue(s, bounds, x, y))
                        && isSimilar(0xFF578143, getValue(s, bounds, x, y + 1))
                        && isSimilar(0xFF578143, getValue(s, bounds, x, y - 1))
                        && isSimilar(0xFF578143, getValue(s, bounds, x, y + 2))
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
                        RECT_MY.setLocation(QQTetris.MyCoordX, QQTetris.MyCoordY);
                        RECT_MY.translate(x, y);
                        QQCoord.x = x;
                        QQCoord.y = y;
                        System.out.println("找到QQTetris视窗: " + x + " / " + y);
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

    private static final int getMyValue(final int[] array, final Rectangle rect, final int i, final int j) {
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

    private static final boolean isBlockColor(final int c) {
        final int r = (c >> 16) & 0x000000FF;
        final int g = (c >> 8) & 0x000000FF;
        final int b = (c) & 0x000000FF;

        Color.RGBtoHSB(r, g, b, HSV);
        final float hue = HSV[0];
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
        } else if (Math.abs(hue - 0.24f) < .0000001f) {
            throw new BoardNotReadyException("Found explosion artifacts!");
        }
        if (findSimilar(BlockType.BLOCK_BOTTOM_COLORS, r, g, b)) {
            return true;
        }
        if (!IGNORE_INVALID_BOARD_COLOR) {
            // QQDebug.debugColor(c);
            throw new UnknownBoardStateException("没找到游戏板：" + Integer.toHexString(c));
        }
        return false;
    }

    private static int[] BUFFER_makeBoardStats = new int[RECT_MY.width * RECT_MY.height];

    private static final boolean[] BUFFER_doAutoBlue = new boolean[QQTetris.PiecesWidth];

    public static final void doAutoBlue() {
        int bluesCounter = 0;
        for (int x = QQTetris.BoardCoordX + QQTetris.PieceSize / 2, i = 0; x < QQTetris.MyAreaWidth; x += QQTetris.PieceSize, i++) {
            final int c = getValue(BUFFER_makeBoardStats, RECT_MY, x, QQTetris.MyAreaHeight - 1);
            final int r = (c >> 16) & 0x000000FF;
            final int b = (c) & 0x000000FF;
            if (b > 150 && r < 180) {
                BUFFER_doAutoBlue[i] = true;
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
            for (boolean blue : BUFFER_doAutoBlue) {
                if (blue) {
                    QQTetris.press(MoveType.PERSON_ME);
                    if (--bluesCounter == 0) {
                        break;
                    }
                } else {
                    QQTetris.press(MoveType.SKIP_ITEM);
                }
            }
            QQTetris.dirty = true;
        }
    }

    public static void press(MoveType move) throws InterruptedException {
        ROBOT.keyPress(move.KEY);
        Thread.sleep(ROBOT_DELAY_MILLIS);
        ROBOT.keyRelease(move.KEY);
        Thread.sleep(1);
        // System.out.println("键：" + move);
    }

    public static final void readBoardData(final int[] myData, final boolean[] board) {
        int x = 0;
        int y = 0;
        int i;
        while (x < PiecesWidth && y < PiecesHeight) {
            i = BoardUtils.getBoardPos(x, y);
            if (isBlockColor(getMyValue(myData, RECT_BOARD, x * PieceSize + 7, y * PieceSize + 15))) {
                board[i] = true;
            } else {
                board[i] = false;
            }
            x++;
            if (x >= PiecesWidth) {
                x = 0;
                y++;
            }
        }
    }

    public static BlockType readFuture2Block(int[] myData) {
        boolean found;
        for (BlockType b : BlockType.values()) {
            found = true;
            for (Point p : b.idCoords) {
                if (!isSimilar(getMyValue(myData, RECT_FUTURE2, p.x, p.y), 0xFF515151)) {
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

    public static BlockType readFutureBlock(final int[] myData) {
        final int c = getMyValue(myData, RECT_FUTURE1, BlockType.L.innerCoord.x, BlockType.L.innerCoord.y);
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
        final int c2 = getMyValue(myData, RECT_FUTURE1, BlockType.I.innerCoord.x, BlockType.I.innerCoord.y);
        if (isSimilar(c2, BlockType.I.innerColor)) {
            return BlockType.I;
        } else if (isSimilar(c2, BlockType.O.innerColor)) {
            return BlockType.O;
        }
        if (!IGNORE_INVALID_FUTURE_1) {
            throw new UnknownBlockTypeException("Future1: Unknown block! (42,35)=" + Integer.toHexString(c)
                    + ", (35,26)=" + Integer.toHexString(c2));
        } else if (DEBUG) {
            System.err.println("Future1: Unknown block! (42,35)=" + Integer.toHexString(c) + ", (35,26)="
                    + Integer.toHexString(c2));
        }
        return null;
    }

    public final static BlockType readFutureBlock(BufferedImage img) {
        return readFutureBlock(img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth()));
    }

    public static final void findWindowLocation(final int[] rgbScreen) {
        QQCoord.setLocation(-1, -1);
        if (!findQQTetris(rgbScreen, RECT_SCREEN)) {
            throw new MissingTetrisWindowException("没有找到QQTetris窗口！");
        }
    }

    public static final void checkBoardExists(final int[] rgbMySpace) {
        if (!isSimilar(getValue(rgbMySpace, RECT_MY, 1, 1), 0xFF353535)) {
            throw new MissingTetrisWindowException("没找到QQTetris窗口！");
        }
    }

    public static final void findBoard(final int[] rgbMySpace, final boolean[] board) {
        readBoardData(rgbMySpace, board);
    }

    public static final void findAndCleanBoard(final boolean[] board, final Tetromino tetromino,
            final BlockType[] nextBlocks) {
        BoardUtils.getAndCleanNextType(board, tetromino);
        nextBlocks[0] = tetromino.block;
        BoardUtils.clearFullLines(board);
    }

    public static final void findFutures(final int[] rgbMySpace, final BlockType[] futures) {
        futures[1] = readFutureBlock(rgbMySpace);
        futures[2] = readFuture2Block(rgbMySpace);
    }

    public static final int findTetromino(final Tetromino tetromino, final int maxFallen) {
        final BlockType t = tetromino.block;
        final BlockRotation r = tetromino.rotation;
        final int xTry = tetromino.x;
        final int yTry = tetromino.y;

        return findTetromino(t, r, xTry, yTry, maxFallen);
    }

    private static int findTetromino(final BlockType t, final BlockRotation r, final int xTry, final int yTry,
            final int maxFallen) {
        final Point bottomPoint = new Point(RECT_MY.x, RECT_MY.y);
        bottomPoint.translate(RECT_BOARD.x, RECT_BOARD.y);
        final int xCoord = (xTry + r.freeLeft) * PieceSize + 7 + bottomPoint.x;
        final int bottomColor = t.bottomColor;
        // fallen-max = 3
        final int l = yTry + maxFallen;

        int y = yTry;
        boolean found = false;
        while (y < l) {
            if (!isSimilar(bottomColor,
                    ROBOT.getRGBPixel(xCoord, (y + r.freeTop + r.height) * PieceSize + 15 + bottomPoint.y))) {
                found = true;
                break;
            }
            y++;
        }
        if (found) {
            // TODO check y-1
            return y;
        } else {
            return -1;
        }
    }

    private static int findTetromino(final int[] rgbMySpace, final BlockType t, final BlockRotation r, final int xTry,
            final int yTry, final int maxFallen) {
        final int xCoord = (xTry + r.freeLeft) * PieceSize + 7;
        final int bottomColor = t.bottomColor;
        // fallen-max = 3
        final int l = yTry + maxFallen;

        int y = yTry;
        boolean found = false;
        while (y < l) {
            if (!isSimilar(bottomColor,
                    getMyValue(rgbMySpace, RECT_BOARD, xCoord, (y + r.freeTop + r.height) * PieceSize + 15))) {
                found = true;
                break;
            }
            y++;
        }
        if (found) {
            // TODO check y-1
            return y;
        } else {
            return -1;
        }
    }

    public static final void captureScreen(final Rectangle rect, final int[] rgbScreen) {
        try {
            assert (rgbScreen.length == rect.width * rect.height);
            ROBOT_getRGBPixels.invoke(ROBOT, rect.x, rect.y, rect.width, rect.height, rgbScreen);
        } catch (Exception e) {
            // silent
        }
    }

    public static final void findTetromino(final int[] rgbMySpace, final Tetromino tetromino,
            final BlockType[] nextBlocks, final int maxFallen) {
        final BlockType nextBlockType = CurrentData.CALCULATED.nextBlocks[1];
        if (nextBlockType != null) {
            final int fallen = findTetromino(rgbMySpace, nextBlockType, nextBlockType.rotations[0], 4, 0, maxFallen);
            tetromino.set(nextBlockType, 0, 4, fallen);
            nextBlocks[0] = nextBlockType;
            return;
        }
        for (BlockType bt : BlockType.values()) {
            if (bt != nextBlockType) {
                final int fallen = findTetromino(rgbMySpace, bt, bt.rotations[0], 4, 0, maxFallen);
                if (fallen != -1) {
                    tetromino.set(bt, 0, 4, fallen);
                    nextBlocks[0] = bt;
                    return;
                }
            }
        }

    }
}

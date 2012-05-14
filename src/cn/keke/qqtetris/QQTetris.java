package cn.keke.qqtetris;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

// for qq tetris beta 3 build 40
// TODO clever use of blue and red items
public final class QQTetris extends JFrame {
    public final static boolean DEBUG = false;
    public final static boolean ANALYZE = false;
    public final static boolean TEST = false;
    private static final String ACTION_SPEED_MINUS = "-";
    private static final String ACTION_SPEED_PLUS = "+";
    private static final long serialVersionUID = 5017677872022894209L;
    public static final Point QQCoord = new Point(-1, -1);
    public static final int QQWidth = 800;
    public static final int QQHeight = 600;
    public static final int MyCoordX = 293; // relative to QQCoord
    public static final int MyCoordY = 139; // relative to QQCoord
    public static final int MyAreaWidth = 290;
    public static final int MyAreaHeight = 358; // big: 591 - 233 = 358 small: 569 - 233 = 336
    public static final int MyHeightSmall = 336; // big: 591 - 233 = 358 small: 569 - 233 = 336
    public static final int MAX_BLOCKS = 3;
    public static final int Future1X = 0; // relative to MyCoord
    public static final int Future1Y = 0; // relative to MyCoord
    public static final int Future1Width = 85;
    public static final int Future1Height = 68;
    public static final int Future2X = 0; // relative to MyCoord
    public static final int Future2Y = 76; // relative to MyCoord
    public static final int Future2Width = 85;
    public static final int Future2Height = 68;
    public static final int BoardCoordX = 97; // relative to MyCoord
    public static final int BoardCoordY = 0; // relative to MyCoord
    public static final int BoardWidth = 192;
    public static final int BoardHeight = 336;
    public static final int BlockDrawSize = 4;
    public static final int PieceSize = 16;
    public static final int PiecesWidth = 12; // 192 / PieceSize;
    public static final int PiecesHeight = 21; // 336 / PieceSize;
    public static final BlockType[] EMPTY_BLOCKTYPE_ARRAY = new BlockType[0];
    private final static JButton btnStart;
    private final static JButton btnScreenCapture;
    private final static JComboBox cbStrategy;
    public static final MoveCalculator calculator;
    public static final QQScreenCaptureThread captureScreenThread;
    public static final QQCalculateMoveThread calculationThread;
    private static QQState state;
    private static StrategyType currentStrategy;
    private Container c;
    private static final Color COLOR_AUTO_BLUE = new Color(0xffb5c5ff);
    private static boolean autoBlue = true;
    private static final JFrame about = createAbout();
    public static ExecutorService executor = Executors.newSingleThreadExecutor();
    public static boolean dirty;
    private static final KeyboardThread keyThread = new KeyboardThread();

    static {
        keyThread.start();
        // TODO: async calculator is buggy. check SyncAsyncTest.
        if (Runtime.getRuntime().availableProcessors() > 1) {
            calculator = new QQCalculatorAsync();
        } else {
            calculator = new QQCalculatorSync();
        }
        captureScreenThread = new QQScreenCaptureThread();
        calculationThread = new QQCalculateMoveThread(calculator);
        Font font = new Font("Monospaced", Font.PLAIN, 12);
        btnStart = new JButton("开始");
        btnScreenCapture = new JButton("帮助");
        btnScreenCapture.setFont(font);
        btnStart.setFont(font);
        cbStrategy = new JComboBox(new String[] { "正常", "群杀", "长生", "聚宝", "忍者" });
        cbStrategy.setFont(font);
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, UnsupportedLookAndFeelException {
        new QQTetris();
    }

    private QQTetris() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
            UnsupportedLookAndFeelException {
        super("QQ俄罗斯方块机器人（兼容QQTetris beta3 build40版）");
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        setAlwaysOnTop(true);
        setLocationByPlatform(true);
        // this.calculator = new QQCalculatorSync();
        btnScreenCapture.setToolTipText("使用说明，软件介绍");
        btnScreenCapture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ANALYZE) {
                    Calendar cal = Calendar.getInstance();
                    QQDebug.save(
                            QQRobot.getScreen(),
                            String.valueOf("qqtetris_" + cal.get(Calendar.YEAR)) + (1 + cal.get(Calendar.MONTH))
                                    + cal.get(Calendar.DAY_OF_MONTH) + "-" + padding(cal.get(Calendar.HOUR_OF_DAY))
                                    + padding(cal.get(Calendar.MINUTE)) + padding(cal.get(Calendar.SECOND)));
                }
                QQTetris.this.about.setVisible(true);
                activate();
            }

            private String padding(int i) {
                return (i < 10) ? "0" + i : String.valueOf(i);
            }
        });
        
        this.currentStrategy = StrategyType.NORMAL;
        btnStart.setToolTipText("请先关闭其它窗口然后打开QQ火拼俄罗斯");
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (!QQTetris.this.captureScreenThread.isRunning()) {
                    startAI();
                } else {
                    stopAI();
                }
                if (QQTetris.this.captureScreenThread.isRunning()) {
                    QQTetris.this.btnStart.setText("暂停");
                } else {
                    QQTetris.this.btnStart.setText("开始");
                }
            }
        });
        ActionListener strategyListener = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                switch (QQTetris.this.cbStrategy.getSelectedIndex()) {
                case 0:
                    QQTetris.this.currentStrategy = StrategyType.NORMAL;
                    break;
                case 1:
                    QQTetris.this.currentStrategy = StrategyType.SAVE_KILL;
                    break;
                case 2:
                    QQTetris.this.currentStrategy = StrategyType.LONG_LIFE;
                    break;
                case 3:
                    QQTetris.this.currentStrategy = StrategyType.MORE_TREASURE;
                    break;
                case 4:
                    QQTetris.this.currentStrategy = StrategyType.KILL_ALL;
                    break;
                }
                System.out.println("Changed strategy to: " + QQTetris.this.currentStrategy);
                activate();
            }
        };
        cbStrategy.addActionListener(strategyListener);
        setState(QQState.STOPPED);
        this.c = getContentPane();
        this.c.setBackground(Color.lightGray);
        this.c.setLayout(new FlowLayout());
        this.c.add(btnStart);
        this.c.add(cbStrategy);
        this.c.add(this.btnScreenCapture);
        this.c.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (QQTetris.this.autoBlue) {
                    QQTetris.this.autoBlue = false;
                } else {
                    QQTetris.this.autoBlue = true;
                }
                updateContainerColor();
                activate();
            }
        });
        updateContainerColor();
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.captureScreenThread.start();
        this.calculationThread.start();
    }

    void updateContainerColor() {
        if (this.autoBlue) {
            this.c.setBackground(COLOR_AUTO_BLUE);
        } else {
            this.c.setBackground(Color.lightGray);
        }
    }

    void startAI() {
        this.captureScreenThread.go();
    }

    void stopAI() {
        this.captureScreenThread.pause();
    }

    public static void setState(final QQState qqState) {
        if (state != qqState) {
            state = qqState;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    btnStart.setBackground(qqState.color);
                }
            });
        }
    }

    public final static StrategyType getStrategy() {
        return currentStrategy;
    }

    public static void activate() {
        if (!ANALYZE && state == QQState.PLAYING) {
            try {
                QQRobot.click(QQCoord.x + MyCoordX, QQCoord.y + MyCoordY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public final static boolean isAutoBlue() {
        return autoBlue;
    }

    private static JFrame createAbout() {
        JFrame aboutFrame = new JFrame();
        aboutFrame.add(new JPanel() {
            private static final long serialVersionUID = -4970372227279706816L;

            @Override
            public void paintComponent(Graphics g) {
                BufferedImage image;
                try {
                    URL url = QQTetris.class.getResource("/about.png");
                    image = ImageIO.read(url);
                    g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        aboutFrame.pack();
        aboutFrame.setTitle("帮助");
        aboutFrame.setSize(410, 260);
        aboutFrame.setResizable(false);
        aboutFrame.setLocationByPlatform(true);
        aboutFrame.setAlwaysOnTop(true);
        return aboutFrame;
    }

    public static final void press(final MoveType... move) {
        keyThread.putMoves(move);
    }

}

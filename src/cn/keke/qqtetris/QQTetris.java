/*  Copyright (c) 2010 Xiaoyun Zhu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy  
 *  of this software and associated documentation files (the "Software"), to deal  
 *  in the Software without restriction, including without limitation the rights  
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell  
 *  copies of the Software, and to permit persons to whom the Software is  
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in  
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN  
 *  THE SOFTWARE.  
 */
package cn.keke.qqtetris;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

// for qq tetris beta 3 build 40
// TODO clever use of blue and red items
// TODO global hotkey
// TODO +-
public final class QQTetris extends JFrame implements HotkeyListener {
    public final static boolean DEBUG = false;
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
    private final static JButton btnSpeedPlus;
    private final static JButton btnSpeedMinus;
    private final static JTextField tfSpeedPct;
    private final static JButton btnScreenCapture;
    private final static JCheckBox chkClever;
    private final static JComboBox cbStrategy;
    public static final MoveCalculator calculator;
    public static final QQScreenCaptureThread captureScreenThread;
    public static final QQCalculationThread calculationThread;
    private static QQState state;
    private static StrategyType currentStrategy;
    private Container c;
    private static final Color COLOR_AUTO_BLUE = new Color(0xffb5c5ff);
    private static boolean autoBlue = true;
    private static final JFrame about = createAbout();
    public static ExecutorService executor = Executors.newSingleThreadExecutor();
    // private static final KeyboardThread keyThread = new KeyboardThread();
    public static boolean cleverMode = false;

    static {
        if (Runtime.getRuntime().availableProcessors() > 2) {
            if (DEBUG) {
                System.out.println("计算器：多轨");
            }
            // calculator = new QQCalculatorSync();
            calculator = new QQCalculatorAsync();
        } else {
            if (DEBUG) {
                System.out.println("单轨");
            }
            calculator = new QQCalculatorSync();
        }
        captureScreenThread = new QQScreenCaptureThread();
        calculationThread = new QQCalculationThread(calculator);
        final Font font = new Font("Monospaced", Font.PLAIN, 12);
        btnStart = new JButton("开始");
        btnScreenCapture = new JButton("帮助");
        btnScreenCapture.setFont(font);
        btnStart.setFont(font);
        cbStrategy = new JComboBox(new String[] { "正常", "群杀", "长生", "聚宝", "忍者" });
        cbStrategy.setFont(font);
        btnSpeedPlus = new JButton(ACTION_SPEED_PLUS);
        btnSpeedMinus = new JButton(ACTION_SPEED_MINUS);
        tfSpeedPct = new JTextField(calculationThread.getSpeedPct());
        tfSpeedPct.setFont(font);
        btnSpeedPlus.setFont(font);
        btnSpeedMinus.setFont(font);
        chkClever = new JCheckBox("智能（测试）");
        chkClever.setFont(font);
        chkClever.setOpaque(false);
    }

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, UnsupportedLookAndFeelException, IOException {
        new QQTetris();
    }

    private QQTetris() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
            UnsupportedLookAndFeelException, IOException {
        super("QQTetris机器人（兼容 beta3 build40版）");
        setIconImage(ImageIO.read(getClass().getResource("/xytetris.png")));
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        setAlwaysOnTop(true);
        setLocationByPlatform(true);
        // this.calculator = new QQCalculatorSync();
        btnScreenCapture.setToolTipText("使用说明，软件介绍");
        btnScreenCapture.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (DEBUG) {
                    Calendar cal = Calendar.getInstance();
                    QQDebug.save(
                            QQRobot.getScreen(),
                            String.valueOf("qqtetris_" + cal.get(Calendar.YEAR)) + (1 + cal.get(Calendar.MONTH))
                                    + cal.get(Calendar.DAY_OF_MONTH) + "-" + padding(cal.get(Calendar.HOUR_OF_DAY))
                                    + padding(cal.get(Calendar.MINUTE)) + padding(cal.get(Calendar.SECOND)));
                }
                QQTetris.about.setVisible(true);
                activate();
            }

            private String padding(int i) {
                return (i < 10) ? "0" + i : String.valueOf(i);
            }
        });

        QQTetris.currentStrategy = StrategyType.NORMAL;
        btnStart.setToolTipText("请先关闭其它窗口然后打开QQ火拼俄罗斯（win+alt+k）");
        btnStart.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ev) {
                startStopAction();
            }

        });
        ActionListener strategyListener = new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                switch (QQTetris.cbStrategy.getSelectedIndex()) {
                case 0:
                    QQTetris.currentStrategy = StrategyType.NORMAL;
                    break;
                case 1:
                    QQTetris.currentStrategy = StrategyType.SAVE_KILL;
                    break;
                case 2:
                    QQTetris.currentStrategy = StrategyType.LONG_LIFE;
                    break;
                case 3:
                    QQTetris.currentStrategy = StrategyType.MORE_TREASURE;
                    break;
                case 4:
                    QQTetris.currentStrategy = StrategyType.KILL_ALL;
                    break;
                }
                if (DEBUG) {
                    System.out.println("策略：" + QQTetris.currentStrategy);
                }
                activate();
            }
        };
        cbStrategy.addActionListener(strategyListener);
        setState(QQState.STOPPED);

        btnSpeedPlus.setToolTipText("加速（win+alt+上）");
        btnSpeedPlus.setActionCommand(ACTION_SPEED_PLUS);
        btnSpeedMinus.setToolTipText("减速（win+alt+下）");
        btnSpeedMinus.setActionCommand(ACTION_SPEED_MINUS);
        ActionListener speedActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (ev.getActionCommand() == ACTION_SPEED_MINUS) {
                    calculationThread.decreaseSpeed();
                } else {
                    calculationThread.increaseSpeed();
                }
                tfSpeedPct.setText(calculationThread.getSpeedPct());
                activate();
            }
        };
        tfSpeedPct.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                QQLevel level = calculator.getLevel();
                if (level == QQLevel.HARD) {
                    level = QQLevel.MEDIUM;
                } else if (level == QQLevel.MEDIUM) {
                    level = QQLevel.EASY;
                } else {
                    level = QQLevel.HARD;
                }
                calculator.setLevel(level);
                tfSpeedPct.setBackground(level.color);
                activate();
            }
        });
        btnSpeedPlus.addActionListener(speedActionListener);
        btnSpeedMinus.addActionListener(speedActionListener);

        chkClever.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                QQTetris.cleverMode = chkClever.isSelected();
            }
        });
        QQTetris.captureScreenThread.start();
        QQTetris.calculationThread.start();
        // QQTetris.keyThread.start();

        addGlobalHotkeys();

        this.c = getContentPane();
        this.c.setBackground(Color.lightGray);
        this.c.setLayout(new FlowLayout());
        this.c.add(btnStart);
        this.c.add(cbStrategy);
        this.c.add(tfSpeedPct);
        this.c.add(btnSpeedPlus);
        this.c.add(btnSpeedMinus);
        this.c.add(chkClever);
        this.c.add(QQTetris.btnScreenCapture);
        this.c.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if (QQTetris.autoBlue) {
                    QQTetris.autoBlue = false;
                } else {
                    QQTetris.autoBlue = true;
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
    }

    private void startStopAction() {
        if (!QQTetris.captureScreenThread.isRunning()) {
            startAI();
        } else {
            stopAI();
        }
        if (QQTetris.captureScreenThread.isRunning()) {
            QQTetris.btnStart.setText("暂停");
        } else {
            QQTetris.btnStart.setText("开始");
        }
    }

    private void addGlobalHotkeys() {
        try {
            JIntellitype.getInstance().registerHotKey(1, JIntellitype.MOD_WIN + JIntellitype.MOD_ALT, KeyEvent.VK_K);
            JIntellitype.getInstance().registerHotKey(2, JIntellitype.MOD_WIN + JIntellitype.MOD_ALT, KeyEvent.VK_UP);
            JIntellitype.getInstance().registerHotKey(3, JIntellitype.MOD_WIN + JIntellitype.MOD_ALT, KeyEvent.VK_DOWN);
            JIntellitype.getInstance().registerHotKey(4, JIntellitype.MOD_WIN + JIntellitype.MOD_ALT, KeyEvent.VK_L);
            JIntellitype.getInstance().registerHotKey(5, JIntellitype.MOD_WIN + JIntellitype.MOD_ALT, KeyEvent.VK_LEFT);
            JIntellitype.getInstance().registerHotKey(6, JIntellitype.MOD_WIN + JIntellitype.MOD_ALT, KeyEvent.VK_RIGHT);
            JIntellitype.getInstance().addHotKeyListener(this);
        } catch (Throwable t) {
            System.err.println(t.toString());
        }
    }

    void updateContainerColor() {
        if (QQTetris.autoBlue) {
            this.c.setBackground(COLOR_AUTO_BLUE);
        } else {
            this.c.setBackground(Color.lightGray);
        }
    }

    void startAI() {
        QQTetris.captureScreenThread.go();
    }

    void stopAI() {
        QQTetris.captureScreenThread.pause();
    }

    public static void setState(final QQState qqState) {
        if (state != qqState) {
            state = qqState;
            SwingUtilities.invokeLater(new Runnable() {

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
        if (state == QQState.PLAYING) {
            try {
                QQRobot.click(QQCoord.x + MyCoordX, QQCoord.y + MyCoordY);
            } catch (InterruptedException e) {
                // e.printStackTrace();
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

    // public static final void press(final MoveType... move) {
    // keyThread.putMoves(move);
    // }

    public void onHotKey(int identifier) {
    	int selected = QQTetris.cbStrategy.getSelectedIndex();
        switch (identifier) {
        case 1:
            startStopAction();
            break;
        case 2:
            calculationThread.increaseSpeed();
            break;
        case 3:
            calculationThread.decreaseSpeed();
            break;
        case 4:
            if (QQTetris.autoBlue) {
                QQTetris.autoBlue = false;
            } else {
                QQTetris.autoBlue = true;
            }
            break;
        case 5:
        	  if (selected > 0) {
        	  	  QQTetris.cbStrategy.setSelectedIndex(selected - 1);
        	  }
        	  break;
        case 6:
        	  if (selected < StrategyType.values().length - 1) {
    	  	      QQTetris.cbStrategy.setSelectedIndex(selected + 1);
    	      }
        	  break;
        default:
            break;
        }
        updateContainerColor();
        tfSpeedPct.setText(calculationThread.getSpeedPct());
        activate();
    }

    protected void finalize() throws Throwable {
        JIntellitype.getInstance().cleanUp();
    }

    public static final void pressDirect(final MoveType m) {
        try {
            QQRobot.press(m);
        } catch (InterruptedException e) {
            // errr
        }
    }

}

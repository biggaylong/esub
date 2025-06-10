package com.my.Minesweeper.ui;

import com.my.Minesweeper.game.Cell;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Random;

// 扫雷游戏主类，使用单例模式
public class minesweeper1 {
    // 难度级别常量
    private static final String EASY = "Easy";
    private static final String MEDIUM = "Medium";
    private static final String HARD = "Hard";
    private static final String HELL = "Hell";
    // 简单难度的棋盘行数
    private static final int EASY_ROWS = 9;
    // 简单难度的棋盘列数
    private static final int EASY_COLS = 9;
    // 简单难度的雷数量
    private static final int EASY_MINES = 10;

    // 中等难度的棋盘行数
    private static final int MEDIUM_ROWS = 16;
    // 中等难度的棋盘列数
    private static final int MEDIUM_COLS = 16;
    // 中等难度的雷数量
    private static final int MEDIUM_MINES = 40;

    // 困难难度的棋盘行数
    private static final int HARD_ROWS = 16;
    // 困难难度的棋盘列数
    private static final int HARD_COLS = 30;
    // 困难难度的雷数量
    private static final int HARD_MINES = 99;

    private static final int HELL_ROWS = 16;
    // 困难难度的棋盘列数
    private static final int HELL_COLS = 30;
    // 困难难度的雷数量
    private static final int HELL_MINES = 150;
    private static final String CUSTOM = "Custom";
    private int customRows = 16;
    private int customCols = 16;
    private int customMines = 40;

    // 当前棋盘的行数
    private int rows;
    // 当前棋盘的列数
    private int cols;
    // 当前棋盘的雷数量
    private int numMines;
    // 棋盘单元格二维数组
    private Cell[][] board;
    // 游戏主窗口
    private JFrame frame;
    // 按钮二维数组，对应棋盘上的每个单元格
    private JButton[][] buttons;
    // 显示剩余雷数的标签
    private JLabel minesLeftLabel;
    // 剩余雷数
    private int minesLeft;
    // 当前难度
    private String currentDifficulty;

    // 单例实例
    private static minesweeper1 instance;

    // 私有构造函数，防止外部实例化
    private minesweeper1() {
        showDifficultyDialog();
    }

    // 获取单例实例的方法
    public static minesweeper1 getInstance() {
        if (instance == null) {
            instance = new minesweeper1();
        }
        return instance;
    }

    // 显示难度选择对话框
    private void showDifficultyDialog() {
        Object[] options = {EASY, MEDIUM, HARD, HELL, CUSTOM};
        int choice = JOptionPane.showOptionDialog(null,
                "Select difficulty level",
                "Minesweeper",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0:
                currentDifficulty = EASY;
                rows = EASY_ROWS;
                cols = EASY_COLS;
                numMines = EASY_MINES;
                break;
            case 1:
                currentDifficulty = MEDIUM;
                rows = MEDIUM_ROWS;
                cols = MEDIUM_COLS;
                numMines = MEDIUM_MINES;
                break;
            case 2:
                currentDifficulty = HARD;
                rows = HARD_ROWS;
                cols = HARD_COLS;
                numMines = HARD_MINES;
                break;
            case 3:
                currentDifficulty = HELL;
                rows = HELL_ROWS;
                cols = HELL_COLS;
                numMines = HELL_MINES;
                break;
            case 4:
                showCustomDifficultyDialog();
                return;
            default:
                currentDifficulty = EASY;
                rows = EASY_ROWS;
                cols = EASY_COLS;
                numMines = EASY_MINES;
        }

        initGame();
    }

    private void showCustomDifficultyDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JSpinner rowsSpinner = new JSpinner(new SpinnerNumberModel(customRows, 5, 30, 1));
        JSpinner colsSpinner = new JSpinner(new SpinnerNumberModel(customCols, 5, 30, 1));
        JSpinner minesSpinner = new JSpinner(new SpinnerNumberModel(customMines, 1, 899, 1));

        panel.add(new JLabel("Rows:"));
        panel.add(rowsSpinner);
        panel.add(new JLabel("Columns:"));
        panel.add(colsSpinner);
        panel.add(new JLabel("Mines:"));
        panel.add(minesSpinner);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Custom Difficulty",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            customRows = (int) rowsSpinner.getValue();
            customCols = (int) colsSpinner.getValue();

            // 确保地雷数量不超过棋盘格子数减1
            int maxMines = customRows * customCols - 1;
            customMines = Math.min((int) minesSpinner.getValue(), maxMines);

            currentDifficulty = CUSTOM;
            rows = customRows;
            cols = customCols;
            numMines = customMines;

            initGame();
        } else {
            // 用户取消，回到难度选择
            showDifficultyDialog();
        }
    }

    // 初始化游戏
    private void initGame() {
        if (frame != null) {
            frame.dispose();
        }

        board = new Cell[rows][cols];
        generateMines();
        calculateAdjacentMines();

        frame = new JFrame("Minesweeper - " + currentDifficulty);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        if (currentDifficulty.equals(CUSTOM)) {
            frame.setTitle("Minesweeper - Custom (" + rows + "x" + cols + ", " + numMines + " mines)");
        } else {
            frame.setTitle("Minesweeper - " + currentDifficulty);
        }

        // 创建棋盘面板
        JPanel boardPanel = new JPanel(new GridLayout(rows, cols));
        buttons = new JButton[rows][cols];
        int squareSize = calculateSquareSize();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                final int finalI = i;
                final int finalJ = j;
                buttons[i][j] = new JButton();
                // 设置按钮的字体和大小
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 16));
                // 设置按钮的背景和前景颜色
                buttons[i][j].setBackground(Color.LIGHT_GRAY);
                buttons[i][j].setForeground(Color.BLACK);
                buttons[i][j].setPreferredSize(new Dimension(squareSize, squareSize));
                buttons[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            leftClick(finalI, finalJ);
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            rightClick(finalI, finalJ);
                        }
                    }
                });
                boardPanel.add(buttons[i][j]);
            }
        }

        minesLeft = numMines;
        minesLeftLabel = new JLabel("Mines left: " + minesLeft);
        // 设置标签的字体和大小
        minesLeftLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // 创建顶部面板，用于显示剩余雷数和控制按钮
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(minesLeftLabel);

        JButton restartButton = new JButton("Restart");
        // 设置按钮的字体和大小
        restartButton.setFont(new Font("Arial", Font.BOLD, 16));
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initGame();
            }
        });
        topPanel.add(restartButton);

        JButton changeDifficultyButton = new JButton("Change Difficulty");
        // 设置按钮的字体和大小
        changeDifficultyButton.setFont(new Font("Arial", Font.BOLD, 16));
        changeDifficultyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDifficultyDialog();
            }
        });
        topPanel.add(changeDifficultyButton);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(boardPanel, BorderLayout.CENTER);
        // 调整窗口大小以确保单元格为正方形
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private int calculateSquareSize() {
        int minDimension = Math.min(rows, cols);
        int baseSize = 40;
        int maxSize = 50;
        int minSize = 30;

        // 根据棋盘大小计算合适的基础尺寸
        int calculatedSize = baseSize + (minDimension / 3); // 更平滑的尺寸变化
        int squareSize = Math.max(minSize, Math.min(maxSize, calculatedSize));

        // 强制返回正方形尺寸
        return squareSize;
    }

    // 随机生成雷的位置
    // 修改后的雷生成方法
    private void generateMines() {
        Random random = new Random();
        int count = 0;
        // 记录首次点击的位置
        boolean firstClick = true;
        int firstRow = -1, firstCol = -1;

        // 初始化所有单元格
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = new Cell(false);
            }
        }

        while (count < numMines) {
            int row = random.nextInt(rows);
            int col = random.nextInt(cols);

            // 首次点击时记录位置
            if (firstClick) {
                firstRow = row;
                firstCol = col;
                firstClick = false;
            }

            // 确保不在首次点击位置及其周围生成雷
            if (board[row][col].hasMine() ||
                    (row >= firstRow - 1 && row <= firstRow + 1 &&
                            col >= firstCol - 1 && col <= firstCol + 1)) {
                continue;
            }

            board[row][col] = new Cell(true);
            count++;
        }
    }

    // 计算每个单元格周围的雷数
    private void calculateAdjacentMines() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int count = 0;
                for (int x = Math.max(0, i - 1); x <= Math.min(rows - 1, i + 1); x++) {
                    for (int y = Math.max(0, j - 1); y <= Math.min(cols - 1, j + 1); y++) {
                        if (board[x][y].hasMine()) {
                            count++;
                        }
                    }
                }
                board[i][j].setAdjacentMines(count);
            }
        }
    }

    // 处理鼠标左键点击事件
    // 修改leftClick方法
    private void leftClick(int row, int col) {
        playSound("leftclick.wav");
        if (board[row][col].isMarked()) {
            return;
        }
        if (board[row][col].hasMine()) {
            gameOver(false);
        } else {
            revealCell(row, col);
            // 只设置图标，不设置文本
            buttons[row][col].setIcon(Image.getNumberImage(board[row][col].getAdjacentMines()));
            buttons[row][col].setText(""); // 清空文本显示

            if (isGameWon()) {
                gameOver(true);
            }
        }
    }

    // 修改revealCell方法，确保不设置文本

    // 处理鼠标右键点击事件
    // 改进的右键点击处理
    private void rightClick(int row, int col) {
        playSound("rightclick.wav");
        if (board[row][col].isClicked()) {
            // 已点击的单元格右键三次点击显示提示
            if (board[row][col].getAdjacentMines() > 0 && !board[row][col].isMarked()) {
                toggleHint(row, col);
            }
            return;
        }

        // 未点击单元格的标记逻辑
        if (board[row][col].isMarked()) {
            board[row][col].setMarked(false);
            buttons[row][col].setIcon(null);
            minesLeft++;
            minesLeftLabel.setText("Mines left: " + minesLeft);
        } else {
            if (minesLeft > 0) {
                board[row][col].setMarked(true);
                buttons[row][col].setIcon(Image.getImage(Image.MARKED));
                minesLeft--;
                minesLeftLabel.setText("Mines left: " + minesLeft);
            }
        }
    }

    // 显示/隐藏提示功能
    private void toggleHint(int row, int col) {
        int adjacentMarked = 0;
        int requiredMarked = board[row][col].getAdjacentMines();

        // 计算周围已标记的数量
        for (int x = Math.max(0, row - 1); x <= Math.min(rows - 1, row + 1); x++) {
            for (int y = Math.max(0, col - 1); y <= Math.min(cols - 1, col + 1); y++) {
                if (board[x][y].isMarked()) {
                    adjacentMarked++;
                }
            }
        }

        // 如果标记数量等于周围雷数，自动打开周围未标记的单元格
        if (adjacentMarked == requiredMarked) {
            for (int x = Math.max(0, row - 1); x <= Math.min(rows - 1, row + 1); x++) {
                for (int y = Math.max(0, col - 1); y <= Math.min(cols - 1, col + 1); y++) {
                    if (!board[x][y].isMarked() && !board[x][y].isClicked()) {
                        leftClick(x, y);
                    }
                }
            }
        }
    }

    // 揭示单元格
    /* private void revealCell(int row, int col) {
         if (row < 0 || row >= rows || col < 0 || col >= cols || board[row][col].isClicked() || board[row][col].isMarked()) {
             return;
         }
         board[row][col].setClicked(true);
         if (board[row][col].getAdjacentMines() > 0) {
             buttons[row][col].setText(String.valueOf(board[row][col].getAdjacentMines()));
             buttons[row][col].setBackground(Color.WHITE);
         } else {
             buttons[row][col].setBackground(Color.WHITE);
             for (int x = Math.max(0, row - 1); x <= Math.min(rows - 1, row + 1); x++) {
                 for (int y = Math.max(0, col - 1); y <= Math.min(cols - 1, col + 1); y++) {
                     if (x != row || y != col) {
                         revealCell(x, y);
                     }
                 }
             }
         }
     }*/
    // 修改revealCell方法
    private void revealCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols ||
                board[row][col].isClicked() || board[row][col].isMarked()) {
            return;
        }
        board[row][col].setClicked(true);

        if (board[row][col].getAdjacentMines() > 0) {
            // 有数字的单元格显示数字图标
            buttons[row][col].setIcon(Image.getNumberImage(board[row][col].getAdjacentMines()));
            buttons[row][col].setText(""); // 清空文本
        } else {
            // 空白单元格显示空白图片
            buttons[row][col].setIcon(Image.getImage(Image.EMPTY));
            buttons[row][col].setText(""); // 清空文本

            // 递归揭露周围单元格
            for (int x = Math.max(0, row - 1); x <= Math.min(rows - 1, row + 1); x++) {
                for (int y = Math.max(0, col - 1); y <= Math.min(cols - 1, col + 1); y++) {
                    revealCell(x, y); // 直接递归，不再排除自身
                }
            }
        }

        // 统一设置背景和样式
        buttons[row][col].setBackground(null);
        buttons[row][col].setOpaque(false);
        buttons[row][col].setContentAreaFilled(false);
    }

    // 判断游戏是否胜利
    private boolean isGameWon() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!board[i][j].hasMine() && !board[i][j].isClicked()) {
                    return false;
                }
            }
        }
        return true;
    }

    // 游戏结束处理
    private void gameOver(boolean won) {
        String message = won ? "You won!" : "Game Over!";
        JOptionPane.showMessageDialog(frame, message);
        // 游戏结束后显示所有雷的位置
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j].hasMine()) {
                    // buttons[i][j].setText("G");
                    buttons[i][j].setIcon(Image.getImage(Image.MINE));
                }
            }
        }
        // 播放音效
        playSound(won ? "win.wav" : "lose.wav");
        // playSound("E:\\study\\Javawork\\Minesweeper\\lose.wav");

        System.out.println("当前工作目录: " + System.getProperty("user.dir"));

    }

    // 播放音效的方法
    private void playSound(String soundFile) {
        try {
            File file = new File(soundFile);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                minesweeper1.getInstance();
            }
        });
    }
}
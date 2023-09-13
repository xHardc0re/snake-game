import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {
    private static final int UNIT_SIZE = 25;
    private static final int GAME_UNITS = (1920 * 1080) / (UNIT_SIZE * UNIT_SIZE); // Full HD resolution
    private static final int DELAY = 75;

    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];
    private int bodyParts = 6;
    private int applesEaten;
    private int appleX;
    private int appleY;
    private char direction = 'R';
    private boolean running = false;
    private boolean gameOver = false;

    private final Random random = new Random();
    private Timer timer;

    public SnakeGame() {
        setPreferredSize(new Dimension(1920, 1080)); // Full-screen resolution
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());

        startGame();
    }

    private void startGame() {
        initializeGame();
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void initializeGame() {
        x[0] = 10 * UNIT_SIZE;
        y[0] = 10 * UNIT_SIZE;
        spawnApple();
        running = true;
        gameOver = false;
        applesEaten = 0;
    }

    private void spawnApple() {
        int randomX = random.nextInt((1920 / UNIT_SIZE)) * UNIT_SIZE;
        int randomY = random.nextInt((1080 / UNIT_SIZE)) * UNIT_SIZE;
        appleX = randomX;
        appleY = randomY;
    }

    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] -= UNIT_SIZE;
                break;
            case 'D':
                y[0] += UNIT_SIZE;
                break;
            case 'L':
                x[0] -= UNIT_SIZE;
                break;
            case 'R':
                x[0] += UNIT_SIZE;
                break;
        }
    }

    private void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            spawnApple();
        }
    }

    private void checkCollisions() {
        // Check for collisions with walls
        if (x[0] < 0 || x[0] >= 1920 || y[0] < 0 || y[0] >= 1080) {
            running = false;
            gameOver = true;
            timer.stop();
            return;
        }

        // Check for collisions with itself
        for (int i = 1; i < bodyParts; i++) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
                gameOver = true;
                timer.stop();
                return;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (running) {
            // Draw apple
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Draw snake body
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(new Color(45, 180, 0)); // Dark green for the rest of the body
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            // Display score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            g.drawString("Score: " + applesEaten, 10, 30);
        } else if (gameOver) {
            // Display "Game Over" message
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("Game Over", 860, 540);

            // Display final score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            g.drawString("Score: " + applesEaten, 930, 600);
            g.drawString("Press SPACE to Restart", 800, 660);
        }
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_SPACE:
                    if (!running || gameOver) {
                        startGame();
                    }
                    break;
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame snakeGame = new SnakeGame();
        frame.add(snakeGame);
        frame.setUndecorated(true); // Full-screen
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
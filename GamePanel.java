import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int UNIT_SIZE = 20;
    private final int GAME_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);

    private int[] snakeX = new int[GAME_UNITS];
    private int[] snakeY = new int[GAME_UNITS];
    private int snakeLength;
    private int foodX;
    private int foodY;
    private char direction;
    private boolean running;
    private boolean paused; // New variable to track pause state
    private Timer timer;
    private JButton restartButton;
    private JButton colorButton; // Button for color selection
    private JButton startButton; // Start button
    private Color snakeColor; // Color for the snake

    // Game state
    private enum GameState {
        WELCOME, PLAYING, GAME_OVER
    }

    private GameState gameState; // Current state of the game

    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT + UNIT_SIZE));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (direction != 'D') direction = 'U';
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 'U') direction = 'D';
                        break;
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R') direction = 'L';
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L') direction = 'R';
                        break;
                    case KeyEvent.VK_P: // Pause functionality
                        paused = !paused; // Toggle pause state
                        break;
                }
            }
        });

        // Initialize snake color (default)
        snakeColor = Color.GREEN;

        // Button for selecting snake color
        colorButton = new JButton("Choose Snake Color");
        colorButton.setBounds(WIDTH / 2 - 100, HEIGHT / 2 - 30, 200, 30);
        colorButton.setFocusable(false);
        colorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseSnakeColor();
            }
        });
        this.setLayout(null);
        this.add(colorButton);
        colorButton.setVisible(false); // Hide button by default

        // Initialize and set up the restart button
        restartButton = new JButton("Restart");
        restartButton.setBounds(WIDTH / 2 - 50, HEIGHT / 2 + 30, 100, 30);
        restartButton.setFocusable(false);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        restartButton.setVisible(false); // Hide button initially
        this.add(restartButton);

        // Initialize and set up the start button
        startButton = new JButton("Start Game");
        startButton.setBounds(WIDTH / 2 - 100, HEIGHT / 2 - 80, 200, 30);
        startButton.setFocusable(false);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        this.add(startButton);

        // Set initial game state to WELCOME
        gameState = GameState.WELCOME;
        displayWelcomeScreen();
    }

    // Method to open the color chooser dialog
    private void chooseSnakeColor() {
        snakeColor = JColorChooser.showDialog(this, "Choose Snake Color", snakeColor);
        if (snakeColor == null) {
            snakeColor = Color.GREEN; // Default color if no selection is made
        }
    }

    public void displayWelcomeScreen() {
        running = false;
        paused = false;
        restartButton.setVisible(false); // Hide restart button on welcome screen
        colorButton.setVisible(true); // Show color button on welcome screen
        startButton.setVisible(true); // Show start button on welcome screen
        repaint(); // Refresh to show the welcome screen
    }

    public void startGame() {
        gameState = GameState.PLAYING; // Set game state to PLAYING
        running = true;
        paused = false; // Ensure paused state is reset
        snakeLength = 1;
        direction = 'R';
        spawnFood();
        timer = new Timer(100, this);
        timer.start();
        restartButton.setVisible(false); // Hide restart button during the game
        colorButton.setVisible(false); // Hide color button during the game
        startButton.setVisible(false); // Hide start button during the game
        repaint();
    }

    private void spawnFood() {
        boolean spawnValid = false;
        Random random = new Random();
    
        while (!spawnValid) {
            // Adjusted to prevent spawning on the edge by subtracting UNIT_SIZE from maximum value
            foodX = random.nextInt((WIDTH / UNIT_SIZE - 1)) * UNIT_SIZE;
            foodY = random.nextInt((HEIGHT / UNIT_SIZE - 1)) * UNIT_SIZE;
    
            // Check if food spawns on the snake
            spawnValid = true;
            for (int i = 0; i < snakeLength; i++) {
                if (snakeX[i] == foodX && snakeY[i] == foodY) {
                    spawnValid = false; // Food is on the snake, need to regenerate
                    break;
                }
            }
        }
    }
    
    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        switch (gameState) {
            case WELCOME:
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.PLAIN, 40));
                String welcomeText = "Welcome to Tehj's Snake Game";
                int welcomeTextWidth = g.getFontMetrics().stringWidth(welcomeText);
                g.drawString(welcomeText, (WIDTH - welcomeTextWidth) / 2, HEIGHT / 2 - 100); // Centered
    
                g.setFont(new Font("Arial", Font.PLAIN, 20));
                String chooseColorText = "Choose snake color and press Start!";
                int chooseColorTextWidth = g.getFontMetrics().stringWidth(chooseColorText);
                g.drawString(chooseColorText, (WIDTH - chooseColorTextWidth) / 2, HEIGHT / 2 + 30); // Centered and moved lower
                break;
            case PLAYING:
                if (paused) {
                    g.setColor(Color.YELLOW);
                    g.setFont(new Font("Arial", Font.PLAIN, 40));
                    g.drawString("Paused", WIDTH / 3, HEIGHT / 3);
                } else {
                    g.setColor(Color.RED);
                    g.fillRect(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

                    for (int i = 0; i < snakeLength; i++) {
                        if (i == 0) {
                            g.setColor(snakeColor); // Head of the snake
                        } else {
                            g.setColor(Color.LIGHT_GRAY); // Body of the snake
                        }
                        g.fillRect(snakeX[i], snakeY[i], UNIT_SIZE, UNIT_SIZE);
                    }

                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.PLAIN, 40));
                    g.drawString("Score: " + (snakeLength - 1), WIDTH - 200, 40);
                }
                break;
            case GAME_OVER:
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.PLAIN, 40));
                g.drawString("Game Over!", WIDTH / 3, HEIGHT / 3);
                g.setFont(new Font("Arial", Font.PLAIN, 20));
                g.drawString("Score: " + (snakeLength - 1), WIDTH / 2 - 40, HEIGHT / 2);
                restartButton.setVisible(true); // Show restart button on game over
                colorButton.setVisible(true); // Show color button on game over
                break;
        }
    }

    private void move() {
        for (int i = snakeLength; i > 0; i--) {
            snakeX[i] = snakeX[i - 1];
            snakeY[i] = snakeY[i - 1];
        }

        switch (direction) {
            case 'U':
                snakeY[0] = snakeY[0] - UNIT_SIZE;
                break;
            case 'D':
                snakeY[0] = snakeY[0] + UNIT_SIZE;
                break;
            case 'L':
                snakeX[0] = snakeX[0] - UNIT_SIZE;
                break;
            case 'R':
                snakeX[0] = snakeX[0] + UNIT_SIZE;
                break;
        }
    }

    private void checkFood() {
        if (snakeX[0] == foodX && snakeY[0] == foodY) {
            snakeLength++;
            spawnFood();
        }
    }

    private void checkCollision() {
        // Check if head collides with body
        for (int i = snakeLength; i > 0; i--) {
            if (snakeX[0] == snakeX[i] && snakeY[0] == snakeY[i]) {
                gameState = GameState.GAME_OVER; // Update game state to GAME_OVER
                running = false;
            }
        }

        // Check if head collides with walls
        if (snakeX[0] < 0 || snakeX[0] >= WIDTH || snakeY[0] < 0 || snakeY[0] >= HEIGHT) {
            gameState = GameState.GAME_OVER; // Update game state to GAME_OVER
            running = false;
        }

        if (!running) {
            timer.stop(); // Stop the timer if the game is over
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            move();
            checkFood();
            checkCollision();
        }
        repaint();
    }

    private void restartGame() {
        snakeLength = 1;
        for (int i = 0; i < GAME_UNITS; i++) {
            snakeX[i] = 0;
            snakeY[i] = 0;
        }
        direction = 'R';
        running = true;
        paused = false; // Reset paused state
        gameState = GameState.WELCOME; // Reset game state to WELCOME
        spawnFood();
        timer.start(); // Restart the timer
        restartButton.setVisible(false); // Hide restart button after restart
        colorButton.setVisible(false); // Hide color button after restart
        startButton.setVisible(true); // Show start button after restart
        displayWelcomeScreen(); // Show welcome screen again
        repaint();
    }
}

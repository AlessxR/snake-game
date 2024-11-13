import java.awt.*;
import java.util.Objects;
import java.util.Random;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

class SnakeGameLogic extends JPanel implements ActionListener, KeyListener {
    int boardWidth;
    int boardHeight;
    int tileSize = 32; // Tail size

    // Snake details
    Move snakeHead;
    ArrayList<Move> foodList;
    Random random;
    Timer loop;
    int veloX;
    int veloY;
    ArrayList<Move> body;
    boolean over = false;
    boolean gameStarted = false;
    private final int initialBodySize = 3;
    private int score;
    protected JLabel instructionLabel;

    // Images a tail on snake
    private final Image headRightImage, headLeftImage, headUpImage, headDownImage;
    private final Image bodyHorizontalImage, bodyVerticalImage;
    private final Image bodyTopLeftImage, bodyTopRightImage, bodyBottomLeftImage, bodyBottomRightImage;
    private final Image tailRightImage, tailLeftImage, tailUpImage, tailDownImage;
    private final Image appleImage;

    private final int wallHeight = 2; // Height of the wall in tiles


    // Class for move
    private class Move {
        int x, y;

        Move(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // Constructor of food

    SnakeGameLogic(int boardWidth, int boardHeight, int gameSpeed) {
        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.yellow);
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Move(5, wallHeight + 1); // Start below the wall

        // A start tail snake's
        body = new ArrayList<>();
        for (int i = 1; i <= initialBodySize; i++) {
            body.add(new Move(snakeHead.x - i, snakeHead.y));
        }

        // A start food
        foodList = new ArrayList<>();
        foodList.add(new Move(10, wallHeight + 1));

        random = new Random();

        veloX = 1;
        veloY = 0;

        loop = new Timer(gameSpeed, this);

        score = 0;

        // Tail snakes.
        appleImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/apple_00.png"))).getImage();
        headRightImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/snake-head/head_right.png"))).getImage();
        headLeftImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/snake-head/head_left.png"))).getImage();
        headUpImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/snake-head/head_up.png"))).getImage();
        headDownImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/snake-head/head_down.png"))).getImage();

        bodyHorizontalImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/snake-body/body_horizontal.png"))).getImage();
        bodyVerticalImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/snake-body/body_vertical.png"))).getImage();
        bodyTopLeftImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/snake-body/body_topleft.png"))).getImage();
        bodyTopRightImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/snake-body/body_topright.png"))).getImage();
        bodyBottomLeftImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/snake-body/body_bottomleft.png"))).getImage();
        bodyBottomRightImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/snake-body/body_bottomright.png"))).getImage();

        tailRightImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/snake-tail/tail_right.png"))).getImage();
        tailLeftImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/snake-tail/tail_left.png"))).getImage();
        tailUpImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/snake-tail/tail_up.png"))).getImage();
        tailDownImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/snake-tail/tail_down.png"))).getImage();

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/keys.png")));
        instructionLabel = new JLabel(icon) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Center img
                int imgWidth = icon.getIconWidth();
                int imgHeight = icon.getIconHeight();
                int x = (getWidth() - imgWidth) / 2;
                int y = (getHeight() - imgHeight) / 2;

                // Size and shadow add
                int shadowOffset = 5;
                int shadowSize = 30;

                // Draw shadow
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2d.setColor(Color.BLACK);
                g2d.fillRoundRect(x + shadowOffset - shadowSize / 2, y + shadowOffset - shadowSize / 2, imgWidth + shadowSize, imgHeight + shadowSize, 10 + shadowSize, 10 + shadowSize);
                g2d.dispose();

                // Draw img
                g.drawImage(icon.getImage(), x, y, this);
            }
        };
        setLayout(new BorderLayout());
        add(instructionLabel, BorderLayout.CENTER);
    }

    public void startGame() {
        if (!loop.isRunning() && gameStarted) {
            over = false;
            snakeHead = new Move(5, wallHeight + 1); // Start below the wall
            body.clear();

            for (int i = 1; i <= initialBodySize; i++) {
                body.add(new Move(snakeHead.x - i, snakeHead.y));
            }

            veloX = 1;
            veloY = 0;
            score = 0;
            foodList.clear();
            foodList.add(new Move(10, wallHeight + 1));
            loop.start();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!over) {
            move();
            repaint();
        } else {
            loop.stop();
            gui_game.showGameOverDialog(this, score);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Start with presses the arrows
        if (!gameStarted && (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN || key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT)) {
            gameStarted = true;
            remove(instructionLabel);
            revalidate();
            repaint();
            startGame();
        }

        // Control snake.
        if (key == KeyEvent.VK_UP && veloY != 1 && !(snakeHead.x == 5 && snakeHead.y == wallHeight + 2)) {
            veloX = 0;
            veloY = -1;
        } else if (key == KeyEvent.VK_DOWN && veloY != -1 && !(snakeHead.x == 5 && snakeHead.y == wallHeight)) {
            veloX = 0;
            veloY = 1;
        } else if (key == KeyEvent.VK_LEFT && veloX != 1 && !(snakeHead.x == 6 && snakeHead.y == wallHeight + 1)) {
            veloX = -1;
            veloY = 0;
        } else if (key == KeyEvent.VK_RIGHT && veloX != -1) {
            veloX = 1;
            veloY = 0;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the grid background
        drawGrid(g);

        // Draw the wall
        g.setColor(new Color(0, 100, 0)); // Very dark green color
        for (int i = 0; i < boardWidth / tileSize; i++) {
            g.fillRect(i * tileSize, 0, tileSize, wallHeight * tileSize);
        }

        // Draw the icons.body.apples using the loaded image
        if (appleImage != null) {
            for (Move food : foodList) {
                g.drawImage(appleImage, food.x * tileSize, food.y * tileSize, tileSize, tileSize, this);
            }
        } else {
            g.setColor(Color.RED);
            for (Move food : foodList) {
                g.fillOval(food.x * tileSize, food.y * tileSize, tileSize, tileSize);
            }
        }

        // Draw the snake with images
        drawSnake(g);

        // Draw the border
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, boardWidth - 1, boardHeight - 1);

        // Draw the score with scaled apple image
        if (appleImage != null) {
            int appleSize = 30; // Fixed size for the apple image
            int x = (boardWidth - appleSize) / 2 - 40; // Adjust x for centering
            int y = (wallHeight * tileSize) / 2 - appleSize / 2;

            g.drawImage(appleImage, x, y, appleSize, appleSize, this);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.setColor(Color.WHITE);
            g.drawString(" x " + score, x + appleSize + 5, y + appleSize / 2 + 10);
        } else {
            // Draw the score text with background and shadow
            String scoreText = "Score: " + score;
            g.setFont(new Font("Arial", Font.BOLD, 24));
            FontMetrics fm = g.getFontMetrics();

            int textWidth = fm.stringWidth(scoreText);
            int textHeight = fm.getHeight();
            int x = (boardWidth - textWidth) / 2;
            int y = (wallHeight * tileSize) / 2 + textHeight / 2;

            // Darker background rectangle for the score text
            g.setColor(new Color(0, 0, 0, 150)); // Darker semi-transparent black
            g.fillRoundRect(x - 10, y - textHeight + 5, textWidth + 20, textHeight, 10, 10);

            // Shadow for the score text
            g.setColor(Color.DARK_GRAY);
            g.drawString(scoreText, x + 2, y + 2);

            // Score text
            g.setColor(Color.WHITE);
            g.drawString(scoreText, x, y);
        }
    }

    private void drawSnake(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw the head using the drawRotatedImage method
        drawRotatedImage(g2d, headRightImage, snakeHead.x * tileSize, snakeHead.y * tileSize, getHeadRotationAngle());

        // Draw the icons.body
        for (int i = 0; i < body.size(); i++) {
            Move part = body.get(i);
            if (i == body.size() - 1) {
                // Last icons.body part (tail)
                g2d.drawImage(getTailImage(), part.x * tileSize, part.y * tileSize, tileSize, tileSize, this);
            } else {
                // Middle icons.body parts
                g2d.drawImage(getBodyImage(i), part.x * tileSize, part.y * tileSize, tileSize, tileSize, this);
            }
        }
    }

    private double getHeadRotationAngle() {
        if (veloX == 1) return 0; // Right
        if (veloX == -1) return Math.PI; // Left
        if (veloY == 1) return Math.PI / 2; // Down
        if (veloY == -1) return -Math.PI / 2; // Up
        return 0; // Default
    }


    private Image getBodyImage(int index) {
        Move current = body.get(index);
        Move previous = index == 0 ? snakeHead : body.get(index - 1);
        Move next = body.get(index + 1);

        if (current.x == previous.x && current.x == next.x) {
            return bodyVerticalImage; // Vertical icons.body segment
        } else if (current.y == previous.y && current.y == next.y) {
            return bodyHorizontalImage; // Horizontal icons.body segment
        } else {
            // Determine corner type
            if ((previous.x < current.x && next.y < current.y) || (next.x < current.x && previous.y < current.y)) {
                return bodyTopLeftImage; // Corner from top to left
            } else if ((previous.x < current.x && next.y > current.y) || (next.x < current.x && previous.y > current.y)) {
                return bodyBottomLeftImage; // Corner from bottom to left
            } else if ((previous.x > current.x && next.y < current.y) || (next.x > current.x && previous.y < current.y)) {
                return bodyTopRightImage; // Corner from top to right
            } else if ((previous.x > current.x && next.y > current.y) || (next.x > current.x && previous.y > current.y)) {
                return bodyBottomRightImage; // Corner from bottom to right
            }
        }
        return bodyHorizontalImage; // Default
    }

    private Image getTailImage() {
        Move tail = body.get(body.size() - 1);
        Move beforeTail = body.get(body.size() - 2);
        if (tail.x > beforeTail.x) return tailRightImage;
        if (tail.x < beforeTail.x) return tailLeftImage;
        if (tail.y > beforeTail.y) return tailDownImage;
        if (tail.y < beforeTail.y) return tailUpImage;
        return tailRightImage; // Default
    }

    private void drawGrid(Graphics g) {
        int boardHeight = getHeight();
        Color lightGreen = new Color(170, 215, 81);  // Light green color
        Color darkGreen = new Color(162, 209, 73);  // Dark green color

        for (int y = wallHeight * tileSize; y < boardHeight; y += tileSize) {
            for (int x = 0; x < boardWidth; x += tileSize) {
                if ((x / tileSize + y / tileSize) % 2 == 0) {
                    g.setColor(lightGreen);
                } else {
                    g.setColor(darkGreen);
                }
                g.fillRect(x, y, tileSize, tileSize);
            }
        }
    }

    public void drawRotatedImage(Graphics2D g2d, Image image, int x, int y, double angle) {
        g2d.translate(x + tileSize / 2, y + tileSize / 2);
        g2d.rotate(angle);
        g2d.drawImage(image, -tileSize / 2, -tileSize / 2, tileSize, tileSize, this);
        g2d.rotate(-angle);
        g2d.translate(-(x + tileSize / 2), -(y + tileSize / 2));
    }

    public void stopGame() {
        if (loop.isRunning()) {
            loop.stop();
        }
    }

    private void move() {
        if (!gameStarted) return;

        Move nextMove = new Move(snakeHead.x + veloX, snakeHead.y + veloY);

        if (nextMove.y < wallHeight || nextMove.x < 0 || nextMove.x >= boardWidth / tileSize || nextMove.y >= boardHeight / tileSize) {
            over = true;
        } else if (body.stream().anyMatch(m -> m.x == nextMove.x && m.y == nextMove.y)) {
            over = true;
        } else {
            body.add(0, snakeHead);
            snakeHead = nextMove;

            boolean ateFood = false;
            for (int i = 0; i < foodList.size(); i++) {
                if (snakeHead.x == foodList.get(i).x && snakeHead.y == foodList.get(i).y) {
                    score++;
                    foodList.remove(i);
                    ateFood = true;
                    break;
                }
            }

            if (ateFood) {
                if (foodList.isEmpty()) {
                    placeFood();
                }
            } else {
                body.remove(body.size() - 1);
            }
        }
    }

    private void placeFood() {
        int applesToSpawn = random.nextInt(4) + 1;
        for (int i = 0; i < applesToSpawn; i++) {
            boolean foodInSnake;
            int x, y;

            do {
                x = random.nextInt(boardWidth / tileSize);
                y = random.nextInt((boardHeight - wallHeight * tileSize) / tileSize) + wallHeight; // Ensure food is placed below the wall
                foodInSnake = false;

                // Check if the new food position is in the snakes head
                if (snakeHead.x == x && snakeHead.y == y) {
                    foodInSnake = true;
                } else {
                    // Check if the new food position is in the snakes body
                    for (Move part : body) {
                        if (part.x == x && part.y == y) {
                            foodInSnake = true;
                            break;
                        }
                    }
                }
            } while (foodInSnake); // Repeat until a valid position is found

            foodList.add(new Move(x, y));
        }
    }

    public void resetGame() {
        over = false;
        snakeHead = new Move(5, wallHeight + 1); // Start below the wall
        body.clear();

        for (int i = 1; i <= initialBodySize; i++) {
            body.add(new Move(snakeHead.x - i, snakeHead.y));
        }

        veloX = 1;
        veloY = 0;
        score = 0;
        foodList.clear();
        foodList.add(new Move(10, wallHeight + 1));
        loop.start();
    }
}
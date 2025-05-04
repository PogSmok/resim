import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Random;

class AnimatedPanel extends JPanel {
    private final int SEGMENT_SIZE = 10;
    private final int INITIAL_LENGTH = 5;
    private final int NUM_SNAKES = 10;  // Change this to the number of snakes
    private Timer timer;
    private final LinkedList<Snake> snakes;

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private class Snake {
        LinkedList<Point> body;
        Direction direction;

        Snake(int startX, int startY) {
            body = new LinkedList<>();
            for (int i = 0; i < INITIAL_LENGTH; i++) {
                body.add(new Point(startX - i * SEGMENT_SIZE, startY));
            }
            direction = Direction.RIGHT;  // Initial direction
        }

        void move() {
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            Point head = body.getFirst();
            Point newHead = new Point(head);

            switch (direction) {
                case UP -> newHead.translate(0, -SEGMENT_SIZE);
                case DOWN -> newHead.translate(0, SEGMENT_SIZE);
                case LEFT -> newHead.translate(-SEGMENT_SIZE, 0);
                case RIGHT -> newHead.translate(SEGMENT_SIZE, 0);
            }

            // Wrap around screen (panel size is dynamic now)
            if (newHead.x < 0) newHead.x = panelWidth - SEGMENT_SIZE;
            if (newHead.x >= panelWidth) newHead.x = 0;
            if (newHead.y < 0) newHead.y = panelHeight - SEGMENT_SIZE;
            if (newHead.y >= panelHeight) newHead.y = 0;

            body.addFirst(newHead);
            body.removeLast();
        }

        void changeDirection(Direction newDirection) {
            // Ensure the snake doesn't move in the opposite direction
            if (newDirection == Direction.UP && direction != Direction.DOWN) direction = Direction.UP;
            else if (newDirection == Direction.DOWN && direction != Direction.UP) direction = Direction.DOWN;
            else if (newDirection == Direction.LEFT && direction != Direction.RIGHT) direction = Direction.LEFT;
            else if (newDirection == Direction.RIGHT && direction != Direction.LEFT) direction = Direction.RIGHT;
        }
    }

    public AnimatedPanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocusInWindow();

        snakes = new LinkedList<>();
        Random random = new Random();

        // Ensure the panel has a valid width and height before proceeding
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                // Initialize the snakes and the timer when the panel is resized
                if (getWidth() > 0 && getHeight() > 0) {
                    initializeSnakesAndTimer();
                }
            }
        });
    }

    // Method to initialize snakes and the timer
    private void initializeSnakesAndTimer() {
        if (timer != null) {
            return;  // Already initialized
        }

        Random random = new Random();
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Initialize multiple snakes
        for (int i = 0; i < NUM_SNAKES; i++) {
            int startX = random.nextInt(panelWidth); // Use valid panel width
            int startY = random.nextInt(panelHeight); // Use valid panel height
            snakes.add(new Snake(startX, startY));
        }

        // Initialize the timer only once
        timer = new Timer(100, e -> {
            moveSnakes();
            repaint();
        });
        timer.start();

        // Randomly change direction for each snake every 500ms
        new Timer(500, e -> randomChangeDirection()).start();
    }

    private void moveSnakes() {
        for (Snake snake : snakes) {
            snake.move();
        }
    }

    private void randomChangeDirection() {
        Random random = new Random();
        for (Snake snake : snakes) {
            // Randomly pick a direction for each snake
            Direction newDirection = Direction.values()[random.nextInt(4)];
            snake.changeDirection(newDirection);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Random random = new Random();

        // Draw each snake
        for (Snake snake : snakes) {
            // Use a random color for each snake
            g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));

            for (Point p : snake.body) {
                g.fillRect(p.x, p.y, SEGMENT_SIZE, SEGMENT_SIZE);
            }
        }
    }
}
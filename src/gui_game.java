import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import javax.swing.*;

public class gui_game {
    private static boolean gameWindowOpen = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(gui_game::createAndShowMainMenu);
    }

    private static void createAndShowMainMenu() {
        JFrame mainFrame = new JFrame("Main menu");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(500, 502);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        JPanel mainMenuPanel = createMainMenuPanel();
        mainFrame.add(mainMenuPanel);
        mainFrame.setVisible(true);
    }

    private static JPanel createMainMenuPanel() {
        JPanel mainMenuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon background = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/SnakeGameMenu.jfif")));
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        mainMenuPanel.setLayout(new GridBagLayout());
        mainMenuPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton startGameButton = createStyledButton("Start the game!");
        startGameButton.addActionListener(e -> startGame());

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainMenuPanel.add(startGameButton, gbc);

        return mainMenuPanel;
    }


    private static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 130, 180));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        button.setOpaque(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 150, 200));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
        return button;
    }

    private static void startGame() {
        if (!gameWindowOpen) {
            SnakeGameLogic game = new SnakeGameLogic(600, 600, 100);
            JFrame gameFrame = new JFrame("Snake");
            gameFrame.add(game);
            gameFrame.pack();
            gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            gameFrame.setLocationRelativeTo(null);
            gameFrame.setResizable(false);

            gameFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    game.stopGame();
                    gameWindowOpen = false;
                }
            });

            gameFrame.setVisible(true);
            gameWindowOpen = true;
        }
    }

    public static void showGameOverDialog(SnakeGameLogic game, int score) {
        Object[] options = {"Play again", "Exit"};

        JPanel messagePanel = new JPanel();
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));

        JLabel messageLabel = new JLabel("Game is over!!! Scored " + score + " score.");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel questionLabel = new JLabel("Do you want to play again?");
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        questionLabel.setForeground(Color.DARK_GRAY);
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        messagePanel.add(messageLabel);
        messagePanel.add(Box.createVerticalStrut(10));
        messagePanel.add(questionLabel);

        int choice = JOptionPane.showOptionDialog(
                null,
                messagePanel,
                "Games is over!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == JOptionPane.YES_OPTION) {
            game.resetGame();
            game.gameStarted = false;
            game.add(game.instructionLabel, BorderLayout.CENTER);
            game.revalidate();
            game.repaint();
        } else {
            SwingUtilities.getWindowAncestor(game).dispose();
            gui_game.gameWindowOpen = false;
        }
    }
}

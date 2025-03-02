import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.SwingUtilities;

public class PasswordGeneratorSwing extends JFrame {
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = "ABCDEFGHIJKLMNOPQURSTUVWXYZ";
    private static final String DIGIT = "0123456789";
    private static final String SPECIAL_CHAR = "!@#$%^&*?";
    private static final String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + DIGIT + SPECIAL_CHAR;
    private static final SecureRandom random = new SecureRandom();
    
    private JTextField lengthField;
    private JTextField usernameField;
    private JPasswordField userPasswordField;
    private JButton generateButton;
    private JButton saveButton;
    private JButton viewSavedButton;
    private JButton deleteButton; // New button for deleting a password
    private JTextArea passwordArea;
    private Timer passwordTimer;

    private final Color primaryColor = new Color(53, 59, 72);
    private final Color secondaryColor = new Color(233, 236, 239);
    private final Color accentColor = new Color(47, 54, 64);
    private final Color buttonColor = new Color(0, 184, 148);

    public PasswordGeneratorSwing() {
        this.setTitle("Password Generator");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.getContentPane().setBackground(this.primaryColor);
        
        JLabel titleLabel = new JLabel("Password Generator");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(this.secondaryColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        this.add(titleLabel, gbc);
        
        JLabel lengthLabel = new JLabel("Enter Password Length:");
        lengthLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lengthLabel.setForeground(this.secondaryColor);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        this.add(lengthLabel, gbc);
        
        this.lengthField = new JTextField(10);
        this.lengthField.setBackground(this.accentColor);
        this.lengthField.setForeground(this.secondaryColor);
        this.lengthField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 1;
        this.add(this.lengthField, gbc);
        
        JLabel passwordLabel = new JLabel("Generated Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        passwordLabel.setForeground(this.secondaryColor);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        this.add(passwordLabel, gbc);
        
        this.passwordArea = new JTextArea(2, 30);
        this.passwordArea.setEditable(false);
        this.passwordArea.setBackground(this.accentColor);
        this.passwordArea.setForeground(this.secondaryColor);
        this.passwordArea.setFont(new Font("Monospaced", Font.BOLD, 18));
        this.passwordArea.setMargin(new Insets(10, 10, 10, 10));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        this.add(this.passwordArea, gbc);
        
        this.generateButton = new JButton("Generate Password");
        this.generateButton.setBackground(this.buttonColor);
        this.generateButton.setForeground(Color.WHITE);
        this.generateButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        this.generateButton.setFocusPainted(false);
        this.generateButton.setBorderPainted(false);
        this.generateButton.setOpaque(true);
        this.generateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.generateButton.addActionListener((e) -> {
            this.generatePasswordAction();
            
            if (passwordTimer != null && passwordTimer.isRunning()) {
                passwordTimer.stop();
            }
            passwordTimer = new Timer(30000, evt -> this.generatePasswordAction());
            passwordTimer.start();
        });
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        this.add(this.generateButton, gbc);
        
        JLabel usernameLabel = new JLabel("Enter Your Username:");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        usernameLabel.setForeground(this.secondaryColor);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        this.add(usernameLabel, gbc);
        
        this.usernameField = new JTextField(15);
        this.usernameField.setBackground(this.accentColor);
        this.usernameField.setForeground(this.secondaryColor);
        this.usernameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 5;
        this.add(this.usernameField, gbc);
        
        JLabel userPasswordLabel = new JLabel("Enter Your Password:");
        userPasswordLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        userPasswordLabel.setForeground(this.secondaryColor);
        gbc.gridx = 0;
        gbc.gridy = 6;
        this.add(userPasswordLabel, gbc);
        
        this.userPasswordField = new JPasswordField(15);
        this.userPasswordField.setBackground(this.accentColor);
        this.userPasswordField.setForeground(this.secondaryColor);
        this.userPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 6;
        this.add(this.userPasswordField, gbc);
        
        this.saveButton = new JButton("Save Username & Password");
        this.saveButton.setBackground(this.buttonColor);
        this.saveButton.setForeground(Color.WHITE);
        this.saveButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        this.saveButton.setFocusPainted(false);
        this.saveButton.setBorderPainted(false);
        this.saveButton.setOpaque(true);
        this.saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.saveButton.addActionListener((e) -> this.savePasswordAction());
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        this.add(this.saveButton, gbc);
        
        this.viewSavedButton = new JButton("View Saved Passwords");
        this.viewSavedButton.setBackground(this.buttonColor);
        this.viewSavedButton.setForeground(Color.WHITE);
        this.viewSavedButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        this.viewSavedButton.setFocusPainted(false);
        this.viewSavedButton.setBorderPainted(false);
        this.viewSavedButton.setOpaque(true);
        this.viewSavedButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.viewSavedButton.addActionListener((e) -> this.viewSavedPasswordsAction());
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        this.add(this.viewSavedButton, gbc);
        
        this.deleteButton = new JButton("Delete Saved Password"); // New delete button
        this.deleteButton.setBackground(this.buttonColor);
        this.deleteButton.setForeground(Color.WHITE);
        this.deleteButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        this.deleteButton.setFocusPainted(false);
        this.deleteButton.setBorderPainted(false);
        this.deleteButton.setOpaque(true);
        this.deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.deleteButton.addActionListener((e) -> this.deletePasswordAction());
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        this.add(this.deleteButton, gbc);

        this.setVisible(true);
    }

    private void generatePasswordAction() {
        String lengthText = this.lengthField.getText();
        try {
            int length = Integer.parseInt(lengthText);
            if (length < 8) {
                this.passwordArea.setText("Length must be at least 8.");
            } else {
                String password = generatePassword(length);
                this.passwordArea.setText(password);
            }
        } catch (NumberFormatException e) {
            this.passwordArea.setText("Invalid input. Please enter a number.");
        }
    }

    private void savePasswordAction() {
        String username = this.usernameField.getText();
        char[] passwordChars = this.userPasswordField.getPassword();
        String password = new String(passwordChars);
        if (password.isEmpty()) {
            password = this.passwordArea.getText();
        }
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/password_manager", "root", "Pymapass@11");
            String query = "INSERT INTO passwords (username, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            conn.close();
            JOptionPane.showMessageDialog(this, "Password saved successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewSavedPasswordsAction() {
        String enteredUsername = JOptionPane.showInputDialog(this, "Enter username:");
        String enteredPassword = JOptionPane.showInputDialog(this, "Enter password:");

        // Check if entered credentials match default username and password
        if ("root".equals(enteredUsername) && "Pymapass@11".equals(enteredPassword)) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/password_manager", "root", "Pymapass@11");
                String query = "SELECT * FROM passwords";
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                StringBuilder savedPasswords = new StringBuilder("Saved Passwords:\n");
                while (rs.next()) {
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    savedPasswords.append("Username: ").append(username).append(", Password: ").append(password).append("\n");
                }
                this.passwordArea.setText(savedPasswords.toString());
                conn.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database connection error.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials. Access denied.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePasswordAction() {
        String username = JOptionPane.showInputDialog(this, "Enter username to delete:");
        
        if (username != null && !username.isEmpty()) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/password_manager", "root", "Pymapass@11");
                String query = "DELETE FROM passwords WHERE username = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Password deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "No password found for the entered username.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                conn.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database connection error.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String generatePassword(int length) {
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(PASSWORD_ALLOW_BASE.length());
            password.append(PASSWORD_ALLOW_BASE.charAt(index));
        }
        return password.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PasswordGeneratorSwing::new);
    }
}
package com.anonychat.main.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;

public abstract class AnonyFrame extends JFrame implements AnonyFace {
    public static final long serialVersionUID = AnonyFace.SERIAL_VERSION_UID;
    
    // Database credentials
    protected static String DATABASE_URL;
    protected static String DATABASE_USER;
    protected static String DATABASE_PASS;
    
    public AnonyFrame(String title) {
        super(title);
        CDBC();
    	setLookAndFeel();
        initializeWindow();
    }
    
    private void CDBC() {
        loadDatabaseCredentials();
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS)) {
            // Connection successful
        } catch (SQLException e) {
            showErrorAndExit("Launcher/DB Failed");
            // Connection unsuccessful
        } catch (Exception x) {
            showErrorAndExit("Launcher/DB Failed");
            // Connection unsuccessful
        }
    }
    
    private void loadDatabaseCredentials() {
        DATABASE_URL = System.getenv("DATABASE_URL");
        DATABASE_USER = System.getenv("DATABASE_USER");
        DATABASE_PASS = System.getenv("DATABASE_PASS");

        if (DATABASE_URL == null || DATABASE_USER == null || DATABASE_PASS == null) {
            throw new RuntimeException("Database credentials not set in environment");
        }
    }
    
    private void setLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void initializeWindow() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                handleWindowClosing();
            }
        });
        setSize(1080, 700);
        setLocationRelativeTo(null);
        setApplicationIcon("/gallery/icon.png");
        setResizable(false);
    }

    protected void setApplicationIcon(String path) {
        try {
            Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource(path));
            setIconImage(icon);
        } catch (Exception e) {
            System.err.println("Error setting application icon: " + e.getMessage());
        }
    }
    
    private void handleWindowClosing() {
        if (ChatServer.isServerRunning) {
            int choice = JOptionPane.showConfirmDialog(this,
                "Server is Running !!\nAre you sure you want to stop the server and exit ?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                if (ChatServer.server != null) {
                	ChatServer.stopServer();
                }
                
                dispose();
                System.exit(0);
            }
        } else {
            dispose();
            System.exit(0);
        }
    }
  
    protected JButton createButton(String text) {
	    JButton button = new JButton(text);
	    button.setFont(new Font("Arial", Font.BOLD, 18));
	    button.setForeground(new Color(200, 200, 200)); // Lighter text color for contrast
	    button.setBackground(new Color(50, 50, 50)); // Dark background color
	    button.setFocusPainted(false);
	    button.addMouseListener(new MouseAdapter() {
	        public void mouseEntered(MouseEvent evt) {
	            button.setBackground(new Color(60, 60, 60)); // Slightly lighter when hovered
	        }

	        public void mouseExited(MouseEvent evt) {
	            button.setBackground(new Color(50, 50, 50)); // Back to the original color
	        }
	    });
	    return button;
	}
    
    protected static String passwordInput(String message) {
        JPasswordField passwordField = new JPasswordField(10);  // Initialize the password field with a width of 10 characters
        passwordField.setEchoChar('*');  // Set the character used to mask the password

        // Display the password field within a JOptionPane
        Object[] messageArray = {
            message,
            passwordField
        };

        int response = JOptionPane.showConfirmDialog(null, messageArray, "Password Required", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        // Check if the user clicked OK and the password field isn't empty
        if (response == JOptionPane.OK_OPTION && passwordField.getPassword().length > 0) {
            return new String(passwordField.getPassword());  // Convert password to a String and return
        } else {
            return null;  // Return null if the user clicked cancel or entered an empty password
        }
    }
    
    protected static void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Message", JOptionPane.INFORMATION_MESSAGE);
    }
    
    protected static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    protected static void showErrorAndExit(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(-1);
    }
    
    // Security && Hashing
    protected class SecurityUtils {

        public static String generateSalt() {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            return Base64.getEncoder().encodeToString(salt);
        }

        public static String hashPassword(String password, String salt) {
            if (password == null || salt == null) {
                throw new IllegalArgumentException("Password and salt cannot be null");
            }
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.reset();
                digest.update(Base64.getDecoder().decode(salt));
                byte[] hash = digest.digest(password.getBytes());
                return Base64.getEncoder().encodeToString(hash);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("[x] Error hashing password", e);
            }
        }

        public static boolean validatePassword(String password, String salt, String expectedHash) {
            String hashed = hashPassword(password, salt);
            return hashed.equals(expectedHash);
        }
    }
}

package com.anonychat.main.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;

public class ChatJoinGUI extends AnonyFrame implements AnonyFace {
    public static final long serialVersionUID = AnonyFace.SERIAL_VERSION_UID;

	private JPanel panel;
    private JTextField txtAddress, txtServerPort, txtUsername, txtCode;
    private String currentVerificationCode;
    private JLabel lblVeriCode;
    private JButton joinButton, btnCancel;
    private JCheckBox anonymousCheckBox;
    
    protected ChatClient client;
    protected static ChatJoin join;
    protected static ChatGUI chat;
    private String username;
    private String server;
    private int port;
    private boolean isAnonymous;
    
    public ChatJoinGUI() {
        super("ChatJoin");
        initializeComponents();
        setupActions();
    }

    @Override
	public void initializeComponents() {
    	panel = new JPanel() {
			private static final long serialVersionUID = AnonyFace.SERIAL_VERSION_UID;
			private Image backgroundImage = new ImageIcon(getClass().getResource("/gallery/StartJoinBackground.jpg")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(null);
        setContentPane(panel);
        
        JLabel lblAddress = new JLabel("SERVER ADDRESS");
        lblAddress.setForeground(Color.WHITE);
        lblAddress.setFont(new Font("Tahoma", Font.ITALIC, 17));
        lblAddress.setBounds(414, 132, 151, 25);
        panel.add(lblAddress);
        
        txtAddress  = new JTextField();
        txtAddress.setColumns(10);
        txtAddress.setBounds(414, 167, 241, 25);
        panel.add(txtAddress);
        
        JLabel lblServerPort = new JLabel("SERVER PORT");
        lblServerPort.setForeground(Color.WHITE);
        lblServerPort.setFont(new Font("Tahoma", Font.ITALIC, 17));
        lblServerPort.setBounds(414, 204, 135, 25);
        panel.add(lblServerPort);
        
        txtServerPort = new JTextField();
        txtServerPort.setColumns(10);
        txtServerPort.setBounds(414, 239, 241, 25);
        panel.add(txtServerPort);
        
        JLabel lblUsername = new JLabel("USERNAME");
        lblUsername.setForeground(Color.WHITE);
        lblUsername.setFont(new Font("Tahoma", Font.ITALIC, 17));
        lblUsername.setBounds(414, 276, 151, 25);
        panel.add(lblUsername);
        
        txtUsername = new JTextField();
        txtAddress.setColumns(20);
        txtUsername.setBounds(414, 309, 241, 25);
        panel.add(txtUsername);
        
        anonymousCheckBox = new JCheckBox("Join Anonymously");
        anonymousCheckBox .setForeground(Color.WHITE);
        anonymousCheckBox .setBackground(Color.WHITE);
        anonymousCheckBox .setFont(new Font("Tahoma", Font.PLAIN, 17));
        anonymousCheckBox .setBounds(460, 519, 160, 32);
        panel.add(anonymousCheckBox );
        
        // Generate a random verification code
        currentVerificationCode = generateRandomCode();
        
        // Random Code Display Label
        lblVeriCode = new JLabel("Verification Code: " + currentVerificationCode);
        lblVeriCode.setForeground(Color.WHITE);
        lblVeriCode.setFont(new Font("Tahoma", Font.ITALIC, 17));
        lblVeriCode.setBounds(414, 375, 241, 25);
        panel.add(lblVeriCode);
        
        txtCode = new JTextField();
        txtCode.setColumns(10);
        txtCode.setBounds(414, 410, 241, 25);
        panel.add(txtCode);
        
        JLabel lblServerConfig = new JLabel("JOIN SERVER");
        lblServerConfig.setForeground(Color.WHITE);
        lblServerConfig.setHorizontalAlignment(SwingConstants.CENTER);
        lblServerConfig.setFont(new Font("Tahoma", Font.ITALIC, 37));
        lblServerConfig.setBounds(203, 20, 662, 37);
        panel.add(lblServerConfig);
        
        // Checker
        joinButton = new JButton("Join");
        joinButton.setBackground(Color.WHITE);
        joinButton.setFont(new Font("Yu Gothic UI", Font.PLAIN, 15));
        joinButton.setBounds(460, 565, 160, 37);
        panel.add(joinButton);
        
        // Home
        btnCancel = new JButton("");
        ImageIcon backIcon = new ImageIcon(getClass().getResource("/gallery/home.png"));
        Image backimg = backIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        btnCancel.setIcon(new ImageIcon(backimg));
        btnCancel.setBorderPainted(false);
        btnCancel.setContentAreaFilled(false);
        btnCancel.setFocusPainted(false);
        btnCancel.setOpaque(false);
        btnCancel.setBounds(965, 29, 85, 46);
        panel.add(btnCancel);
    }
    
    @Override
    public void setupActions() {
        joinButton.addActionListener(e -> {
            try {
            	joinChat();
            } catch (IOException | NumberFormatException e1) {
                e1.printStackTrace();
            }
        });
    	
    	btnCancel.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            dispose(); // Close the current frame
	            new MainGUI().setVisible(true); // Open the Home frame
	        }
	    });
    }
    
    private String generateRandomCode() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number); // Ensures the code is a 6-digit number
    }
    
    private boolean validateVerificationCode() {
        // Check if the entered code matches the displayed code
        return txtCode.getText().equals(currentVerificationCode);
    }

    private void joinChat() throws IOException {
    	server = txtAddress.getText();
    	try {
    		port = Integer.parseInt(txtServerPort.getText()); // Next validating
    	} catch (NumberFormatException e) {/*ignore*/}
        username = txtUsername.getText();
        isAnonymous = anonymousCheckBox.isSelected();
        
    	if (!validateInput()) {
        	emptyForAll();
            return;
        }
        
        client = new ChatClient();
        join = new ChatJoin(username, server, port, client, isAnonymous);

        if (join.serverReq()) {
            ChatClient joinClient = null;  // Initialize with null to ensure we're getting a new assignment
            if (join.isRoot) {
                joinClient = new Root(join.jusername, server, port, client, isAnonymous);
            } else {
                joinClient = new User(join.jusername, server, port, client, isAnonymous);
            }

            chat = new ChatGUI(joinClient);
            this.dispose();
            chat.setVisible(true);
        } else {
        	emptyForAll();
            showError("Login Failed.");
        }
    }

    private void emptyForAll() {
    	txtAddress.setText("");
        txtServerPort.setText("");
        txtUsername.setText("");
        txtCode.setText("");
        generateRandomCode();
    }
    
    @Override
    public boolean validateInput() {
        // Validate Server Address
        if (server.isEmpty()) {
            showError("Empty Server Address !!");
            return false;
        } else if (server.length() < 2 || server.length() > 50) {
            showError("Server address must be between 2 and 50 characters.");
            return false;
        }

        // Validate Port
        try {
            port = Integer.parseInt(txtServerPort.getText());
        } catch (NumberFormatException e) {
            showError("Port must be a number");
            return false;
        }

        if (port <= 1024 || port > 65535) {
            showError("Port must be between 1024 and 65535.");
            return false;
        }
        
        // Validate Username
        if (username.isEmpty()) {
            showError("Empty Username !!");
            return false;
        } else if (!username.matches("^[A-Za-z][A-Za-z0-9]*$")) {
            showError("Username must start with a letter and contain only letters and numbers.");
            return false;
        } else if (username.length() < 3 || username.length() > 20) {
            showError("Username must be between 3 and 20 characters.");
            return false;
        }

        // Validate Verification Code
        if (!validateVerificationCode()) {
            // If the code is incorrect, generate a new one and update the label
            currentVerificationCode = generateRandomCode();
            lblVeriCode.setText("Verification Code: " + currentVerificationCode);
            txtCode.setText("");
            showError("Incorrect verification code.");
            return false;
        }

        return true; // If all validations pass
    }
}
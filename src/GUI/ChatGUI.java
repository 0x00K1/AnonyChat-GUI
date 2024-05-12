package com.anonychat.main.GUI;

import javax.swing.*;
import java.awt.*;
import javax.swing.text.BadLocationException;
import java.io.IOException;
import java.net.SocketException;

public class ChatGUI extends AnonyFrame implements AnonyFace {
    public static final long serialVersionUID = AnonyFace.SERIAL_VERSION_UID;

    private JTextPane chatArea;
    private JTextField messageField;
    private JButton sendButton;
    
    private ChatClient client;


    @SuppressWarnings("static-access")
	public ChatGUI(ChatClient client) throws IOException {
        super(client.join.serverName  + " - AnonyChat");

    	this.client = client;

        setSize(600, 450); // Edit
        initializeComponents();
        setupActions();
        initializeConnection(); 
    }

    @Override
    public void initializeComponents() {
    	// Load the background image
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/gallery/ChatBackground.jpg"));
        Image backgroundImage = backgroundIcon.getImage();

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(Color.BLACK);
        getContentPane().add(mainPanel);

        // Change JTextArea to JTextPane for background image support
        chatArea = new JTextPane() {
			private static final long serialVersionUID = AnonyFace.SERIAL_VERSION_UID;

			@Override
            protected void paintComponent(Graphics g) {
                // Paint the background image
                g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
                super.paintComponent(g);
            }
        };
        chatArea.setForeground(Color.WHITE);
        chatArea.setBackground(new Color(0, true)); // true here makes the background transparent
        chatArea.setFont(new Font("SimSun-ExtB", Font.PLAIN, 16));
        chatArea.setEditable(false);
        chatArea.setOpaque(false); // Make the JTextPane transparent

        JScrollPane scrollPane = new JScrollPane(chatArea) {
			private static final long serialVersionUID = 1L;

			@Override
            protected JViewport createViewport() {
                return new JViewport() {
					private static final long serialVersionUID = 1L;

					@Override
                    protected void paintComponent(Graphics g) {
                        // Paint the background image
                        g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
                        super.paintComponent(g);
                    }
                };
            }
        };
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBackground(Color.BLACK);
        messageField = new JTextField();
        sendButton = new JButton("Send");
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
    }

    @Override
    public void setupActions() {
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());
    }

    private void initializeConnection() throws IOException {
        Thread listenerThread = new Thread(() -> {
            try {
                while (true) {
                    final String message = client.receiveMessage();
                    if (message != null) {
                        SwingUtilities.invokeLater(() -> {
                            try {
                                javax.swing.text.Document doc = chatArea.getDocument();
                                doc.insertString(doc.getLength(), message + "\n", null);
                            } catch (BadLocationException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            } catch (SocketException e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Failed to connect to the server", "Error", JOptionPane.ERROR_MESSAGE));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        listenerThread.start();
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (validateInput()) {
            try {
            	if (message.startsWith("/")) {
            		if (client instanceof Root) {
            			((Root) client).handleCommand(message);
            		} else {
            			((User) client).handleCommand(message);
            		}
            	} else {
                    client.sendMessage(message);
            	}
                messageField.setText("");
            } catch (IOException e) {
            	showError("Failed to send message");
            }
        }
    }

	@SuppressWarnings("static-access")
	@Override
	public boolean validateInput() {
	    String message = messageField.getText().trim();
	    if (message.isEmpty()) return false;

	    if (message.equals("/exit")) {
	        return true;  // Always allow "/exit" command to proceed
	    }

	    if (!ChatServerGUI.isServerRunning(client.join.SessionID)) {
	        showError("Server Down!!\nType '/exit' to return to AnonyMain.");
	        return false;
	    }

	    return true;
	}
}
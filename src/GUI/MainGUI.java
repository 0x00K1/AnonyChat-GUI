package com.anonychat.main.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainGUI extends AnonyFrame implements AnonyFace {
    public static final long serialVersionUID = AnonyFace.SERIAL_VERSION_UID;
    
    private JPanel panel;
    private JLabel lblTitle;
    private JButton btnStartServer, btnJoinServer, btnExit;

	    /**************************
	     * Launch the application *
	     **************************/
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainGUI frame = new MainGUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Launcher Failed");
                }
            }
        });
    }

    public MainGUI() {
        super("AnonyChat");

        initializeComponents();
        setupActions();
    }

    @Override
	public void initializeComponents() {
    	panel = new JPanel() {
			private static final long serialVersionUID = AnonyFace.SERIAL_VERSION_UID;
			private Image backgroundImage = new ImageIcon(getClass().getResource("/gallery/MainBackground.jpg")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(null);
        setContentPane(panel);
        
        // Title
        lblTitle = new JLabel("AnonyChat");
        lblTitle.setForeground(new Color(200, 200, 200));
        lblTitle.setFont(new Font("Viner Hand ITC", Font.BOLD, 55));
        lblTitle.setBounds(400, 130, 330, 65);
        panel.add(lblTitle);

        // Start Chat button
        btnStartServer = createButton("Start Server");
        btnStartServer.setBounds(475, 300, 170, 45);
        panel.add(btnStartServer);

        // Join Chat button
        btnJoinServer = createButton("Join Server");
        btnJoinServer.setBounds(475, 370, 170, 45);;
        panel.add(btnJoinServer);

        // Exit button
        btnExit = createButton("Exit");
        btnExit.setBounds(510, 600, 100, 35);
        panel.add(btnExit);
    }

	@Override
	public void setupActions() {
		btnStartServer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startChat();
            }
        });
		
		btnJoinServer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                joinChat();
            }
        });
		
		btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (validateInput()) {
            		exit();
                }
            }
        });		
	}

    private void startChat() {
        ChatServerGUI serverConfigGUI = new ChatServerGUI();
        serverConfigGUI.setVisible(true);
    	this.dispose();  // Close the current window
    }

    private void joinChat() {
    	ChatJoinGUI serverJoinGUI = new ChatJoinGUI();
        serverJoinGUI.setVisible(true);
    	this.dispose();  // Close the current window
    }
    
    private void exit() {
    	if (ChatServer.isServerRunning && ChatServer.server != null) {
            ChatServer.stopServer();  // Ensure the server is stopped if running
        }
        dispose();  // Dispose of the GUI
        System.exit(0);  // Terminate the application
    }
    
	@Override
	public boolean validateInput() {
		// return 0x1 == 0;
		 if (ChatServer.isServerRunning) {
		        int choice = JOptionPane.showConfirmDialog(this,
		            "Server is Running !!\nAre you sure you want to stop the server and exit?",
		            "Exit Confirmation",
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE);

		        return choice == JOptionPane.YES_OPTION;
		    }
		    // If the server is not running, proceed to exit without confirmation
		    return true;
	}
}
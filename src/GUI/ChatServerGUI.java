package com.anonychat.main.GUI;

import javax.swing.*;
import java.awt.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.Document;
import javax.swing.text.AttributeSet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;



public class ChatServerGUI extends AnonyFrame implements AnonyFace {
    public static final long serialVersionUID = AnonyFace.SERIAL_VERSION_UID;
    
    private JPanel panel;
    private JTextField txtServerName, txtServerPort, txtUserRoot;
    private JPasswordField txtServerPass, txtPassRoot;
    private JCheckBox chkPrivate;
    private JSpinner txtServerCapacity;
    private JButton btnConnect, btnStart, btnStop, btnCreate, btnCancel;
    private JTextPane console;
    private JLabel lblStart, lblStop;
    
    private ChatServer server;
    public static boolean isServerRunning = false;
    protected static String savvy;
    private String connectionStatusMessage = "";
    private String serverIP;
    private int serverPort;
    private String serverName;
    private int serverCapacity;
    private boolean isPrivate;
    private String serverPass;
    private static String userRoot;
    private String passRoot;
    private String hashedRootPassword;
    private String hashedServerPassword;

    public ChatServerGUI() {
    	super("ChatServer");
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
        
        JLabel lblServerName = new JLabel("SERVER NAME");
        lblServerName.setForeground(Color.WHITE);
        lblServerName.setFont(new Font("Tahoma", Font.ITALIC, 17));
        lblServerName.setBounds(31, 29, 135, 25);
        panel.add(lblServerName);
        
        txtServerName = new JTextField();
        txtServerName.setColumns(10);
        txtServerName.setBounds(31, 64, 241, 25);
        panel.add(txtServerName);
        
        JLabel lblServerPort = new JLabel("SERVER PORT");
        lblServerPort.setForeground(Color.WHITE);
        lblServerPort.setFont(new Font("Tahoma", Font.ITALIC, 17));
        lblServerPort.setBounds(31, 112, 135, 25);
        panel.add(lblServerPort);
        
        txtServerPort = new JTextField();
        txtServerPort.setColumns(10);
        txtServerPort.setBounds(31, 147, 241, 25);
        panel.add(txtServerPort);
        
        JLabel lblServerCapacity = new JLabel("SERVER CAPACITY");
        lblServerCapacity.setForeground(Color.WHITE);
        lblServerCapacity.setFont(new Font("Tahoma", Font.ITALIC, 17));
        lblServerCapacity.setBounds(31, 204, 151, 25);
        panel.add(lblServerCapacity);
        
        txtServerCapacity = new JSpinner(new SpinnerNumberModel(10, 2, 99, 1));
        txtServerCapacity.setFont(new Font("Tahoma", Font.PLAIN, 17));
        txtServerCapacity.setBounds(31, 232, 63, 32);
        panel.add(txtServerCapacity);
        
        chkPrivate = new JCheckBox("Private");
        chkPrivate.setForeground(Color.WHITE);
        chkPrivate.setBackground(Color.WHITE);
        chkPrivate.setFont(new Font("Tahoma", Font.PLAIN, 17));
        chkPrivate.setBounds(31, 299, 104, 32);
        panel.add(chkPrivate);
        
        JLabel lblServerPassword = new JLabel("SERVER PASSWORD");
        lblServerPassword.setForeground(Color.WHITE);
        lblServerPassword.setFont(new Font("Tahoma", Font.ITALIC, 17));
        lblServerPassword.setBounds(31, 337, 167, 25);
        panel.add(lblServerPassword);
        
        txtServerPass = new JPasswordField();
        txtServerPass.setEnabled(false);
        txtServerPass.setColumns(10);
        txtServerPass.setBounds(31, 372, 241, 25);
        panel.add(txtServerPass);
        
        JLabel lblUsernameRoot = new JLabel("USERNAME");
        lblUsernameRoot.setForeground(Color.WHITE);
        lblUsernameRoot.setFont(new Font("Tahoma", Font.ITALIC, 17));
        lblUsernameRoot.setBounds(31, 472, 167, 25);
        panel.add(lblUsernameRoot);
        
        txtUserRoot = new JTextField();
        txtUserRoot.setColumns(10);
        txtUserRoot.setBounds(31, 507, 241, 25);
        panel.add(txtUserRoot);
        
        JLabel lblPasswordRoot = new JLabel("PASSWORD");
        lblPasswordRoot.setForeground(Color.WHITE);
        lblPasswordRoot.setFont(new Font("Tahoma", Font.ITALIC, 17));
        lblPasswordRoot.setBounds(31, 542, 167, 25);
        panel.add(lblPasswordRoot);
        
        txtPassRoot = new JPasswordField();
        txtPassRoot.setColumns(10);
        txtPassRoot.setBounds(31, 577, 241, 25);
        panel.add(txtPassRoot);
        
        JLabel lblRoot = new JLabel("Root");
        lblRoot.setForeground(Color.RED);
        lblRoot.setFont(new Font("Urdu Typesetting", Font.ITALIC, 37));
        lblRoot.setHorizontalAlignment(SwingConstants.CENTER);
        lblRoot.setBounds(31, 409, 241, 46);
        panel.add(lblRoot);
        
        btnConnect = new JButton("CONNECT");
        btnConnect.setForeground(Color.BLACK);
        btnConnect.setBackground(Color.LIGHT_GRAY);
        btnConnect.setFont(new Font("Yu Gothic UI", Font.PLAIN, 15));
        btnConnect.setBounds(73, 614, 160, 37);
        panel.add(btnConnect);
        
        JLabel lblServerConfig = new JLabel("SERVER CONFIGURATION");
        lblServerConfig.setForeground(Color.WHITE);
        lblServerConfig.setHorizontalAlignment(SwingConstants.CENTER);
        lblServerConfig.setFont(new Font("Tahoma", Font.PLAIN, 25));
        lblServerConfig.setBounds(363, 64, 662, 37);
        panel.add(lblServerConfig);

        // Starter
        lblStart = new JLabel("START");
        lblStart.setHorizontalAlignment(SwingConstants.CENTER);
        lblStart.setForeground(Color.LIGHT_GRAY);
        lblStart.setFont(new Font("Tahoma", Font.ITALIC, 17));
        lblStart.setBounds(978, 572, 82, 25);
        panel.add(lblStart);
        btnStart = new JButton("");
        btnStart.setForeground(Color.WHITE);
        ImageIcon startIcon = new ImageIcon(getClass().getResource("/gallery/start-on.png"));
        Image startimg = startIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        btnStart.setIcon(new ImageIcon(startimg));
        btnStart.setBorderPainted(false);
        btnStart.setContentAreaFilled(false);
        btnStart.setFocusPainted(false);
        btnStart.setOpaque(false);
        btnStart.setBounds(978, 574, 82, 89);
        btnStart.setEnabled(false); // Disabled by default
        panel.add(btnStart);
        
        
        // Stop
        lblStop = new JLabel("STOP");
        lblStop.setHorizontalAlignment(SwingConstants.CENTER);
        lblStop.setForeground(Color.LIGHT_GRAY);
        lblStop.setFont(new Font("Tahoma", Font.ITALIC, 17));
        lblStop.setBounds(890, 572, 82, 25);
        panel.add(lblStop);
        btnStop = new JButton("");
        btnStop.setForeground(Color.WHITE);
        ImageIcon stopIcon = new ImageIcon(getClass().getResource("/gallery/start-off.png"));
        Image stopimg = stopIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        btnStop.setIcon(new ImageIcon(stopimg));
        btnStop.setBorderPainted(false);
        btnStop.setContentAreaFilled(false);
        btnStop.setFocusPainted(false);
        btnStop.setOpaque(false);
        btnStop.setBounds(890, 574, 82, 89);
        btnStop.setEnabled(false); // Disabled by default
        panel.add(btnStop);
        
        // Checker
        btnCreate = new JButton("CREATE");
        btnCreate.setForeground(Color.BLACK);
        btnCreate.setBackground(Color.LIGHT_GRAY);
        btnCreate.setFont(new Font("Yu Gothic UI", Font.PLAIN, 15));
        btnCreate.setBounds(636, 582, 160, 37);
        panel.add(btnCreate);
        
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
        
        // Console
        console = new JTextPane();
        console.setEditable(false);
        console.setBackground(Color.BLACK);
        console.setBorder(null);
        console.setCaretColor(Color.BLACK);
        // Set the content type to HTML to allow for colored text later
        console.setContentType("text/html");
        // Ensure the HTML content has no extra styling that might cause a white line
        console.setText("<html><body style='color: white; background-color: black; margin: 0; padding: 0; border: none;'>");
        appendTextToConsole("\n                    "
        		+ "*** Welcome to AnonyChat server ***", Color.CYAN, 24);
        appendTextToConsole("\n\n[!] Root connection required.", Color.RED, 18);

        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setViewportBorder(null); // Remove the viewport border
        scrollPane.setBorder(null); // Remove the JScrollPane border itself
        scrollPane.getViewport().setBackground(Color.BLACK); // Ensure viewport background is black
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setBounds(363, 112, 662, 415); // Set bounds as per your layout
        panel.add(scrollPane);
        
        // Initially disable all fields except for root username and password
        disableInputComponentsExceptRoot();
    }

    private void disableInputComponentsExceptRoot() {
        txtServerName.setEnabled(false);
        txtServerPort.setEnabled(false);
        txtServerCapacity.setEnabled(false);
        chkPrivate.setEnabled(false);
        txtServerPass.setEnabled(false);
        btnStart.setEnabled(false);
        btnStop.setEnabled(false);
        btnCreate.setEnabled(false);
    }

    @Override
    public void setupActions() {
    	chkPrivate.addActionListener(new ActionListener() {
    	    @Override
    	    public void actionPerformed(ActionEvent e) {
    	        if (!chkPrivate.isSelected()) {
    	            txtServerPass.setText(""); // Clear the password field if "Private" is unchecked
    	        }
    	        txtServerPass.setEnabled(chkPrivate.isSelected()); // Enable or disable based on the checkbox
    	    }
    	});

    	btnConnect.addActionListener(new ActionListener() {
    	    @SuppressWarnings("static-access")
			@Override
    	    public void actionPerformed(ActionEvent e) {  
    	        if (btnConnect.getText().equals("CONNECT")) {
    	            try {
						if (!validateRoot()) {
						    return; // Validation failed, exit early
						}
					} catch (SQLException x) {
						// x.printStackTrace();
						showErrorAndExit("NotValid Connection");
					}
    	        } else {
    	            // This block executes when disconnecting
    	            server.stopServer();
    	            btnConnect.setText("CONNECT");
    	            disableInputComponents(1);
    	            emptyForAll(0);
    	            connectionStatusMessage = ""; // Clear the connection status message
                    refreshConsole(); // Clear the JTextPane and refresh based on current state
    	            btnStart.setEnabled(false);
    	            btnStop.setEnabled(false);
    	        }
    	    }
    	});
    	
    	btnCreate.addActionListener(new ActionListener() {
    	    @Override
    	    public void actionPerformed(ActionEvent e) {
    	        if (btnCreate.getText().equals("CREATE")) {
	                if (!validateInput()) {
	                    return;
	                }
	                
    	        } else {
	                userRoot = txtUserRoot.getText();
	                serverName = txtServerName.getText();
    	            deleteChatroom(userRoot, serverName);
    	            decrementNumOfRoomsForRoot(userRoot);
    	            // Reset UI to allow reconfiguration
    	            enableInputComponents(1);
    	            emptyForAll(1);
    	            // Append the report in green
    	            refreshConsole(); // Clear previous content
    	            btnStart.setEnabled(false); // Disable the start button as we are not CREATEed
    	        }
    	    }
    	});
    	
    	btnStart.addActionListener(new ActionListener() {
    	    @Override
    	    public void actionPerformed(ActionEvent e) {
    	        refreshConsole(); // Clear previous content
    	        btnCreate.setEnabled(false);
    	        btnStart.setEnabled(false);

    	        // Check if the root is already running a server
    	        if (isRootRunningServer(getIP() ,userRoot)) {
    	            appendTextToConsole("\n[x] You are already running a server. Only one server per IP/Root is allowed.\n", Color.RED, 18);
	                handleStartFailure(); // Handle the failure case
    	            return; // Prevent starting another server
    	        }
    	        
    	        serverName = txtServerName.getText();

    	        // Fetch room and root user details from the DB
    	        Map<String, Object> details = getToStart(userRoot, serverName);
    	        
    	        if (!details.isEmpty()) {
    	            // Extract room and root details
    	            serverIP = (String) details.get("CRoomIP");
    	            serverPort = (Integer) details.get("CRoomPORT");
    	            serverName = (String) details.get("CRoomName");
    	            serverCapacity = (Integer) details.get("Capacity");
    	            isPrivate = (Boolean) details.get("isPrivate");
    	            hashedServerPassword = (String) details.get("RoomPasswordHash");
    	            
    	            // Check if serverIP is empty or null
    	            if (serverIP == null || serverIP.trim().isEmpty()) {
    	                appendTextToConsole("\n[x] Server IP address is not configured properly.\n", Color.RED, 18);
    	                handleStartFailure(); // Handle the failure case
    	                return; // Early return to prevent further execution
    	            }
    	            
    	            // Check if the port is available
    	            try (ServerSocket ss = new ServerSocket(serverPort)) {
    	                // Port is available, close the resource
    	            } catch (IOException ex) {
    	                appendTextToConsole("[x] Port " + serverPort + " is not available. Choose another port.\n", Color.RED, 18);
    	                handleStartFailure(); // Handle the failure case
    	                return; // Early return to prevent further execution
    	            }
    	            
    	            // Start Server
    	            server = new ChatServer(serverIP, serverPort, serverName, serverCapacity, isPrivate, hashedServerPassword, userRoot);
    	            server.startServer();
    	                            
    	            appendTextToConsole("\n[✓] Server started successfully. " + serverName + " Room created.\n", Color.GREEN, 18);
    	            appendTextToConsole("[!] Note: The server room will down if you close the application.\n", Color.RED, 12);
    	            appendTextToConsole("\n\nServer running on " + serverIP + ":" + serverPort + " [" + (isPrivate ? "Private" : "Public") + "]\n", Color.BLUE, 20);
    	            if (!server.isSSLEnabled()) {
    	            	appendTextToConsole("\n!!Warning!! (No SSL/TLS encryption)\n", Color.RED, 15);
    	            	appendTextToConsole("INFO: https://github.com/0x00K1/AnonyChat", Color.GRAY, 12);
    	            }
    	            btnStop.setEnabled(true);
    	        } else {
        	        appendTextToConsole("\n[x] No details found for " + userRoot + ".\n", Color.RED, 18);
    	        	handleStartFailure();
    	        }
    	    }
    	    
    	    private void handleStartFailure() {
    	        btnConnect.setText("CONNECT");
    	        disableInputComponents(1);
    	        emptyForAll(0);
    	        connectionStatusMessage = ""; // Clear the connection status message
    	        refreshConsole(); // Clear the JTextPane and refresh based on current state
    	        btnStart.setEnabled(false);
    	        btnStop.setEnabled(false);
    	    }
    	});
    	
    	btnStop.addActionListener(new ActionListener() {
    	    @SuppressWarnings("static-access")
			@Override
    	    public void actionPerformed(ActionEvent e) {
    	    	if (server != null) {
	    	    	server.stopServer();
    	            connectionStatusMessage = ""; // Clear the connection status message
	    	    	refreshConsole(); // Clear previous content
	    	    	appendTextToConsole("Server stopped.\n", Color.red, 18); // Provide feedback in the GUI
    	            disableInputComponents(1);
	    	    	emptyForAll(0);
	                btnConnect.setText("CONNECT");
	                btnCreate.setText("CREATE");
	    	        btnStop.setEnabled(false);
    	    	}
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

    public boolean validateRoot() throws SQLException {
        refreshConsole(); // Clear previous content
    	StringBuilder errorMessages = new StringBuilder();
        StringBuilder infoMessages = new StringBuilder();
        boolean allValid = true;
        
        userRoot = txtUserRoot.getText();
        passRoot = new String(txtPassRoot.getPassword());
        
        if (userRoot.isEmpty() || passRoot.isEmpty()) {
        	errorMessages.append("[x] Username and password cannot be empty.\n");
            allValid = false;
        }
        
    	// Root Username Validation
        if (!userRoot.matches("^[A-Za-z][A-Za-z0-9]*$")) {
        	errorMessages.append("\n[x] Root Username must start with a letter and contain only En/letters and numbers.\n");
            allValid = false;
        } else if (userRoot.length() < 3 || userRoot.length() > 20) {
        	errorMessages.append("\n[x] Root Username must be between 3 and 20 characters.\n");
            allValid = false;	
        }

        // Root Password Validation
        if (passRoot.length() < 8 || passRoot.length() > 99) {
        	errorMessages.append("\n[x] Root Password must be between 8 and 99 characters.\n");
            allValid = false;
        }
        
        // If there are errors, append them in red
        if (errorMessages.length() > 0) {
            appendTextToConsole(errorMessages.toString(), Color.RED, 18);
        }
        
        if(!allValid) return allValid; // SavvyChecker
        
        // Check if the root username already exists
        try {
        	boolean rootExists = checkRootExists(userRoot);
            if (rootExists) {
                Map<String, String> credentials = getRootPasswordAndSalt(userRoot);
                String storedPasswordHash = credentials.get("passwordHash");
                String storedSalt = credentials.get("salt");
                
                if (!SecurityUtils.validatePassword(passRoot, storedSalt, storedPasswordHash)) {
                    errorMessages.append("\n[x] Incorrect password for existing root username.\n");
                    allValid = false;
                } else {
                	// Update the connection status message
                    btnConnect.setText("DISCONNECT");
                    connectionStatusMessage = "Connected as " + txtUserRoot.getText() + "\n";
                    appendTextToConsole(connectionStatusMessage, Color.GREEN, 18);
                	appendTextToConsole("Welcome back, " + userRoot + ".\n", Color.GREEN, 18);
                    int roomCount = getRoomCountForRoot(userRoot);
                    appendTextToConsole("Rooms available: " + roomCount + " (Max 3)\n", Color.BLUE, 18);
                    offerRoomManagementOptions(userRoot);
                }
            } else {
                // Prompt for password confirmation since this is a new root username
                JPasswordField pwd = new JPasswordField(10);
                int action = JOptionPane.showConfirmDialog(null, pwd, "Confirm Password", JOptionPane.OK_CANCEL_OPTION);
                if (action == JOptionPane.OK_OPTION) {
                    String confirmPassRoot = new String(pwd.getPassword());
                    if (!passRoot.equals(confirmPassRoot)) {
                        errorMessages.append("\n[x] Passwords do not match.\n");
                        allValid = false;
                    } else {
                    	String salt = SecurityUtils.generateSalt();
                        savvy = salt;
                    	hashedRootPassword = SecurityUtils.hashPassword(passRoot, savvy);
                        insertRootData(userRoot, 0, hashedRootPassword, savvy);
                        
                        // Update the connection status message
                        btnConnect.setText("DISCONNECT");
                        connectionStatusMessage = "Connected as " + txtUserRoot.getText() + "\n";
                        appendTextToConsole(connectionStatusMessage, Color.GREEN, 18);
                        infoMessages.append("New root user created successfully.\n");

        	            // Check how many rooms the root has (initially 0)
        	            int roomCount = getRoomCountForRoot(userRoot);
        	            infoMessages.append("Rooms available: " + roomCount + " (Max 3)\n");
        	            appendTextToConsole(infoMessages.toString(), Color.BLUE, 18);
        	            offerRoomManagementOptions(userRoot);
                    }
                } else {
                    // The user cancelled the password confirmation
                    errorMessages.append("\n[x] Password confirmation was cancelled.\n");
                    allValid = false;
                }
            }
        } catch (Exception e){
        	throw new SQLException();
        }
        
        // If there are errors, append them in red
        if (errorMessages.length() > 0) {
            appendTextToConsole(errorMessages.toString(), Color.RED, 18);
        }
        
        return allValid;
    }
    @Override
    public boolean validateInput() {
        refreshConsole(); // Clear previous content
        StringBuilder errorMessages = new StringBuilder();
        StringBuilder reportMessages = new StringBuilder();
        StringBuilder infoMessages = new StringBuilder();
        boolean allValid = true;

        // Assign values from input fields here to ensure they are up to date
        serverName = txtServerName.getText();
        serverCapacity = (Integer) txtServerCapacity.getValue();
        isPrivate = chkPrivate.isSelected();
        serverPass = new String(txtServerPass.getPassword());
        
        // Server Name Validation
        if (!serverName.matches("^[A-Za-z][A-Za-z0-9]*$")) {
        	errorMessages.append("\n[x] Server Name must start with a letter and contain only En/letters and numbers (No Spaces).\n");
            allValid = false;
        } else if (serverName.length() < 3 || serverName.length() > 20) {
        	errorMessages.append("\n[x] Server Name must be between 3 and 20 characters.\n");
            allValid = false;	
        }

        // Server Port Validation
        String portStr = txtServerPort.getText();
        if (portStr.isEmpty()) {
            errorMessages.append("\n[x] Server Port cannot be empty.\n");
            allValid = false;
        } else {
            try {
                serverPort = Integer.parseInt(portStr);
                if (serverPort < 1024 || serverPort > 65535) {
                    errorMessages.append("\n[x] Server Port must be between 1024 and 65535.\n");
                    allValid = false;
                } else {
                	ServerSocket ss = null;
                    try {
                        ss = new ServerSocket(serverPort);
                    } catch (IOException e) {
                    	errorMessages.append("[x] Port is not available. Choose another port.\n");
                        allValid = false;
                    } finally {
                        if (ss != null) {
                            try {
                                ss.close();
                            } catch (IOException x) {}
                        }
                    }
                }
            } catch (NumberFormatException e) {
                errorMessages.append("\n[x] Server Port must be a valid number.\n");
                allValid = false;
            }
        }

        // Server Capacity Validation
        if (serverCapacity < 2 || serverCapacity > 99) {
        	errorMessages.append("\n[x] Server Capacity must be between 2 and 99.\n");
            allValid = false;
        }

        // Private Server Password Validation
        if (chkPrivate.isSelected()) {
            serverPass = new String(txtServerPass.getPassword());
            isPrivate = true;
            if (serverPass.length() < 4 || serverPass.length() > 99) {
            	errorMessages.append("\n[x] Server Password must be between 4 and 99 characters.\n");
                allValid = false;
            }
        } else {
        	serverPass = null;
            isPrivate = false;
        }
        
        // If there are errors, append them in red
        if (errorMessages.length() > 0) {
            appendTextToConsole(errorMessages.toString(), Color.RED, 18);
        }

        serverIP = getIP();
        if (serverIP ==  null) allValid = false;
        
        if (allValid) {
        	String salt = isPrivate ? SecurityUtils.generateSalt() : null;
            hashedServerPassword = (serverPass != null) ? SecurityUtils.hashPassword(serverPass, salt) : null; // Only hash server password if the server is private
            if(insertChatroomData(serverIP, serverPort, userRoot, serverName, serverCapacity, isPrivate, 0, hashedServerPassword, salt)) {
                incrementNumOfRoomsForRoot(userRoot);
            	// Append a configuration report to the console
                reportMessages.append(String.format("\n[✓] Server Name: %s\n", txtServerName.getText()));
                reportMessages.append(String.format("\n[✓] Server Port: %s\n", txtServerPort.getText()));
                reportMessages.append(String.format("\n[✓] Server Capacity: %s\n", txtServerCapacity.getValue().toString()));
                reportMessages.append(String.format("\n[✓] Private: %s\n", chkPrivate.isSelected() ? "Yes" : "No"));
                if (chkPrivate.isSelected()) {
                	reportMessages.append("\n[✓] Server Password is set.\n");
                }
                reportMessages.append(String.format("\n[✓] Root name: %s\n", txtUserRoot.getText()));
                reportMessages.append("\n[✓] Root Password is set.\n\n");
                
                infoMessages.append("\n\n[!] Click the 'START' button to proceed.");
                
                // Append the texts
                appendTextToConsole(reportMessages.toString(), Color.GREEN, 18);
                appendTextToConsole(infoMessages.toString(), Color.BLUE, 18);
                
                disableInputComponents(0);
                btnCreate.setText("DELETE"); // Change the button text to "DELETE"
                btnStart.setEnabled(true);
            }
        } else {
            btnStart.setEnabled(false);
        }
        
        return allValid;
    }
    
    private String getIP() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface intf : Collections.list(interfaces)) {
                if (intf.getName().contains("wlan")) {  // "wlan" is commonly used for wireless interfaces
                    Enumeration<InetAddress> addresses = intf.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()) {
                            return addr.getHostAddress();
                        }
                    }
                }
            }
            // No wireless IP address found
            appendTextToConsole("[x] No IP address found for the wireless interface.", Color.RED, 18);
        } catch (Exception e) {
            showErrorAndExit("Network Misconfiguration");
        }
        return null;  // Return null if no suitable IP address found.
    }

	private void appendTextToConsole(String text, Color color, int textSize) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        
        // Initial attributes for the text
        final AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
        AttributeSet modifiedAset = sc.addAttribute(aset, StyleConstants.FontSize, textSize);
        
        SwingUtilities.invokeLater(() -> {
            try {
                Document doc = console.getDocument();
                int start = doc.getLength();
                doc.insertString(start, text, modifiedAset);

                // Create a new set of attributes for paragraph alignment
                SimpleAttributeSet center = new SimpleAttributeSet();
                StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
                // Apply paragraph attributes from 'start' to the end of the document
                ((StyledDocument)doc).setParagraphAttributes(start, doc.getLength(), center, false);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }
    
    private void refreshConsole() {
    	console.setText(""); // Clear the console
        if (!connectionStatusMessage.isEmpty()) {
            appendTextToConsole(connectionStatusMessage, Color.GREEN, 18); // Re-add the connection status message
        }
    }
    
    private void enableInputComponents(int t) {
        txtServerName.setEnabled(true);
        txtServerPort.setEnabled(true);
        txtServerCapacity.setEnabled(true);
        chkPrivate.setEnabled(true);
        // The password field should be conditionally enabled based on the chkPrivate checkbox state
        txtServerPass.setEnabled(chkPrivate.isSelected());
        if (t != 1) {
        	txtUserRoot.setEnabled(true);
            txtPassRoot.setEnabled(true);
        } else {
        	txtUserRoot.setEnabled(false);
            txtPassRoot.setEnabled(false);
        }
    }
    
    private void disableInputComponents(int t) {
        txtServerName.setEnabled(false);
        txtServerPort.setEnabled(false);
        txtServerCapacity.setEnabled(false);
        chkPrivate.setEnabled(false);
        txtServerPass.setEnabled(false);
        
        if (t != 1) {
        	txtUserRoot.setEnabled(false);
            txtPassRoot.setEnabled(false);
        } else {
        	 txtUserRoot.setEnabled(true);
             txtPassRoot.setEnabled(true);
        }
    }
    
    private void enableInputComponentsForCreation() {
        txtUserRoot.setEnabled(false);
        txtPassRoot.setEnabled(false);
    	txtServerName.setEnabled(true);
        txtServerPort.setEnabled(true);
        txtServerCapacity.setEnabled(true);
        chkPrivate.setEnabled(true);
        txtServerPass.setEnabled(chkPrivate.isSelected());
        btnStart.setEnabled(false); // Start button should only be enabled after a room is created or selected
        btnCreate.setText("CREATE");
        btnCreate.setEnabled(true);
    }
    
    private void emptyForAll(int t) {
    	if (t != 1) {
    		txtUserRoot.setText("");
            txtPassRoot.setText("");
    	}
    	txtServerName.setText("");
        txtServerPort.setText("");
        txtServerCapacity.setDropTarget(null);
        txtServerPass.setText("");
        btnCreate.setText("CREATE");
    }
    
    // Method to insert data into the ROOT table
    private void insertRootData(String rootName,int NumOfRooms, String password, String salt) {
        String sql = "INSERT INTO ROOT (RootName, NumOfRooms, PASSWORDHASH, SALT) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, rootName);
            pstmt.setInt(2, NumOfRooms);
            pstmt.setString(3, password);
            pstmt.setString(4, salt);            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private boolean RoomCheck(String cRoomIP, int cRoomPort, String rootName) {
        // Assuming you have a Connection object dbConnection
        String query = "SELECT COUNT(*) FROM CHATROOMS WHERE CRoomIP = ? AND CRoomPORT = ? AND RootName = ?";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS); 
        		PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, cRoomIP);
            stmt.setInt(2, cRoomPort);
            stmt.setString(3, rootName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Returns true if count is greater than 0
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to false in case of query failure
    }
    
    private boolean checkRoomNameExists(String roomName, String rootName) {
        String sql = "SELECT COUNT(*) FROM CHATROOMS WHERE CRoomName = ? AND RootName = ?";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomName);
            pstmt.setString(2, rootName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;  // Returns true if count is greater than 0, indicating a duplicate name
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Default to false in case of query failure
    }

    // Method to insert data into the CHATROOMS table
    private boolean insertChatroomData(String cRoomIP, int cRoomPort, String rootName, String cRoomName, int capacity, boolean isPrivate, int OnlineUsers, String passwordHash, String salt) {
    	if (RoomCheck(cRoomIP, cRoomPort, rootName)) {
            appendTextToConsole("A room with the same IP and port already exists for this root account. Please choose different parameters.\n", Color.RED, 18);
            return false;
    	}
    	
    	if (checkRoomNameExists(cRoomName, rootName)) {
            appendTextToConsole("A room with the same name already exists. Please choose a different name.\n", Color.RED, 18);
            return false;
        }

    	int roomCount = getRoomCountForRoot(userRoot);
        if (roomCount >= 3) {
            // Inform the user that no more rooms can be created
            appendTextToConsole("Maximum number of rooms reached. Cannot create more rooms.\n", Color.RED, 18);
			appendTextToConsole("\nReConnect.", Color.GRAY, 20);
            return false;
        }
    	String sql = "INSERT INTO CHATROOMS (CRoomIP, CRoomPORT, RootName, CRoomName, Capacity, isPrivate, OnlineUsers, PASSWORDHASH, SALT) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set parameters for the prepared statement
            pstmt.setString(1, cRoomIP);
            pstmt.setInt(2, cRoomPort);
            pstmt.setString(3, rootName);
            pstmt.setString(4, cRoomName);
            pstmt.setInt(5, capacity);
            pstmt.setBoolean(6, isPrivate);
            pstmt.setInt(7, OnlineUsers);
            pstmt.setString(8, passwordHash); // May be null if the room is not private
            pstmt.setString(9, salt); // May be null if the room is not private or no password is set
            
            // Execute the update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    protected void insertUserData(String SessionID, String cRoomIP, int cRoomPort, String rootName, String userIP, String userName, boolean isRoot, boolean isAnonymous, boolean isOnline) {
        String sql = "INSERT INTO USERS (SessionID, CRoomIP, CRoomPORT, RootName, UserIP, UserName, isRoot, isAnonymous, isOnline) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, SessionID);
            pstmt.setString(2, cRoomIP);
            pstmt.setInt(3, cRoomPort);
            pstmt.setString(4, rootName);
            pstmt.setString(5, userIP);
            pstmt.setString(6, userName);
            pstmt.setBoolean(7, isRoot);
            pstmt.setBoolean(8, isAnonymous); 
            pstmt.setBoolean(9, isOnline);            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Method to check if a root username already exists in the database
    private boolean checkRootExists(String rootName) {
        String sql = "SELECT COUNT(*) FROM ROOT WHERE RootName = ?";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, rootName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to get the stored password hash for a given root username
    protected Map<String, String> getRootPasswordAndSalt(String rootName) {
        String sql = "SELECT PASSWORDHASH, SALT FROM ROOT WHERE RootName = ?";
        Map<String, String> credentials = new HashMap<>();
        
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, rootName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    credentials.put("passwordHash", rs.getString("PASSWORDHASH"));
                    credentials.put("salt", rs.getString("SALT"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return credentials;
    }
    
    protected Map<String, String> getServerCredentials(String cRoomIP, int cRoomPort, String RootName) {
        Map<String, String> credentials = new HashMap<>();
        String sql = "SELECT PASSWORDHASH, SALT FROM CHATROOMS WHERE CRoomIP = ? AND CRoomPORT = ? AND RootName = ?";
        
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cRoomIP);
            pstmt.setInt(2, cRoomPort);
            pstmt.setString(3, RootName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    credentials.put("passwordHash", rs.getString("PASSWORDHASH"));
                    credentials.put("salt", rs.getString("SALT"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return credentials;
    }
    
    protected String getRootNameFromChatroom(String cRoomIP, int cRoomPort, String RootName) {
        String rootName = null;
        String sql = "SELECT RootName FROM CHATROOMS WHERE CRoomIP = ? AND CRoomPORT = ? AND RootName = ?";
        
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cRoomIP);
            pstmt.setInt(2, cRoomPort);
            pstmt.setString(3, RootName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    rootName = rs.getString("RootName");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rootName;
    }
    
    private int getRoomCountForRoot(String rootName) {
        int roomCount = 0;
        String sql = "SELECT COUNT(*) AS RoomCount FROM CHATROOMS WHERE RootName = ?";
        
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, rootName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    roomCount = rs.getInt("RoomCount");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roomCount;
    }
    
    private List<String> getChatroomNamesForRoot(String rootName) {
        List<String> chatroomNames = new ArrayList<>();
        String sql = "SELECT CRoomName FROM CHATROOMS WHERE RootName = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, rootName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String cRoomName = rs.getString("CRoomName");
                    chatroomNames.add(cRoomName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chatroomNames;
    }
    
    private Map<String, Object> getToStart(String rootName, String roomName) {
        Map<String, Object> details = new HashMap<>();
        // Adjust the query to also join with the ROOT table and fetch the PASSWORDHASH for the root user
        String query = "SELECT CHATROOMS.CRoomIP, CHATROOMS.CRoomPORT, CHATROOMS.CRoomName, " +
                       "CHATROOMS.Capacity, CHATROOMS.isPrivate, CHATROOMS.PASSWORDHASH AS RoomPasswordHash, " +
                       "CHATROOMS.SALT AS RoomSalt, ROOT.PASSWORDHASH AS RootPasswordHash, ROOT.SALT AS RootSalt " +
                       "FROM CHATROOMS JOIN ROOT ON CHATROOMS.RootName = ROOT.RootName " +
                       "WHERE CHATROOMS.RootName = ? AND CHATROOMS.CRoomName = ? ORDER BY CHATROOMS.CRoomName DESC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, rootName);
            stmt.setString(2, roomName);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                details.put("CRoomIP", rs.getString("CRoomIP"));
                details.put("CRoomPORT", rs.getInt("CRoomPORT"));
                details.put("CRoomName", rs.getString("CRoomName"));
                details.put("Capacity", rs.getInt("Capacity"));
                details.put("isPrivate", rs.getBoolean("isPrivate"));
                details.put("RoomPasswordHash", rs.getString("RoomPasswordHash"));
                details.put("RoomSalt", rs.getString("RoomSalt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    private void displayChatroomsAndSelect(String userRoot) {
        List<String> chatroomNames = getChatroomNamesForRoot(userRoot);
        StringBuilder message = new StringBuilder("Select a room to run:\n");

        for (int i = 0; i < chatroomNames.size(); i++) {
            message.append(i + 1).append(". ").append(chatroomNames.get(i)).append("\n");
        }

        appendTextToConsole(message.toString(), Color.BLUE, 18);

        // Optionally, prompt for selection
        String selection = JOptionPane.showInputDialog(panel, "Enter the number of the room to run:");
        try {
            int selectedIndex = Integer.parseInt(selection.trim()) - 1;
            if (selectedIndex >= 0 && selectedIndex < chatroomNames.size()) {
                String selectedRoomName = chatroomNames.get(selectedIndex);
                if (!isServerRunning(userRoot, selectedRoomName)) {
                    appendTextToConsole("\n                                           "
                    		+ "Running room: " + selectedRoomName + " . .", Color.GREEN, 18);
                    appendTextToConsole("\n\n[!] Click the 'START' button to proceed.", Color.BLUE, 18);
                    runExistingRoom(userRoot, selectedRoomName);
        		} else {
        			appendTextToConsole("\n[x] Server is already running.\n", Color.RED, 18);
        			offerRoomManagementOptions(userRoot);
        		}
            } else {
                appendTextToConsole("[x] Invalid room selection.", Color.RED, 18);
	            disableInputComponents(0);
            }
        } catch (NumberFormatException e) {
            appendTextToConsole("[x] Invalid input. Please enter a valid room number.", Color.RED, 18);
            disableInputComponents(0);
        } catch (Exception e) {
        	appendTextToConsole("[x] Invalid input.\n", Color.RED, 18);
			appendTextToConsole("\n\nReConnect.\n", Color.GRAY, 20);
			appendTextToConsole("\n[!] All running servers will down.\n", Color.RED, 12);
            disableInputComponents(0);
        }
    }
    
    private void offerRoomManagementOptions(String userRoot) {
    	/* @Logic
    	 * No existing rooms: Offers the option to create a new room.
    	 * 1-2 existing rooms: Offers options to run an existing room or create a new one.
    	 * 3 existing rooms: Offers options to run or delete an existing room, since creating more is not allowed.
    	 */
    	int roomCount = getRoomCountForRoot(userRoot);
        List<String> options = new ArrayList<>();
        if (roomCount > 0) {
            options.add("Run an existing room");
        }
        if (roomCount < 3) {
            options.add("Create a new room");
        }
        if (roomCount > 0) {
            options.add("Delete a room");
        }
        
        String[] optionsArray = options.toArray(new String[0]);
        
        int action = JOptionPane.showOptionDialog(null, "Select an action:", "Room Management",
                                                  JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                                                  null, optionsArray, optionsArray[0]);
        if (action != -1 && action < optionsArray.length) {
            String selectedOption = optionsArray[action];
            switch (selectedOption) {
                case "Run an existing room":
                    displayChatroomsAndSelect(userRoot);
                    break;
                case "Create a new room":
                    enableInputComponentsForCreation();
                    break;
                case "Delete a room":
                    deleteRoom(userRoot);
                    break;
                default:
                    // This should never happen unless new options are added without updating the switch.
                    appendTextToConsole("[x] Unexpected action selected.", Color.RED, 18);
                    break;
            }
        } else {
            // User closed the dialog or clicked Cancel
            appendTextToConsole("[!] No action selected.\n", Color.GRAY, 18);
			appendTextToConsole("\n\nReConnect.\n", Color.GRAY, 20);
			appendTextToConsole("[!] All running servers will down.\n", Color.RED, 12);
            disableInputComponents(0);
        }
    }
    
    private void deleteRoom(String userRoot) {
        List<String> chatroomNames = getChatroomNamesForRoot(userRoot);
        
        try {
        	String selectedRoom = (String) JOptionPane.showInputDialog(null, "Select a room to delete:", 
                    "Delete Room", JOptionPane.QUESTION_MESSAGE, 
                    null, chatroomNames.toArray(), chatroomNames.get(0));
			if (selectedRoom != null && !selectedRoom.isEmpty()) {
				
				if (!isServerRunning(userRoot, selectedRoom)) {
					deleteChatroom(userRoot, selectedRoom);
					appendTextToConsole("Room '" + selectedRoom + "' deleted successfully.\n", Color.GREEN, 18);
                    decrementNumOfRoomsForRoot(userRoot);
					offerRoomManagementOptions(userRoot);
				} else {
					appendTextToConsole("\n\nRoom '" + selectedRoom + "' is Running.\n", Color.BLUE, 18);
					appendTextToConsole("[!] Stop the server before delete it.\n", Color.RED, 12);
					offerRoomManagementOptions(userRoot);
				}		
			} else {
				appendTextToConsole("[x] Invalid room selection.\n", Color.RED, 18);
				appendTextToConsole("\n\nReConnect.\n", Color.GRAY, 20);
				appendTextToConsole("[!] All running servers will down.\n", Color.RED, 12);
	            disableInputComponents(0);
			}
        } catch (Exception e) {
            appendTextToConsole("Invalid input.\n", Color.RED, 18);
			appendTextToConsole("\n\nReConnect.\n", Color.GRAY, 20);
			appendTextToConsole("[!] All running servers will down.\n", Color.RED, 12);
            disableInputComponents(0);
        }
    }

    private void deleteChatroom(String userRoot, String roomName) {
        String sql = "DELETE FROM CHATROOMS WHERE RootName = ? AND CRoomName = ?";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userRoot);
            pstmt.setString(2, roomName);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                // Successful deletion
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isServerRunning(String userRoot, String roomName) {
    	String sql = "SELECT isRunning FROM CHATROOMS WHERE RootName = ? AND CRoomName = ?";
    	try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS); 
    		 PreparedStatement pstmt = conn.prepareStatement(sql)) {
    		pstmt.setString(1, userRoot);
    		pstmt.setString(2, roomName);
    		
    		ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("isRunning");
            }
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	return false;
    }
    
    private void runExistingRoom(String rootName, String roomName) {
    	updateRoomIP(getIP(), roomName, rootName);
    	populateRoomDetails(rootName, roomName);
        disableInputComponents(0);
        btnStart.setEnabled(true);
    }
    
    private void updateRoomIP(String newIP, String roomName, String rootName) {
        // SQL query to update the room IP address
        String sql = "UPDATE CHATROOMS SET CRoomIP = ? WHERE CRoomName = ? AND RootName = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newIP);
            pstmt.setString(2, roomName);
            pstmt.setString(3, rootName);

            // Execute the update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors
        }
    }

    private void populateRoomDetails(String rootName, String roomName) {
        String sql = "SELECT * FROM CHATROOMS WHERE RootName = ? AND CRoomName = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, rootName);
            pstmt.setString(2, roomName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Assuming these are the fields in your CHATROOMS table
                    txtServerName.setText(rs.getString("CRoomName"));
                    txtServerPort.setText(String.valueOf(rs.getInt("CRoomPORT")));
                    txtServerCapacity.setValue(rs.getInt("Capacity"));
                    chkPrivate.setSelected(rs.getBoolean("isPrivate"));
                    txtServerPass.setText(""); // Passwords are not retrieved for security reasons
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    protected void updateUserStatusInDatabase(String SessionID, boolean isOnline, String serverIP, int serverPort, String RootName) {
        // This is a simplified version. You might need to adjust it based on your actual database schema.
        String updateUserSql = "UPDATE USERS SET isOnline = ? WHERE SessionID = ?";
        String updateRoomSql = isOnline ? "UPDATE CHATROOMS SET OnlineUsers = OnlineUsers + 1 WHERE CRoomIP = ? AND CRoomPORT = ? AND RootName = ?"
                                        : "UPDATE CHATROOMS SET OnlineUsers = OnlineUsers - 1 WHERE CRoomIP = ? AND CRoomPORT = ? AND RootName = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement updateUserStmt = conn.prepareStatement(updateUserSql);
             PreparedStatement updateRoomStmt = conn.prepareStatement(updateRoomSql)) {

            // Update user status
            updateUserStmt.setBoolean(1, isOnline);
            updateUserStmt.setString(2, SessionID);
            updateUserStmt.executeUpdate();

            // Update room's online user count
            updateRoomStmt.setString(1, serverIP);
            updateRoomStmt.setInt(2, serverPort);
            updateRoomStmt.setString(3, RootName);
            updateRoomStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    protected int getOnlineUsersCount(String cRoomIP, int cRoomPort, String RootName) {
        String sql = "SELECT OnlineUsers FROM CHATROOMS WHERE CRoomIP = ? AND CRoomPORT = ? AND RootName = ?";
        int onlineUsers = 0;

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cRoomIP);
            pstmt.setInt(2, cRoomPort);
            pstmt.setString(3, RootName);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                onlineUsers = rs.getInt("OnlineUsers");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return onlineUsers;
    }
    
    protected boolean isRootOnline(String cRoomIP, int cRoomPort, String rootName) {
        String sql = "SELECT COUNT(*) AS RootOnline FROM USERS WHERE CRoomIP = ? AND CRoomPORT = ? AND UserName = ? AND isOnline = TRUE";
        boolean rootOnline = false;

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cRoomIP);
            pstmt.setInt(2, cRoomPort);
            pstmt.setString(3, rootName);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                rootOnline = rs.getInt("RootOnline") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return rootOnline;
    }
    
    protected boolean isUserOnline(String userName, String cRoomIP, int cRoomPort, String rootName) {
        String sql = "SELECT COUNT(*) AS UserCount FROM USERS WHERE UserName = ? AND CRoomIP = ? AND CRoomPORT = ? AND RootName = ? AND isOnline = TRUE";
        boolean isOnline = false;

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userName);
            pstmt.setString(2, cRoomIP);
            pstmt.setInt(3, cRoomPort);
            pstmt.setString(4, rootName);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                isOnline = rs.getInt("UserCount") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return isOnline;
    }
    
    private void incrementNumOfRoomsForRoot(String rootName) {
        String sql = "UPDATE ROOT SET NumOfRooms = NumOfRooms + 1 WHERE RootName = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rootName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void decrementNumOfRoomsForRoot(String rootName) {
        String sql = "UPDATE ROOT SET NumOfRooms = NumOfRooms - 1 WHERE RootName = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rootName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    protected boolean updateCapacityInDatabase(int newCapacity, String serverIP, int serverPort) {
        String sql = "UPDATE CHATROOMS SET Capacity = ? WHERE CRoomIP = ? AND CRoomPORT = ?";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newCapacity);
            pstmt.setString(2, serverIP);
            pstmt.setInt(3, serverPort);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;  // return true if the row was updated
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // return false if there was a SQL error
        }
    }
    
    protected void updateAnonymityStatus(String SessionID, boolean isAnonymous) {
        String sql = "UPDATE USERS SET isAnonymous = ? WHERE SessionID = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, isAnonymous);
            pstmt.setString(2, SessionID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    protected boolean isRoot(String SessionID, String userName) {
        String sql = "SELECT isRoot FROM USERS WHERE SessionID = ? AND UserName = ?";
        boolean isRoot = false;

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, SessionID);
            pstmt.setString(2, userName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    isRoot = rs.getBoolean("isRoot");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isRoot;
    }
    
    protected boolean isAnonymous(String SessionID) {
        String sql = "SELECT isAnonymous FROM USERS WHERE SessionID = ?";
        boolean isAnonymous = false;

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, SessionID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    isAnonymous = rs.getBoolean("isAnonymous");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isAnonymous;
    }
    
    protected void showUserHelp() {
        JFrame helpFrame = new JFrame("User Command Help");
        helpFrame.setSize(500, 400);
        helpFrame.setLocationRelativeTo(null);
        helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        ImageIcon icon = new ImageIcon(getClass().getResource("/gallery/icon.png"));
        helpFrame.setIconImage(icon.getImage());

        JTextArea helpText = new JTextArea();
        helpText.setText(
            "User Command Help:\n\n" +
            "/anonyme - Toggle anonymity on for yourself. Makes your username appear as 'Anonymous'.\n" +
            "/revealme - Reveal your actual username if you are currently anonymous.\n" +
            "/exit - Back to AnonyMain.\n" +
            "/help - Show this help information.\n"
        );
        helpText.setEditable(false);
        helpText.setCaretPosition(0);
        helpText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        helpText.setForeground(Color.LIGHT_GRAY);  // Set text color
        helpText.setBackground(Color.BLACK);       // Set background color
        helpText.setFont(new Font("Monospaced", Font.PLAIN, 12));  // Set font

        JScrollPane scrollPane = new JScrollPane(helpText);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        helpFrame.add(scrollPane);
        helpFrame.setVisible(true);
    }

    protected void showRootHelp() {
        JFrame helpFrame = new JFrame("Root Command Help");
        helpFrame.setSize(600, 500);
        helpFrame.setLocationRelativeTo(null);
        helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        ImageIcon icon = new ImageIcon(getClass().getResource("/gallery/icon.png"));
        helpFrame.setIconImage(icon.getImage());
        
        JTextArea helpText = new JTextArea();
        helpText.setText(
            "Root Command Help:\n\n" +
            "/INFO - Display server information (IP, Port, Privacy, SSL).\n" +
            "/broadcast <message> - Broadcast a message to all users.\n" +
            "/anonyme - Toggle anonymity on for yourself. Makes your username appear as 'Anonymous'.\n" +
            "/revealme - Reveal your actual username if you are currently anonymous.\n" +
            "/list - List all connected users.\n" +
            "/capa - Show current server capacity and number of online users.\n" +
            "/updcapa <new capacity> - Update the server capacity.\n" +
            "/kick <username> - Kick a user off the server.\n" +
            "/ban <username> - Ban a user from the server.\n" +
            "/unban <IP> - Remove a user IP from the ban list.\n" +
            "/showban - Show a list of banned users IP.\n" +
            "/toggleprivacy private or public or reset - Convert the privacy of the room to private or publuc or change the password.\n" +
            "/exit - Back to AnonyMain.\n" +
            "/shutdown - Shut down the server.\n" +
            "/help - Show this help information.\n"
        );
        helpText.setEditable(false);
        helpText.setCaretPosition(0);
        helpText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        helpText.setForeground(Color.LIGHT_GRAY);  // Set text color
        helpText.setBackground(Color.BLACK);       // Set background color
        helpText.setFont(new Font("Monospaced", Font.PLAIN, 12));  // Set font

        JScrollPane scrollPane = new JScrollPane(helpText);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        helpFrame.add(scrollPane);
        helpFrame.setVisible(true);
    }
    
    protected boolean updateServerPassword(String cRoomIP, int cRoomPort, String rootName, String newPass, String newSalt, boolean isPrivate) {
        // SQL query to update the password hash, salt, and private flag.
        String sql = "UPDATE CHATROOMS SET PASSWORDHASH = ?, SALT = ?, isPrivate = ? WHERE CRoomIP = ? AND CRoomPORT = ? AND RootName = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPass);  // Set the hashed password or null if public
            pstmt.setString(2, newSalt);            // Set the salt or null if public
            pstmt.setBoolean(3, isPrivate);      // Set the isPrivate flag
            pstmt.setString(4, cRoomIP);         // Server IP
            pstmt.setInt(5, cRoomPort);          // Server port
            pstmt.setString(6, rootName);        // Root user name

            int affectedRows = pstmt.executeUpdate();  // Execute the update
            return affectedRows > 0;  // Return true if the row was updated
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Return false if there was a SQL error
        }
    }
    
    protected boolean checkSessionIDInDatabase(String sessionID) {
        String query = "SELECT COUNT(*) FROM USERS WHERE SessionID = ?";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, sessionID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Default to false, indicating no conflict by default (consider more robust error handling)
    }
    
    protected String getUserIPByUsername(String username) {
        String query = "SELECT UserIP FROM USERS WHERE UserName = ? AND isOnline = TRUE";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("UserIP");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // Return null if the user is not found or an error occurs
    }
    
    protected static String updateIsRunning(boolean isRunning, String cRoomIP, int cRoomPort, String rootName) {
        String query = "UPDATE CHATROOMS SET isRunning = ? WHERE CRoomIP = ? AND CRoomPORT = ? AND RootName = ?";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, isRunning);
        	stmt.setString(2, cRoomIP);
            stmt.setInt(3, cRoomPort);
            stmt.setString(4, rootName);
            
            // Execute the update
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    protected static boolean getIsRunning(String cRoomIP, int cRoomPort, String rootName) {
        String query = "SELECT isRunning FROM CHATROOMS WHERE CRoomIP = ? AND CRoomPORT = ? AND RootName = ?";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, cRoomIP);
            stmt.setInt(2, cRoomPort);
            stmt.setString(3, rootName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("isRunning");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    protected static boolean isServerRunning(String sessionID) {
        String query = "SELECT c.isRunning FROM USERS u JOIN CHATROOMS c ON u.CRoomIP = c.CRoomIP AND u.CRoomPORT = c.CRoomPORT WHERE u.SessionID = ?";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, sessionID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("isRunning");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default return value if server running status cannot be determined
    }
    
    private boolean isRootRunningServer(String IP, String rootName) {
        String sql = "SELECT isRunning FROM CHATROOMS WHERE CRoomIP = ? OR RootName = ? AND isRunning = TRUE";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, IP);
            pstmt.setString(2, rootName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("isRunning"); // If there is any server running, return true
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to false if no server is running
    }
}
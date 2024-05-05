package com.anonychat.main.GUI;

import java.io.IOException;

public class User extends ChatJoin {
    public static final long serialVersionUID = AnonyFace.SERIAL_VERSION_UID;

    public User(String username, String server, int port, ChatClient client, boolean isAnony) throws IOException {
        super(username, server, port, client, isAnony);
    }
    
    // Handle user commands
    public void handleCommand(String command) throws IOException {
        String[] parts = command.trim().split("\\s+", 2);
        String cmd = parts[0].toLowerCase();

        switch (cmd) {
            case "/anonyme":
                toggleAnonymity(true);
                break;
            case "/revealme":
                toggleAnonymity(false);
                break;
            case "/exit":
            	exit();
            	break;
            case "/help":
                showHelp();
                break;
            default:
                client.sendMessage("/unknown");
                break;
        }
    }
    
    private void toggleAnonymity(boolean anonymous) throws IOException {
        if (anonymous) {
            client.sendMessage("/AnonyMe");
        } else {
            client.sendMessage("/RevealMe");
        }
    }
    
    private void exit() throws IOException {
        client.sendMessage("/exit");
    	chat.dispose();
        MainGUI mainGUI = new MainGUI();
        mainGUI.setVisible(true);  // Show the MainGUI
    }

    private void showHelp() throws IOException {
        client.sendMessage("/help");
    }
}
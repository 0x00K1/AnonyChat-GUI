package com.anonychat.main.GUI;

import java.io.IOException;

public class Root extends User {
    public static final long serialVersionUID = AnonyFace.SERIAL_VERSION_UID;
    
    public Root(String username, String server, int port, ChatClient client, boolean isAnony) throws IOException {
        super(username, server, port, client, isAnony);
    }
    
    // Extend user commands with additional root commands
    @Override
    public void handleCommand(String command) throws IOException {
        String[] parts = command.trim().split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1] : "";

        if (!handleRootCommands(cmd, argument)) {
            super.handleCommand(command);  // Handle common user commands
        }
    }

    private boolean handleRootCommands(String cmd, String argument) throws IOException {
        switch (cmd) {
            case "/broadcast":
                broadcastMessage(argument);
                return true;
            case "/list":
            	listusers();
                return true;
            case "/capa":
            	capacity();
                return true;
            case "/updcapa":
            	updcapacity(argument);
                return true;
            case "/kick":
                kick(argument);
                return true;
            case "/ban":
                ban(argument);
                return true;
            case "/unban":
                unban(argument);
                return true;
            case "/showban":
            	showbanned();
            	return true;
            case "/toggleprivacy":
                togglePrivacy(argument);
                return true;
            case "/shutdown":
                shutdownServer();
                return true;
            default:
                return false;  // Command not recognized here, might be a user command
        }
    }

    private void broadcastMessage(String message) throws IOException {
        client.sendMessage("/broadcast " + message);
    }
    
    private void listusers() throws IOException {
        client.sendMessage("/list");
    }
    
    private void capacity() throws IOException {
        client.sendMessage("/capa");
    }
    
    private void updcapacity(String message) throws IOException {
        client.sendMessage("/updcapa " + message);
    }

    private void kick(String username) throws IOException {
        client.sendMessage("/kick " + username);
    }

    private void ban(String username) throws IOException {
        client.sendMessage("/ban " + username);
    }
    
    private void unban(String message) throws IOException {
        client.sendMessage("/unban " + message);
    }
    
    private void showbanned() throws IOException {
        client.sendMessage("/showban");
    }
    
    public void togglePrivacy(String privacyState) throws IOException {
        this.isPrivate = "private".equals(privacyState);
        client.sendMessage("/toggleprivacy " + privacyState);
    }

    private void shutdownServer() throws IOException {
        client.sendMessage("/shutdown");
    }
}
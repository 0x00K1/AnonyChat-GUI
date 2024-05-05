package com.anonychat.main.GUI;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.net.ssl.SSLServerSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ChatServer extends ChatServerGUI {
    public static final long serialVersionUID = AnonyFace.SERIAL_VERSION_UID;
	
	private static String serverIP;
    private static int serverPort;
	private String serverName;
    private int serverCapacity;
    private boolean isPrivate;
    private String serverPass;
    private static String userRoot;
    private String passRoot;
    public static ServerSocket server;
    private static List<Handler> Handlers = new CopyOnWriteArrayList<>();
    private static final Map<String, Long> messageTime = new HashMap<>();
    private static final Set<String> bannedIPs = new HashSet<>();
    private static final Set<String> RESERVED_USERNAMES = new HashSet<>(
            Arrays.asList("SERVER", "ANONYMOUS", "BROADCAST")
        );

    @SuppressWarnings("static-access")
	public ChatServer(String serverIP, int serverPort, String serverName, int serverCapacity, boolean isPrivate, String serverPass, String userRoot, String passRoot) {
        this.serverIP = serverIP;
    	this.serverPort = serverPort;
    	this.serverName = serverName;
        this.serverCapacity = serverCapacity;
        this.isPrivate = isPrivate;
        this.serverPass = serverPass;
        this.userRoot = userRoot;
        this.passRoot = passRoot;
    }

    public void startServer() {
        new Thread(() -> {
            try {
            	server = new ServerSocket(serverPort);
                isServerRunning = true;
                updateIsRunning(isServerRunning, serverIP, serverPort, userRoot);
                while (isServerRunning) {
                    Socket clientSocket = server.accept(); // Accept a connection
                    Handler handler = new Handler(clientSocket);
                    handler.start(); // Start a new thread for handling the connection
                    Handlers.add(handler); // Add to the list of handlers
                }
            } catch (IOException e) {
                // System.err.println("Error starting the server: " + e.getMessage());
            	isServerRunning = false;
            } finally {
            	stopServer();
            }
        }).start();
    }

    public static void stopServer() {
    	isServerRunning = false;
        updateIsRunning(isServerRunning, serverIP, serverPort, userRoot);
        try {
            for (Handler handler : Handlers) {
                handler.closeConnection();
            }
            if (server != null) {
            	server.close();
            }
        } catch (IOException e) {
            // System.err.println("Error stopping the server: " + e.getMessage());
        }
    }
    
    protected boolean isSSLEnabled() {
        return server instanceof SSLServerSocket;
    }

    private class Handler extends Thread {
        private Socket socket;
        private String SessionID;
        private PrintWriter out;
        private BufferedReader in;
    	private String username;
		private boolean VALID;

        public Handler(Socket socket) {
            this.socket = socket;
            this.SessionID = generateSessionID(); // Generate a unique session ID
            try {
                // Initialize input and output streams
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                // System.err.println("Error setting up client handler streams: " + e.getMessage());
                closeConnection(); // Ensure resources are freed and the handler is removed
            }
        }

        @Override
        public void run() {
            try {     	
                if(!authenticator(socket, in, out)) return;
                
                updateUserStatusInDatabase(this.SessionID, true, serverIP, serverPort, userRoot); // Mark as online
                
                String input;
                final int MAX_MESSAGE_LENGTH = 50; // 50 Good for our GUI
                final long MIN_TIME_BETWEEN_MESSAGES = 500; // 0.5s
                while ((input = in.readLine()) != null) {
                	
                	// Check if the client has closed the connection.
                    if (socket.isClosed() || !socket.isConnected()) {
                        break;
                    }
                    
                	// NO SPAM.
                    long currentTime = System.currentTimeMillis();
                    Long lastMessageTime = messageTime.getOrDefault(username, 0L);
                    if (currentTime - lastMessageTime < MIN_TIME_BETWEEN_MESSAGES) {
                        out.println("[SERVER]> NO SPAM!!");
                        continue;
                    }
                    messageTime.put(username, currentTime);
                    
                    // Length check.
                    if (input.length() > MAX_MESSAGE_LENGTH) {
                        out.println("[SERVER]> Your message is too long. Maximum " + MAX_MESSAGE_LENGTH + " characters.");
                        continue;
                    }
                    
                    if (input.startsWith("/")) {
                        if (isRoot(this.SessionID, this.username)) {
                            // This is a root user, handle root-specific commands
                            handleRootCommand(input);
                        } else {
                            // Handle regular user commands
                            handleUserCommand(input);
                        }
                    } else {
                        // Handle regular message broadcasting
                        broadcastMessage(this.username, input);
                    }
                }
            } catch (IOException e) {
                // System.err.println("Exception in client handler: " + e.getMessage());
            } finally {
            	if (this.VALID) updateUserStatusInDatabase(this.SessionID, false, serverIP, serverPort, userRoot); // mark as offline           
                closeConnection();
            }
        }
        
        private void broadcastMessage(String senderUsername, String message) {
            String formattedMessage = senderUsername + ": " + message;
            
            // Iterate over all handlers (clients)
            for (Handler handler : Handlers) {
                // Check if the sender is anonymous and adjust the sender's username accordingly for non-root users
                if (isAnonymous(this.SessionID) && !isRoot(handler.SessionID, handler.username)) {
                    formattedMessage = "Anonymous: " + message;
                } else {
                    formattedMessage = this.username + ": " + message;
                }

                handler.out.println(formattedMessage);
            }
        }
        
        private boolean authenticator(Socket socket, BufferedReader in, PrintWriter out) throws IOException {
        	String userIP = socket.getInetAddress().getHostAddress();
        	boolean Rooter = false;
        	boolean Anonymous = false;
        	try {
        		if (isPrivate) {
	        		if (serverPass == null || serverPass.trim().isEmpty()) {
	        			out.println("NOPASSWORD");
	        		} else {
	        			out.println("REQUESTSERVERPASSWORD");
	        			String clientServerPassword = in.readLine();
	        			Map<String, String> serverCredentials = getServerCredentials(serverIP, serverPort, userRoot);
	        			String serverSalt = serverCredentials.get("salt");
	
	        			if (clientServerPassword == null || !SecurityUtils.validatePassword(clientServerPassword, serverSalt, serverPass)) {
	        			    out.println("WRONGSERVERPASSWORD");
	        			    return false;
	        			}
	        		}
        		} else {
        			out.println("NOPASSWORD");
        		}

        		out.println("REQUESTUSERNAME");
        		String GETusername = in.readLine();
        		if (GETusername == null || GETusername.trim().isEmpty()) {
        		    out.println("INVALIDUSERNAME");
        		    return false;
        		} else if (isUserOnline(GETusername)) {
        		    out.println("INVALIDUSER");
        		    return false;
        		} else if (!Character.isLetter(GETusername.charAt(0))) {
        		    out.println("INVALIDSTRINGUSERNAME"); // Must start with a letter
        		    return false;
        		} else if (!GETusername.matches("[A-Za-z0-9]+")) {
        		    out.println("INVALIDALPHANUMERICUSERNAME"); // Must be alphanumeric
        		    return false;
        		} else if (isUsernameReserved(GETusername)) {
        		    out.println("RESERVEDUSER");
        		    return false;
        		}
        		GETusername = GETusername.trim();

        		String Root = getRootNameFromChatroom(serverIP, serverPort, userRoot);
				if (Root.equalsIgnoreCase(GETusername)) {
        			out.println("REQUESTROOTPASSWORD");
        			String clientRootPassword = in.readLine();
        			Map<String, String> credentials = getRootPasswordAndSalt(Root);
                    String storedSalt = credentials.get("salt");
        			if (clientRootPassword == null || !SecurityUtils.validatePassword(clientRootPassword, storedSalt,  passRoot)) {
        				out.println("WRONGROOTPASSWORD");
        				return false;
        			}
        			Rooter = true;
        		}
				
				// Ban ?
				if (isIPBanned(userIP)) {
			        out.println("BANNED");
			        return false;
			    }
				
				// تهقى به مكان
				if (isRoomFull(serverIP, serverPort, Rooter)) {
				    out.println("SERVERFULL");
				    return false;
				}
        	    
        	    //	***********
        	    //	* Welcome *
        	    //	***********
        	    this.VALID = true;
				out.println("VALID"); // SYN-ACK
               
				// Set
        		this.username = GETusername;        		
        		
        		// AnonyMe.
				String isAnony = in.readLine();
        		if("ANONYMOUS".equalsIgnoreCase(isAnony)) Anonymous = true;
        		
        		// hiDB
                insertUserData(SessionID, serverIP, serverPort, userRoot, userIP, username, Rooter, Anonymous, true);
        		
        		// INFO 
        		out.println("INFO");
        		out.println(serverName);
        		out.println(serverCapacity);
        		out.println(isPrivate);
        		out.println(SessionID);
        		
        		// Hey.
        		out.println("LOGINPASS");
        		return true;
        	} catch (IOException e) {
        		e.printStackTrace();
        		return false;
        	}
        }

        private String generateSessionID() {
            String uniqueID;
            do {
                uniqueID = UUID.randomUUID().toString();
            } while (checkSessionIDInDatabase(uniqueID));
            return uniqueID;
        }

        private boolean isRoomFull(String cRoomIP, int cRoomPort, boolean isRooter) {
            // Directly return true if the room is full, false otherwise.
            // isRooter indicates if the current connection attempt is by the root user.
            if (isRooter) {
                return false; // Root user can always join.
            }
            
            int currentOnlineUsers = getOnlineUsersCount(cRoomIP, cRoomPort, userRoot);
            return currentOnlineUsers >= serverCapacity;
        }

        private void handleRootCommand(String command) {
            String[] parts = command.split(" ", 2);
            String cmd = parts[0].toLowerCase();
            String argument = (parts.length > 1) ? parts[1] : "";

            switch (cmd) {
                case "/broadcast":
                    broadcastAllUsers(argument);
                    break;
                case "/anonyme":
                    AnonyMe();
                    break;
                case "/revealme":
                    RevealMe();
                    break;
                case "/list":
                    listConnectedUsers();
                    break;
                case "/capa":
                    showServerCapacity();
                    break;
                case "/updcapa":
                    updateServerCapacity(argument);
                    break;
                case "/kick":
                    kickUser(argument);
                    break;
                case "/ban":
                    banUser(argument);
                    break;
                case "/unban":
                    unbanUser(argument);
                    break;
                case "/showban":
                    showBannedUsers();
                    break;
                case "/toggleprivacy":
                    togglePrivacy(argument);
                    break;
                case "/exit":
                	exit();
                	break;
                case "/shutdown":
                    shutdownServer();
                    break;
                case "/help":
                    showHelp();
                    break;
                default:
                    out.println("[SERVER]> Unknown command, Type '/help' for help.");
                    break;
            }
        }
        
        private void handleUserCommand(String command) {
            String[] parts = command.split(" ", 2);
            String cmd = parts[0].toLowerCase();

            switch (cmd) {
                case "/anonyme":
                	AnonyMe();
                	break;
                case "/revealme":
                	RevealMe();
                    break;
                case "/exit":
                	exit();
                	break;
                case "/help":
                    showHelp();
                    break;
                default:
                    out.println("[SERVER]> Unknown command, Type '/help' for help.");
                    break;
            }
        }
        
        private void AnonyMe() {
            if (!isAnonymous(this.SessionID)) {
                updateAnonymityStatus(this.SessionID, true);
                this.out.println("[SERVER]> Anonymous mode activated!");
            } else {
                this.out.println("[SERVER]> You are already anonymized.");
            }
        }

        private void RevealMe() {
            if (isAnonymous(this.SessionID)) {
                updateAnonymityStatus(this.SessionID, false);
                this.out.println("[SERVER]> Your username is now back to " + this.username);
            } else {
                this.out.println("[SERVER]> You are not currently anonymized.");
            }
        }
        
        private void broadcastAllUsers(String message) {
            for (Handler handler : Handlers) {
                handler.out.println("[SERVER]> " + message);
            }
        }

        private void listConnectedUsers() {
            StringBuilder sb = new StringBuilder("[SERVER]> Connected Users:\n");
            for (Handler handler : Handlers) {
                sb.append(handler.username).append("\n");
            }
            out.println(sb.toString());
        }
        
        private void showServerCapacity() {
            out.println("[SERVER]> Current Capacity: " + serverCapacity + ", Online Users: " + Handlers.size());
        }
        
        private void updateServerCapacity(String newCapacityStr) {
            try {
                int newCapacity = Integer.parseInt(newCapacityStr);
                if (newCapacity >= Handlers.size() && newCapacity < 100) {
                    if (updateCapacityInDatabase(newCapacity, serverIP, serverPort)) {  // Update the database and check if successful
                        serverCapacity = newCapacity;  // Update the server variable if database update is successful
                        out.println("[SERVER]> Capacity updated to " + newCapacity + ".");
                    } else {
                        out.println("[SERVER]> Failed to update capacity in the database.");
                    }
                } else {
                    out.println("[SERVER]> New capacity must be greater than the number of connected users (" 
                                + Handlers.size() + ") and less than 100.");
                }
            } catch (NumberFormatException e) {
                out.println("[SERVER]> Invalid capacity value. Please enter a valid integer.");
            }
        }
        
        private void kickUser(String username) {
            Handler toKick = Handlers.stream()
                                     .filter(h -> h.username.equalsIgnoreCase(username) && !isRoot(h.SessionID, h.username))
                                     .findFirst()
                                     .orElse(null);

            if (toKick != null) {
                toKick.out.println("[SERVER]> You have been kicked out by the Root. Type '/exit'");
                toKick.closeConnection();
                out.println("[SERVER]> User '" + username + "' has been kicked out.");
            } else {
                out.println("[SERVER]> User '" + username + "' not found or is the root.");
            }
        }

        private void banUser(String username) {
            Handler userHandler = Handlers.stream()
                                          .filter(h -> h.username.equalsIgnoreCase(username))
                                          .findFirst()
                                          .orElse(null);

            if (userHandler != null && !isRoot(userHandler.SessionID, userHandler.username)) {
                String ipToBan = userHandler.socket.getInetAddress().getHostAddress();

                if (!bannedIPs.contains(ipToBan)) {
                    bannedIPs.add(ipToBan);
                    Handlers.stream()
                            .filter(handler -> handler.socket.getInetAddress().getHostAddress().equals(ipToBan))
                            .forEach(handler -> {
                                handler.out.println("[SERVER]> You have been banned by the Root. Type '/exit'");
                                handler.closeConnection();  // Disconnect after sending the ban message.
                            });
                    out.println("[SERVER]> IP '" + ipToBan + "' associated with username '" + username + "' has been banned and disconnected.");
                } else {
                    out.println("[SERVER]> IP '" + ipToBan + "' is already banned.");
                }
            } else {
                out.println("[SERVER]> User '" + username + "' not found or is the root.");
            }
        }

        private void unbanUser(String ip) {
        	ip = ip.trim(); // Normalize the IP address input
        	if (bannedIPs.remove(ip)) {
        	    out.println("[SERVER]> IP '" + ip + "' has been unbanned.");
        	} else {
        		out.println("[SERVER]> IP '" + ip + "' is not in the banned list.");
        	}
       }

        private void showBannedUsers() {
        	if (bannedIPs.isEmpty()) {
                out.println("[SERVER]> No IPs are currently banned.");
            } else {
                String bannedIPList = String.join(", ", bannedIPs);
                out.println("[SERVER]> Banned IPs: " + bannedIPList);
            }
       }
        
        private void togglePrivacy(String privacy) {
            if ("private".equals(privacy)) {
                isPrivate = true;
                String newServerPass = passwordInput("Enter server password:");
                if (newServerPass.length() < 4 || newServerPass.length() > 99) {
                	out.println("[SERVER]> Server Password must be between 4 and 99 characters.");
                	return;
                }                 
                serverPass = newServerPass;
                updateServerPassword(serverIP, serverPort, userRoot, serverPass, isPrivate);
                out.println("[SERVER]> Server set to private.");
            } else if ("public".equals(privacy)) {
                isPrivate = false;
                updateServerPassword(serverIP, serverPort, userRoot, serverPass, isPrivate);
                out.println("[SERVER]> Server set to public.");
            } else {
                out.println("[SERVER]> Invalid privacy setting. Use 'private' or 'public'.");
            }
        }
        
        private void exit() {
        	closeConnection();
        }

        private void shutdownServer() {
        	broadcastAllUsers("Server is down.");
        	closeConnection();
            stopServer();
            // System.exit(0); // Bug!! (Don't use system functions in Root commands)
        }

        private void showHelp() {
            if (isRoot(this.SessionID, this.username)) {
            	showRootHelp();
            } else {
            	showUserHelp();
            }
        }

        public void closeConnection() {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException e) {
                showError("Error closing client connection: " + e.getMessage());
            } finally {
                Handlers.remove(this);
            }
        }
        
        private boolean isIPBanned(String ip) {
            return bannedIPs.contains(ip);
        }
        
        // Checks if the provided user name is in the list of reserved user names.
        private static boolean isUsernameReserved(String username) {
            for (String reserved : RESERVED_USERNAMES) {
                if (reserved.equalsIgnoreCase(username)) {
                    return true;
                }
            }
            return false;
        }
    }
}
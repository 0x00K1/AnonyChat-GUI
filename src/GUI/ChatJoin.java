package com.anonychat.main.GUI;

public class ChatJoin extends ChatClient {
    public static final long serialVersionUID = AnonyFace.SERIAL_VERSION_UID;
    
	protected String jusername;
    private String server;
    private int port;
    private boolean isAnony;
    protected ChatClient client;
    protected boolean isRoot; // Flag to determine if the user is a root user
    
    String serverName;
    int serverCapacity;
    boolean isPrivate;
    String SessionID;

    public ChatJoin(String username, String server, int port, ChatClient existingClient, boolean isAnony) {
        super(existingClient);

        this.jusername = username;
        this.server = server;
        this.port = port;
        this.isAnony = isAnony;
        this.client = existingClient != null ? existingClient : new ChatClient();
     }

    // The initial handshake with the server.
    // It handles various server responses like request for server password, send user name, bans, root, and anonymous.
    public boolean serverReq() {
    	try {
            client.startConnection(server, port); // start a connection with the server

            String serverResponse = client.receiveMessage();
            if ("NOPASSWORD".equals(serverResponse)) {
            	serverResponse = client.receiveMessage();
            } else if ("REQUESTSERVERPASSWORD".equals(serverResponse)) {
                String serverPass = passwordInput("Enter server password:");
                client.sendMessage(serverPass);

                serverResponse = client.receiveMessage();
                if ("WRONGSERVERPASSWORD".equals(serverResponse)) {
                	showError("Wrong server address or password. Try again.");
                    return false;
                }
            } else {
            	showError("Somthing Wrong!!");
                return false;
            }
            
            client.sendMessage(jusername);
            serverResponse = client.receiveMessage();
            if ("INVALIDUSERNAME".equals(serverResponse)) {
                showError("Invalid Username. Try again.");
                return false;
            } else if ("INVALIDUSER".equals(serverResponse)) {
            	showError("User already exists. Try again with a different username.");
            	return false;
            } else if ("INVALIDSTRINGUSERNAME".equals(serverResponse)) {
            	showError("Username must start with a letter. Try again.");
            	return false;
            } else if ("INVALIDALPHANUMERICUSERNAME".equals(serverResponse)) {
            	showError("Username must contain only letters and numbers. Try again");
            	return false;
            } else if ("RESERVEDUSER".equals(serverResponse)) {
            	showError("The chosen username is reserved. Select a different username.");
            	return false;
            } else if ("REQUESTROOTPASSWORD".equals(serverResponse)) {
                String rootPass = passwordInput("Enter root password:");
                client.sendMessage(rootPass);

                serverResponse = client.receiveMessage();
                if ("WRONGROOTPASSWORD".equals(serverResponse)) {
                	showMessage("Wrong Cardinality.");
                    return false;
                } else {
                	isRoot = true;
                }
            } else {
            	if ("BANNED".equals(serverResponse)) {
                	showError("You are banned from this server.");
                    return false;
                }
            	
            	if ("SERVERFULL".equals(serverResponse)) {
            		showError("Server Full.");
                    return false;
                }
            	if ("VALID".equals(serverResponse)) {/*SYN-ACK*/}
            }
            
            // Anonymous
            client.sendMessage(isAnony ? "ANONYMOUS" : "NONANONYMOUS");

            // Room INFO
            serverResponse = client.receiveMessage();
            if ("INFO".equals(serverResponse)) {
                serverName = client.receiveMessage(); // Read server name
                serverCapacity = Integer.parseInt(client.receiveMessage()); // Read server capacity
                isPrivate = Boolean.parseBoolean(client.receiveMessage()); // Read privacy status
                SessionID = client.receiveMessage(); // Read session ID
            }
            
            // Final check to see if the server returned a successful login response.
            serverResponse = client.receiveMessage();
            return "LOGINPASS".equals(serverResponse);
        } catch (Exception e) {
            // e.printStackTrace(); // Debugging
        	showError("Unable to connect to the server at " + server + ":" + port + ". Check your internet connection or try again later.");
            return false;
        }
    }
}
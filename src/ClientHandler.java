import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class ClientHandler extends Thread {
	private Socket socket;
	private String clientIP;
	private int clientPort;
	private int clientNumber;
	private Directory serverDir; //directory that current client is in
	
	public ClientHandler(Socket socket, int clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber;
		System.out.println("New connection with client#" + clientNumber + " at" + socket);
		serverDir = new Directory();
		clientIP = socket.getInetAddress().getHostAddress();
		clientPort = socket.getPort();
	}
	public void run() {
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			CommandHandler ch = new CommandHandler();
			out.writeInt(clientNumber);
			boolean running = true;
			String command = "";
			LocalDateTime now = LocalDateTime.now();
			String time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
			while(running) {
				out.writeUTF(serverDir.getDir());
				command = in.readUTF();
				if (command.startsWith("exit")) {
					running = false;
				}
				now = LocalDateTime.now();
				time = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd@HH:mm:ss"));
				System.out.println('['+clientIP+':'+clientPort+ " - " + time + "]: " + command);
				
				out.writeUTF(ch.handle(command, serverDir));
				if (command.startsWith("upload ")) {
					CommandHandler.uploadFileServer(serverDir,command,in,out);
				}
				else if (command.startsWith("download ")) {
					CommandHandler.downloadFileServer(serverDir,command,in,out);
				}
			}
			
			
		} catch (IOException e) {
			System.out.println("Error handling client # " + clientNumber + ": " + e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error uploading/downloading file for #" + clientNumber);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Couldn't close a socket, what's going on?");
			}
			System.out.println("Connection with client# " + clientNumber + " closed");
		}
	}
}
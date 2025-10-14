import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
					String uploadFileName = command.substring(7);
					File f = new File(serverDir.getDir() + File.separator + uploadFileName);
					if (f.exists()) {
						out.writeBoolean(false);
					}
					else {
						out.writeBoolean(true);
					}
					FileOutputStream fos = new FileOutputStream(serverDir.getDir() +File.separator + uploadFileName);
					long fileLength = in.readLong();
					String checksum = in.readUTF();
					long totalRead = 0;
				    byte[] buffer = new byte[8192];
					while(totalRead < fileLength) {
				        int bytesToRead = (int) Math.min(buffer.length, fileLength - totalRead);
				        int bytesRead = in.read(buffer, 0, bytesToRead);
				        if (bytesRead == -1) {
				        	break;
				        }
				        fos.write(buffer, 0, bytesRead);
				        totalRead += bytesRead;
					}
					fos.close();
					try {
						if (checksum.equals(MD5Calc.getMD5(serverDir.getDir() + File.separator + uploadFileName))) {
							out.writeBoolean(true);
						}
						else {
							out.writeBoolean(false);
							serverDir.remove(uploadFileName);
						}
					} catch (Exception e) {
						System.out.print("Checksum failed for " + uploadFileName);
						out.writeBoolean(false);
						serverDir.remove(uploadFileName);
					}
					
				}
				else if (command.startsWith("download ")) {
					String uploadFileName = serverDir.getDir() +File.separator + command.substring(9);
					File f = new File(uploadFileName);
					if (f.isFile()) {
						out.writeBoolean(true);
						FileInputStream fis = new FileInputStream(uploadFileName);
						out.writeLong(f.length());
						String checksum = "inv";
						try {
							checksum = MD5Calc.getMD5(uploadFileName);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.print("Checksum failed for " + uploadFileName);
						}
						out.writeUTF(checksum);
						byte[] buffer = new byte[8192];
						int bytesRead = 0;
						while((bytesRead = fis.read(buffer)) != -1) {
							out.write(buffer,0,bytesRead);
						}
						fis.close();
					}
					else {
						out.writeBoolean(false);
					}
					
				}
			}
			
			
		} catch (IOException e) {
			System.out.println("Error handling client # " + clientNumber + ": " + e);
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
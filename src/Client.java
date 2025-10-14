import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;
// Application client
public class Client {
	private static Socket socket;
	public static void main(String[] args) throws Exception {
		// Address input
		InputValidator iv = new InputValidator();
		
		String serverAddress = iv.inputIP();
//		String serverAddress = "127.0.0.1"; // to remove
		int port = iv.inputPort();
//		int port = 5000; // to remove
		socket = new Socket(serverAddress, port);
		System.out.format("Server : [%s:%d]%n", serverAddress, port);
		
		//STREAMS
		DataInputStream in = new DataInputStream(socket.getInputStream());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());		
		
		int clientNo = in.readInt();
		System.out.format("You are client : %d %n",clientNo);
		boolean running = true;
		String currPath = "";
		String uinput = "";
		while (running) {
			currPath = in.readUTF();
			System.out.print("["+currPath+"]:");
			uinput = iv.inputCommand();
			if (uinput.startsWith("exit")) {
				running = false;
			}
			out.writeUTF(uinput);
			System.out.print(in.readUTF() + '\n');
			
			//uploading files
			if (uinput.startsWith("upload ")) {
				if (in.readBoolean()) {
					uinput = uinput.substring(7);
					File f = new File(uinput);
					FileInputStream fis = new FileInputStream(uinput);
					out.writeLong(f.length());
					out.writeUTF(MD5Calc.getMD5(uinput));
					byte[] buffer = new byte[8192];
					int bytesRead = 0;
					while((bytesRead = fis.read(buffer)) != -1) {
						out.write(buffer,0,bytesRead);
					}
					fis.close();
					if (in.readBoolean()) {
						System.out.println("Send successful, no errors in file.");
					}
					else {
						System.out.println("Errors in file, try uploading again!");
					}
				}
				else {
					System.out.println("File already exists on server.");
				}
			}
			//downloading files
			else if (uinput.startsWith("download ")) {
				if (in.readBoolean()) {
					uinput = uinput.substring(9);
					long fileLength = in.readLong();
					String checksum = in.readUTF();
					FileOutputStream fos = new FileOutputStream(uinput);
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
					if (checksum.equals(MD5Calc.getMD5(uinput))){
						System.out.println("Successful download of file : " + uinput);
					}
					else {
						System.out.println("Download of file " + uinput + " failed, try downloading again");
						File f = new File(uinput);
						f.delete();
					}
				}
				else {
					System.out.println("File does not exist.");
				}
			}
			
		}
		in.close();
		out.close();
		socket.close();
	}
}
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
// Application client
public class Client {
	private static Socket socket;
	public static void main(String[] args) throws Exception {
		System.out.println("Client :");
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
				System.out.println(CommandHandler.uploadFileClient(in, out, uinput));
			}
			//downloading files
			else if (uinput.startsWith("download ")) {
				System.out.println(CommandHandler.downloadFileClient(in,out,uinput));
			}
			
		}
		in.close();
		out.close();
		socket.close();
		iv.close();
	}
}
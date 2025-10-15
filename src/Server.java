import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
public class Server {
	private static ServerSocket Listener;
	
	public static void main(String[] args) throws Exception {
		System.out.println("Server :");
		InputValidator iv = new InputValidator();
		int clientNumber = 0;
		String serverAddress = iv.inputIP();
//		String serverAddress = "127.0.0.1"; // to remove
		int serverPort = iv.inputPort();
//		int serverPort = 5000; // to remove
		Listener = new ServerSocket();
		Listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(serverAddress);
		Listener.bind(new InetSocketAddress(serverIP, serverPort));
		System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
		try {
			while (true) {
				new ClientHandler(Listener.accept(), clientNumber++).start();
			}
		} finally {
			Listener.close();
		} 
	} 
}
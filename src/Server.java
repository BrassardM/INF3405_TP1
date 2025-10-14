import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;
public class Server {
	private static ServerSocket Listener;
	
	private static String inputIP() {
		InputValidator iv = new InputValidator();
		System.out.print("Input the server's IP address : ");
		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine();
		if (iv.isValid(input)) {
			System.out.format("The IP address : [%s] is valid %n",input);
			return input;
		}
		else {
			System.err.format("The IP address : [%s] is invalid%n",input);
			return inputIP();
		}
	}
	
	private static int InputPort() {
		System.out.print("Input the server's port (between 5000 & 5050) : ");
		Scanner scanner = new Scanner(System.in);
		int input = scanner.nextInt();
		if (input >= 5000 && input <= 5050) {
			System.out.format("The port %d is valid.%n", input);
			return input;
		}
		else {
			System.err.format("The port %d is invalid.%n",input);
			return InputPort();
		}
	}
	
	public static void main(String[] args) throws Exception {
		int clientNumber = 0;
		String serverAddress = inputIP();
		int serverPort = InputPort();
		Listener = new ServerSocket();
		Listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(serverAddress);
		// Association de l'adresse et du port à la connexien
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
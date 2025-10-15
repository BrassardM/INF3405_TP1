import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class CommandHandler {
	public String handle(String command, Directory dir) {
		if (command.startsWith("cd ")) {
			command = command.substring(3);
			if (command.equals("..")) {
				dir.goBack();
				return "current directory is now : " + dir.getDir();
			}
			else if (dir.goToChild(command)) {
				return "current directory is now : " + dir.getDir();
			}
			else {
				return "invalid directory, directory is still : " + dir.getDir();
			}
		}
		else if (command.startsWith("ls")) {
			return dir.listFiles();
		}
		else if (command.startsWith("mkdir ")) {
			command = command.substring(6);
			if (dir.createDir(command)) {
				return "successfully created " + command + " in " + dir.getDir();
			}
			else {
				return "impossible to make dir : " + command + " it's likely that the file/folder already exists";
			}
		}
		else if (command.startsWith("upload ")) {
			return "preparing to " + command;
		}
		else if (command.startsWith("download ")) {
			return "donwloading to " + command;
		}
		else if (command.startsWith("delete ")) {
			command = command.substring(7);
			if (dir.remove(command)) {
				return "successfully removed " + command;
			}
			else {
				return "remove failed " + command + " it's likely that the file/folder does not exist";
			}
		}
		else if (command.startsWith("exit")){
			return "exiting server";
		}
		return "Somehow Invalid";
	}
	
	public static String uploadFileClient(DataInputStream in, DataOutputStream out, String uinput) throws Exception{
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
				return "Send successful, no errors in file.";
			}
			else {
				return "Errors in file, try uploading again!";
			}
		}
		else {
			return "File already exists on server.";
		}
	}
	
	public static String downloadFileClient(DataInputStream in, DataOutputStream out, String uinput) throws Exception{
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
				return "Successful download of file : " + uinput;
			}
			else {
				File f = new File(uinput);
				f.delete();
				return "Download of file " + uinput + " failed, try downloading again";
			}
		}
		else {
			return "File does not exist.";
		}
	}
	
	public static void uploadFileServer(Directory serverDir, String command, DataInputStream in, DataOutputStream out) throws Exception {
		String uploadFileName = command.substring(7);
		File f = new File(serverDir.getDir() + File.separator + uploadFileName);
		if (f.exists()) {
			out.writeBoolean(false);
			return;
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
			out.writeBoolean(false);
			serverDir.remove(uploadFileName);
		}
		
	}
	
	public static void downloadFileServer(Directory serverDir, String command, DataInputStream in, DataOutputStream out) throws Exception {
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

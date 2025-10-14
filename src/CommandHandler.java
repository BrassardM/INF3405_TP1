
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
}

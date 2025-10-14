import java.io.File;

public class Directory {
	private String dir;
	
	public Directory() {
		dir = System.getProperty("user.dir");
		goBack();
	}
	
	final public String getDir() {
		return dir;
	}
	
	public boolean goBack() {
		int lastslash = dir.lastIndexOf(File.separator);
		if (lastslash > 0 ) {
			dir = dir.substring(0,lastslash);
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean goToChild(String path) {
		String nextpath = dir+File.separator+path;
		if (new File(nextpath).isDirectory()) {
			dir = nextpath;
			return true;
		}
		else {
			return false;
		}

	}
	
	public boolean createDir(String dirName) {
		String dirPath = dir + File.separator + dirName;
		File folder = new File(dirPath);
		return folder.mkdir();
	}
	
	public String listFiles() {
		File currFolder = new File(dir);
		
		File[] contents = currFolder.listFiles();
		if (!(contents == null || contents.length == 0)) {
			String out = "Folder contents as follows : \n";
			for (File file : contents) {
				if (file.isDirectory()) {
					out += "\t[DIR]   :" + file.getName() +'\n';
				}
				else {
					out += "\t[FILE]  :" + file.getName() + '\n';
				}
			}
			return out;
			
		}
		else {
			return "Folder is empty\n";
		}
	}
	
	public File getFile(String name) {
		File target = new File(dir + File.separator + name);
		if (target.isFile()) {
			return target;
		}
		else {
			return null;
		}
	}
	
	public boolean remove(String path) {
		File target = new File(dir + File.separator + path);
		if (target.isFile()) {
			target.delete();
			return true;
		}
		else if (target.isDirectory()) {
			for (File child : target.listFiles()) {
				remove(path+File.separator+child.getName());
			}
			target.delete();
			return true;
		}
		else {
			return false;
		}
	}
}

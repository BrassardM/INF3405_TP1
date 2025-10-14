import java.io.FileInputStream;
import java.security.MessageDigest;

public class MD5Calc {
	public static String getMD5(String path) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		FileInputStream fis = new FileInputStream(path);
		byte buffer[] = new byte[8192];
		int readBytes = 0;
		while((readBytes = fis.read(buffer)) != -1) {
			md.update(buffer,0,readBytes);
		}
		
		byte[] digest = md.digest();
		StringBuilder out = new StringBuilder();
		for (byte d : digest) {
			out.append(String.format("%02x", d));
		}
		fis.close();
		return out.toString();
	}
}

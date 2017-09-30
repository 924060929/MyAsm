package utils;

import java.util.UUID;

public class Uuid {
	public static String getId() {
		String s = UUID.randomUUID().toString();
		return s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24); 
	}
	public static String getFileName(String fileName) {
		int begin = fileName.lastIndexOf(".");
		return getId() + fileName.substring(begin);
	}
}

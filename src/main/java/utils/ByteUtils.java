package utils;

/**
 * <p>Title: ByteUtils</p>
 * <p>Description: </p>
 * <p>Company: null</p> 
 * @author simon
 * @date 2014年11月23日 下午8:08:32
 */
public class ByteUtils {
	private ByteUtils() {}
	/**
	 * <p>Title: bytesToInt</p>
	 * <p>Description: 将b数组从begin开始length长度的数组转换成整数</p>
	 * @param b
	 * @param begin
	 * @param length
	 * @return
	 */
	public static int bytesToInt(byte[] b, int begin, int length) {
		if (b == null || begin < 0 || length < 1 || begin + length > b.length) {
			return 0;
		}
		if (length > 4) {
			begin = begin + length - 4;
		}
		int res = 0;
		for (int i = begin; i < begin + length; ++i) {
			res <<= 8;
			res += b[i] & 0xff;
		}
		return res;
	}
	
	/**
	 * <p>Title: bytesToInt</p>
	 * <p>Description: 将b数组转换成int</p>
	 * @param b
	 * @return
	 */
	public static int bytesToInt(byte[] b) {
		if (b == null) {
			return 0;
		}
		
		return bytesToInt(b, 0, b.length);
	}
	
	
	/**
	 * <p>Title: intToBytes</p>
	 * <p>Description: 将int转换成长度为length的byte数组</p>
	 * @param value
	 * @param length
	 * @return
	 */
	public static byte[] intToBytes(int value, int length) {
		if (length < 1) {
			return null;
		}
		byte[] bs = new byte[length];
		for (int i = length-1; i >= 0; --i) {
			bs[i] = (byte)(value & 0xff);
			value >>= 8;
		}
		
		return bs;
	}
	/**
	 * <p>Title: longToBytes</p>
	 * <p>Description: 将long转换成长度为length的byte数组</p>
	 * @param value
	 * @param length
	 * @return
	 */
	public static byte[] longToBytes(long value, int length) {
		if (length < 1) {
			return null;
		}
		byte[] bs = new byte[length];
		for (int i = length-1; i >= 0; --i) {
			bs[i] = (byte)(value & 0xff);
			value >>= 8;
		}
		
		return bs;
	}
	
	/**
	 * <p>Title: printBytes</p>
	 * <p>Description: 测试用，打印无符号的byte数组</p>
	 * @param bArray
	 */
	public static void printBytes(byte[] bArray, int begin, int length) {
		if (bArray == null) {
			return;
		}
		if (begin < 0 || length < 1 || begin + length > bArray.length) {
			return;
		}
		for (int i = begin; i < begin + length; ++i) {
			System.out.print((bArray[i] & 0xff) + " ");
		}
		System.out.println();
	}
	
	public static void printBytes(byte[] bArray) {
		if (bArray == null) {
			return;
		}
		printBytes(bArray, 0, bArray.length);
	}
	
	public static void printHex(byte[] bArray) {
		System.out.printf("%02X\n", bytesToInt(bArray, 0, bArray.length));
	}
	
	public static void printHexs(byte[] bArray) {
		for (int i = 0; i < bArray.length; ++i) {
			System.out.printf("%02X ", bytesToInt(bArray, i, 1));
			if ((i+1) % 16 == 0) {
				System.out.println();
			}
		}
		System.out.println();
	}
	
	/**
	 * <p>Title: equals</p>
	 * <p>Description: 比较两个byte数组是否相等</p>
	 * @param b1
	 * @param b2
	 * @return
	 */
	public static boolean equals(byte[] b1, byte[] b2) {
		if (b1 == b2) {
			return true;
		}
		if (b1 == null || b2 == null) {
			return false;
		}
		if (b1.length != b2.length) {
			return false;
		}
		for (int i = 0; i < b1.length; ++i) {
			if (b1[i] != b2[i]) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * <p>Title: subBytes</p>
	 * <p>Description: 获得bArray从begin位置开始的length长度的子数组</p>
	 * @param bArray
	 * @param begin
	 * @param length
	 * @return
	 */
	public static byte[] subBytes(byte[] bArray, int begin, int length) {
		if (bArray == null) {
			return null;
		}
		if (begin < 0 || length < 0 || begin + length > bArray.length) {
			return null;
		}
		byte[] b = new byte[length];
		for (int i = 0; i < length; ++i) {
			b[i] = bArray[begin + i];
		}
		return b;
	}
	
	/**
	 * <p>Title: copy</p>
	 * <p>Description: 将from数组从toBegin到末尾的子串复制给to数组</p>
	 * @param from
	 * @param to
	 * @param toBegin
	 * @return
	 */
	public static int copy(byte[] from, byte[] to, int toBegin) {
		if (from == null || to == null) {
			throw new RuntimeException("源数组和目标数组不能为null");
		}
		if (toBegin + from.length > to.length) {
			throw new RuntimeException("源数组的长度不能大于目标数组从下标为" + toBegin + "到其尾部的长度");
		}
		System.arraycopy(from, 0, to, toBegin, from.length);
		return from.length;
	}
}

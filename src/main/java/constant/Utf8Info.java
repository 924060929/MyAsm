package constant;

import core.Constant;
import core.ConstantPoolTag;
import utils.ByteUtils;

public class Utf8Info implements ConstantType {
	private byte[] bytes;
	
	
	/**
	 * <p>Title: bytesLength</p>
	 * <p>Description: 测试用的方法，将来会删除</p>
	 *  TODO 框架稳定后删除这个方法
	 * @return
	 */
	public int bytesLength() {
		if (bytes == null) {
			return 0;
		}
		return bytes.length;
	}
	
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
	@Override
	public int getTag() {
		return ConstantPoolTag.CONSTANT_Utf8_info;
	}

	@Override
	public int size() {//tag长1字节，length长2字节
		if (bytes == null) {
			return Constant.U1 + Constant.U2;
		}
		return Constant.U1 + Constant.U2 + bytes.length;
	}
	
	@Override
	public int write(byte[] byteClass, int begin) {
		byteClass[begin++] = (byte)getTag();
		begin += ByteUtils.copy(ByteUtils.intToBytes(bytes.length, Constant.U2), byteClass, begin);
		begin += ByteUtils.copy(bytes, byteClass, begin);
		
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin) {
		int length = ByteUtils.bytesToInt(byteClass, begin, Constant.U2);
		begin += Constant.U2;
		bytes = ByteUtils.subBytes(byteClass, begin, length);
		return begin + length;
	}
	
	@Override
	public String toString() {
		return new String(bytes);
	}
}

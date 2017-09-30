package constant;

import core.Constant;
import core.ConstantPoolTag;
import utils.ByteUtils;

public class LongInfo implements ConstantType {
	private byte[] bytes;
	
	public byte[] getBytes() {
		return bytes;
	}
	
	public void setBytes(byte[] bytes) {
		if (bytes == null || bytes.length != Constant.U8) {
			throw new RuntimeException("LongInfo的bytes不能为空且长度必须为8字节");
		}
		this.bytes = bytes;
	}
	
	@Override
	public int getTag() {
		return ConstantPoolTag.CONSTANT_Long_info;
	}

	@Override
	public int size() {//tag、bytes
		return Constant.U1 + Constant.U8;
	}

	@Override
	public int write(byte[] byteClass, int begin) {
		byteClass[begin++] = (byte)getTag();
		begin += ByteUtils.copy(bytes, byteClass, begin);
		
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin) {
		bytes = ByteUtils.subBytes(byteClass, begin, Constant.U8);
		return begin + Constant.U8;
	}
}

package constant;

import core.Constant;
import core.ConstantPoolTag;
import utils.ByteUtils;

public class FloatInfo implements ConstantType {
	private byte[] bytes;
	
	public void setBytes(byte[] bytes) {
		if (bytes == null || bytes.length != Constant.U4) {
			throw new RuntimeException("FloatInfo的bytes不能为空且长度必须为4字节");
		}
		this.bytes = bytes;
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
	@Override
	public int getTag() {//tag、bytes
		return ConstantPoolTag.CONSTANT_Float_info;
	}

	@Override
	public int size() {
		return Constant.U1 + Constant.U4;
	}

	@Override
	public int write(byte[] byteClass, int begin) {
		byteClass[begin++] = (byte)getTag();
		begin += ByteUtils.copy(bytes, byteClass, begin);
		
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin) {
		bytes = ByteUtils.subBytes(byteClass, begin, Constant.U4);
		return begin + Constant.U4;
	}
}

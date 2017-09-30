package constant;

import core.Constant;
import core.ConstantPoolTag;
import utils.ByteUtils;

public class StringInfo implements ConstantType {
	private byte[] index;
	
	public StringInfo() {}
	
	public byte[] getIndex() {
		return index;
	}
	
	public void setIndex(byte[] index) {
		if (index == null || index.length != Constant.U2) {
			throw new RuntimeException("StringInfo的index不能为空且长度必须为2字节");
		}
		this.index = index;
	}

	@Override
	public int getTag() {
		return ConstantPoolTag.CONSTANT_String_info;
	}

	@Override
	public int size() {//tag、index
		return Constant.U1 + Constant.U2;
	}
	
	@Override
	public int write(byte[] byteClass, int begin) {
		byteClass[begin++] = (byte)getTag();
		begin += ByteUtils.copy(index, byteClass, begin);
		
		return begin;
	}

	@Override
	public int read(byte[] byteClass, int begin) {
		index = ByteUtils.subBytes(byteClass, begin, Constant.U2);
		return begin + Constant.U2;
	}
}

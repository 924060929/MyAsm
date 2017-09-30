package constant;

import core.Constant;
import core.ConstantPoolTag;
import utils.ByteUtils;

public class ClassInfo implements ConstantType {
	private byte[] index;
	
	public byte[] getIndex() {
		return index;
	}
	
	public void setIndex(byte[] index) {
		if (index == null || index.length != Constant.U2) {
			throw new RuntimeException("ClassInfo的index不能为空且长度必须为2字节");
		}
		this.index = index;
	}

	@Override
	public int getTag() {//tag、index
		return ConstantPoolTag.CONSTANT_Class_info;
	}

	@Override
	public int size() {
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
